package ru.team.pickup.persistence;

import ru.team.pickup.exception.DataFormatException;
import ru.team.pickup.model.AppSnapshot;
import ru.team.pickup.model.Order;
import ru.team.pickup.model.StorageCell;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * TextStateStore — чтение и запись файла состояния, У2.
 *
 * Получает через конструктор: Path, LineCodec<Order>, LineCodec<StorageCell>.
 *
 * Формат файла (UTF-8):
 *   Строка 1 (заголовок): PVZ|1|<числоЗаказов>|<числоЯчеек>
 *   Далее — строки ORDER и CELL через кодеки.
 *
 * При сохранении:
 *   — проверяет снимок через validate();
 *   — создаёт родительский каталог, если его нет;
 *   — записывает заголовок и все строки через try-with-resources.
 *
 * При загрузке:
 *   — если файла нет — Optional.empty;
 *   — читает заголовок, проверяет версию и количество записей;
 *   — восстанавливает заказы и ячейки во временные списки;
 *   — создаёт AppSnapshot и проверяет его связи через validate();
 *   — возвращает только полностью корректный результат.
 *
 * Репозитории этот класс не заполняет — это сделает Main.
 */
public class TextStateStore implements StateStore {

    // Версия формата файла. Если формат изменится, увеличить число.
    private static final String FORMAT_VERSION = "1";

    // Префикс заголовка — для быстрой проверки, что это наш файл.
    private static final String HEADER_PREFIX = "PVZ";

    // Путь к файлу состояния.
    private final Path path;

    // Кодек заказа — преобразует Order в строку и обратно.
    private final LineCodec<Order> orderCodec;

    // Кодек ячейки — преобразует StorageCell в строку и обратно.
    private final LineCodec<StorageCell> cellCodec;

    /**
     * Конструктор.
     *
     * @param path        путь к файлу состояния
     * @param orderCodec  кодек заказов
     * @param cellCodec   кодек ячеек
     */
    public TextStateStore(Path path, LineCodec<Order> orderCodec, LineCodec<StorageCell> cellCodec) {
        this.path = path;
        this.orderCodec = orderCodec;
        this.cellCodec = cellCodec;
    }

    /**
     * Сохраняет снимок в файл.
     *
     * Порядок действий:
     * 1. Проверка целостности снимка (validate).
     * 2. Создание родительского каталога.
     * 3. Запись заголовка: PVZ|1|orderCount|cellCount.
     * 4. Запись строк заказов через orderCodec.
     * 5. Запись строк ячеек через cellCodec.
     *
     * @param snapshot снимок состояния
     * @throws IOException если запись не удалась
     */
    @Override
    public void save(AppSnapshot snapshot) throws IOException {
        // Сначала проверяем целостность — не сохраняем битые данные.
        // validate() бросит DataFormatException при нарушении связей.
        snapshot.validate();

        List<Order> orders = snapshot.getOrders();
        List<StorageCell> cells = snapshot.getCells();

        // Создаём родительский каталог, если его нет.
        // null-check нужен: если path — просто "state.txt" без папки,
        // getParent() вернёт null, и Files.createDirectories выбросит NPE.
        Path parent = path.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        // try-with-resources автоматически закрывает BufferedWriter.
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            // Заголовок: PVZ|1|<кол-во заказов>|<кол-во ячеек>
            writer.write(String.join("|",
                    HEADER_PREFIX,
                    FORMAT_VERSION,
                    String.valueOf(orders.size()),
                    String.valueOf(cells.size())
            ));
            writer.newLine();

            // Записываем все заказы через кодек.
            for (Order order : orders) {
                writer.write(orderCodec.encode(order));
                writer.newLine();
            }

            // Записываем все ячейки через кодек.
            for (StorageCell cell : cells) {
                writer.write(cellCodec.encode(cell));
                writer.newLine();
            }
        }
    }

    /**
     * Загружает снимок из файла.
     *
     * Порядок действий:
     * 1. Если файла нет — Optional.empty.
     * 2. Читаем заголовок, проверяем префикс и версию.
     * 3. Читаем указанное количество строк заказов через orderCodec.
     * 4. Читаем указанное количество строк ячеек через cellCodec.
     * 5. Создаём AppSnapshot, вызываем validate().
     * 6. Возвращаем Optional с корректным снимком.
     *
     * Любое нарушение формата — DataFormatException (наследник IOException).
     *
     * @return Optional с снимком, или empty если файла нет
     * @throws IOException если файл повреждён или не читается
     */
    @Override
    public Optional<AppSnapshot> load() throws IOException {
        // Файла нет — это нормально, возвращаем empty.
        // Main создаст пустой склад.
        if (!Files.exists(path)) {
            return Optional.empty();
        }

        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            // --- Чтение заголовка ---
            String header = reader.readLine();

            // Существующий, но пустой файл — ошибка, не молчаливый пустой склад.
            if (header == null) {
                throw new DataFormatException("Файл состояния пуст — заголовок отсутствует");
            }

            // Разбиваем заголовок: PVZ|1|orders|cells
            String[] headerTokens = header.split("\\|", -1);
            if (headerTokens.length != 4) {
                throw new DataFormatException(
                        "Неверный заголовок: ожидалось 4 поля, получено " + headerTokens.length
                );
            }

            // Проверяем префикс — это наш файл?
            if (!HEADER_PREFIX.equals(headerTokens[0])) {
                throw new DataFormatException(
                        "Неверный префикс файла: ожидался '" + HEADER_PREFIX + "', получен '" + headerTokens[0] + "'"
                );
            }

            // Проверяем версию формата.
            if (!FORMAT_VERSION.equals(headerTokens[1])) {
                throw new DataFormatException(
                        "Неподдерживаемая версия формата: ожидалась " + FORMAT_VERSION
                                + ", получена " + headerTokens[1]
                );
            }

            // Парсим количество заказов и ячеек.
            int orderCount;
            int cellCount;
            try {
                orderCount = Integer.parseInt(headerTokens[2]);
                cellCount = Integer.parseInt(headerTokens[3]);
            } catch (NumberFormatException e) {
                throw new DataFormatException(
                        "Некорректное количество записей в заголовке: "
                                + headerTokens[2] + ", " + headerTokens[3], e
                );
            }

            // Отрицательные количества — ошибка формата.
            if (orderCount < 0 || cellCount < 0) {
                throw new DataFormatException(
                        "Отрицательное количество записей: orders=" + orderCount + ", cells=" + cellCount
                );
            }

            // --- Чтение заказов во временный список ---
            List<Order> orders = new ArrayList<>(orderCount);
            for (int i = 0; i < orderCount; i++) {
                String line = reader.readLine();
                if (line == null) {
                    throw new DataFormatException(
                            "Неожиданный конец файла: прочитано " + i + " заказов, ожидалось " + orderCount
                    );
                }
                // Декодируем через кодек — при ошибке бросит DataFormatException.
                orders.add(orderCodec.decode(line));
            }

            // --- Чтение ячеек во временный список ---
            List<StorageCell> cells = new ArrayList<>(cellCount);
            for (int i = 0; i < cellCount; i++) {
                String line = reader.readLine();
                if (line == null) {
                    throw new DataFormatException(
                            "Неожиданный конец файла: прочитано " + i + " ячеек, ожидалось " + cellCount
                    );
                }
                cells.add(cellCodec.decode(line));
            }

            // --- Проверка: лишние строки после ожидаемых записей ---
            // Если после всех заказов и ячеек есть ещё строки — файл повреждён.
            String extraLine = reader.readLine();
            if (extraLine != null && !extraLine.isBlank()) {
                throw new DataFormatException(
                        "Лишние данные в конце файла: '" + extraLine + "'"
                );
            }

            // --- Создание снимка и валидация связей ---
            AppSnapshot snapshot = new AppSnapshot(orders, cells);

            // validate() проверяет: дубликаты id, ссылки заказов на ячейки,
            // соответствие занятости, размеры и т.д.
            // Если что-то не так — DataFormatException, данные не возвращаем.
            snapshot.validate();

            // Только полностью корректный результат попадает наружу.
            return Optional.of(snapshot);

        } catch (DataFormatException e) {
            // Пробрасываем как есть — это уже IOException.
            throw e;
        }
        // Другие IOException (например, нет прав на чтение) пробрасываются автоматически.
    }
}
