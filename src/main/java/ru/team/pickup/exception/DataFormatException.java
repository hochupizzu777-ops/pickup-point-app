package ru.team.pickup.exception;

import java.io.IOException;

/**
 * DataFormatException — повреждённые данные, У2
 *
 * Наследуется от IOException.
 *
 * Используется для:
 *
 * некорректных строк;
 * неизвестной версии формата;
 * неверных счётчиков;
 * повреждённых связей между заказами и ячейками.
 *
 * Конструкторы принимают сообщение и, при необходимости, причину.
 *
 * Это наш класс ru.team.pickup.exception.DataFormatException, не одноимённый класс из java.util.zip.
 */
public class DataFormatException extends IOException {
    // TODO: добавить реализацию в задаче ответственного участника.
}
