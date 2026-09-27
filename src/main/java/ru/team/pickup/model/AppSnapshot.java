package ru.team.pickup.model;

import java.util.List;
import java.util.Set;
import java.util.HashSet;
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

    // Неизменяемый список заказов. List.copyOf в конструкторе гарантирует,
    // что никто не сможет изменить список после создания снимка.
    private final List<Order> orders;

    // Неизменяемый список ячеек.
    private final List<StorageCell> cells;

    /**
     * Конструктор снимка.
     *
     * @param orders список заказов (будет скопирован через List.copyOf)
     * @param cells  список ячеек (будет скопирован через List.copyOf)
     */
    public AppSnapshot(List<Order> orders, List<StorageCell> cells) {
        // List.copyOf создаёт неизменяемую копию списка.
        // Если передать null — бросит NullPointerException, что нормально:
        // снимок без списков не имеет смысла.
        this.orders = List.copyOf(orders);
        this.cells = List.copyOf(cells);
    }

    // Геттер для списка заказов. Возвращает неизменяемый список.
    public List<Order> getOrders() {
        return orders;
    }

    // Геттер для списка ячеек. Возвращает неизменяемый список.
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
        // Set хранит уникальные значения. Если размер Set меньше размера списка — есть дубликаты.
        Set<String> orderIds = new HashSet<>();
        for (Order order : orders) {
            // Если add вернул false — такой id уже есть в Set, значит дубликат.
            if (!orderIds.add(order.getId())) {
                throw new DataFormatException(
                        "Дубликат id заказа: " + order.getId()
                );
            }
        }

        // --- Проверка 2: дубликаты id ячеек ---
        // Аналогично: Set для уникальных id ячеек.
        Set<String> cellIds = new HashSet<>();
        // Одновременно строим Map: cellId → StorageCell для быстрого поиска ячейки по id.
        // HashMap даёт O(1) доступ — быстрее, чем линейный поиск по списку.
        java.util.Map<String, StorageCell> cellMap = new java.util.HashMap<>();
        for (StorageCell cell : cells) {
            if (!cellIds.add(cell.getId())) {
                throw new DataFormatException(
                        "Дубликат id ячейки: " + cell.getId()
                );
            }
            // Кладём ячейку в Map: cellId → StorageCell.
            cellMap.put(cell.getId(), cell);
        }

        // --- Проверки 3–5 и 7: для каждого заказа ---
        for (Order order : orders) {
            // cellId заказа — может быть null для ISSUED/RETURNED без исторической ячейки,
            // но план говорит, что заказ хранит исторический cellId и после освобождения.
            String cellId = order.getCellId();

            // isActive — true для заказов, которые занимают ячейку.
            // READY_FOR_PICKUP и EXPIRED занимают ячейку (правило 9 из плана).
            boolean isActive = order.getStatus() == OrderStatus.READY_FOR_PICKUP
                    || order.getStatus() == OrderStatus.EXPIRED;

            if (isActive) {
                // Проверка 3: активный заказ должен иметь cellId.
                if (cellId == null) {
                    throw new DataFormatException(
                            "Активный заказ " + order.getId() + " не имеет cellId"
                    );
                }

                // Проверка 4: ячейка с таким cellId должна существовать.
                StorageCell cell = cellMap.get(cellId);
                if (cell == null) {
                    throw new DataFormatException(
                            "Заказ " + order.getId() + " ссылается на несуществующую ячейку " + cellId
                    );
                }

                // Проверка 4 (продолжение): ячейка должна быть занята именно этим заказом.
                if (cell.isFree() || !cell.getOrderId().equals(order.getId())) {
                    throw new DataFormatException(
                            "Ячейка " + cellId + " не занята заказом " + order.getId()
                    );
                }

                // Проверка 5: размер ячейки должен вмещать требуемый размер заказа.
                if (!cell.getSize().fits(order.getRequiredSize())) {
                    throw new DataFormatException(
                            "Ячейка " + cellId + " (" + cell.getSize() + ") слишком мала для заказа "
                                    + order.getId() + " (требуется " + order.getRequiredSize() + ")"
                    );
                }
            } else {
                // Проверка 7: для ISSUED/RETURNED — исторический cellId должен существовать.
                // По правилу 9 заказ хранит исторический cellId и после освобождения.
                if (cellId != null && !cellMap.containsKey(cellId)) {
                    throw new DataFormatException(
                            "Заказ " + order.getId() + " имеет исторический cellId " + cellId
                                    + ", но такая ячейка не существует"
                    );
                }
            }
        }

        // --- Проверка 6: каждая занятая ячейка указывает на активный заказ ---
        // Строим Map: orderId → Order для быстрого поиска заказа по id.
        java.util.Map<String, Order> orderMap = new java.util.HashMap<>();
        for (Order order : orders) {
            orderMap.put(order.getId(), order);
        }

        for (StorageCell cell : cells) {
            // Если ячейка занята (orderId != null) — проверяем, что заказ существует и активен.
            if (!cell.isFree()) {
                String orderId = cell.getOrderId().orElse(null);
                Order order = orderMap.get(orderId);

                // Заказ, занимающий ячейку, должен существовать.
                if (order == null) {
                    throw new DataFormatException(
                            "Ячейка " + cell.getId() + " занята несуществующим заказом " + orderId
                    );
                }

                // Заказ должен быть активным (READY_FOR_PICKUP или EXPIRED).
                boolean orderActive = order.getStatus() == OrderStatus.READY_FOR_PICKUP
                        || order.getStatus() == OrderStatus.EXPIRED;
                if (!orderActive) {
                    throw new DataFormatException(
                            "Ячейка " + cell.getId() + " занята неактивным заказом " + orderId
                                    + " (статус: " + order.getStatus() + ")"
                    );
                }

                // cellId в заказе должен совпадать с id этой ячейки.
                if (!cell.getId().equals(order.getCellId())) {
                    throw new DataFormatException(
                            "Ячейка " + cell.getId() + " указывает на заказ " + orderId
                                    + ", но заказ указывает на ячейку " + order.getCellId()
                    );
                }
            }
        }
    }
}
