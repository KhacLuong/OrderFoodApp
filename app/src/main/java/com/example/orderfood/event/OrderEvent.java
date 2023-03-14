package com.example.orderfood.event;

import com.example.orderfood.model.Order;

import java.util.List;

public class OrderEvent {
    private boolean orderList;

    public OrderEvent(boolean orderList) {
        this.orderList = orderList;
    }

    public boolean getOrderList() {
        return orderList;
    }
}
