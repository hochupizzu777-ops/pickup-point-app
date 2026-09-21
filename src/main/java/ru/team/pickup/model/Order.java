package ru.team.pickup.model;

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
    // TODO: добавить реализацию в задаче ответственного участника.
}
