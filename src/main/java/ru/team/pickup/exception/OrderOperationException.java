package ru.team.pickup.exception;

/**
 * OrderOperationException — ошибка операции с заказом, У1
 *
 * Наследуется от PickupPointException.
 *
 * Ситуации:
 *
 * повторный номер;
 * неверный код;
 * недопустимый статус;
 * выдача после окончания срока.
 *
 * Те же два конструктора: сообщение и сообщение с причиной.
 */
public class OrderOperationException extends PickupPointException {
    // TODO: добавить реализацию в задаче ответственного участника.
}
