package ru.team.pickup.model;

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

public enum OrderStatus {

    READY_FOR_PICKUP,
    ISSUED,
    EXPIRED,
    RETURNED
}
