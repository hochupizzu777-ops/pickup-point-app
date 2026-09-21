package ru.team.pickup.model;

//2. OrderItem — позиция заказа, У1
//
//Поля:
//
//String name;
//int quantity;
//
//Что реализовать:
//
//Конструктор с названием и количеством.
//Название должно быть непустым.
//Количество — строго больше нуля.
//        Getters.
//equals() и hashCode() по названию и количеству.
//toString().
//
//Один объект означает, например, «Книга по Java, 2 штуки». Автоматически объединять одинаковые позиции не требуется.

public final class OrderItem {

    private final String name;
    private final int quantity;

    public OrderItem(String name, int quantity){
        if (name == null || name.isBlank()){
            throw new IllegalArgumentException(
                    "Название товара не должно отстустовать"
            );
        }
        if (quantity <= 0){
            throw new IllegalArgumentException(
                    "Колчество товара должно быть больше нуля"
            );
        }

        this.name = name.strip();
        this.quantity = quantity;
    }

    public String getName(){
        return name;
    }

    public int getQuantity(){
        return quantity;
    }

    @Override
    public String toString(){
        return "OrderItem{" +
                "name='" + name + '\'' +
                ", quantity=" + quantity +
                '}';
    }
}
