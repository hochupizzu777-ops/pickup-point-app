package ru.team.pickup.repository;

import ru.team.pickup.model.Order;

/**
 * InMemoryOrderRepository — хранилище заказов, У1
 *
 * Реализует:
 *
 * Repository<Order, String>
 *
 * Внутри:
 *
 * Map<String, Order> orders;
 *
 * Использовать HashMap.
 *
 * Реализация методов:
 *
 * save() — сохранить по order.getId().
 * findById() — найти по ключу.
 * findAll() — скопировать значения карты в отдельный неизменяемый список.
 *
 * Здесь нет проверки кода получения, сроков и доступности склада.
 */
public class InMemoryOrderRepository implements Repository<Order, String> {
    // TODO: добавить реализацию в задаче ответственного участника.
}
