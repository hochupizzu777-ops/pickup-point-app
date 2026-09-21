package ru.team.pickup.repository;

/**
 * Repository<T, ID> — общий интерфейс хранилища, У1
 *
 * public interface Repository<T, ID> {
 *
 *     Optional<T> findById(ID id);
 *
 *     List<T> findAll();
 *
 *     void save(T entity);
 * }
 *
 * Контракт:
 *
 * findById() возвращает объект или Optional.empty().
 * findAll() возвращает копию списка объектов.
 * save() добавляет объект либо заменяет существующий с тем же номером.
 * null передавать нельзя.
 *
 * Сам интерфейс не хранит данные.
 */
public interface Repository<T, ID> {
    // TODO: добавить реализацию в задаче ответственного участника.
}
