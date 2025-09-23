package pac16.tests;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.restassured.RestAssured;
import org.junit.jupiter.api.*;
import pac16.api.ApiOrder;
import pac16.api.ApiUser;
import pac16.base.BaseClass;
import pac16.model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import pac16.api.ApiOrder;
import pac16.api.ApiUser;
import pac16.base.BaseClass;
import pac16.model.ApiResponse;
import pac16.model.User;
import static com.github.tomakehurst.wiremock.client.WireMock.*;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static io.restassured.path.json.JsonPath.given;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

@Epic("order api")
@Feature("negative test")

public class OtherNegativeContr extends BaseClass {
    private ApiUser apiUser;
    private ApiOrder apiOrder;
    @BeforeEach
    public void setUp(){
        super.setAll();
        apiUser=new ApiUser(userSpec,userCreateWithList);
        apiOrder=new ApiOrder(orderSpec,inventorySpec);
    }
    @Test
    @Story("CRUD User")
    @Description("Создание, получение и удаление пользователя с проверкой негативного кейса")
    public void userTest(){
        step("create user",()->{
            User newUser = new User(
                    1234,
                    "qa_test",
                    "QA",
                    "Engineer",
                    "qa@test.com",
                    "password123",
                    "1234567890",
                    1
            );
            ApiResponse response=apiUser.createUser(newUser);
            Assertions.assertEquals(200,response.getCode());

        });
        step("wait user",()->{
            await().atMost(5,TimeUnit.SECONDS)
                    .pollInterval(500,TimeUnit.MILLISECONDS)
                    .until(()->{
                        try{
                            User user=apiUser.getByUserName("qa_test");
                            return user!=null&&user.getId()>0;
                        }
                        catch (Exception e){
                            return false;
                        }
                    });
        });
        step("get user",()->{
            User getUser=apiUser.getByUserName("qa_test");
            Assertions.assertEquals("qa_test",getUser.getUserName());
            Assertions.assertTrue(getUser.getId()>0);
        });
        step("delete user",()->{
            ApiResponse response=apiUser.deleteUser("qa_test");
            Assertions.assertEquals(200,response.getCode());
        });
        step("wait delition",()->{
            await().atMost(5,TimeUnit.SECONDS)
                    .pollInterval(500,TimeUnit.MILLISECONDS)
                    .until(()->{
                        try{
                            Response response =
                                    RestAssured.given()
                                            .spec(userSpec)
                                            .pathParam("username","qa_test")
                                            .when()
                                            .get("/{username}")
                                            .andReturn();
                            int code=response.getStatusCode();
                            return code==404;
                        }
                        catch (Exception e){
                            return false;
                        }
                    });
        });
        step("negative get",()->{
            Response response=RestAssured.given()
                    .spec(userSpec)
                    .pathParam("username","qa_test")
                    .when()
                    .get("/{username}")
                    .andReturn();
            Assertions.assertEquals(404,response.getStatusCode());
        });
        step("negative get",()->{
            ApiResponse response=
                    RestAssured.given()
                            .spec(userSpec)
                            .pathParam("username","qa_test")
                            .when()
                            .get("/{username}")
                            .then()
                            .statusCode(404)
                            .extract()
                            .as(ApiResponse.class);
            Assertions.assertEquals(1, response.getCode()); // пример
            Assertions.assertTrue(response.getMessage().contains("User not found"));
        });


    }
}
class WiremockPet{
    static WireMockServer wireMockServer;
    @BeforeAll
    public static void setServer(){
        wireMockServer=new WireMockServer(8080);
        wireMockServer.start();
        configureFor("localhost",8080);
        stubFor(post(urlEqualTo("/pet"))
                .withRequestBody(equalToJson("{}"))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-type","application/json")
                        .withBody("{\"code\":400,\"message\":\"Invalid input\"}")));
        RestAssured.baseURI="http://localhost:8080";
    }
    @AfterAll
    public static void stopServer(){
        wireMockServer.stop();
    }
    @Test
    public void test1(){
        String json="{}";
        Response response=
                RestAssured.given()
                        .header("Content-type","application/json")
                        .body(json)
                        .when()
                        .post("/pet")
                        .andReturn();
        int code=response.getStatusCode();
        Assertions.assertEquals(400,code);
        ErrorResponse errorResponse=response.as(ErrorResponse.class);
        Assertions.assertEquals(400,errorResponse.getCode());
        Assertions.assertEquals("Invalid input",errorResponse.getMessage());
    }
}

class WireMockDelete{
    static WireMockServer wireMockServer;
    @BeforeAll
    public static void setServer(){
        wireMockServer=new WireMockServer(8080);
        wireMockServer.start();
        configureFor("localhost",8080);
        RestAssured.baseURI="http://localhost:8080";
        stubFor(delete(urlPathMatching("/pet/([1-9][0-9]{2,})"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-type","application/json")
                        .withBody("{\"code\":404, \"message\":\"Pet not found\"}")));

        stubFor(delete(urlPathMatching("/pet/[0-9]{1,2}"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-type","application/json")
                        .withBody("{\"code\":200, \"message\":\"Pet deleted\"}")));

    }
    @AfterAll
    public static void stopServer(){
        wireMockServer.stop();
    }
    @Test
    public void test(){
        Response response=
                RestAssured.given()
                        .when()
                        .delete("/pet/50")
                        .andReturn();
        Assertions.assertEquals(200,response.getStatusCode());
        ErrorResponse response1=response.as(ErrorResponse.class);
        Assertions.assertEquals(200,response1.getCode());
        Assertions.assertEquals("Pet deleted",response1.getMessage());
    }
    @Test
    public void test2(){
        Response response=RestAssured.given()
                .when()
                .delete("/pet/4585")
                .andReturn();
        Assertions.assertEquals(404,response.getStatusCode());
        ErrorResponse errorResponse=response.as(ErrorResponse.class);
        Assertions.assertEquals(404,errorResponse.getCode());
        Assertions.assertEquals("Pet not found",errorResponse.getMessage());

    }
}
class WireMockPut{
    static WireMockServer wireMockServer;
    @BeforeAll
    public static void setWireMockServer(){
        wireMockServer=new WireMockServer(8080);
        wireMockServer.start();
        configureFor("localhost",8080);
        RestAssured.baseURI="http://localhost:8080";
        stubFor(put(urlEqualTo("/pet"))
                .withRequestBody(equalToJson("{}"))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-type","application/json")
                        .withBody("{\"code\":400,\"message\":\"Invalid input for update\"}")));

        stubFor(put(urlEqualTo("/pet"))
                .withRequestBody(matchingJsonPath("$.name"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-type","application/json")
                        .withBody("{\"code\":200,\"message\":\"Pet updated\"}")));

    }
    @AfterAll
    public static void stopWireMockServer(){
        wireMockServer.stop();
    }
    @Test
    public static void test1(){
        String json="{}";
        Response response=
                RestAssured.given()
                        .body(json)
                        .when()
                        .put("/pet");
        Assertions.assertEquals(400,response.getStatusCode());
        ErrorResponse errorResponse=response.as(ErrorResponse.class);
        Assertions.assertEquals("Invalid input for update",errorResponse.getMessage());
        Assertions.assertEquals(400,errorResponse.getCode());
    }

    @Test
    public void test2(){
        String json = "{\"name\":\"Buddy\"}";
        Response response=RestAssured.given()
                .body(json)
                .when()
                .put("/pet")
                .andReturn();
        Assertions.assertEquals(200,response.getStatusCode());
    }
}