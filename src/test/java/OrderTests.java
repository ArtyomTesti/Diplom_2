import com.stellarburgers.api.clients.OrderClient;
import com.stellarburgers.api.clients.UserClient;
import com.stellarburgers.api.models.User;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.apache.http.HttpStatus.*;
import java.util.List;

import static org.hamcrest.Matchers.*;

public class OrderTests {
    private final UserClient userClient = new UserClient();
    private final OrderClient orderClient = new OrderClient();
    private String accessToken;
    private List<String> ingredients;

    @Before
    public void setUp() {
        // Получаем список ингредиентов перед тестами
        Response ingredientsResponse = orderClient.getIngredients();
        ingredients = ingredientsResponse.jsonPath().getList("data._id");
    }

    @After
    public void tearDown() {
        // Удаляем пользователя после тестов
        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Создание заказа с авторизацией - успешно")
    @Description("Проверка успешного создания заказа авторизованным пользователем")
    public void createOrderWithAuthTest() {
        User user = User.getRandomUser();
        Response registerResponse = userClient.createUser(user);
        accessToken = registerResponse.path("accessToken");

        List<String> ingredientsToOrder = ingredients.subList(0, 2);
        Response orderResponse = orderClient.createOrder(ingredientsToOrder, accessToken);

        orderResponse.then()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа без авторизации - ошибка")
    @Description("Проверка невозможности создания заказа без авторизации")
    public void createOrderWithoutAuthTest() {
        List<String> ingredientsToOrder = ingredients.subList(0, 2);
        Response orderResponse = orderClient.createOrder(ingredientsToOrder, "");

        orderResponse.then()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", equalTo(false));
    }

    @Test
    @DisplayName("Создание заказа с ингредиентами - успешно")
    @Description("Проверка успешного создания заказа с несколькими ингредиентами")
    public void createOrderWithMultipleIngredientsTest() {
        User user = User.getRandomUser();
        Response registerResponse = userClient.createUser(user);
        accessToken = registerResponse.path("accessToken");

        List<String> ingredientsToOrder = ingredients.subList(0, 3);
        Response orderResponse = orderClient.createOrder(ingredientsToOrder, accessToken);

        orderResponse.then()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов - ошибка")
    @Description("Проверка невозможности создания заказа без указания ингредиентов")
    public void createOrderWithoutIngredientsTest() {
        User user = User.getRandomUser();
        Response registerResponse = userClient.createUser(user);
        accessToken = registerResponse.path("accessToken");

        Response orderResponse = orderClient.createOrder(List.of(), accessToken);

        orderResponse.then()
                .statusCode(SC_BAD_REQUEST)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиента - ошибка")
    @Description("Проверка обработки ошибки при указании неверного хеша ингредиента")
    public void createOrderWithInvalidIngredientHashTest() {
        User user = User.getRandomUser();
        Response registerResponse = userClient.createUser(user);
        accessToken = registerResponse.path("accessToken");

        Response orderResponse = orderClient.createOrder(List.of("invalid_hash"), accessToken);

        orderResponse.then()
                .statusCode(SC_INTERNAL_SERVER_ERROR)
                .body("success", equalTo(false));
    }
}
