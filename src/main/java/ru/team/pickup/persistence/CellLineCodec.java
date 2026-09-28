//package ru.team.pickup.persistence;
//
//import ru.team.pickup.exception.DataFormatException;
//import ru.team.pickup.model.CellSize;
//import ru.team.pickup.model.StorageCell;
//
//import java.util.Base64;
//
///**
// * CellLineCodec — преобразование ячейки в строку и обратно, У2.
// *
// * Реализует LineCodec<StorageCell>.
// *
// * Формат строки (разделитель «|», 4 токена):
// *   CELL|<base64(id)>|<SIZE>|<base64(orderId) или ->
// *
// * Правила кодирования (из плана):
// * — Произвольные строковые поля (id, orderId) кодируются Base64 от UTF-8.
// * — Имена enum (SMALL, MEDIUM, LARGE) не кодируются.
// * — Для свободной ячейки orderId обозначается символом «-».
// * — Base64 не содержит «|», поэтому разделитель безопасен.
// *
// * При чтении проверяется структура строки, восстанавливается
// * свободная либо занятая ячейка. Повреждённая строка — DataFormatException.
// */
//public class CellLineCodec implements LineCodec<StorageCell> {
//
//    @Override
//    public String encode(StorageCell value) {
//        if (value == null) {
//            throw new IllegalArgumentException("Нельзя закодировать null объект");
//        }
//        // Формат: ID,SIZE,IS_FREE
//        return value.getId() + "," + value.getSize().name() + "," + value.isFree();
//    }
//
//    @Override
//    public StorageCell decode(String line) throws DataFormatException {
//        if (line == null || line.trim().isEmpty()) {
//            throw new DataFormatException("Строка не может быть пустой или null");
//        }
//
//        String[] parts = line.split(",");
//
//        if (parts.length != 3) {
//            throw new DataFormatException(
//                    "Неверный формат строки. Ожидалось 3 поля (ID,Size,IsFree), получено: " + parts.length
//            );
//        }
//
//        String id = parts[0];
//        String sizeName = parts[1];
//        String isFreeStr = parts[2];
//
//
//        CellSize size;
//        try {
//            size = CellSize.valueOf(sizeName.toUpperCase());
//        } catch (IllegalArgumentException e) {
//            throw new DataFormatException("Неизвестный размер ячейки: " + sizeName);
//        }
//
//        boolean isFree;
//        try {
//            isFree = Boolean.parseBoolean(isFreeStr);
//        } catch (Exception e) {
//            throw new DataFormatException("Неверный формат статуса занятости: " + isFreeStr);
//        }
//
//        // ВАЖНО: убедись, что конструктор StorageCell принимает (String, CellSize, boolean)
//        return new StorageCell(id, size, isFree);
//    }
//}
