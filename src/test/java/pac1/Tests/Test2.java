package pac1.Tests;

import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.Test;
import pac1.Base.BaseClass;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.matcher.ResponseAwareMatcher;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import pac1.Base.BaseClass;
import pac1.Model.Activity;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

import static org.hamcrest.Matchers.equalTo;

public class Test2 extends BaseClass {
    @Test
    @Order(1)
    public void get() {
        given()
                .when()
                .get("/api/v1/Activities")
                .then()
                .statusCode(200)
                .body("[0].id", notNullValue());
    }

    @Test
    @Order(2)
    public void create() {
        Activity activity = new Activity(10, "dinamo", "2025-07-25T13:37:17.092Z", true);
        given()
                .contentType(ContentType.JSON)
                .body(activity)
                .when()
                .post("/api/v1/Activities")
                .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("title", equalTo("dinamo"))
                .body("completed", equalTo(true));
    }

    @Test
    @Order(3)
    public void create400() {
        Activity activity = new Activity();
        activity.setId(77);
        activity.setTitle("ddd");
        given()
                .contentType(ContentType.JSON)
                .body(activity)
                .when()
                .post("/api/v1/Activities")
                .then()
                .statusCode(400);

    }

    @Test
    @Order(4)
    public void create3() {
        String json = "{\n" +
                "  \"id\": 2,\n" +
                "  \"title\": \"t\",\n" +
                "  \"dueDate\": \"2025-07-25T13:37:17.092Z\",\n" +
                "  \"completed\": true\n" +
                "}";
        given()
                .contentType(ContentType.JSON)
                .body(json)
                .when()
                .post("/api/v1/Activities")
                .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("id", equalTo(2));
    }

    @Test
    @Order(5)
    public void getById() {
        given()
                .when()
                .get("api/v1/Activities/2")
                .then()
                .statusCode(200)
                .body("id", equalTo(2));
    }

    @Test
    @Order(6)
    public void delete() {
        given()
                .when()
                .delete("api/v1/Activities/2")
                .then()
                .statusCode(200);
    }

    @Test
    @Order(7)
    public void getById2() {
       ValidatableResponse response=given()
               .when()
               .get("api/v1/Activities/2")
               .then();
       if(response.extract().statusCode()==200)
           System.out.println("dont delete");
       else
           System.out.println(response.extract().statusCode());
    }

}
