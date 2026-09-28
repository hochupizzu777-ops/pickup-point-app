package ru.team.pickup.exception;

/**
 * OrderOperationException — ошибка операции с заказом, У1
 *
 * Наследуется от PickupPointException.
 *
 * Ситуации:
 * - повторный номер;
 * - неверный код;
 * - недопустимый статус;
 * - выдача после окончания срока.
 *
 * Те же два конструктора: сообщение и сообщение с причиной.
 */
public class OrderOperationException extends PickupPointException {

    // КОНСТРУКТОР 1: только сообщение об ошибке.
    // String message — текст ошибки, например "Повторный номер заказа" или "Недопустимый статус: DELIVERED".
    public OrderOperationException(String message) {
        // super(message) — вызывает конструктор родителя (PickupPointException),
        // тот, в свою очередь, передаёт сообщение в RuntimeException.
        // В итоге сообщение сохраняется в поле detailMessage и доступно через getMessage().
        super(message);
    }

    // КОНСТРУКТОР 2: сообщение + причина (cause).
    // Throwable cause — другое исключение, которое привело к этой ошибке.
    // Например, если при проверке статуса что-то упало с IllegalStateException —
    // передаём его как cause, чтобы в стек-трейсе была полная цепочка.
    public OrderOperationException(String message, Throwable cause) {
        // super(message, cause) — вызывает конструктор родителя с двумя параметрами.
        // Родитель передаст их дальше в RuntimeException, и оба сохранятся.
        super(message, cause);
    }
}

