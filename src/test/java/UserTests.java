import com.stellarburgers.api.clients.UserClient;
import com.stellarburgers.api.models.User;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Test;

import static org.hamcrest.Matchers.*;

// Тесты для API пользователя
public class UserTests {
    private final UserClient userClient = new UserClient();
    private String accessToken;

    @After
    public void tearDown() {
        // Удаление пользователя после теста
        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Создание уникального пользователя - успешно")
    public void createUniqueUserSuccess() {
        User user = User.getRandomUser();

        Response response = userClient.createUser(user);
        accessToken = response.path("accessToken");

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(user.getEmail()))
                .body("user.name", equalTo(user.getName()))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue());
    }

    @Test
    @DisplayName("Создание уже зарегистрированного пользователя - ошибка")
    public void createDuplicateUserError() {
        User user = User.getRandomUser();

        // Первая регистрация - должна быть успешной
        Response firstResponse = userClient.createUser(user);
        accessToken = firstResponse.path("accessToken");

        // Вторая регистрация с теми же данными - должна вернуть ошибку
        Response secondResponse = userClient.createUser(user);

        secondResponse.then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Создание пользователя без обязательного поля - ошибка")
    public void createUserWithoutRequiredFieldError() {
        User user = new User("test@example.com", "", "Test User");

        Response response = userClient.createUser(user);

        response.then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Авторизация существующего пользователя - успешно")
    public void loginWithExistingUserSuccess() {
        User user = User.getRandomUser();

        // Сначала регистрируем пользователя
        Response registerResponse = userClient.createUser(user);
        accessToken = registerResponse.path("accessToken");

        // Авторизуемся с теми же учетными данными
        Response loginResponse = userClient.loginUser(user);

        loginResponse.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(user.getEmail()))
                .body("user.name", equalTo(user.getName()))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue());
    }

    @Test
    @DisplayName("Авторизация с неверными учетными данными - ошибка")
    public void loginWithInvalidCredentialsError() {
        User user = new User("nonexistent@example.com", "wrongpassword", "Test User");

        Response response = userClient.loginUser(user);

        response.then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }
}