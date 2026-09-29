package ru.team.pickup.service;

import ru.team.pickup.model.Order;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * OrderQueries — поиск и сортировка, У1
 *
 * Главный метод:
 *
 * public static List<Order> filterAndSort(
 *         List<Order> orders,
 *         Predicate<Order> filter,
 *         Comparator<Order> comparator);
 *
 * Что реализовать:
 *
 * Фильтрацию через filter.
 * Сортировку через sorted.
 * Получение нового списка.
 * Сохранение исходного списка без изменений.
 *
 * Например, меню передаёт условие поиска по телефону и сортировку по дате.
 *
 * Класс не получает репозиторий: ему передают список из снимка.
 */
public final class OrderQueries {

    private OrderQueries(){
    }

    public static List<Order>  filterAndSort(
            List<Order> orders,
            Predicate<Order> filter,
            Comparator<Order> comparator
    ){
        Objects.requireNonNull(orders, "Заказ не может быть null");
        Objects.requireNonNull(filter, "filter не может быть с null");
        Objects.requireNonNull(comparator, "Правило сортировки не должно быть null");

        return orders.stream()
                .filter(filter)
                .sorted(comparator)
                .toList();
    }
}
