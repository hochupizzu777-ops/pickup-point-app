package ru.team.pickup.service;

import ru.team.pickup.model.AppSnapshot;
import ru.team.pickup.model.OrderStatus;
import ru.team.pickup.model.StorageCell;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * ReportService — отчёты по снимку состояния, У2.
 *
 * Не хранит состояние, не обращается к репозиториям.
 * Все методы получают AppSnapshot параметром — это позволяет
 * выполнять отчёты вне монитора сервиса, на неизменяемой копии.
 *
 * Меню само форматирует результат для консоли — здесь только данные.
 */
public class ReportService {

    /**
     * Подсчитывает количество заказов каждого статуса.
     *
     * В результате присутствуют ВСЕ статусы из OrderStatus,
     * даже если количество заказов с этим статусом — 0.
     * Для карты используется EnumMap — компактнее HashMap для enum-ключей.
     *
     * @param snapshot снимок состояния с заказами и ячейками
     * @return карта OrderStatus → количество заказов (все статусы, включая 0)
     */
    public Map<OrderStatus, Long> countByStatus(AppSnapshot snapshot) {
        // EnumMap работает быстрее и занимает меньше памяти, чем HashMap,
        // потому что ключи — заранее известный конечный набор enum-значений.
        Map<OrderStatus, Long> result = new EnumMap<>(OrderStatus.class);

        // Инициализируем все статусы нулём, чтобы в карте были все ключи.
        for (OrderStatus status : OrderStatus.values()) {
            result.put(status, 0L);
        }

        // Если снимок пустой (нет заказов) — возвращаем карту со всеми нулями.
        // Это корректная обработка пустого снимка, как требует план.
        if (snapshot == null) {
            return result;
        }

        List<?> orders = snapshot.getOrders();

        // Перебираем заказы и считаем по статусам.
        // Используем getOrders() из AppSnapshot, который возвращает List<Order>.
        for (Object obj : orders) {
            if (obj instanceof ru.team.pickup.model.Order order) {
                OrderStatus status = order.getStatus();
                result.put(status, result.get(status) + 1);
            }
        }

        return result;
    }

    /**
     * Подсчитывает количество занятых ячеек.
     *
     * Занятой считается ячейка, у которой orderId не пуст (isFree == false).
     * Корректно обрабатывает пустой снимок — возвращает 0.
     *
     * @param snapshot снимок состояния с заказами и ячейками
     * @return количество занятых ячеек
     */
    public long countOccupiedCells(AppSnapshot snapshot) {
        // Пустой снимок — 0 занятых ячеек.
        if (snapshot == null) {
            return 0;
        }

        List<StorageCell> cells = snapshot.getCells();

        // Считаем ячейки, где isFree() == false.
        // stream().filter().count() — лаконичный способ подсчёта.
        return cells.stream()
                .filter(cell -> !cell.isFree())
                .count();
    }
}
