package ru.team.pickup.ui;

/**
 * ConsoleInput — общий ввод, У1
 *
 * Получает Scanner и PrintStream.
 *
 * Методы:
 *
 * String readRequiredString(String prompt);
 *
 * int readInt(String prompt, int min, int max);
 *
 * <E extends Enum<E>> E readEnum(
 *         String prompt, Class<E> enumType);
 *
 * Требования:
 *
 * Читать ввод через nextLine().
 * Числа преобразовывать через Integer.parseInt().
 * При ошибке объяснять проблему и повторять запрос.
 * Для enum показывать пронумерованные варианты.
 * При завершении входного потока передавать NoSuchElementException наверх, а не повторять запрос бесконечно.
 *
 * Самостоятельно закрывать общий Scanner этот класс не должен.
 */
public class ConsoleInput {
    // TODO: добавить реализацию в задаче ответственного участника.
}
