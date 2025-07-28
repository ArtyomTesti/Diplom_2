package com.stellarburgers.api.models;

// Модель пользователя
public class User {
    private String email;
    private String password;
    private String name;

    public User(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    // Геттеры и сеттеры
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Генерация случайного пользователя
    public static User getRandomUser() {
        long timestamp = System.currentTimeMillis();
        return new User(
                "testuser" + timestamp + "@example.com",
                "password" + timestamp,
                "Test User " + timestamp
        );
    }
}