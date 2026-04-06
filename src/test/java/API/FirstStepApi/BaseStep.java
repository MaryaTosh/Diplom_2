package API.FirstStepApi;

import API.POJO.LoginRequest;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;

import static io.restassured.RestAssured.given;

public abstract class BaseStep {
    @BeforeAll
    static void setUpAll() {
        RestAssured.baseURI = "https://stellarburgers.education-services.ru";
        RestAssured.filters(new AllureRestAssured());

    }
    protected static String generateRandomEmail() {
        return "user_" + System.currentTimeMillis() + "@yandex.ru";
    }

    private  String getAccessToken(String email, String password) {
        LoginRequest login = new LoginRequest(email, password);

        return given()
                .header("Content-type", "application/json")
                .body(login)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .extract()
                .path("accessToken");
    }

}
