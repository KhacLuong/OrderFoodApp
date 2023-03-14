package com.example.orderfood.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.orderfood.model.Food;

import java.util.List;

@Dao
public interface FoodDAO {
    @Insert
    void insertFood(Food food);

    @Query("SELECT * FROM food")
    List<Food> getListFoodCart();

    @Query("SELECT * FROM food WHERE id = :id")
    List<Food> getFoodById(int id);

    @Delete
    void deleteFood(Food food);

    @Update
    void updateFood(Food food);

    @Query("Delete from food")
    void deleteAllFood();
}
