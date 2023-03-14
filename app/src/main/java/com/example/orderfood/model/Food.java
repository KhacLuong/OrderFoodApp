package com.example.orderfood.model;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.List;

@Entity(tableName = "food")
public class Food implements Serializable, Cloneable {
    @PrimaryKey
    private int id;
    private String name;
    private String image;
    private String banner;
    private String description;
    private int price;
    private int sale;
    private int count;
    private int totalPrice;
    private boolean popular;
    private boolean setBanner;
    private String type;
    private String kind;
    @Ignore
    private List<Image> images;

    public Food() {
    }

    @NonNull
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();

    }

    public Food(int id, String name, String image, String banner, String description, int price, int sale, int count, int totalPrice, boolean popular, boolean setBanner, String type, String kind, List<Image> images) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.banner = banner;
        this.description = description;
        this.price = price;
        this.sale = sale;
        this.count = count;
        this.totalPrice = totalPrice;
        this.popular = popular;
        this.setBanner = setBanner;
        this.type = type;
        this.kind = kind;
        this.images = images;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public boolean isSetBanner() {
        return setBanner;
    }

    public void setSetBanner(boolean setBanner) {
        this.setBanner = setBanner;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getSale() {
        return sale;
    }

    public void setSale(int sale) {
        this.sale = sale;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public boolean isPopular() {
        return popular;
    }

    public void setPopular(boolean popular) {
        this.popular = popular;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public int getRealPrice() {
        if (sale <= 0) {
            return price;
        }
        return price - (price * sale / 100);
    }

    @Override
    public String toString() {
        return "Food{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", image='" + image + '\'' +
                ", banner='" + banner + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", sale=" + sale +
                ", count=" + count +
                ", totalPrice=" + totalPrice +
                ", popular=" + popular +
                ", setBanner=" + setBanner +
                ", type='" + type + '\'' +
                ", kind='" + kind + '\'' +
                ", images=" + images +
                '}';
    }
}