package com.stellarburgers.api.clients;

import com.stellarburgers.api.models.User;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

// Клиент для работы с API пользователя
public class UserClient {
    private static final String BASE_URL = "https://stellarburgers.nomoreparties.site/api";
    private static final String USER_PATH = "/auth/user";
    private static final String REGISTER_PATH = "/auth/register";
    private static final String LOGIN_PATH = "/auth/login";
    private static final String LOGOUT_PATH = "/auth/logout";

    @Step("Создать пользователя")
    public Response createUser(User user) {
        return given()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .post(BASE_URL + REGISTER_PATH);
    }

    @Step("Авторизовать пользователя")
    public Response loginUser(User user) {
        return given()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .post(BASE_URL + LOGIN_PATH);
    }

    @Step("Удалить пользователя")
    public void deleteUser(String accessToken) {
        given()
                .header("Authorization", accessToken)
                .when()
                .delete(BASE_URL + USER_PATH);
    }
}