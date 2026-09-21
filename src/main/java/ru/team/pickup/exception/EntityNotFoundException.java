package ru.team.pickup.exception;

/**
 * EntityNotFoundException — сущность не найдена, У2
 *
 * Наследуется от PickupPointException.
 *
 * Используется, когда операция требует существующий заказ или ячейку. Сообщение должно содержать тип сущности и искомый номер.
 *
 * Обычный поиск в репозитории возвращает Optional.empty(). Уже вызывающая операция решает, нужно ли исключение.
 */
public class EntityNotFoundException extends PickupPointException {
    // TODO: добавить реализацию в задаче ответственного участника.
}
