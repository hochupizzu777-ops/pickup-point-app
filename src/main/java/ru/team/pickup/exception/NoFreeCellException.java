// Объявление пакета, в котором находится класс. Это помогает организовать код по модулям.
package ru.team.pickup.exception;

// Импорт не нужен — PickupPointException находится в том же пакете.

/**
 * Javadoc-комментарий: описание класса, его назначение и контекст использования.
 * У2 — уровень ошибки (вторая категория в системе классификации проекта).
 */
public class NoFreeCellException extends PickupPointException {

    // КОНСТРУКТОР 1: только сообщение об ошибке.
    public NoFreeCellException(String message) {
       super(message);
    }

    // КОНСТРУКТОР 2: сообщение + причина (cause).
        public NoFreeCellException(String message, Throwable cause) {
        super(message, cause);
    }

    // КОНСТРУКТОР 3: только причина, без текстового сообщения.
    // Удобен, когда само исключение-причина уже содержит достаточно информации.
    public NoFreeCellException(Throwable cause) {
        super(cause);
    }

    // СТАТИЧЕСКИЙ ФАБРИЧНЫЙ МЕТОД — удобный способ создать исключение с типизированным сообщением.
    public static NoFreeCellException forSize(String requiredSize) {
        return new NoFreeCellException(
                String.format("Нет свободной ячейки требуемого размера: %s", requiredSize)
        );
    }
}
