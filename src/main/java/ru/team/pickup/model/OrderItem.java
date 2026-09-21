package ru.team.pickup.model;

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
