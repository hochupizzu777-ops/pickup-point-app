package ru.team.pickup.model;

/**
 * 6. StorageCell — ячейка, У2
 *
 * Поля:
 *
 * String id;
 * CellSize size;
 * String orderId;
 *
 * orderId == null означает, что ячейка свободна.
 *
 * Методы:
 *
 * String getId();
 * CellSize getSize();
 * Optional<String> getOrderId();
 * boolean isFree();
 *
 * StorageCell occupy(String orderId);
 * StorageCell release();
 *
 * Что реализовать:
 *
 * Конструкторы для свободной ячейки и восстановления занятой.
 * Проверки номера и размера.
 * occupy() создаёт новый занятый объект.
 * release() создаёт новый свободный объект.
 * Занять уже занятую или освободить свободную ячейку нельзя.
 * equals() и hashCode() по id.
 */
public final class StorageCell {
    // TODO: добавить реализацию в задаче ответственного участника.
}
