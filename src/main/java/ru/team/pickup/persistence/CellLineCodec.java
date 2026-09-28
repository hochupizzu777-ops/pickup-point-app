package ru.team.pickup.persistence;

import ru.team.pickup.exception.DataFormatException;
import ru.team.pickup.model.CellSize;
import ru.team.pickup.model.StorageCell;

import java.util.Base64;

/**
 * CellLineCodec — преобразование ячейки в строку и обратно, У2.
 *
 * Реализует LineCodec<StorageCell>.
 *
 * Формат строки (разделитель «|», 4 токена):
 *   CELL|<base64(id)>|<SIZE>|<base64(orderId) или ->
 *
 * Правила кодирования:
 * — ID и orderId кодируются в Base64 (UTF-8).
 * — Размер (CellSize) передаётся как имя enum (SMALL/MEDIUM/LARGE).
 * — Свободная ячейка: orderId = "-".
 */
public class CellLineCodec implements LineCodec<StorageCell> {

    private static final String PREFIX = "CELL";
    private static final String FREE_MARKER = "-";
    private static final String DELIMITER = "|";

    @Override
    public String encode(StorageCell value) {
        if (value == null) {
            throw new IllegalArgumentException("Нельзя закодировать null объект");
        }

        String encodedId = Base64.getEncoder().encodeToString(value.getId().getBytes());
        String sizeName = value.getSize().name();

        String encodedOrderId;
        if (value.isFree()) {
            encodedOrderId = FREE_MARKER;
        } else {
            String orderId = value.getOrderId().orElseThrow(
                    () -> new IllegalArgumentException("Отсутствует orderId для занятой ячейки")
            );
            encodedOrderId = Base64.getEncoder().encodeToString(orderId.getBytes());
        }

        return PREFIX + DELIMITER + encodedId + DELIMITER + sizeName + DELIMITER + encodedOrderId;
    }


    @Override
    public StorageCell decode(String line) throws DataFormatException {
        if (line == null || line.trim().isEmpty()) {
            throw new DataFormatException("Строка не может быть пустой или null");
        }

        String[] parts = line.split(DELIMITER, -1);

        if (parts.length != 4) {
            throw new DataFormatException(
                    "Неверный формат строки. Ожидалось 4 поля, получено: " + parts.length
            );
        }

        // Проверка префикса
        if (!PREFIX.equals(parts)) {
            throw new DataFormatException("Неверный префикс строки: " + parts);
        }

        String encodedId = parts[1];
        String sizeName = parts[2];
        String encodedOrderId = parts[3];

        // Декодируем ID
        String id;
        try {
            id = new String(Base64.getDecoder().decode(encodedId));
        } catch (IllegalArgumentException e) {
            throw new DataFormatException("Неверный формат ID ячейки: " + encodedId);
        }

        // Восстанавливаем размер
        CellSize size;
        try {
            size = CellSize.valueOf(sizeName.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new DataFormatException("Неизвестный размер ячейки: " + sizeName);
        }

        // Определяем статус занятости и orderId
        boolean isFree;
        String orderId = null;

        if (FREE_MARKER.equals(encodedOrderId)) {
            isFree = true;
        } else {
            isFree = false;
            try {
                orderId = new String(Base64.getDecoder().decode(encodedOrderId));
            } catch (IllegalArgumentException e) {
                throw new DataFormatException("Неверный формат orderId: " + encodedOrderId);
            }
        }

        // ВАЖНО: сигнатура конструктора StorageCell должна быть (String id, CellSize size, String orderId)
        // Если в вашей модели StorageCell хранит isFree как boolean, а не orderId как String,
        // измените конструктор на: new StorageCell(id, size, isFree);
        return new StorageCell(id, size, orderId);
    }
}
