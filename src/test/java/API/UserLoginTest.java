package API;

import API.FirstStepApi.BaseStep;
import API.FirstStepApi.UserAPI;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.Matchers.*;

public class UserLoginTest extends BaseStep {
    private String email;
    private String userToken;

    @BeforeEach
    void setUp() {
        email = generateRandomEmail();
        userToken = UserAPI.createUser(email, "qwerty123", "testUser");
    }

    @AfterEach
    void tearDown() {
        if (userToken != null) {
            UserAPI.deleteUser(userToken);
        }
    }

    @Test
    @DisplayName("Успешный логин существующего пользователя")
    public void successfulLoginTest() {
        Response response = UserAPI.loginUser(email, "qwerty123");

        response.then()
                .statusCode(SC_OK)  // 200
                .body("success", equalTo(true))
                .body("accessToken", notNullValue());
    } //логин под существующим пользователем,

    @ParameterizedTest
    @DisplayName("Логин с неверными данными")
    @MethodSource("invalidLoginData")
    public void loginWithInvalidCredentialsReturnsError(String email, String password) {
        Response response = given()
                .contentType("application/json")
                .body("{\"email\":\"" + email + "\", \"password\":\"" + password + "\"}")
                .post("/api/auth/login");

        response.then()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", containsString("email or password"));
    }

    static Stream<Arguments> invalidLoginData() {
        return Stream.of(
                Arguments.of("user1@test.com", "WRONG_PASSWORD"),
                Arguments.of("fake@fake.com", "qwerty123")
        );
    }

}
