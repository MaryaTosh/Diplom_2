package API;

import API.FirstStepApi.BaseStep;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.equalTo;

public class UserCreateTest extends BaseStep {
    private String userToken;

    @AfterEach
    void tearDown() {
        if (userToken != null && !userToken.isBlank()) {
            Response deleteResponse = given()
                    .header("Authorization", "Bearer " + userToken)
                    .when()
                    .delete("/api/auth/user");

            deleteResponse.then().log().all();
        }
    }

    @Test
    @DisplayName("Успешное создание пользователя")
    public void createNewUserTestReturnsOK() {
        String email = generateRandomEmail();

        Response response = given()
                .contentType("application/json")
                .body(Map.of(
                        "email", email,
                        "password", "qwerty123",
                        "name", "user"
                ))
                .when()
                .post("/api/auth/register");

        userToken = response.then()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .extract()
                .path("accessToken");
    }


    @Test
    @DisplayName("Повторное создание пользователя")
    public void createDoubleUsersReturns403() {
        String email = generateRandomEmail();

        given()
                .contentType("application/json")
                .body(Map.of(
                        "email", email,
                        "password", "qwerty123",
                        "name", "user"
                ))
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(SC_OK)
                .body("success", equalTo(true));

        given()
                .contentType("application/json")
                .body(Map.of(
                        "email", email,
                        "password", "qwerty123",
                        "name", "user"
                ))
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(SC_FORBIDDEN);
    }

    @Test
    @DisplayName("Создание пользователя без email")
    public void createUserWithoutEmailReturns403() {
        given()
                .contentType("application/json")
                .body(Map.of(
                        "password", "qwerty123",
                        "name", "user"
                ))
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(SC_FORBIDDEN);
    }
}

