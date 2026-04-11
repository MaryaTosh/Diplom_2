package API.FirstStepApi;
import API.POJO.CreateUserRequest;
import API.POJO.LoginRequest;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import static io.restassured.RestAssured.given;


public class UserAPI {
    private static final String USER_BASE_PATH_REGISTER = "/api/auth/register";
    private static final String USER_BASE_PATH_LOGIN = "/api/auth/login";
    private static final String USER_BASE_PATH_DELETE = "/api/auth/user";


    @Step("Создать пользователя. Регистрация и получение токена")
    public static String createUser(String email, String password, String name) {
        CreateUserRequest user = new CreateUserRequest(email, password, name);

        Response response = given()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .post(USER_BASE_PATH_REGISTER);

        return response
                .then()
                .statusCode(200)
                .extract()
                .path("accessToken");
    }

    @Step("Удалить пользователя по токену")
    public static void deleteUser(String token) {
        if (token != null) {
            given()
                    .header("Authorization", "Bearer " + token)
                    .when()
                    .delete(USER_BASE_PATH_DELETE);
        }
    }


    @Step("Авторизация пользователя. Логин и получение токена")
    public static Response loginUser(String email, String password) {
        LoginRequest user = new LoginRequest(email, password);

        Response response = given()
                .header("Content-Type", "application/json")
                .body(user)
                .when()
                .post(USER_BASE_PATH_LOGIN);
        return response;
    }
}
