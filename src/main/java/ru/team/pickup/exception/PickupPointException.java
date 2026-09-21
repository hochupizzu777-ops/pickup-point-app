package ru.team.pickup.exception;

/**
 * PickupPointException — базовое бизнес-исключение, У1
 *
 * Наследуется от RuntimeException.
 *
 * Два конструктора:
 *
 * PickupPointException(String message);
 *
 * PickupPointException(String message, Throwable cause);
 *
 * Внутри — вызов соответствующего конструктора super.
 *
 * Общий предок позволяет меню обрабатывать ожидаемые ошибки приложения одним catch.
 */
public class PickupPointException extends RuntimeException {
    // TODO: добавить реализацию в задаче ответственного участника.
}
