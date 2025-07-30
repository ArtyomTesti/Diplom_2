package com.stellarburgers.api.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import com.github.javafaker.Faker;

@Data
@AllArgsConstructor
public class User {
    private String email;
    private String password;
    private String name;

    public static User getRandomUser() {
        Faker faker = new Faker();
        return new User(
                faker.internet().emailAddress(),
                faker.internet().password(),
                faker.name().fullName()
        );
    }
}