package com.example.orderfood.model;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Order implements Serializable, Cloneable{

    private long id;
    private String userId;
    private String userName;
    private String tableCode;
    private long amount;

    private List<Food> foodList;
    private String dateTime;

    @NonNull
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();

    }
    public Order() {
    }

    public Order(long id, String userId, String userName, String tableCode, long amount, List<Food> foodList, String dateTime) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.tableCode = tableCode;
        this.amount = amount;
        this.foodList = foodList;
        this.dateTime = dateTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public String getTableCode() {
        return tableCode;
    }

    public void setTableCode(String tableCode) {
        this.tableCode = tableCode;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }


    public List<Food> getFoodList() {
        return foodList;
    }

    public void setFoodList(List<Food> foodList) {
        this.foodList = foodList;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}
