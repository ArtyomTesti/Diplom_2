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

public class AuthTests {
    private final UserClient userClient = new UserClient();
    private String accessToken;
    private User registeredUser;

    @Before
    public void setUp() {
        registeredUser = User.getRandomUser();
        Response registerResponse = userClient.createUser(registeredUser);
        accessToken = registerResponse.path("accessToken");
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Авторизация существующего пользователя - успешно")
    @Description("Проверка успешной авторизации с валидными данными")
    public void loginWithValidCredentialsTest() {
        Response loginResponse = userClient.loginUser(registeredUser);

        loginResponse.then()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("user.email", equalTo(registeredUser.getEmail()))
                .body("user.name", equalTo(registeredUser.getName()))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue());
    }

    @Test
    @DisplayName("Авторизация с неверным email - ошибка")
    @Description("Проверка ошибки при авторизации с неверным email")
    public void loginWithInvalidEmailTest() {
        User user = new User("wrong@example.com", registeredUser.getPassword(), registeredUser.getName());

        Response response = userClient.loginUser(user);

        response.then()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Авторизация с неверным паролем - ошибка")
    @Description("Проверка ошибки при авторизации с неверным паролем")
    public void loginWithInvalidPasswordTest() {
        User user = new User(registeredUser.getEmail(), "wrongpassword", registeredUser.getName());

        Response response = userClient.loginUser(user);

        response.then()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }
}