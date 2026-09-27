package ru.team.pickup.repository;

import ru.team.pickup.model.StorageCell;
import ru.team.pickup.exception.EntityNotFoundException;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * InMemoryCellRepository — хранилище ячеек, У2
 *
 * Реализует:
 *
 * Repository<StorageCell, String>
 *
 * Внутри:
 *
 * Map<String, StorageCell> cells;
 *
 * Работает аналогично хранилищу заказов.
 *
 * Не выбирает подходящую ячейку: только сохраняет и возвращает объекты.
 */
public class InMemoryCellRepository implements Repository<StorageCell, String> {
    //Хранилище ключ для хранения ID ячейки, ConcurrentHashMap безопасность при одновременном доступе из разных потоков
    private final Map<String, StorageCell> cells = new ConcurrentHashMap<>();
    /*
    *сохраняем ячейку в хранилище
    * логика: проверяем что объект и его id не null
    * кладем в MAP по id
     */
    @Override
    public StorageCell save(StorageCell entity) {
        if (entity == null || entity.getId() == null){
            throw new IllegalArgumentException("Cell cannot be null and must have an ID");
        }
        return cells.put(entity.getId(), entity);
    }
    //Ищет ячейку по ID и возвращает Optional.
    @Override
    public Optional<StorageCell> findById(String id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(cells.get(id));
    }
    //Ищет ячейку по ID и возвращает её, либо выбрасывает исключение.
    @Override
    public StorageCell getById(String id) {
        return findById(id)
                .orElseThrow(() -> new EntityNotFoundException("StorageCell with id '" + id + "' not found"));
    }
    //Возвращает коллекцию всех ячеек в хранилище.
    @Override
    public Collection<StorageCell> findAll() {
        return cells.values();
    }
    //Удаляем ячейку из хранилища по ID.
    @Override
    public void delete(String id) {
        if (id == null) {
            return;
        }
        cells.remove(id);
    }
    //Проверяет, существует ли ячейка с данным ID.
    @Override
    public boolean existsById(String id) {
        return id != null && cells.containsKey(id);
    }

}
