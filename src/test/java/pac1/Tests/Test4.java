package pac1.Tests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.joda.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import pac1.Base.BaseClass;
import pac1.Model.Activity;
import pac2.Author;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.requestSpecification;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class Test4 extends BaseClass {
    @Test
    public void getActivityById() {
        given()
                .baseUri("https://fakerestapi.azurewebsites.net/api/v1")
                .basePath("/Activities/1")
                .when()
                .get()
                .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("id", equalTo(1))
                .body("completed", equalTo(false));
    }

    @Test
    public void getActivityById2() {
        given()
                .when()
                .get("api/v1/Activities/1")
                .then()
                .statusCode(200)
                .body("id", equalTo(1));

    }

    @Test
    public void create1() {
        String localDateTime = LocalDateTime.now().toString();
        Activity activity = new Activity(15, "test", localDateTime, true);
        given()
                .baseUri("https://fakerestapi.azurewebsites.net/")
                .basePath("api/v1/Activities")
                .contentType(ContentType.JSON)
                .body(activity)
                .when()
                .post()
                .then()
                .statusCode(200)
                .body("id", equalTo(15))
                .body("title", equalTo("test"))
                .body("completed", equalTo(true))
                .body("dueDate", equalTo(localDateTime));
    }

    @Test
    public void create2() {
        String json = "{\n" +
                "  \"id\": 5,\n" +
                "  \"title\": \"test\",\n" +
                "  \"dueDate\": \"2025-07-29T07:19:14.082Z\",\n" +
                "  \"completed\": true\n" +
                "}";
        given()
                .baseUri("https://fakerestapi.azurewebsites.net/")
                .basePath("api/v1/Activities")
                .contentType(ContentType.JSON)
                .body(json)
                .when()
                .post()
                .then()
                .statusCode(200)
                .body("id", equalTo(5))
                .body("title", equalTo("test"));
    }

    @Test
    public void getAllActivities() {
        Response response = RestAssured
                .given()
                .basePath("api/v1/Activities")
                .when()
                .get()
                .andReturn();
        response.prettyPrint();
    }

    @Test
    public void getAllActivities2() {
        given()
                .basePath("api/v1/Activities")
                .when()
                .get()
                .then()
                .statusCode(200)
                .body("[0].id", notNullValue());
    }

    @Test
    public void getAllActivities3() {
        Response response = given()
                .basePath("api/v1/Activities")
                .when()
                .get()
                .then()
                .statusCode(200).extract().response();
        response.then().body("[0].title", notNullValue());

    }

    @Test
    public void put() {
        Activity activity = new Activity(16, "test1", "2025-07-29T07:38:16.805Z", true);

        given()
                .baseUri("https://fakerestapi.azurewebsites.net") // ОБЯЗАТЕЛЬНО добавить!
                .basePath("/api/v1/Activities/1")
                .contentType("application/json")
                .body(activity) // ← вот этого не хватало
                .when()
                .put()
                .then()
                .statusCode(200)
                .body("id", equalTo(16))
                .body("title", equalTo("test1"));

    }

    @Test
    public void delete() {
        given()
                .basePath("/api/v1/Activities/16")
                .when()
                .delete()
                .then()
                .statusCode(200);
    }
    @Test
    public void test1(){
        String json="{\n" +
                "  \"id\": 0,\n" +
                "  \"title\": \"string\",\n" +
                "  \"dueDate\": \"2025-07-29T07:19:14.082Z\",\n" +
                "  \"completed\": true\n" +
                "}";
        given()
                .basePath("/api/v1/Activities")
                .contentType(ContentType.JSON)
                .when()
                .patch(json)
                .then()
                .statusCode(405);
    }
    @Test
    public void get404(){
        given()
                .basePath("/api/v1/Activities/1555")
                .when()
                .get()
                .then()
                .statusCode(404);
    }


}
