package pac1.Tests;

import org.joda.time.LocalDateTime;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import pac1.Base.BaseClass;
import pac1.Model.Activity;
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
import static org.hamcrest.Matchers.equalTo;

public class Test3 extends BaseClass {
    @Test
    @Order(1)
    public void create() {
        // String d=LocalDateTime.now().toString();
        Activity activity1 = new Activity(1, "activity1", "2025-07-25T13:37:17.092Z", true);
        //String dueDate=LocalDateTime.now().toString();
        String json = "{\n" +
                "  \"id\": 2,\n" +
                "  \"title\": \"activity2\",\n" +
                "  \"dueDate\": \"2025-07-25T13:37:17.092Z\",\n" +
                "  \"completed\": true\n" +
                "}";
        given()
                .contentType(ContentType.JSON)
                .body(activity1)
                .when()
                .post("api/v1/Activities")
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("title", equalTo("activity1"))
                .body("dueDate", equalTo("2025-07-25T13:37:17.092Z"))
                .body("completed", equalTo(true));
        given()
                .contentType(ContentType.JSON)
                .body(json)
                .when()
                .post("api/v1/Activities")
                .then()
                .statusCode(200)
                .body("id", equalTo(2))
                .body("title", equalTo("activity2"))
                .body("dueDate", equalTo("2025-07-25T13:37:17.092Z"))
                .body("completed", equalTo(true));

    }

    @Test
    @Order(2)
    public void get() {
        given()
                .when()
                .get("api/v1/Activities/1")
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("title", equalTo("activity1"))
                .body("dueDate", equalTo("2025-07-25T13:37:17.092Z"))
                .body("completed", equalTo(true));

        given()
                .get("api/v1/Activities/2")
                .then()
                .statusCode(200)
                .body("id", equalTo(2))
                .body("title", equalTo("activity2"))
                .body("dueDate", equalTo("2025-07-25T13:37:17.092Z"))
                .body("completed", equalTo(true));

    }
}
