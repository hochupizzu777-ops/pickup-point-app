package ru.team.pickup.model;

import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import ru.team.pickup.exception.DataFormatException;

/**
 * AppSnapshot — неизменяемый снимок состояния приложения.
 *
 * Содержит списки всех заказов и всех ячеек на определённый момент времени.
 * Снимок создаётся под монитором PickupPointService, затем используется
 * отчётами и сохранением вне монитора — поэтому он должен быть неизменяемым.
 *
 * validate() проверяет целостность связей между заказами и ячейками.
 * Если связи нарушены — бросает DataFormatException с описанием проблемы.
 */
public class AppSnapshot {

    private final List<Order> orders;
    private final List<StorageCell> cells;

    public AppSnapshot(List<Order> orders, List<StorageCell> cells) {
        // List.copyOf создаёт неизменяемую копию списка.
        this.orders = List.copyOf(orders);
        this.cells = List.copyOf(cells);
    }

    public List<Order> getOrders() {
        return orders;
    }

    public List<StorageCell> getCells() {
        return cells;
    }

    /**
     * Проверяет целостность связей между заказами и ячейками.
     *
     * Проверки:
     * 1. Нет дубликатов id заказов.
     * 2. Нет дубликатов id ячеек.
     * 3. Каждый активный заказ (READY_FOR_PICKUP, EXPIRED) занимает ячейку.
     * 4. Ячейка активного заказа существует и занята именно этим заказом.
     * 5. Размер ячейки вмещает требуемый размер заказа.
     * 6. Каждая занятая ячейка указывает на активный заказ с соответствующим cellId.
     * 7. Выданные/возвращённые заказы имеют исторический cellId, указывающий на существующую ячейку.
     *
     * @throws DataFormatException если найдено нарушение связей
     */
    public void validate() throws DataFormatException {
        // --- Проверка 1: дубликаты id заказов ---
        Set<String> orderIds = new HashSet<>();
        Map<String, Order> orderMap = new HashMap<>();

        for (Order order : orders) {
            // Используем реальный метод из твоего Order.java: getId()
            String id = order.getId();

            if (!orderIds.add(id)) {
                throw new DataFormatException("Дубликат id заказа: " + id);
            }
            orderMap.put(id, order);
        }

        // --- Проверка 2: дубликаты id ячеек ---
        Set<String> cellIds = new HashSet<>();
        Map<String, StorageCell> cellMap = new HashMap<>();

        for (StorageCell cell : cells) {
            if (!cellIds.add(cell.getId())) {
                throw new DataFormatException("Дубликат id ячейки: " + cell.getId());
            }
            cellMap.put(cell.getId(), cell);
        }

        // --- Проверки 3–5 и 7: для каждого заказа ---
        for (Order order : orders) {
            // Используем реальный метод из твоего Order.java: getCellId()
            String cellId = order.getCellId();
            // Используем реальный метод из твоего Order.java: getStatus()
            OrderStatus status = order.getStatus();

            boolean isActive = status == OrderStatus.READY_FOR_PICKUP
                    || status == OrderStatus.EXPIRED;

            if (isActive) {
                if (cellId == null || cellId.isBlank()) {
                    throw new DataFormatException("Активный заказ " + order.getId() + " не имеет cellId");
                }

                StorageCell cell = cellMap.get(cellId);
                if (cell == null) {
                    throw new DataFormatException("Заказ " + order.getId() + " ссылается на несуществующую ячейку " + cellId);
                }

                // Исправление бага с Optional: сравниваем строки корректно
                String cellOrderId = cell.getOrderId().orElse(null);
                String currentOrderId = order.getId();

                if (cell.isFree() || !currentOrderId.equals(cellOrderId)) {
                    throw new DataFormatException("Ячейка " + cellId + " не занята заказом " + currentOrderId);
                }

                // Используем реальный метод из твоего Order.java: getRequiredSize()
                CellSize requiredSize = order.getRequiredSize();
                if (!cell.getSize().fits(requiredSize)) {
                    throw new DataFormatException("Ячейка " + cellId + " (" + cell.getSize() + ") слишком мала для заказа " + currentOrderId + " (требуется " + requiredSize + ")");
                }
            } else {
                // Для неактивных заказов (ISSUED, RETURNED) проверяем, что исторический cellId существует
                if (cellId != null && !cellId.isBlank() && !cellMap.containsKey(cellId)) {
                    throw new DataFormatException("Заказ " + order.getId() + " имеет исторический cellId " + cellId + ", но такая ячейка не существует");
                }
            }
        }

        // --- Проверка 6: каждая занятая ячейка указывает на активный заказ ---
        for (StorageCell cell : cells) {
            if (!cell.isFree()) {
                String orderId = cell.getOrderId().orElse(null);
                Order order = orderMap.get(orderId);

                if (order == null) {
                    throw new DataFormatException("Ячейка " + cell.getId() + " занята несуществующим заказом " + orderId);
                }

                OrderStatus orderStatus = order.getStatus();
                boolean orderActive = orderStatus == OrderStatus.READY_FOR_PICKUP
                        || orderStatus == OrderStatus.EXPIRED;

                if (!orderActive) {
                    throw new DataFormatException("Ячейка " + cell.getId() + " занята неактивным заказом " + orderId + " (статус: " + orderStatus + ")");
                }

                String orderCellId = order.getCellId();
                if (!cell.getId().equals(orderCellId)) {
                    throw new DataFormatException("Ячейка " + cell.getId() + " указывает на заказ " + orderId + ", но заказ указывает на ячейку " + orderCellId);
                }
            }
        }
    }
}
