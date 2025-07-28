package com.stellarburgers.api.models;

import java.util.List;

// Модель заказа
public class Order {
    private List<String> ingredients;

    public Order(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    // Геттеры и сеттеры
    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }
}