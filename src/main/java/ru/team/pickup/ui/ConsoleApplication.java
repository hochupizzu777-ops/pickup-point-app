package ru.team.pickup.ui;

import ru.team.pickup.background.ExpirationScheduler;
import ru.team.pickup.model.AppSnapshot;
import ru.team.pickup.persistence.StateStore;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * ConsoleApplication — главное меню приложения, У2.
 *
 * Соединяет подменю (OrderMenu, WarehouseMenu), сохранение через StateStore
 * и корректное завершение с остановкой фонового планировщика.
 *
 * Требования из плана:
 * — Переключать подменю (Заказы / Склад).
 * — Перед очередным запросом главного меню показывать накопленные уведомления
 *   из ConcurrentLinkedQueue (фоновый поток не печатает поверх ввода).
 * — Для сохранения передавать снимок в StateStore.
 * — При выходе: остановить фон, дождаться завершения задачи, сохранить финальный снимок.
 * — Сообщать об ошибках записи; не печатать «сохранено» после неудачи.
 * — Корректно обработать завершение ввода (Ctrl+D / конец потока).
 */
public class ConsoleApplication {

    // Поток вывода — обычно System.out, передаётся для тестов.
    private final PrintStream out;

    // Единый ввод (файл Ильи, У1).
    private final ConsoleInput input;

    // Меню заказов (файл Ильи, У1).
    private final OrderMenu orderMenu;

    // Меню склада и отчётов (твой файл, У2).
    private final WarehouseMenu warehouseMenu;

    // Центральный сервис — даёт снимок состояния.
    private final PickupPointService service;

    // Хранилище файла состояния — сохраняет и загружает снимок.
    private final StateStore stateStore;

    // Фоновый планировщик проверки просрочки.
    private final ExpirationScheduler scheduler;

    // Очередь уведомлений от фонового потока к меню.
    // Фоновый поток складывает сюда сообщения (через service),
    // меню забирает их через poll перед следующим запросом ввода.
    private final ConcurrentLinkedQueue<String> notifications;

    /**
     * Конструктор.
     *
     * @param out          поток вывода
     * @param input        единый ввод
     * @param orderMenu    меню заказов
     * @param warehouseMenu меню склада
     * @param service      центральный сервис
     * @param stateStore   хранилище файла состояния
     * @param scheduler    фоновый планировщик
     * @param notifications очередь уведомлений от фонового потока
     */
    public ConsoleApplication(PrintStream out,
                              ConsoleInput input,
                              OrderMenu orderMenu,
                              WarehouseMenu warehouseMenu,
                              PickupPointService service,
                              StateStore stateStore,
                              ExpirationScheduler scheduler,
                              ConcurrentLinkedQueue<String> notifications) {
        this.out = out;
        this.input = input;
        this.orderMenu = orderMenu;
        this.warehouseMenu = warehouseMenu;
        this.service = service;
        this.stateStore = stateStore;
        this.scheduler = scheduler;
        this.notifications = notifications;
    }

    /**
     * Главный цикл приложения.
     *
     * Показывает меню, переключает подменю, сохраняет по команде,
     * перед каждым запросом ввода выводит накопленные уведомления.
     *
     * @throws IOException          если финальное сохранение не удалось
     * @throws InterruptedException если ожидание остановки фона прервано
     */
    public void run() throws IOException, InterruptedException {
        boolean running = true;

        while (running) {
            // Перед показом меню — вывести накопленные уведомления.
            // Фоновый поток не печатает поверх вводимой строки,
            // сообщения аккумулируются в очереди и выводятся здесь.
            drainNotifications();

            out.println();
            out.println("=== Пункт выдачи ===");
            out.println("1. Заказы");
            out.println("2. Склад");
            out.println("3. Сохранить");
            out.println("0. Сохранить и выйти");

            int choice;
            try {
                choice = input.readInt("Выберите действие: ");
            } catch (RuntimeException e) {
                // Конец ввода (Ctrl+D) или ввод закрыт — выходим корректно.
                // Не выбрасываем исключение, а переходим к штатному завершению.
                out.println();
                out.println("Ввод завершён. Выполняю сохранение и выход...");
                choice = 0;
            }

            switch (choice) {
                case 1 -> orderMenu.show();
                case 2 -> warehouseMenu.show();
                case 3 -> saveState(false);
                case 0 -> {
                    // Сохранить и выйти.
                    saveState(true);
                    // Останавливаем фоновый планировщик.
                    // stop() отменяет задачу и ждёт завершения до 60 секунд.
                    scheduler.stop();
                    running = false;
                }
                default -> out.println("Неверный выбор. Попробуйте снова.");
            }
        }
    }

    /**
     * Выводит накопленные уведомления из очереди.
     *
     * Берёт все сообщения через poll (не блокирует, возвращает null если пусто).
     * Фоновый поток складывает сюда уведомления о просрочке заказов.
     * Печать здесь, в главном потоке, гарантирует, что текст
     * не накладывается на вводимую пользователем строку.
     */
    private void drainNotifications() {
        if (notifications.isEmpty()) {
            return;
        }

        out.println();
        out.println("--- Уведомления ---");

        String message;
        while ((message = notifications.poll()) != null) {
            out.println("  " + message);
        }

        out.println("------------------");
    }

    /**
     * Сохраняет текущий снимок состояния в файл.
     *
     * Берёт снимок через service.snapshot() — неизменяемую копию
     * под монитором сервиса. Сама запись происходит вне монитора.
     *
     * @param isExit true, если это сохранение перед выходом.
     *               Используется для уточнения сообщения.
     */
    private void saveState(boolean isExit) {
        try {
            // Получаем неизменяемый снимок текущего состояния.
            // snapshot() синхронизирован внутри сервиса — безопасно.
            AppSnapshot snapshot = service.snapshot();

            // Сохраняем через StateStore (TextStateStore).
            // save() вызывает validate() внутри, так что файл
            // всегда содержит корректные данные.
            stateStore.save(snapshot);

            out.println("Состояние сохранено.");
        } catch (IOException e) {
            // Ошибка записи — сообщаем, но не печатаем «сохранено».
            out.println("Ошибка сохранения: " + e.getMessage());

            // При выходе с ошибкой — всё равно завершаем,
            // но пользователь видит, что файл не записан.
            if (isExit) {
                out.println("Файл не сохранён. Данные в памяти потеряются при выходе.");
            }
        }
    }
}
