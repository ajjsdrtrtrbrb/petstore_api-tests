package pac9.mocks;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import static com.github.tomakehurst.wiremock.client.WireMock.*;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.hamcrest.Matchers.equalTo;
//import org.junit.jupiter.api.*;
import com.github.tomakehurst.wiremock.stubbing.Scenario;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pac9.model.Person;
import com.github.tomakehurst.wiremock.stubbing.Scenario;

import java.util.*;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

public class WireMockTest6 {
    private WireMockServer wireMockServer;
    private String contentType = "Content-Type";
    private String applicationJson = "application/json";

    @BeforeEach
    public void setUp() {
        wireMockServer = new WireMockServer(options().dynamicPort());
        wireMockServer.start();
        configureFor("localhost", wireMockServer.port());

        stubFor(get(urlEqualTo("/person/1"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(contentType, applicationJson)
                        .withBody("{\n" +
                                "  \"id\": 1,\n" +
                                "  \"personName\": \"John\",\n" +
                                "  \"email\": \"john@example.com\"\n" +
                                "}")));

        stubFor(put(urlEqualTo("/person/1"))
                .withRequestBody(containing("test1"))
                .withRequestBody(containing("test2"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(contentType, applicationJson)
                        .withBody("{ \"message\": \"Person updated\" }")));

        stubFor(put(urlEqualTo("/person/2"))
                .withRequestBody(matchingJsonPath("$.personName", matching("kolia")))
                .withRequestBody(matchingJsonPath("$.email", matching("test")))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(contentType, applicationJson)
                        .withBody("{ \"message\": \"Kolia updated\" }")));
        stubFor(post(urlEqualTo("/person"))
                .withRequestBody(equalToJson("{\n" +
                        "  \"id\": 1,\n" +
                        "  \"personName\": \"dinamo\",\n" +
                        "  \"email\": \"kiev\"\n" +
                        "}"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(contentType, applicationJson)
                        .withBody("{\n" +
                                "  \"id\": 1,\n" +
                                "  \"personName\": \"dinamo\",\n" +
                                "  \"email\": \"kiev\"\n" +
                                "}")));

        stubFor(post(urlEqualTo("/person"))
                .withRequestBody(matchingJsonPath("$.id", matching("777")))
                .willReturn(aResponse()
                        .withStatus(500)
                        .withHeader(contentType, applicationJson)
                        .withBody("{ \"message\": \"internal error\" }")));
        stubFor(get(urlEqualTo("/person/88"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader(contentType, applicationJson)
                        .withBody("{ \"message\": \"not found\" }")));

        stubFor(delete(urlEqualTo("/person7"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(contentType, applicationJson)
                        .withBody("{ \"message\": \"deleted\" }")));

        stubFor(post(urlEqualTo("/person"))
                .withRequestBody(matchingJsonPath("id",matching("777")))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader(contentType,applicationJson)
                        .withBody("{ \"message\": \"bad request\" }")));

        stubFor(get(urlEqualTo("/person"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(contentType,applicationJson)
                        .withBody("[\n" +
                                "  {\n" +
                                "    \"id\": 1,\n" +
                                "    \"personName\": \"John1\",\n" +
                                "    \"email\": \"test1\"\n" +
                                "  },\n" +
                                "  {\n" +
                                "    \"id\": 2,\n" +
                                "    \"personName\": \"John2\",\n" +
                                "    \"email\": \"test2\"\n" +
                                "  }\n" +
                                "]")));
        setStubsScenario();
        setStubsScenario2();

        RestAssured.baseURI = "http://localhost:" + wireMockServer.port();
    }
    public void setStubsScenario(){
        stubFor(post(urlEqualTo("/person"))
                .inScenario("Person scenario")
                .whenScenarioStateIs(Scenario.STARTED)
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(contentType,applicationJson)
                        .withBody("{\"id\":5,\"personName\":\"barsa\",\"email\":\"spain\"}")
                        ).willSetStateTo("CREATED"));
        stubFor(get(urlEqualTo("/person/5"))
                .inScenario("Person scenario")
                .whenScenarioStateIs("CREATED")
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(contentType,applicationJson)
                        .withBody("{\"id\":5,\"personName\":\"barsa\",\"email\":\"spain\"}")));
    }
    public void setStubsScenario2(){
        stubFor(get(urlPathMatching("/person/\\d+"))
                .inScenario("Person scenario2")
                .whenScenarioStateIs(Scenario.STARTED)
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(contentType,applicationJson)
                        .withBody("{\"id\":1,\"personName\":\"real\",\"email\":\"madrid\"}"))
                .willSetStateTo("CREATED"));

        stubFor(get(urlPathEqualTo("/person"))
                .withQueryParam("active", matching("true"))
                .inScenario("Person scenario2")
                .whenScenarioStateIs("CREATED")
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(contentType,applicationJson)
                        .withBody("[{\"id\":1,\"personName\":\"John1\"}]")));

    }

    @AfterEach
    public void down() {
        wireMockServer.stop();
    }

    @Test
    public void get1() {
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
    public void put1() {
        String json = "{\n" +
                "  \"id\": 1,\n" +
                "  \"personName\": \"test1\",\n" +
                "  \"email\": \"test2\"\n" +
                "}";
        given()
                .body(json)
                .header(contentType, applicationJson)
                .when()
                .put("/person/1")
                .then()
                .statusCode(200)
                .body("message", equalTo("Person updated"));

    }

    @Test
    public void put2() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", 99);
        map.put("personName", "kolia");
        map.put("email", "test");
        given()
                .body(map)
                .header(contentType, applicationJson)
                .when()
                .put("/person/2")
                .then()
                .statusCode(200)
                .body("message", equalTo("Kolia updated"));
    }

    @Test
    public void create() {
        String json = "{\n" +
                "  \"id\": 1,\n" +
                "  \"personName\": \"dinamo\",\n" +
                "  \"email\": \"kiev\"\n" +
                "}";
        Response response =
                given()
                        .body(json)
                        .header(contentType, applicationJson)
                        .when()
                        .post("/person")
                        .then()
                        .statusCode(200)
                        .extract().response();
        Person person = response.as(Person.class);
        Assertions.assertEquals(1, person.getId());
        Assertions.assertEquals("dinamo", person.getName());
        Assertions.assertEquals("kiev", person.getEmail());
    }

    @Test
    public void create2() {
        Person person = new Person(777, "ddd", "sss");
        given()
                .body(person)
                .header(contentType, applicationJson)
                .when()
                .post("/person")
                .then()
                .statusCode(500)
                .body("message", equalTo("internal error"));
    }

    @Test
    public void get2() {
        given()
                .when()
                .get("/person/88")
                .then()
                .statusCode(404)
                .body("message", equalTo("not found"));
    }

    @Test
    public void deleteUser() {
        given()
                .when()
                .delete("/person7")
                .then()
                .statusCode(200)
                .header(contentType, applicationJson)
                .body("message", equalTo("deleted"));
    }
    @Test
    public void create3(){
        Map<String,Object>map=new HashMap<>();
        map.put("id",777);
        map.put("personName","kkk");
        map.put("email","ddd");
        given()
                .header(contentType,applicationJson)
                .body(map)
                .when()
                .post("/person")
                .then()
                .statusCode(400)
                .body("message",equalTo("bad request"));
    }
    @Test
    public void get3(){
        List<Person>list=given()
                .when()
                .get("/person")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .jsonPath()
                .getList("",Person.class);
        Assertions.assertFalse(list.isEmpty(),"not empty");
        for(Person s:list){
            Assertions.assertTrue(s.getId()>0);
        }
    }
    @Test
    public void get4() throws JsonProcessingException {
        Response response=
                given()
                        .when()
                        .get("/person")
                        .then()
                        .statusCode(200)
                        .extract().response();
        ObjectMapper mapper=new ObjectMapper();
        List<Person>list=mapper.readValue(response.asString(), new TypeReference<List<Person>>() {});
        Assertions.assertEquals(1,list.get(0).getId());

    }
    @Test
    public void stubTest1(){
        String json = "{\"id\":5,\"personName\":\"badsa\",\"email\":\"spain\"}";
        given()
                .header(contentType,applicationJson)
                .body(json)
                .when()
                .post("/person")
                .then()
                .statusCode(200);
        given()
                .pathParam("id",5)
                .when()
                .get("/person/{id}")
                .then()
                .statusCode(200)
                .body("id",equalTo(5))
                .body("personName",equalTo("barsa"))
                .body("email",equalTo("spain"));
    }
    @Test
    public void  stubTest2(){
        given()
                .pathParam("id",1)
                .when()
                .get("/person/{id}")
                .then()
                .statusCode(200)
                .body("id",equalTo(1))
                .body("personName",equalTo("real"));

        given()
                .queryParam("active", "true")
                .when()
                .get("/person")
                .then()
                .statusCode(200)
                .body("[0].personName",equalTo("John"));
    }
}
