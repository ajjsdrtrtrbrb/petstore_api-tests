package pac9.mocks;
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

public class WireMockTest3 {
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
                        .withBody(("{\n" +
                                "  \"id\": 1,\n" +
                                "  \"personName\": \"test\",\n" +
                                "  \"email\": \"test2\"\n" +
                                "}"))));

        stubFor(get(urlEqualTo("/person/111"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("content-type","application/json")
                        .withBody("{\n" +
                                "  \"error\": \"User not found\"\n" +
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
        RestAssured.baseURI = "http://localhost:" + wireMockServer.port();

    }
    @AfterEach
    public void down(){
        wireMockServer.stop();
    }
    @Test
    public void test1(){
        given()
                .when()
                .get("/person/1")
                .then()
                .statusCode(200)
                .body("personName",equalTo("test"))
                .body("email",equalTo("test2"));
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
    public void get2(){
        Response response=
                given()
                        .when()
                        .get("/person/111")
                        .then()
                        .statusCode(404)
                        .extract().response();
        String json=response.jsonPath().toString();
        System.out.println(json);
    }
    @Test
    public void get3(){
        List<Person>list=RestAssured
                .given()
                .when()
                .get("/persons")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .jsonPath()
                .getList("", Person.class);
        Assertions.assertFalse(list.isEmpty(),"is empty");
        int count=0;
        for(Person s:list){
            Assertions.assertEquals(++count,s.getId());
            Assertions.assertTrue(s.getId()>0);
            Assertions.assertTrue(s.getEmail()!=null);
            Assertions.assertNotNull(s.getName());
        }
    }
    @Test
    public void delete11(){
        given()
                .when()
                .delete("/person/1")
                .then()
                .statusCode(200)
                .body("message",equalTo("person1 deleted"));
    }
}
