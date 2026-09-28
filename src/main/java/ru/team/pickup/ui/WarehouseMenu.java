package ru.team.pickup.ui;

import ru.team.pickup.model.AppSnapshot;
import ru.team.pickup.model.Order;
import ru.team.pickup.model.OrderStatus;
import ru.team.pickup.model.StorageCell;
import ru.team.pickup.service.PickupPointService;
import ru.team.pickup.service.ReportService;

import java.io.PrintStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * WarehouseMenu — меню склада, отчётов и просрочки, У2.
 *
 * Получает ConsoleInput, PrintStream, PickupPointService, ReportService.
 * Данные берёт через service.snapshot() — неизменяемую копию состояния.
 * Меню само форматирует результат для консоли.
 * Ошибки ловятся в ConsoleApplication, здесь только логика раздела.
 */
public class WarehouseMenu {

    // Поток вывода — обычно System.out, передаётся для тестов.
    private final PrintStream out;

    // Единый ввод строк, чисел и enum (файл Ильи, У1).
    private final ConsoleInput input;

    // Центральный сервис — даёт снимок и ручную просрочку.
    private final PickupPointService service;

    // Сервис отчётов — считает статусы и занятость.
    private final ReportService reportService;

    /**
     * Конструктор.
     *
     * @param out           поток вывода
     * @param input         единый ввод
     * @param service       центральный сервис
     * @param reportService сервис отчётов
     */
    public WarehouseMenu(PrintStream out,
                         ConsoleInput input,
                         PickupPointService service,
                         ReportService reportService) {
        this.out = out;
        this.input = input;
        this.service = service;
        this.reportService = reportService;
    }

    /**
     * Показывает меню склада и обрабатывает выбор пользователя.
     * Цикл работает, пока пользователь не выберет «Назад».
     */
    public void show() {
        while (true) {
            out.println();
            out.println("=== Склад и отчёты ===");
            out.println("1. Показать все ячейки");
            out.println("2. Показать свободные ячейки");
            out.println("3. Показать просроченные заказы");
            out.println("4. Вывести статистику");
            out.println("5. Запустить проверку просрочки вручную");
            out.println("0. Назад");

            int choice = input.readInt("Выберите действие: ");

            switch (choice) {
                case 1 -> showAllCells();
                case 2 -> showFreeCells();
                case 3 -> showExpiredOrders();
                case 4 -> showStatistics();
                case 5 -> runExpirationCheck();
                case 0 -> {
                    return;
                }
                default -> out.println("Неверный выбор. Попробуйте снова.");
            }
        }
    }

    /**
     * Показывает все ячейки: номер, размер, статус (свободна/занята).
     * Берёт снимок через service.snapshot() — неизменяемую копию.
     */
    private void showAllCells() {
        AppSnapshot snapshot = service.snapshot();
        List<StorageCell> cells = snapshot.getCells();

        out.println();
        out.println("--- Все ячейки склада ---");

        if (cells.isEmpty()) {
            out.println("Ячейки не настроены.");
            return;
        }

        // Заголовок таблицы.
        out.printf("%-8s %-8s %-20s%n", "Номер", "Размер", "Статус");
        out.println("-".repeat(38));

        for (StorageCell cell : cells) {
            String status;
            if (cell.isFree()) {
                status = "свободна";
            } else {
                // getOrderId() возвращает Optional — показываем, кем занята.
                status = "занята: " + cell.getOrderId().orElse("?");
            }
            out.printf("%-8s %-8s %-20s%n",
                    cell.getId(),
                    cell.getSize().name(),
                    status);
        }

        out.println("Всего ячеек: " + cells.size());
    }

    /**
     * Показывает только свободные ячейки.
     * Фильтрует snapshot.getCells() по isFree().
     */
    private void showFreeCells() {
        AppSnapshot snapshot = service.snapshot();
        List<StorageCell> cells = snapshot.getCells();

        // Фильтруем только свободные.
        List<StorageCell> freeCells = cells.stream()
                .filter(StorageCell::isFree)
                .toList();

        out.println();
        out.println("--- Свободные ячейки ---");

        if (freeCells.isEmpty()) {
            out.println("Свободных ячеек нет.");
            return;
        }

        out.printf("%-8s %-8s%n", "Номер", "Размер");
        out.println("-".repeat(18));

        for (StorageCell cell : freeCells) {
            out.printf("%-8s %-8s%n", cell.getId(), cell.getSize().name());
        }

        out.println("Свободных ячеек: " + freeCells.size());
    }

    /**
     * Показывает просроченные заказы (статус EXPIRED).
     * Берёт заказы из снимка и фильтрует по статусу.
     */
    private void showExpiredOrders() {
        AppSnapshot snapshot = service.snapshot();
        List<Order> orders = snapshot.getOrders();

        // Фильтруем только просроченные.
        List<Order> expired = orders.stream()
                .filter(o -> o.getStatus() == OrderStatus.EXPIRED)
                .toList();

        out.println();
        out.println("--- Просроченные заказы ---");

        if (expired.isEmpty()) {
            out.println("Просроченных заказов нет.");
            return;
        }

        out.printf("%-12s %-20s %-12s %-12s%n", "Номер", "Получатель", "Срок", "Ячейка");
        out.println("-".repeat(58));

        for (Order order : expired) {
            out.printf("%-12s %-20s %-12s %-12s%n",
                    order.getId(),
                    order.getRecipient().getName(),
                    order.getExpiresOn(),
                    order.getCellId() != null ? order.getCellId() : "-"
            );
        }

        out.println("Просроченных заказов: " + expired.size());
    }

    /**
     * Показывает статистику: заказы по статусам и занятость ячеек.
     * Использует ReportService для подсчёта.
     */
    private void showStatistics() {
        AppSnapshot snapshot = service.snapshot();

        out.println();
        out.println("--- Статистика ---");

        // --- Заказы по статусам ---
        Map<OrderStatus, Long> counts = reportService.countByStatus(snapshot);

        out.println("Заказы по статусам:");
        for (OrderStatus status : OrderStatus.values()) {
            long count = counts.getOrDefault(status, 0L);
            out.printf("  %-20s %d%n", status.name(), count);
        }

        long totalOrders = counts.values().stream().mapToLong(Long::longValue).sum();
        out.println("  " + "-".repeat(25));
        out.printf("  %-20s %d%n", "Итого заказов:", totalOrders);

        // --- Занятость ячеек ---
        out.println();
        long occupied = reportService.countOccupiedCells(snapshot);
        int totalCells = snapshot.getCells().size();
        long free = totalCells - occupied;

        out.println("Ячейки:");
        out.printf("  Всего:     %d%n", totalCells);
        out.printf("  Занято:     %d%n", occupied);
        out.printf("  Свободно:   %d%n", free);

        if (totalCells > 0) {
            double percent = (double) occupied / totalCells * 100;
            out.printf("  Заполнено:  %.1f%%%n", percent);
        }
    }

    /**
     * Запускает ручную проверку просроченных заказов.
     * Вызывает service.markExpiredOrders(), который возвращает
     * количество заказов, переведённых из READY_FOR_PICKUP в EXPIRED.
     *
     * Это резервный механизм на случай, если фоновый планировщик
     * ещё не запущен или отключён.
     */
    private void runExpirationCheck() {
        out.println();
        out.println("Проверка просроченных заказов...");

        int expiredCount = service.markExpiredOrders();

        if (expiredCount == 0) {
            out.println("Просроченных заказов не найдено.");
        } else {
            out.println("Переведено в статус EXPIRED: " + expiredCount);
        }
    }
}
