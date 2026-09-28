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

    // Конструктор с сообщением — передаёт описание ошибки (тип сущности + номер)
    public EntityNotFoundException(String message) {
        super(message);
    }

    // Конструктор с сообщением и причиной — для случаев, когда ошибка вызвана другим исключением
    public EntityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    // Конструктор только с причиной — если причина важнее текстового описания
    public EntityNotFoundException(Throwable cause) {
        super(cause);
    }

    /**
     * Фабричный метод для удобного создания исключения с указанием типа сущности и идентификатора.
     * Пример: EntityNotFoundException.forEntity("Order", 12345)
     *
     * @param entityType тип сущности (например, "Order", "Cell")
     * @param id         идентификатор искомой сущности
     * @return экземпляр EntityNotFoundException с понятным сообщением
     */
    public static EntityNotFoundException forEntity(String entityType, long id) {
        return new EntityNotFoundException(
                String.format("Сущность типа '%s' с идентификатором %d не найдена", entityType, id)
        );
    }

    /**
     * Фабричный метод для строковых идентификаторов (если применимо).
     * Пример: EntityNotFoundException.forEntity("Cell", "A1-B2")
     *
     * @param entityType тип сущности
     * @param id         строковый идентификатор
     * @return экземпляр EntityNotFoundException
     */
    public static EntityNotFoundException forEntity(String entityType, String id) {
        return new EntityNotFoundException(
                String.format("Сущность типа '%s' с идентификатором '%s' не найдена", entityType, id)
        );
    }
}
