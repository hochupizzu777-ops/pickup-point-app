package ru.team.pickup.model;

import java.util.Objects;

//3. OrderStatus — статус заказа, У1
//
//Значения:
//
//READY_FOR_PICKUP
//        ISSUED
//EXPIRED
//        RETURNED
//Статус	Значение	Занимает ячейку
//READY_FOR_PICKUP	Ожидает выдачи	Да
//ISSUED	Выдан	Нет
//EXPIRED	Истёк срок хранения	Да
//RETURNED	Возвращён отправителю	Нет
//
//Дополнительные методы пока не обязательны. Правила смены статусов проверяет сервис.

public enum CellSize {

    SMALL(1),
    MEDIUM(2),
    LARGE(3);

    private final int level;

    CellSize(int level){
        this.level = level;
    }

    public int getLevel(){
        return level;
    }

    public boolean fits(CellSize requiredSize){
        Objects.requireNonNull(requiredSize, "Размер заказа не должен быть null");

        return this.level >= requiredSize.level;
    }
}
