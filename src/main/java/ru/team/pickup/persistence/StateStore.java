package ru.team.pickup.persistence;

import ru.team.pickup.model.AppSnapshot;

import java.io.IOException;
import java.util.Optional;

/**
 * Интерфейс хранилища состояния приложения (State Store).
 * <p>
 * Реализует контракт сохранения и загрузки полного снимка состояния ({@link AppSnapshot}).
 * <p>
 * Контракт поведения:
 * <ul>
 *   <li>{@code save()} — полностью перезаписывает файл снимка.</li>
 *   <li>{@code load()} — возвращает {@code Optional.empty()}, только если файл физически отсутствует.</li>
 *   <li>Если файл существует, но не может быть корректно прочитан (повреждён), выбрасывается {@code IOException}.
 *       Повреждённый файл не считается отсутствующим.</li>
 * </ul>
 */
public interface StateStore {

    /**
     * Сохраняет полный снимок состояния приложения в постоянное хранилище.
     * <p>
     * Операция является полной перезаписью: предыдущее содержимое файла будет заменено.
     *
     * @param snapshot снимок состояния, который необходимо сохранить
     * @throws IOException если произошла ошибка ввода-вывода при записи файла
     */
    void save(AppSnapshot snapshot) throws IOException;

    /**
     * Загружает снимок состояния приложения из постоянного хранилища.
     * <p>
     * Возвращает {@code Optional.empty()}, только если файл не существует на диске.
     * Если файл существует, но не может быть прочитан или десериализован (повреждён),
     * выбрасывается {@code IOException}. Это позволяет различать сценарии
     * "данных нет" и "данные есть, но они невалидны".
     *
     * @return {@code Optional} с загруженным {@code AppSnapshot}, либо {@code Optional.empty()},
     *         если файл не найден
     * @throws IOException если файл существует, но повреждён или не может быть прочитан
     */
    Optional<AppSnapshot> load() throws IOException;
}
