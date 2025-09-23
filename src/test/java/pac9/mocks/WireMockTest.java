package pac9.mocks;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.BeforeEach;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

public class WireMockTest {
    private WireMockServer wireMockServer;
    @BeforeEach
    public void setUp(){
        wireMockServer=new WireMockServer(options().dynamicPort());
        wireMockServer.start();
        WireMock.configureFor("localhost",wireMockServer.port());
        stubFor(get(urlEqualTo("/users/1"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("content-type","application/json")
                        .withBody("{\n" +
                                "  \"id\": 5,\n" +
                                "  \"userName\": \"string1\",\n" +
                                "  \"password\": \"string2\"\n" +
                                "}")));
        RestAssured.baseURI="http://localhost:" + wireMockServer.port();
    }
    @AfterEach
    public void down(){
        wireMockServer.stop();
    }
    @Test
    public void test(){
        given()
                .when()
                .get("/users/1")
                .then()
                .statusCode(200)
                .body("id",equalTo(5))
                .body("userName",equalTo("string1"))
                .body("password",equalTo("string2"));
    }
}
