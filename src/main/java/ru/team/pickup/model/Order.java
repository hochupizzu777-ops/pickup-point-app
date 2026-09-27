package ru.team.pickup.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * 5. Order — заказ, У1
 *
 * Поля:
 *
 * Поле	Тип
 * id	String
 * recipient	Recipient
 * items	List<OrderItem>
 * requiredSize	CellSize
 * pickupCode	String
 * receivedOn	LocalDate
 * expiresOn	LocalDate
 * status	OrderStatus
 * cellId	String
 *
 * Что реализовать:
 *
 * Конструктор со всеми полями.
 * Проверки обязательных значений.
 * Запрет пустого списка товаров и null внутри него.
 * Проверку: срок окончания хранения не раньше поступления.
 * Защитное копирование списка.
 * Getters.
 * equals() и hashCode() только по id.
 * toString() без вывода кода получения.
 * Метод:
 * Order withStatus(OrderStatus newStatus);
 *
 * Он возвращает новый заказ с изменённым статусом. Старый объект остаётся прежним. Разрешение на переход проверяет сервис.
 *
 * cellId сохраняется и после выдачи: это история того, где лежал заказ.
 */
public final class Order {

    private final String id;
    private final Recipient recipient;
    private final List<OrderItem> items;
    private final CellSize requiredSize;
    private final String pickupCode;
    private final LocalDate receivedOn;
    private final LocalDate expiresOn;
    private final OrderStatus status;
    private final String cellId;

    private static String requireText(String value, String fieldName){
        if (value == null || value.isBlank()){
            throw new IllegalArgumentException(
                    fieldName + " не может быть пустым"
            );
        }

        return value.strip();
    }

    public Order(
            String id,
            Recipient recipient,
            List<OrderItem> items,
            CellSize requiredSize,
            String pickupCode,
            LocalDate receivedOn,
            LocalDate expiresOn,
            OrderStatus status,
            String cellId
    ){
        this.id = requireText(id, "Номер заказа");
        this.pickupCode = requireText(pickupCode, "Код получения");
        this.cellId = requireText(cellId, "Номер ячейки");

        if (recipient == null){
            throw new IllegalArgumentException(
                    "Получатель не может быть null"
            );
        }
        if (items == null || items.isEmpty()){
            throw new IllegalArgumentException(
                    "Список товаров не может юыть пустым"
            );
        }
        for (OrderItem item : items){
            if (item == null){
                throw new IllegalArgumentException(
                        "Товар не может быть null"
                );
            }
        }
        if (requiredSize == null){
            throw new IllegalArgumentException(
                    "Размер заказа не может быть null"
            );
        }
        if (receivedOn == null || expiresOn == null){
            throw new IllegalArgumentException(
                    "Даты заказа не могут быть null"
            );
        }
        if (status == null){
            throw new IllegalArgumentException(
                    "Статус заказа не может быть null"
            );
        }

        this.recipient = recipient;
        this.items = List.copyOf(items);
        this.requiredSize = requiredSize;
        this.receivedOn = receivedOn;
        this.expiresOn = expiresOn;
        this.status = status;
    }

    public String getId(){
        return id;
    }
    public Recipient getRecipient(){
        return recipient;
    }

    public List<OrderItem> getItems() {
        return items;
    }
    public CellSize getRequiredSize(){
        return requiredSize;
    }
    public String getPickupCode(){
        return pickupCode;
    }
    public LocalDate getReceivedOn(){
        return receivedOn;
    }
    public LocalDate getExpiresOn(){
        return expiresOn;
    }
    public OrderStatus getStatus(){
        return status;
    }
    public String getCellId(){
        return cellId;
    }


    public Order withStatus(OrderStatus newStatus){
        return new Order(
                id,
                recipient,
                items,
                requiredSize,
                pickupCode,
                receivedOn,
                expiresOn,
                newStatus,
                cellId
        );
    }

    @Override
    public boolean equals(Object other){
        if (this == other){
            return true;
        }
        if (!(other instanceof Order order)){
            return false;
        }

        return id.equals(order.id);
    }
    @Override
    public int hashCode(){
        return id.hashCode();
    }
    @Override
    public String toString() {
        return "Order{" +
                "id='" + id + '\'' +
                ", status=" + status +
                ", receivedOn=" + receivedOn +
                ", expiresOn=" + expiresOn +
                ", cellId='" + cellId + '\'' +
                '}';
    }
}
