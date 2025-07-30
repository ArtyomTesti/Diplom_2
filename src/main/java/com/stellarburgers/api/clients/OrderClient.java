package com.stellarburgers.api.clients;

import com.stellarburgers.api.models.Order;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import java.util.List;

import static io.restassured.RestAssured.given;

// Клиент для работы с API заказов
public class OrderClient {
    private static final String BASE_URL = "https://stellarburgers.nomoreparties.site/api";
    private static final String INGREDIENTS_PATH = "/ingredients";
    private static final String ORDERS_PATH = "/orders";

    @Step("Получить список ингредиентов")
    public Response getIngredients() {
        return given()
                .when()
                .get(BASE_URL + INGREDIENTS_PATH);
    }

    @Step("Создать заказ")
    public Response createOrder(List<String> ingredients, String accessToken) {
        Order order = new Order(ingredients);

        if (accessToken == null || accessToken.isEmpty()) {
            return given()
                    .header("Content-type", "application/json")
                    .body(order)
                    .when()
                    .post(BASE_URL + ORDERS_PATH);
        } else {
            return given()
                    .header("Content-type", "application/json")
                    .header("Authorization", accessToken)
                    .body(order)
                    .when()
                    .post(BASE_URL + ORDERS_PATH);
        }
    }
}