package pac9.mocks;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.*;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import pac9.model.User;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

public class WireMockTest2 {
    private WireMockServer wireMockServer;
    @BeforeEach
    public void setUp(){
        wireMockServer=new WireMockServer(options().dynamicPort());
        wireMockServer.start();
        configureFor("localhost",wireMockServer.port());

        stubFor(get(urlEqualTo("/users/1"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("content-type","application/json")
                        .withBody("{\n" +
                                "  \"id\": 5,\n" +
                                "  \"userName\": \"string1\",\n" +
                                "  \"password\": \"string2\"\n" +
                                "}")));

        stubFor(get(urlEqualTo("/users/55"))
                .willReturn(aResponse()
                        .withStatus(404)));

        stubFor(get(urlEqualTo("/users/10"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("content-type","application/json")
                        .withBody("{\n" +
                                "  \"id\": 10,\n" +
                                "  \"userName\": \"string10\",\n" +
                                "  \"password\": \"string10\"\n" +
                                "}")));

        stubFor(post(urlEqualTo("/users"))
                .withRequestBody(equalToJson("{ \"id\": 10,\"userName\": \"test\", \"password\": \"12345\" }"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("content-type","application/json")
                        .withBody("{ \"id\": 10, \"userName\": \"test\", \"password\": \"12345\" }")));

        RestAssured.baseURI="http://localhost:" + wireMockServer.port();
    }
    @Test
    public void testGet1(){
        given()
                .when()
                .get("/users/1")
                .then()
                .statusCode(200)
                .body("id",equalTo(5))
                .body("userName",equalTo("string1"))
                .body("password",equalTo("string2"));
    }
    @Test
    public void get404(){
        given()
                .when()
                .get("/users/55")
                .then()
                .statusCode(404);
    }
    @ParameterizedTest
    @ValueSource(ints = {10})
    public void testGet(int id){
        User user=RestAssured.given()
                .when()
                .get("/users/"+id)
                .then()
                .statusCode(200)
                .extract()
                .as(User.class);
        Assertions.assertEquals(10,user.getId());
        Assertions.assertEquals("string10",user.getName());
        Assertions.assertEquals("string10",user.getPassword());
    }
    @Test
    public void create(){
        User user1=new User(10,"test","12345");
        User user=
                given()
                        .body(user1)
                        .when()
                        .post("/users")
                        .then()
                        .statusCode(200)
                        .extract()
                        .as(User.class);
        Assertions.assertEquals(10,user.getId());
        Assertions.assertEquals("test",user.getName());
        Assertions.assertEquals("12345",user.getPassword());

    }
}
