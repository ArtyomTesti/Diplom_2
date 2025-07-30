import com.stellarburgers.api.clients.UserClient;
import com.stellarburgers.api.models.User;
import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.Description;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.*;

public class RegistrationTests {
    private final UserClient userClient = new UserClient();
    private String accessToken;
    private User testUser;

    @Before
    public void setUp() {
        testUser = User.getRandomUser();
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Регистрация нового пользователя - успешно")
    @Description("Проверка успешной регистрации с валидными данными")
    public void registerNewUserTest() {
        Response response = userClient.createUser(testUser);
        accessToken = response.path("accessToken");

        response.then()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("user.email", equalTo(testUser.getEmail()))
                .body("user.name", equalTo(testUser.getName()))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue());
    }

    @Test
    @DisplayName("Регистрация существующего пользователя - ошибка")
    @Description("Проверка ошибки при повторной регистрации")
    public void registerDuplicateUserTest() {
        Response firstResponse = userClient.createUser(testUser);
        accessToken = firstResponse.path("accessToken");

        Response secondResponse = userClient.createUser(testUser);

        secondResponse.then()
                .statusCode(SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"))
                .body("$", not(hasKey("user"))); // Проверяем отсутствие данных пользователя
    }

    @Test
    @DisplayName("Регистрация без пароля - ошибка")
    @Description("Проверка валидации обязательных полей")
    public void registerWithoutPasswordTest() {
        User user = new User("test@example.com", "", "Test User");

        Response response = userClient.createUser(user);

        response.then()
                .statusCode(SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Регистрация без email - ошибка")
    public void registerWithoutEmailTest() {
        User user = new User("", "password", "Test User");

        Response response = userClient.createUser(user);

        response.then()
                .statusCode(SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Регистрация без имени - ошибка")
    public void registerWithoutNameTest() {
        User user = new User("test@example.com", "password", "");

        Response response = userClient.createUser(user);

        response.then()
                .statusCode(SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }
}