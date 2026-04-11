package API;

import API.FirstStepApi.BaseStep;
import API.FirstStepApi.UserAPI;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;
import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.*;

public class CreateOrderTest extends BaseStep {
    private String userToken;
    private String refreshToken;
    private final String INVALID_INGREDIENT = "60d3b41abdacab002";
    private static String VALID_INGREDIENT_1;
    private static String VALID_INGREDIENT_2;

    @BeforeEach
    void setUp() {
        String email = generateRandomEmail();
        UserAPI.createUser(email, "qwerty123", "testUser");

        Response loginResponse = UserAPI.loginUser(email, "qwerty123");
        loginResponse.then().statusCode(SC_OK);

        userToken = loginResponse.path("accessToken");
        refreshToken = loginResponse.path("refreshToken");

        Response ingredientsResponse = given()
                .get("/api/ingredients");

        ingredientsResponse.then()
                .statusCode(SC_OK)
                .body("success", equalTo(true));

        List<String> ids = ingredientsResponse.path("data._id");

        Assertions.assertNotNull(ids);
        Assertions.assertTrue(ids.size() >= 2, "В /api/ingredients должно быть минимум 2 ингредиента");

        VALID_INGREDIENT_2 = ids.get(0);
        VALID_INGREDIENT_1 = ids.get(1);
    }

    @AfterEach
    void tearDown() {
        if (userToken != null) {
            given()
                    .header("Authorization", userToken)
                    .contentType("application/json")
                    .body("{\"token\": \"" + refreshToken + "\"}")
                    .post("/api/auth/logout")
                    .then()
                    .statusCode(SC_OK);
            UserAPI.deleteUser(userToken);
        }
    }
    private static Stream<Arguments> ingredientsProvider() {
        return Stream.of(
                Arguments.of("с одним ингредиентом", new String[]{VALID_INGREDIENT_1}),
                Arguments.of("с двумя ингредиентами", new String[]{VALID_INGREDIENT_1, VALID_INGREDIENT_2})
        );
    }

    @ParameterizedTest(name = "Создание заказа с авторизацией и {0}")
    @MethodSource("ingredientsProvider")
    @DisplayName("Создание заказа с авторизацией и разными наборами ингредиентов")
    public void createOrder_AuthWithIngredients_ReturnsOK(String caseName, String[] ingredients) {
        String body = String.format("{\"ingredients\":[\"%s\"]}",
                String.join("\",\"", ingredients)
        );

        given()
                .header("Authorization", userToken)
                .contentType("application/json")
                .body(body)
                .post("/api/orders")
                .then()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("name", not(emptyString()))
                .body("order.number", greaterThanOrEqualTo(1));
    }
    @DisplayName("Создание заказа без авторизации с валидными ингредиентами возвращает 400")
    @Test
    public void createOrder_WithoutAuth_WithIngredients_Returns400() {
        String body = String.format(
                "{\"ingredients\":[\"%s\",\"%s\"]}",
                VALID_INGREDIENT_1, VALID_INGREDIENT_2
        );

        given()
                .header("Authorization", "Bearer invalid_fake_token")
                .contentType("application/json")
                .body(body)
                .post("/api/orders")
                .then()
                .statusCode(SC_FORBIDDEN)
                .body("success", equalTo(false));
    }

    @DisplayName("Создание заказа с авторизацией, но без ингредиентов возвращает 400")
    @Test
    public void createOrder_Auth_WithoutIngredients_Returns400() {
        String body = "{\"ingredients\":[]}";

        given()
                .header("Authorization", userToken)
                .contentType("application/json")
                .body(body)
                .post("/api/orders")
                .then()
                .statusCode(SC_BAD_REQUEST)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @DisplayName("Создание заказа с невалидным хешем ингредиентов возвращает 500")
    @Test
    public void createOrder_WithInvalidIngredientHash_Returns400() {
        String body = String.format(
                "{\"ingredients\":[\"%s\",\"%s\"]}",
                INVALID_INGREDIENT, INVALID_INGREDIENT
        );

        given()
                .header("Authorization", userToken)
                .contentType("application/json")
                .body(body)
                .post("/api/orders")
                .then()
                .statusCode(SC_INTERNAL_SERVER_ERROR);
    }
    }
