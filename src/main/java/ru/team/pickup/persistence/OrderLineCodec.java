package ru.team.pickup.persistence;

import ru.team.pickup.model.Order;

/**
 * OrderLineCodec — преобразование заказа, У1
 *
 * Реализует:
 *
 * LineCodec<Order>
 *
 * Должен сохранять и восстанавливать все поля заказа, включая получателя и все позиции товаров.
 *
 * Требования:
 *
 * Использовать согласованный порядок полей.
 * Даты записывать в ISO-формате.
 * Статусы и размеры — через name().
 * Проверять количество полей и товаров.
 * Некорректные числа, даты и значения enum превращать в DataFormatException.
 * Сохранять первоначальную причину ошибки.
 *
 * Чтобы символ-разделитель внутри названия не ломал запись, текстовые поля кодируем в Base64 от UTF-8. Точный формат приведён в документе.
 */


import ru.team.pickup.exception.DataFormatException;
import ru.team.pickup.model.CellSize;
import ru.team.pickup.model.OrderItem;
import ru.team.pickup.model.OrderStatus;
import ru.team.pickup.model.Recipient;

import java.nio.charset.StandardCharsets;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.StringJoiner;

public class OrderLineCodec implements LineCodec<Order> {

    private static String encodeText(String value){
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);

        return Base64.getEncoder().encodeToString(bytes);
    }
    private static String decoderText(String value){
        byte[] bytes = Base64.getDecoder().decode(value);

        return new String(bytes, StandardCharsets.UTF_8);
    }

    @Override
    public String encode(Order order){
        if(order == null){
            throw new NullPointerException(
                    "Нельзя сохранить пустой заказ"
            );
        }

        StringJoiner result = new StringJoiner("|");

        result.add("ORDER");
        result.add(encodeText(order.getId()));
        result.add(encodeText(order.getRecipient().getName()));
        result.add(encodeText(order.getRecipient().getPhone()));
        result.add(encodeText(order.getPickupCode()));
        result.add(order.getRequiredSize().name());
        result.add(order.getReceivedOn().toString());
        result.add(order.getExpiresOn().toString());
        result.add(order.getStatus().name());
        result.add(encodeText(order.getCellId()));
        result.add(Integer.toString(order.getItems().size()));

        for (OrderItem item : order.getItems()){
            result.add(encodeText(item.getName()));
            result.add(Integer.toString(item.getQuantity()));
        }

        return result.toString();
    }

    @Override
    public Order decode(String encodeString) throws DataFormatException {
        if (encodeString == null || encodeString.isBlank()){
            throw new DataFormatException(
                    "Нельзя раскодировать в объект пустую строку"
            );
        }

        String[] parts = encodeString.split("\\|", -1);

        if (!"ORDER".equals(parts[0])){
            throw new DataFormatException(
                    "Строка должна начинаться с ORDER"
            );
        }

        if (parts.length < 11){
            throw new DataFormatException(
                    "В строке заказа недостаточно полей"
            );
        }

        try{
            int itemCount = Integer.parseInt(parts[10]);

            if (itemCount <= 0){
                throw new DataFormatException(
                        "Заказ должен содержать хотя бы одну позицию товара"
                );
            }

            long expectedFieldCount = 11L + 2L * itemCount;

            if (expectedFieldCount != parts.length){
                throw new DataFormatException(
                        "Количество полей не соответствует числу товаров"
                );
            }

            String id = decoderText(parts[1]);
            Recipient recipient = new Recipient(
                    decoderText(parts[2]),
                    decoderText(parts[3])
            );
            String pickupCode = decoderText(parts[4]);
            CellSize requiredSize = CellSize.valueOf(parts[5]);
            LocalDate receivedOn = LocalDate.parse(parts[6]);
            LocalDate expiresOn = LocalDate.parse(parts[7]);
            OrderStatus status = OrderStatus.valueOf(parts[8]);
            String cellId = decoderText(parts[9]);

            int index = 11;

            List<OrderItem> items = new ArrayList<>();

            for (int i = 0; i < itemCount; i++){
                String name = decoderText(parts[index]);
                int quantity = Integer.parseInt(parts[index+1]);

                items.add(new OrderItem(name, quantity));

                index += 2;
            }

            return new Order(
                    id,
                    recipient,
                    items,
                    requiredSize,
                    pickupCode,
                    receivedOn,
                    expiresOn,
                    status,
                    cellId
            );

        }catch(IllegalArgumentException | DateTimeException exception){
            throw new DataFormatException(
                    "Не удалось восстановить заказ: некорректные данные",
                    exception
            );
        }
    }
}
