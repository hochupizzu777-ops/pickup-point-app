package ru.team.pickup.model;

import java.util.Objects;
import java.util.Optional;

/**
 * StorageCell — неизменяемая ячейка склада.
 *
 * Содержит id (например, "S001"), размер и orderId (null, если свободна).
 * Изменение состояния (занятие/освобождение) возвращает новый объект,
 * а не меняет текущий — это принцип неизменяемости из плана.
 *
 * equals/hashCode — только по id, как указано в плане.
 */
public class StorageCell {

    // id ячейки — уникальный идентификатор, например "S001", "M003", "L002".
    // final — задаётся в конструкторе и не меняется.
    // private — доступ только через геттер.
    private final String id;

    // Размер ячейки — SMALL, MEDIUM или LARGE.
    // final — размер ячейки не меняется со временем.
    private final CellSize size;

    // id заказа, который занимает ячейку. null, если ячейка свободна.
    // final — изменение занятости возвращает новый объект, это поле не меняется.
    private final String orderId;

    /**
     * Конструктор ячейки.
     *
     * @param id      уникальный идентификатор ячейки (например, "S001")
     * @param size    размер ячейки
     * @param orderId id заказа или null, если ячейка свободна
     * @throws IllegalArgumentException если id или size равны null
     */
    public StorageCell(String id, CellSize size, String orderId) {
        // Objects.requireNonNull проверяет, что значение не null.
        // Если null — бросает NullPointerException с сообщением.
        // Это защита от создания ячейки без id или размера.
        this.id = Objects.requireNonNull(id, "id ячейки не может быть null");
        this.size = Objects.requireNonNull(size, "размер ячейки не может быть null");
        // orderId может быть null — это нормально, значит ячейка свободна.
        this.orderId = orderId;
    }

    // Геттер для id.
    public String getId() {
        return id;
    }

    // Геттер для размера.
    public CellSize getSize() {
        return size;
    }

    /**
     * Геттер для orderId, обёрнутый в Optional.
     * Возвращает Optional.empty(), если ячейка свободна (orderId == null),
     * или Optional.of(orderId), если ячейка занята.
     *
     * План требует: "внешнему коду getter возвращает Optional<String>".
     * Это заставляет вызывающий код явно обрабатывать случай свободной ячейки.
     */
    public Optional<String> getOrderId() {
        // Optional.ofNullable — если orderId != null, вернёт Optional.of(orderId),
        // если null — вернёт Optional.empty().
        return Optional.ofNullable(orderId);
    }

    /**
     * Проверяет, свободна ли ячейка.
     * Ячейка свободна, если orderId == null.
     *
     * @return true, если ячейка не занята заказом
     */
    public boolean isFree() {
        // orderId == null означает, что ни один заказ не занимает ячейку.
        return orderId == null;
    }

    /**
     * Занять ячейку заказом.
     * Возвращает НОВЫЙ объект StorageCell с тем же id и размером, но с заполненным orderId.
     * Текущий объект не меняется — это принцип неизменяемости.
     *
     * @param orderId id заказа, который занимает ячейку
     * @return новый объект StorageCell с занятым orderId
     * @throws IllegalArgumentException если ячейка уже занята или orderId равен null
     */
    public StorageCell occupy(String orderId) {
        // Проверяем, что ячейка свободна — нельзя занять уже занятую.
        if (!isFree()) {
            throw new IllegalStateException("Ячейка " + id + " уже занята");
        }
        // Проверяем, что orderId не null — нельзя занять ячейку без заказа.
        Objects.requireNonNull(orderId, "orderId не может быть null при занятии ячейки");
        // Создаём и возвращаем новый объект с тем же id и размером, но с orderId.
        // Текущий объект остаётся неизменным.
        return new StorageCell(this.id, this.size, orderId);
    }

    /**
     * Освободить ячейку.
     * Возвращает НОВЫЙ объект StorageCell с orderId = null.
     * Текущий объект не меняется.
     *
     * @return новый объект StorageCell со свободным состоянием
     * @throws IllegalStateException если ячейка уже свободна
     */
    public StorageCell release() {
        // Проверяем, что ячейка занята — нельзя освободить свободную.
        if (isFree()) {
            throw new IllegalStateException("Ячейка " + id + " уже свободна");
        }
        // Создаём и возвращаем новый объект с orderId = null.
        return new StorageCell(this.id, this.size, null);
    }

    /**
     * equals — сравнение только по id, как указано в плане.
     * Две ячейки с одинаковым id считаются равными, даже если у них разные orderId.
     * Это удобно для поиска в репозитории и в коллекциях.
     */
    @Override
    public boolean equals(Object o) {
        // Если это тот же объект — true.
        if (this == o) return true;
        // Если o не StorageCell (или null) — false.
        if (!(o instanceof StorageCell that)) return false;
        // Сравниваем только id.
        return Objects.equals(id, that.id);
    }

    /**
     * hashCode — только по id, согласован с equals.
     * Если equals возвращает true для двух объектов,
     * то hashCode должен вернуть одинаковое значение.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * toString — удобное представление для логов и отладки.
     * Пример: "StorageCell{id=S001, size=SMALL, orderId=null}" — свободная ячейка.
     * Пример: "StorageCell{id=M003, size=MEDIUM, orderId=ORD-42}" — занятая ячейка.
     */
    @Override
    public String toString() {
        return "StorageCell{" +
                "id=" + id +
                ", size=" + size +
                ", orderId=" + orderId +
                '}';
    }
}
