package API;

import API.FirstStepApi.BaseStep;
import API.FirstStepApi.UserAPI;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.*;
public class GetOrderTest extends BaseStep{
    private String userToken;

    @BeforeEach
    void setUp() {
        String email = generateRandomEmail();
        UserAPI.createUser(email, "qwerty123", "testUser");

        Response loginResponse = UserAPI.loginUser(email, "qwerty123");
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

    @Test
    @DisplayName("Авторизованный пользователь получает свои заказы (максимум 50, с total и totalToday)")
    public void getUserOrders_Auth_ReturnsOrdersList() {
        given()
                .header("Authorization", userToken)
                .get("/api/orders")
                .then()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("orders", notNullValue())
                .body("orders.size()", greaterThanOrEqualTo(0))
                .body("total", notNullValue())
                .body("totalToday", notNullValue());
    }

    @Test
    @DisplayName("Неавторизованный пользователь при получении заказов получает 401 и сообщение об ошибке")
    public void getUserOrders_WithoutAuth_Returns401() {
        given()
                .get("/api/orders")
                .then()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }
}