package com.example.orderfood.model;

import java.io.Serializable;

public class SnapShot implements Serializable {
    public String key;
    public Table value;

    public SnapShot(String key, Table value) {
        this.key = key;
        this.value = value;
    }

    public SnapShot() {
    }
}
