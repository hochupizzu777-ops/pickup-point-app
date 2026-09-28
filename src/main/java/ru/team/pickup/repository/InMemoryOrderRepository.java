package ru.team.pickup.repository;

import ru.team.pickup.model.Order;
import java.util.*;

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

    private final Map<String, Order> orders = new HashMap<>();

    @Override
    public Optional<Order> findById(String id){
        if (id == null || id.isBlank()){
            throw new IllegalArgumentException(
                    "Значение Id не может быть null или отсутствовать"
            );
        }

        return Optional.ofNullable(orders.get(id.strip()));
    }

    @Override
    public List<Order> findAll(){
        return List.copyOf(orders.values());
    }

    @Override
    public void save(Order order){
        if (order == null){
            throw new IllegalArgumentException(
                    "Нельзя добавить пустой заказ"
            );
        }

        orders.put(order.getId(), order);
    }
}
