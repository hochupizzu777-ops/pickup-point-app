package ru.team.pickup.service;

import ru.team.pickup.model.CellSize;
import ru.team.pickup.model.StorageCell;

import java.util.List;

/**
 * WarehouseOperations — граница между заказами (У1) и складом (У2).
 *
 * PickupPointService (Илья) вызывает эти методы,
 * а WarehouseService (ты) их реализует.
 *
 * Контракт:
 * — reserveCell() находит и сразу занимает минимальную подходящую ячейку,
 *   возвращает её id. Если места нет — NoFreeCellException.
 * — releaseCell() проверяет, что ячейка занята именно указанным заказом,
 *   и освобождает её. Чужую ячейку не трогает.
 * — listCells() возвращает неизменяемую копию списка ячеек.
 *
 * Отдельных публичных шагов «найти, затем занять» между сервисами нет:
 * reserveCell — единая атомарная операция.
 */
public interface WarehouseOperations {

    /**
     * Находит и сразу занимает минимальную свободную ячейку подходящего размера.
     *
     * Правило выбора (правило 3 из плана):
     * — минимальный размер, который вмещает заказ (fits);
     * — среди одинаковых по размеру — первая по номеру в лексикографическом порядке.
     *
     * @param orderId      id заказа, для которого резервируется ячейка
     * @param requiredSize требуемый размер ячейки
     * @return id занятой ячейки
     * @throws ru.team.pickup.exception.NoFreeCellException если нет подходящей свободной ячейки
     */
    String reserveCell(String orderId, CellSize requiredSize);

    /**
     * Освобождает ячейку, занятую указанным заказом.
     *
     * Проверяет существование ячейки и совпадение orderId перед освобождением.
     * Ошибка не освобождает чужую ячейку.
     *
     * @param cellId  id ячейки для освобождения
     * @param orderId id заказа, который занимал ячейку
     */
    void releaseCell(String cellId, String orderId);

    /**
     * Возвращает неизменяемую копию списка всех ячеек.
     *
     * @return неизменяемый список ячеек
     */
    List<StorageCell> listCells();
}
