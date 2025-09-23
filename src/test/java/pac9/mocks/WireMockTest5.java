package pac9.mocks;

import com.github.tomakehurst.wiremock.WireMockServer;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.hamcrest.Matchers.equalTo;
//import org.junit.jupiter.api.*;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pac9.model.Person;

import java.util.*;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

public class WireMockTest5 {
    private WireMockServer wireMockServer;

    @BeforeEach
    public void setUp() {
        wireMockServer = new WireMockServer(options().dynamicPort());
        wireMockServer.start();
        configureFor("localhost", wireMockServer.port());
        stubFor(get(urlEqualTo("/person/1"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-type", "application/json")
                        .withBody("{\n" +
                                "  \"id\": 1,\n" +
                                "  \"personName\": \"John\",\n" +
                                "  \"email\": \"john@example.com\"\n" +
                                "}")));
        stubFor(get(urlEqualTo("/person/11"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-type", "application/json")
                        .withBody("{ \"error\": \"Person not found\" }")));

        stubFor(put(urlEqualTo("/person/1"))
                .withRequestBody(equalToJson("{\n" +
                        "  \"id\": 11,\n" +
                        "  \"personName\": \"test1\",\n" +
                        "  \"email\": \"test2\"\n" +
                        "}"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-type","application/json")
                        .withBody("{\n" +
                                "  \"id\": 11,\n" +
                                "  \"personName\": \"test1\",\n" +
                                "  \"email\": \"test2\"\n" +
                                "}")));
        stubFor(put(urlEqualTo("/person/5"))
                .withRequestBody(containing("updatedName"))
                .withRequestBody(containing("updatedEmail"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-type","application/json")
                        .withBody("{ \"message\": \"Person updated\" }")));

        stubFor(post(urlEqualTo("/person"))
                .withRequestBody(equalToJson("{\n" +
                        "  \"id\": 11,\n" +
                        "  \"personName\": \"test1\",\n" +
                        "  \"email\": \"test2\"\n" +
                        "}"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-type","application/json")
                        .withBody("{\n" +
                                "  \"id\": 11,\n" +
                                "  \"personName\": \"test1\",\n" +
                                "  \"email\": \"test2\"\n" +
                                "}")));

        stubFor(put(urlEqualTo("/person/57"))
                .withRequestBody(matchingJsonPath("$.personName", matching("ggg")))
                .withRequestBody(matchingJsonPath("$.email",matching("jjj")))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"message\": \"OK\"}")));




        RestAssured.baseURI = "http://localhost:" + wireMockServer.port();

    }

    @AfterEach
    public void down() {
        wireMockServer.stop();
    }

    @Test
    public void get1() {
        given()
                .when()
                .get("/person/1")
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("personName", equalTo("John"))
                .body("email", equalTo("john@example.com"));
    }

    @Test
    public void get2() {
        Person person =
                given()
                        .when()
                        .get("/person/1")
                        .then()
                        .statusCode(200)
                        .extract()
                        .as(Person.class);
        Assertions.assertEquals(1, person.getId());
        Assertions.assertEquals("John", person.getName());
        Assertions.assertEquals("john@example.com", person.getEmail());
    }
    @Test
    public void get3(){
        given()
                .when()
                .get("/person/11")
                .then()
                .statusCode(404)
                .body("error",equalTo("Person not found"));
    }
    @Test
    public void put1(){
        String body="{\n" +
                "  \"id\": 11,\n" +
                "  \"personName\": \"test1\",\n" +
                "  \"email\": \"test2\"\n" +
                "}";
      Person person=  given()
                .header("Content-type","application/json")
                .body(body)
                .when()
                .put("/person/1")
                .then()
                .statusCode(200)
              .extract()
              .as(Person.class);
      Assertions.assertEquals(11,person.getId());
      Assertions.assertEquals("test1",person.getName());
      Assertions.assertEquals("test2",person.getEmail());
    }
    @Test
    public void put2(){
        String body="{\n" +
                "  \"id\": 11,\n" +
                "  \"personName\": \"updatedName\",\n" +
                "  \"email\": \"updatedEmail\"\n" +
                "}";;
        given()
                .body(body)
                .header("Content-type","application/json")
                .when()
                .put("/person/5")
                .then()
                .statusCode(200)
                .body("message",equalTo("Person updated"));
    }
    @Test
    public void create(){
        Map<String, Object>map=new HashMap<>();
        map.put("id",11);
        map.put("personName","test1");
        map.put("email","test2");

        Person person =
                given()
                        .header("Content-type","application/json")
                        .body(map)
                        .when()
                        .post("/person")
                        .then()
                        .statusCode(200)
                        .extract()
                        .as(Person.class);
        Assertions.assertEquals(11,person.getId());
        Assertions.assertEquals("test1",person.getName());
        Assertions.assertEquals("test2",person.getEmail());
    }
    @Test
    public void put4(){
            String body="{\n" +
                    "  \"id\": 11,\n" +
                    "  \"personName\": \"ggg\",\n" +
                    "  \"email\": \"jjj\"\n" +
                    "}";;

                    given()
                            .header("Content-type","application/json")
                            .body(body)
                            .when()
                            .put("/person/57")
                            .then()
                            .statusCode(200)
                            .body("message",equalTo("OK"));
    }
}
