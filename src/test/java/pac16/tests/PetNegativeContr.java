package pac16.tests;

import io.qameta.allure.Description;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import pac16.api.ApiPet;
import pac16.base.BaseClass;
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
import pac16.model.ErrorResponse;
import pac16.model.Pet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import static io.restassured.RestAssured.given;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.path.json.JsonPath.given;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import static com.github.tomakehurst.wiremock.client.WireMock.*;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PetNegativeContr extends BaseClass {
    private  ApiPet apiPet;

    @BeforeEach
    public void setUp(){
        super.setAll();
        apiPet=new ApiPet(petSpec,formSpec);
    }
    @Story("get 404")
    @Description("get 404")
    @ParameterizedTest
    @ValueSource(ints = {77,88,99})
    public void get404(int id) throws JsonProcessingException {
        step("get 404",()->{
            Response response=
                    given()
                            .spec(petSpec)
                            .pathParam("id",id)
                            .when()
                            .get("/{id}")
                            .andReturn();
            int code=response.getStatusCode();
            Assertions.assertEquals(404,code);
            ApiResponse apiResponse= null;
            try {
                apiResponse = mapper.readValue(response.asString(), ApiResponse.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            Assertions.assertEquals(1,apiResponse.getCode());
            Assertions.assertEquals("error",apiResponse.getType());
            Assertions.assertEquals("Pet not found",apiResponse.getMessage());
        });


    }
    @Story("get 404 status")
    @Description("get 404 status")
    @Test
    public void get404status(){
        Response response=
                given()
                        .spec(petSpec)
                        .queryParam("status","test")
                        .when()
                        .get("/findByStatus")
                        .andReturn();
        Assertions.assertEquals(400,response.getStatusCode());
    }

    @Story("post 404")
    @Description("post 404")
    @Test
    public void post404(){
        String json="{}";
        Response response=
                given()
                        .spec(petSpec)
                        .body(json)
                        .when()
                        .post()
                        .andReturn();
        int code=response.getStatusCode();
        Assertions.assertEquals(200,code);
    }
    @Story("404 delete")
    @Description("404 delete")
    @ParameterizedTest
    @ValueSource(ints = {7775578,887768786,997786786})
    public void delete404(int id){
        Response response=
                given()
                        .pathParam("id",id)
                        .when()
                        .delete("/{id}")
                        .andReturn();
        int code=response.getStatusCode();
        Assertions.assertEquals(404,code);

    }
}
class WireMockNegative{
    static WireMockServer wireMockServer;
    @BeforeAll
    public static void setUpServer(){
        wireMockServer=new WireMockServer(8080);
        wireMockServer.start();
        configureFor("localhost",8080);
        stubFor(post(urlEqualTo("/pet"))
                .withRequestBody(equalToJson("{}"))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"code\":400,\"message\":\"Invalid input\"}")));
        RestAssured.baseURI="http://localhost:8080";
    }
    @AfterAll
    public static void stopServer(){
        wireMockServer.stop();
    }
    @Test
    public void test(){
        String json="{}";
        Response response=
                given()
                        .header("Content-type","application/json")
                        .body(json)
                        .post("/pet")
                        .andReturn();
        int code=response.getStatusCode();
        Assertions.assertEquals(400,code);
        ErrorResponse errorResponse=response.as(ErrorResponse.class);
        Assertions.assertEquals(400,errorResponse.getCode());
        Assertions.assertEquals("Invalid input",errorResponse.getMessage());
    }

}
class NegativeDelete{
    static WireMockServer wireMockServer;
    @BeforeAll
    static void setUp(){
        wireMockServer=new WireMockServer(8080);
        wireMockServer.start();
        configureFor("localhost",8080);
        RestAssured.baseURI="http://localhost:8080";
        stubFor(delete(urlPathMatching("/pet/\\d+"))
                .withRequestBody(absent())
                .willReturn(aResponse()
                        .withStatus(200)));

        stubFor(delete(urlMatching("/pet/(1[0-9][0-9]|[2-9][0-9]{2,})"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"code\":404,\"message\":\"Pet not found\"}")));
    }
    @AfterAll
    static void stop(){
        wireMockServer.stop();
    }
    @Test
    public void test1(){
        Response response=
                given()
                        .when()
                        .delete("/pet/50")
                        .andReturn();
        Assertions.assertEquals(200,response.getStatusCode());
    }
}