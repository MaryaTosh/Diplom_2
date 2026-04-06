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
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.*;

public class UserUpdateTest extends BaseStep {
    private String testEmail;
    private String userToken;

    @BeforeEach
    void setUp() {
        testEmail = generateRandomEmail();

        UserAPI.createUser(testEmail, "qwerty123", "testUser");
        Response loginResponse = UserAPI.loginUser(testEmail, "qwerty123");
        loginResponse.then().statusCode(SC_OK);

        userToken = loginResponse.path("accessToken");
    }

    @AfterEach
    void tearDown() {
        if (userToken != null) {
            given()
                    .header("Authorization", userToken.startsWith("Bearer ") ? userToken : "Bearer " + userToken)
                    .delete("/api/auth/user")
                    .then()
                    .statusCode(202);
        }
    }

    @ParameterizedTest(name = "Обновление пользователя: {0}")
    @MethodSource("provideUpdateUserBodies")
    @DisplayName("Изменение данных пользователя с авторизацией возвращает OK для разных полей")
    public void updateUserWithAuthReturnsOK_MultipleFields(String testCaseName, String body) {
        given()
                .header("Authorization", userToken)
                .contentType("application/json")
                .body(body)
                .patch("/api/auth/user")
                .then()
                .statusCode(SC_OK)
                .body("success", equalTo(true));
    }

    private static Stream<Arguments> provideUpdateUserBodies() {
        return Stream.of(
                Arguments.of(
                        "обновление name",
                        "{\"name\":\"newSuperUser\"}"
                ),
                Arguments.of(
                        "обновление email",
                        "{\"email\":\"" + generateRandomEmail() + "\"}"
                ),
                Arguments.of(
                        "обновление name и email",
                        "{\"name\":\"newSuperUser\",\"email\":\"" + generateRandomEmail() + "\"}"
                )
        );
    }


    @Test
    @DisplayName("Изменение данных пользователя без авторизации возвращает 401")
    public void updateUserWithoutAuthReturns401WithError() {
        given()
                .contentType("application/json")
                .body("{\"name\":\"hacker\",\"email\":\"hacker@hack.com\"}")
                .patch("/api/auth/user")
                .then()
                .statusCode(SC_UNAUTHORIZED)
                .body("message", equalTo("You should be authorised"));
    }
}
