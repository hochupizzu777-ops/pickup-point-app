package ru.team.pickup.persistence;

/**
 * LineCodec<T> — преобразование строки, У1
 *
 * public interface LineCodec<T> {
 *
 *     String encode(T value);
 *
 *     T decode(String line) throws DataFormatException;
 * }
 * encode() превращает объект в одну строку.
 * decode() восстанавливает объект.
 * Повреждённая строка вызывает DataFormatException.
 *
 * Файл здесь не открывается.
 */
public interface LineCodec<T> {
    // TODO: добавить реализацию в задаче ответственного участника.
}
