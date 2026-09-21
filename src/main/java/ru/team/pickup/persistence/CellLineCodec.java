package ru.team.pickup.persistence;

import ru.team.pickup.model.StorageCell;

/**
 * CellLineCodec — преобразование ячейки, У2
 *
 * Реализует:
 *
 * LineCodec<StorageCell>
 *
 * Сохраняет номер ячейки, размер и номер заказа.
 *
 * Для свободной ячейки в файле используем специальное значение -.
 *
 * При чтении проверяет структуру строки и восстанавливает свободную либо занятую ячейку.
 */
public class CellLineCodec implements LineCodec<StorageCell> {
    // TODO: добавить реализацию в задаче ответственного участника.
}
