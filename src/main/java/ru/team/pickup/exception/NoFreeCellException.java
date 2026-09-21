package ru.team.pickup.exception;

/**
 * NoFreeCellException — нет места, У2
 *
 * Наследуется от PickupPointException.
 *
 * Выбрасывается при невозможности разместить заказ. В сообщении указывается требуемый размер.
 *
 * После ошибки склад и список заказов должны остаться прежними.
 */
public class NoFreeCellException extends PickupPointException {
    // TODO: добавить реализацию в задаче ответственного участника.
}
