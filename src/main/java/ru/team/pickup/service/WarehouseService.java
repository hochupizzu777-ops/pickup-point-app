package ru.team.pickup.service;

import ru.team.pickup.model.CellSize;
import ru.team.pickup.model.StorageCell;
import ru.team.pickup.repository.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Сервис управления складом (Warehouse Service).
 * <p>
 * Реализует бизнес-логику резервирования и освобождения ячеек хранения.
 * Работает с неизменяемыми объектами {@link StorageCell}:
 * методы occupy/release возвращают новые экземпляры, текущее состояние не меняется.
 * <p>
 * Основные контракты:
 * <ul>
 *   <li>Резервирование: поиск свободной ячейки минимального подходящего размера.
 *       При равенстве размеров выбирается ячейка с наименьшим номером.</li>
 *   <li>Освобождение: строгое соответствие владельца ячейки и запрашивающего освобождения.</li>
 * </ul>
 */
public class WarehouseService implements WarehouseOperations {

    private final Repository<StorageCell, String> repository;

    /**
     * Конструктор сервиса.
     *
     * @param repository репозиторий для доступа к данным ячеек хранения
     * @throws IllegalArgumentException если repository равен null
     */
    public WarehouseService(Repository<StorageCell, String> repository) {
        this.repository = Objects.requireNonNull(repository, "Repository не может быть null");
    }

    /**
     * Возвращает список всех ячеек склада.
     *
     * @return список всех ячеек хранения
     */
    @Override
    public List<StorageCell> listCells() {
        return repository.findAll();
    }

    /**
     * Резервирует ячейку для заказа.
     * <p>
     * Алгоритм:
     * <ol>
     *   <li>Валидация входных параметров.</li>
     *   <li>Проверка, не зарезервирована ли уже ячейка под этот заказ.</li>
     *   <li>Поиск всех свободных ячеек, размер которых >= требуемого.</li>
     *   <li>Сортировка: по возрастанию размера, затем по возрастанию номера ячейки.</li>
     *   <li>Выбор первой подходящей ячейки, создание нового занятого состояния через occupy(), сохранение.</li>
     * </ol>
     *
     * @param orderId идентификатор заказа
     * @param requiredSize требуемый минимальный размер ячейки
     * @return номер зарезервированной ячейки
     * @throws IllegalArgumentException если аргументы невалидны
     * @throws IllegalStateException если нет свободных ячеек подходящего размера
     *         или заказ уже занимает другую ячейку
     */
    @Override
    public String reserveCell(String orderId, CellSize requiredSize) {
        Objects.requireNonNull(orderId, "orderId не может быть null");
        Objects.requireNonNull(requiredSize, "requiredSize не может быть null");

        // Проверка: не зарезервирована ли ячейка под этот заказ ранее
        for (StorageCell cell : repository.findAll()) {
            Optional<String> currentOrderId = cell.getOrderId();
            if (currentOrderId.isPresent() && currentOrderId.get().equals(orderId)) {
                throw new IllegalStateException("Заказ " + orderId + " уже занимает ячейку " + cell.getId());
            }
        }

        // Поиск свободных ячеек подходящего размера
        List<StorageCell> availableCells = repository.findAll().stream()
                .filter(StorageCell::isFree)
                .filter(cell -> cell.getSize().compareTo(requiredSize) >= 0)
                .toList();

        if (availableCells.isEmpty()) {
            throw new IllegalStateException("Нет свободных ячеек размера " + requiredSize + " или больше");
        }

        // Сортировка: минимальный размер, затем минимальный номер
        availableCells.sort(Comparator
                .comparing(StorageCell::getSize)
                .thenComparing(StorageCell::getId));

        StorageCell selectedCell = availableCells.get(0);
        // occupy() возвращает новый объект StorageCell с занятым состоянием
        StorageCell occupiedCell = selectedCell.occupy(orderId);
        repository.save(occupiedCell);

        return occupiedCell.getId();
    }

    /**
     * Освобождает ячейку от заказа.
     * <p>
     * Алгоритм:
     * <ol>
     *   <li>Валидация идентификатора ячейки.</li>
     *   <li>Получение ячейки из хранилища.</li>
     *   <li>Проверка существования ячейки.</li>
     *   <li>Проверка, что ячейка занята и принадлежит указанному заказу.</li>
     *   <li>Создание нового свободного состояния через release(), сохранение.</li>
     * </ol>
     *
     * @param cellId идентификатор ячейки
     * @param orderId идентификатор заказа, который должен освободить ячейку
     * @throws IllegalArgumentException если аргументы невалидны
     * @throws IllegalStateException если ячейка не найдена, свободна или принадлежит другому заказу
     */
    @Override
    public void releaseCell(String cellId, String orderId) {
        Objects.requireNonNull(cellId, "cellId не может быть null");
        Objects.requireNonNull(orderId, "orderId не может быть null");

        StorageCell cell = repository.findById(cellId)
                .orElseThrow(() -> new IllegalStateException("Ячейка " + cellId + " не найдена"));

        if (cell.isFree()) {
            throw new IllegalStateException("Ячейка " + cellId + " уже свободна");
        }

        Optional<String> currentOrderId = cell.getOrderId();
        if (currentOrderId.isEmpty() || !currentOrderId.get().equals(orderId)) {
            throw new IllegalStateException(
                    "Ячейка " + cellId + " занята заказом " + currentOrderId.orElse("null") +
                            ", а не заказом " + orderId
            );
        }

        // release() возвращает новый объект StorageCell со свободным состоянием
        StorageCell freeCell = cell.release();
        repository.save(freeCell);
    }
}
