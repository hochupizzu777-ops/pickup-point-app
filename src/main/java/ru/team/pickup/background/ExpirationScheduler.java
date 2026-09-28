package ru.team.pickup.background;

import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * Планировщик периодических задач с контролем одновременного выполнения,
 * отправкой ошибок в очередь и корректным завершением.
 */
public class ExpirationScheduler {

    // Исполнитель для периодических задач: 1 поток гарантирует отсутствие параллельных запусков
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    // Хранит запланированную задачу, чтобы можно было её отменить при остановке
    private volatile ScheduledFuture<?> scheduledTask;

    // Очередь для передачи ошибок, куда планировщик будет складывать исключения
    private final BlockingQueue<Throwable> errorQueue;

    // Флаг, чтобы не запускать вторую задачу, если первая ещё выполняется
    private volatile boolean isRunning = false;

    /**
     * Конструктор принимает очередь для ошибок.
     * @param errorQueue очередь, куда будут складываться исключения из задачи
     */
    public ExpirationScheduler(BlockingQueue<Throwable> errorQueue) {
        this.errorQueue = errorQueue;
    }

    /**
     * Запускает периодическую задачу с заданным интервалом.
     * Использует scheduleWithFixedDelay, чтобы интервал отсчитывался от конца предыдущего запуска.
     * Это предотвращает наложение задач друг на друга и реализует требование «между выполнениями выдерживать интервал».
     *
     * @param task задача, которую нужно выполнять периодически
     * @param initialDelay начальная задержка перед первым запуском
     * @param period интервал между завершением одной задачи и началом следующей
     * @param unit единицы измерения времени
     */
    public void start(Runnable task, long initialDelay, long period, TimeUnit unit) {
        // Если уже запущено — ничего не делаем, чтобы не допустить повторного запуска второго фонового процесса
        if (scheduledTask != null && !scheduledTask.isCancelled()) {
            return;
        }

        // Оборачиваем задачу в логику контроля одновременного выполнения и обработки ошибок
        Runnable wrappedTask = () -> {
            // Если флаг уже установлен, значит, задача уже выполняется — не запускаем повторно
            if (isRunning) {
                return;
            }
            // Устанавливаем флаг, чтобы не допустить параллельного запуска
            isRunning = true;
            try {
                // Выполняем переданную задачу
                task.run();
            } catch (Throwable e) {
                // При ошибке задачи — отправляем исключение в очередь, как требуется
                // offer не блокирует и не выбрасывает исключение, если очередь полна
                errorQueue.offer(e);
            } finally {
                // Сбрасываем флаг после завершения задачи (успешного или с ошибкой)
                isRunning = false;
            }
        };

        // scheduleWithFixedDelay: интервал считается от конца предыдущего выполнения до начала следующего
        // Это гарантирует, что между запусками выдерживается заданный интервал
        scheduledTask = executor.scheduleWithFixedDelay(
                wrappedTask,
                initialDelay,
                period,
                unit
        );
    }

    /**
     * Останавливает планировщик.
     * Завершает исполнителя и ожидает окончания текущей работы.
     */
    public void stop() {
        // Отменяем запланированную задачу, если она есть
        if (scheduledTask != null) {
            scheduledTask.cancel(false); // false: не прерывать текущую задачу, если она выполняется
        }

        // shutdown: не принимает новые задачи, но дожидается завершения уже запущенных
        executor.shutdown();

        try {
            // awaitTermination: ждём до 60 секунд, пока исполнитель завершит текущую задачу
            // Если за это время задача не завершится, shutdownNow попытается её прервать
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            // Если поток был прерван во время ожидания — принудительно останавливаем исполнитель
            executor.shutdownNow();
            // Восстанавливаем флаг прерывания, чтобы вызывающий код мог обработать его при необходимости
            Thread.currentThread().interrupt();
        }
    }
}
