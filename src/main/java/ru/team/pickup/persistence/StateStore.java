package ru.team.pickup.persistence;

/**
 * StateStore — интерфейс сохранения, У2
 *
 * public interface StateStore {
 *
 *     void save(AppSnapshot snapshot) throws IOException;
 *
 *     Optional<AppSnapshot> load() throws IOException;
 * }
 *
 * Контракт:
 *
 * save() сохраняет весь снимок.
 * load() загружает его.
 * Если файла нет — Optional.empty().
 * Если файл существует, но повреждён, — исключение.
 *
 * Повреждённый файл нельзя считать отсутствующим.
 */
public interface StateStore {
    // TODO: добавить реализацию в задаче ответственного участника.
}
