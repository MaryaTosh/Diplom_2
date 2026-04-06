package API;

import API.FirstStepApi.BaseStep;
import API.FirstStepApi.UserAPI;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;

public class UserCreateTest extends BaseStep {
    private String userToken;

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
    @Test
    @DisplayName("Успешное создание пользователя")
    public void createNewUserTestReturnsOK() {
        String email = generateRandomEmail();
        given()
                .contentType("application/json")
                .body("{\"email\":\"" + email + "\",\"password\":\"qwerty123\",\"name\":\"user\"}")
                .post("/api/auth/register")
                .then()
                .statusCode(SC_OK);
    }

    @Test
    @DisplayName("Повторное создание пользователя")
    public void createDoubleUsersReturns403() {
        String email = generateRandomEmail();
        given()
                .contentType("application/json")
                .body("{\"email\":\"" + email + "\",\"password\":\"qwerty123\",\"name\":\"user\"}")
                .post("/api/auth/register")
                .then()
                .statusCode(SC_OK)
                .body("success", equalTo(true));

        given()
                .contentType("application/json")
                .body("{\"email\":\"" + email + "\",\"password\":\"qwerty123\",\"name\":\"user\"}")
                .post("/api/auth/register")
                .then()
                .statusCode(SC_FORBIDDEN);
    }

    @Test
    @DisplayName("Создание пользователя без email")
    public void createUserWithoutEmailReturns403() {
        given()
                .contentType("application/json")
                .body("{\"password\":\"qwerty123\",\"name\":\"user\"}")
                .post("/api/auth/register")
                .then()
                .statusCode(SC_FORBIDDEN);
    }
}

