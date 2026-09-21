package ru.team.pickup.model;

//Recipient — получатель, У1
//
//Поля:
//
//String name;
//String phone;
//
//Что реализовать:
//
//Конструктор с именем и телефоном.
//        Проверку: значения не null, не пустые и не состоят из пробелов.
//Удаление пробелов по краям через strip().
//getName(), getPhone().
//equals() и hashCode() по обоим полям.
//toString().
//
//Телефон хранится строкой. Проверять существование номера или отправлять сообщения не нужно.
//
//Результат: корректный неизменяемый объект получателя.

public final class Recipient {

    private final String name;
    private final String phone;

    public Recipient(String name, String phone) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Имя получателя не может быть пустым"
            );
        }
        if (phone == null || phone.isBlank()) {
            throw new IllegalArgumentException(
                    "Номер получателя не модет быть пустым"
            );
        }

        this.name = name.strip();
        this.phone = phone.strip();
    }

    public String getName(){
        return name;
    }

    public String getPhone(){
        return phone;
    }

    @Override
    public String toString(){
        return "Recipient{" +
                "name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
