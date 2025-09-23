package pac9.mocks;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import io.restassured.response.Response;
import org.checkerframework.checker.units.qual.A;
//import org.junit.jupiter.api.*;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import pac9.model.Person;
import pac9.model.User;

import java.util.List;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.hamcrest.Matchers.notNullValue;

public class WiremockTest4 {
    private WireMockServer wireMockServer;
    @BeforeEach
    public void setUp(){
        wireMockServer=new WireMockServer(options().dynamicPort());
        wireMockServer.start();
        configureFor("localhost",wireMockServer.port());
        stubFor(get(urlEqualTo("/person/1"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("content-type","application/json")
                        .withBody("{\n" +
                                "  \"id\": 1,\n" +
                                "  \"personName\": \"test\",\n" +
                                "  \"email\": \"test2\"\n" +
                                "}")));

        stubFor(get(urlEqualTo("/person/111"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("content-type","application/json")
                        .withBody(
                                "{  \"error\": \"User not found\"\n" +
                                        "}")));

        stubFor(get(urlEqualTo("/persons"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("content-type","application/json")
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

        stubFor(delete(urlEqualTo("/person/1"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("content-type","application/json")
                        .withBody("{\n" +
                                "  \"message\": \"person1 deleted\"\n" +
                                "}")));

        stubFor(post(urlEqualTo( "/persons"))
                .withRequestBody(equalToJson("{\n" +
                        "  \"id\": 11,\n" +
                        "  \"personName\": \"test11\",\n" +
                        "  \"email\": \"test22\"\n" +
                        "}"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("content-type","application/json")
                        .withBody("{\n" +
                                "  \"id\": 11,\n" +
                                "  \"personName\": \"test11\",\n" +
                                "  \"email\": \"test22\"\n" +
                                "}")));
        stubFor(post(urlEqualTo("/personss"))
                .withRequestBody(equalToJson("{\n" +
                        "  \"id\": 11,\n" +
                        "  \"personName\": \"test11\",\n" +
                        "  \"email\": \"test22\"\n" +
                        "}"))
                .willReturn(aResponse()
                        .withStatus(500)
                        .withHeader("content-type","application/json")
                        .withBody("{\n" +
                                "  \"message\": \"internal error\"\n" +
                                "}")));

        stubFor(put(urlEqualTo("/persons/11"))
                .withRequestBody(equalToJson("{\n" +
                        "  \"id\": 12,\n" +
                        "  \"personName\": \"test12\",\n" +
                        "  \"email\": \"test222\"\n" +
                        "}"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("content-type","application/json")
                        .withBody("{\n" +
                                "  \"id\": 12,\n" +
                                "  \"personName\": \"test12\",\n" +
                                "  \"email\": \"test222\"\n" +
                                "}")));


        RestAssured.baseURI="http://localhost:"+wireMockServer.port();

    }
    @AfterEach
    public void down(){
        wireMockServer.stop();
    }
    @Test
    public void get1(){
        given()
                .when()
                .get("/person/1")
                .then()
                .statusCode(200)
                .body("id",equalTo(1))
                .body("personName",equalTo("test"))
                .body("email",equalTo("test2"));
    }
    @Test
    public void get2(){
        Person person=
                given()
                        .when()
                        .get("/person/1")
                        .then()
                        .statusCode(200)
                        .body("id",equalTo(1))
                        .extract()
                        .as(Person.class);
        Assertions.assertNotNull(person.getId());
        Assertions.assertTrue(person.getId()>0);
        Assertions.assertEquals(1,person.getId());
        Assertions.assertEquals("test",person.getName());
        Assertions.assertEquals("test2",person.getEmail());
    }
    @Test
    public void get3(){
        Response response=
                given()
                        .when()
                        .get("/person/1")
                        .then()
                        .statusCode(200)
                        .extract().response();
        Person person=response.as(Person.class);
        Assertions.assertEquals(1,person.getId());
    }
    @Test
    public void get404(){
        given()
                .when()
                .get("/person/111")
                .then()
                .statusCode(404)
                .body("error",equalTo("User not found"));
    }
    @Test
    public void getAll1(){
        List<Person>list=
                given()
                        .when()
                        .get("/persons")
                        .then()
                        .statusCode(200)
                        .extract()
                        .body()
                        .jsonPath()
                        .getList("",Person.class);
        Assertions.assertFalse(list.isEmpty(),"is empty");
        for(Person s:list){
            Assertions.assertNotNull(s.getId());
            Assertions.assertTrue(s.getId()>0);
            Assertions.assertNotNull(s.getName());
            Assertions.assertNotNull(s.getEmail());
        }

    }
    @Test
    public void getAll2() throws JsonProcessingException {
        Response response=
                given()
                        .when()
                        .get("/persons");
        ObjectMapper mapper=new ObjectMapper();
        List<Person>list=mapper.readValue(response.asString(), new TypeReference<List<Person>>() {
        });
        Assertions.assertFalse(list.isEmpty(),"is empty");
        for(Person s:list){
            Assertions.assertTrue(s.getId()>0);
        }
    }
    @Test
    public void getAll3(){
       Response response = given()
                .when()
                .get("/persons")
                .then()
                .statusCode(200)
                .body("[0].id",equalTo(1))
                .body("[1].id",equalTo(2))
               .extract().response();
       response.prettyPrint();

    }
    @Test
    public void deletePerson(){
        given()
                .when()
                .delete("/person/1")
                .then()
                .statusCode(200)
                .body("message",equalTo("person1 deleted"));
    }
    @Test
    public void create1(){
        Response response=
                given()
                        .when()
                        .post("/persons")
                        .then()
                        .statusCode(200)
                        .body("id",equalTo(11))
                        .body("personName",equalTo("test1"))
                        .body("email",equalTo("test2"))
                        .extract().response();
        response.prettyPrint();
        Person person=response.as(Person.class);
        Assertions.assertEquals(1,person.getId());
        Assertions.assertEquals("test1",person.getName());
        Assertions.assertEquals("test2",person.getEmail());

    }
    @Test
    public void create2(){
        given()
                .when()
                .post("/personss")
                .then()
                .statusCode(500)
                .body("message",equalTo("internal error"));
    }
    @Test
    public void test(){
        String requestBody = "{\n" +
                "  \"id\": 12,\n" +
                "  \"personName\": \"test12\",\n" +
                "  \"email\": \"test222\"\n" +
                "}";
        Person person=
                given()
                        .header("content-type","application/json")
                        .body(requestBody)
                        .when()
                        .put("/persons/11")
                        .then()
                        .statusCode(200)
                        .body("id",notNullValue())
                        .extract()
                        .as(Person.class);
        Assertions.assertEquals(12, person.getId());
        Assertions.assertEquals("test12",person.getName());
        Assertions.assertEquals("test222",person.getEmail());
    }
}
