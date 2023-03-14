package com.example.orderfood.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Bill implements Serializable {
    private long id;
    private String tableCode;
    private long amount;
    private String timeStart;
    private String timeEnd;
    private List<Food> foodList;

    public Bill() {
        this.foodList= new ArrayList<>();
    }

    public Bill(long id, String tableCode, long amount, String timeStart, String timeEnd, List<Food> foodList) {
        this.id = id;
        this.tableCode = tableCode;
        this.amount = amount;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.foodList = foodList;
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

    public String getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(String timeStart) {
        this.timeStart = timeStart;
    }

    public String getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(String timeEnd) {
        this.timeEnd = timeEnd;
    }

    public List<Food> getFoodList() {
        return foodList;
    }

    public void setFoodList(List<Food> foodList) {
        this.foodList = foodList;
    }


}
