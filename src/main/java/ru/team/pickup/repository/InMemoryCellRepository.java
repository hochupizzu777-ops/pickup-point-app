package ru.team.pickup.repository;

import ru.team.pickup.model.StorageCell;

/**
 * InMemoryCellRepository — хранилище ячеек, У2
 *
 * Реализует:
 *
 * Repository<StorageCell, String>
 *
 * Внутри:
 *
 * Map<String, StorageCell> cells;
 *
 * Работает аналогично хранилищу заказов.
 *
 * Не выбирает подходящую ячейку: только сохраняет и возвращает объекты.
 */
public class InMemoryCellRepository implements Repository<StorageCell, String> {
    // TODO: добавить реализацию в задаче ответственного участника.
}
