package pac9.mocks;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pac9.model.Person;

import java.util.HashMap;
import java.util.Map;
public class WireMockTest7 {
    private static WireMockServer wireMockServer;
    private static final ObjectMapper mapper=new ObjectMapper();
    private String contentType="Content-type";
    private String applicationJson="application/json";
    @BeforeAll
    public static void setUp(){
        wireMockServer=new WireMockServer(options().dynamicPort());
        wireMockServer.start();
        configureFor("localhost",wireMockServer.port());
        RestAssured.baseURI="http://localhost:"+wireMockServer.port();
    }
    @AfterAll
    public static void down(){
        wireMockServer.stop();
    }
    @Test
    public void test1() throws JsonProcessingException {
        Map<String,Object>map=new HashMap<>();
        map.put("personName","dinamo");
        stubFor(post(urlEqualTo("/person"))
                .withRequestBody(matchingJsonPath("$.personName",matching("dinamo")))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(contentType,applicationJson)
                        .withBody("person created")));
        given()
                .header(contentType,applicationJson)
                .body(mapper.writeValueAsString(map))
                .when()
                .post("/person")
                .then()
                .statusCode(200)
                .body(equalTo("person created"));
    }
    @Test
    public void test2() throws JsonProcessingException {
        Map<String,Object>map=new HashMap<>();
        map.put("personName","barsa");
        stubFor(post(urlEqualTo("/person"))
                .withRequestBody(matchingJsonPath("$.personName",matching("barsa")))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(contentType,applicationJson)
                        .withBody("barsa created")));
        given()
                .header(contentType,applicationJson)
                .body(mapper.writeValueAsString(map))
                .when()
                .post("/person")
                .then()
                .statusCode(200)
                .header(contentType,applicationJson)
                .body(equalTo("barsa created"));
    }
    @Test
    public void test3() throws JsonProcessingException {
        Map<String,Object>map=new HashMap<>();
        map.put("personName","test");
        stubFor(post(urlEqualTo("/person"))
                .withRequestBody(matchingJsonPath("$.personName",matching("test")))
                .willReturn(aResponse()
                        .withHeader(contentType,applicationJson)
                        .withStatus(200)
                        .withBody("{\n" +
                                "  \"id\": 1,\n" +
                                "  \"personName\": \"test\",\n" +
                                "  \"email\": \"test1\"\n" +
                                "}")));
        Response response=
                given()
                        .header(contentType,applicationJson)
                        .body(mapper.writeValueAsString(map))
                        .when()
                        .post("/person")
                        .then()
                        .statusCode(200)
                        .extract().response();
        Person person=mapper.readValue(response.getBody().asString(),Person.class);
        Assertions.assertEquals("test",person.getName());

    }
}
