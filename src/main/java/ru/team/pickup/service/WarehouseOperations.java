package ru.team.pickup.service;

/**
 * WarehouseOperations — интерфейс склада, У2
 *
 * public interface WarehouseOperations {
 *
 *     String reserveCell(String orderId, CellSize requiredSize);
 *
 *     void releaseCell(String cellId, String orderId);
 *
 *     List<StorageCell> listCells();
 * }
 *
 * Контракт:
 *
 * reserveCell() находит и сразу занимает ячейку, возвращает её номер.
 * Если места нет — NoFreeCellException.
 * releaseCell() проверяет, что ячейка занята именно указанным заказом, и освобождает её.
 * listCells() возвращает копию списка.
 *
 * Через этот интерфейс твоя часть обращается к части напарника.
 */
public interface WarehouseOperations {
    // TODO: добавить реализацию в задаче ответственного участника.
}
