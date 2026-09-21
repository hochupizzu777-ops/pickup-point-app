package ru.team.pickup.model;

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
