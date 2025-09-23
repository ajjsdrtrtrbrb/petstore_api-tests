package pac16.tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import pac16.api.ApiUser;
import pac16.base.BaseClass;
import pac16.model.*;

import static io.restassured.RestAssured.given;
import static org.awaitility.Awaitility.await;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.internal.shadowed.jackson.annotation.JsonTypeInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pac16.api.ApiPet;
import pac16.base.BaseClass;
import pac16.model.ApiResponse;
import com.github.tomakehurst.wiremock.WireMockServer;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import io.restassured.RestAssured;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Epic("user crud")
@Feature("user crud")
public class UserTest4 extends BaseClass {
    ApiUser apiUser;

    @BeforeEach
    public void setUp() {
        super.setAll();
        apiUser = new ApiUser(userSpec, userCreateWithList);
    }

    @Story("create user")
    @Description("create user")
    @Test
    public void createAndGet() {
        step("create user", () -> {
            User newUser = new User(
                    1234,               // id
                    "qa_test",          // username
                    "QA",               // firstName
                    "Engineer",         // lastName
                    "qa@test.com",      // email
                    "password123",      // password
                    "1234567890",       // phone
                    1                   // userStatus
            );
            ApiResponse response = apiUser.createUser(newUser);
            Assertions.assertEquals(200, response.getCode());
            Assertions.assertEquals("unknown", response.getType());
            Assertions.assertFalse(response.getMessage().isEmpty());

        });
        step("wait creation", () -> {
            await().atMost(5, TimeUnit.SECONDS)
                    .pollInterval(500, TimeUnit.MILLISECONDS)
                    .until(() -> {
                        try {
                            User user=apiUser.getByUserName("qa_test");
                            return user!=null&&user.getId()>0;
                        } catch (Exception e) {
                            return false;
                        }

                    });
        });
        step("get user",()->{
            User createdUser= given()
                    .spec(userSpec)
                    .pathParam("username","qa_test")
                    .when()
                    .get("/{username}")
                    .then()
                    .statusCode(200)
                    .extract()
                    .as(User.class);
            Assertions.assertNotNull(createdUser); // объект не null
            Assertions.assertEquals(1234, createdUser.getId(), "Id не совпадает");
            Assertions.assertEquals("qa_test", createdUser.getUserName(), "Username не совпадает");
            Assertions.assertEquals("QA", createdUser.getFirstName(), "FirstName не совпадает");
            Assertions.assertEquals("Engineer", createdUser.getLastName(), "LastName не совпадает");
            Assertions.assertEquals("qa@test.com", createdUser.getEmail(), "Email не совпадает");
            Assertions.assertEquals("password123", createdUser.getPassword(), "Password не совпадает");
            Assertions.assertEquals("1234567890", createdUser.getPhone(), "Phone не совпадает");
            Assertions.assertEquals(1, createdUser.getUserStatus(), "UserStatus не совпадает");
        });
        step("delete",()->{
            ApiResponse response=apiUser.deleteUser("qa_test");
            Assertions.assertEquals(200,response.getCode());
        });
        step("wait delition",()->{
            await().atMost(5,TimeUnit.SECONDS)
                    .pollInterval(500,TimeUnit.MILLISECONDS)
                    .until(()->{
                        Response resp = given()
                                .pathParam("username", "qa_test")
                                .get("/{username}");
                        return resp.getStatusCode() == 404;
                    });
        });
        step("404 get",()->{
            Response response= given()
                    .pathParam("username","qa_test")
                    .when()
                    .get("/{username}")
                    .andReturn();
            Assertions.assertEquals(404,response.getStatusCode());
        });

    }
}
class UserMock extends BaseClass{
    static WireMockServer wireMockServer;
    @BeforeAll
    public static void startServer(){
        wireMockServer = new WireMockServer(8080);
        wireMockServer.start();
        configureFor("localhost", 8080);

        // Установим RestAssured прямо на WireMock
        RestAssured.baseURI = "http://localhost:8080";

        // GET /user/{username} → 404
        stubFor(get(urlPathMatching("/user/.*"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"message\":\"User not found\"}")));

        // POST /user → 500
        stubFor(post(urlEqualTo("/user")) // важно urlEqualTo для точного совпадения
                .withRequestBody(equalToJson("{}"))
                .willReturn(aResponse()
                        .withStatus(500)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"message\":\"Invalid request body\"}")));

    }
    @AfterAll
    public static void stopServer(){
        if(wireMockServer!=null)
            wireMockServer.stop();
    }
    @Test
    public void test(){
        step("get user",()->{
            Response response=given()
                    .baseUri("http://localhost:8080")
                    .contentType("application/json")
                    .pathParam("username","userjkshjs")
                    .when()
                    .get("/{username}")
                    .andReturn();
            int code=response.getStatusCode();
            Assertions.assertEquals(404,code);


        });
        step("create user",()->{
            String json="{}";
            Response response=given()
                    .baseUri("http://localhost:8080")
                    .contentType("application/json")
                    .body(json)
                    .when()
                    .post("/user")
                    .andReturn();
            Assertions.assertEquals(500,response.getStatusCode());
        });
    }
}