package pac1.Tests;

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

public class Test11 extends BaseClass {

    @Test
    @Order(1)
    public void get() {
        Response response = given()
                .when()
                .get("/api/v1/Activities")
                .andReturn();
        response.prettyPrint();
    }

    @Test
    @Order(2)
    public void get2() {
        given()
                .when()
                .get("/api/v1/Activities")
                .then()
                .statusCode(200);

    }

    @Test
    @Order(3)
    public void create() {
        Activity activity = new Activity(10, "test", "2025-07-25T13:37:17.092Z", true);
        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(activity)
                .when()
                .post("/api/v1/Activities")
                .then()
                .statusCode(200)
                .log().body()
                .body("id", notNullValue())
                .body("title", equalTo("test"))
                .body("id", equalTo(10))
                .body("dueDate", equalTo("2025-07-25T13:37:17.092Z"))
                .body("completed", equalTo(true));

    }

    @Test
    @Order(4)
    public void create2() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", 88);
        map.put("title", "test2");
        map.put("dueDate", "2025-07-25T13:37:17.092Z");
        map.put("completed", false);
        given()
                .header("Content-Type", "application/json")
                .body(map)
                .when()
                .post("/api/v1/Activities")
                .then()
                .statusCode(200)
                .body("id", equalTo(88))
                .body("title", equalTo("test2"))
                .body("completed",equalTo(false));
    }

    @Test
    @Order(5)
    public void put() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", 888);
        map.put("title", "test22");
        map.put("dueDate", "2025-07-25T13:37:17.092Z");
        map.put("completed", true);
        given()
                .contentType(ContentType.JSON)
                .body(map)
                .when()
                .put("/api/v1/Activities/88")
                .then()
                .log().body()
                .body("id", equalTo(888))
                .body("title", equalTo("test22"))
                .body("completed",equalTo(true));
    }

    @Test
    @Order(6)
    public void get888() {
        given()
                .when()
                .get("api/v1/Activities/88999")
                .then()
                .statusCode(404);
    }
    @Test
    @Order(7)
    public void delete(){
        given()
                .when()
                .delete("api/v1/Activities/10")
                .then()
                .statusCode(200);
    }
}
