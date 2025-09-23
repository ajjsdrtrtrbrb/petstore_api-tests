package pac4;

import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class Tests extends BaseClass{
    @Test
    public void getAllBooks1(){
        given()
                .spec(spec)
                .basePath("/api/v1/Books")
                .when()
                .get()
                .then()
                .statusCode(200)
                .body("[0].id",equalTo(1))
                .body("[1].id",equalTo(2))
                .body("[2].title",notNullValue());
    }
    @Test
    public void getAllBooks2(){
        Response response=given()
                .spec(spec)
                .basePath("/api/v1/Books")
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract().response();
        response.prettyPrint();
    }
    @Test
    public void createBook(){
        Book book=new Book(77,"title","description",5,"expert","2025-07-30T05:46:37.819Z");
        System.out.println(book);
        given()
                .spec(spec)
                .basePath("/api/v1/Books")
                .body(book)
                .when()
                .post()
                .then()
                .log().all()
                .statusCode(200)
                .body("id",equalTo(77))
                .body("title",equalTo("title"))
                .body("description",equalTo("description"))
                .body("publishDate",equalTo("2025-07-30T05:46:37.819Z"));

    }
    @Test
    public void put(){
        Book book=new Book(88,"title","description",5,"expert","2025-07-30T05:46:37.819Z");
        given()
                .spec(spec)
                .basePath("/api/v1/Books/77")
                .body(book)
                .when()
                .put()
                .then()
                .log().all()
                .statusCode(200)
                .body("id",equalTo(88));
    }
    @Test
    public void delete(){
        given()
                .spec(spec)
                .basePath("/api/v1/Books/88")
                .when()
                .delete()
                .then()
                .statusCode(200);
    }
    @Test
    public void search400(){
        given()
                .spec(spec)
                .basePath("/api/v1/Books/885498489")
                .when()
                .get()
                .then()
                .statusCode(404);
    }
    @ParameterizedTest
    @ValueSource(ints = {1,2,3})
    public void test(int id){
        given()
                .spec(spec)
                .basePath("/api/v1/Books/"+id)
                .when()
                .get()
                .then()
                .statusCode(200)
                .body("id",equalTo(id));
    }
    @Test
    public void test2(){
        Book book=given()
                .spec(spec)
                .basePath("/api/v1/Books/1")
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract()
                .as(Book.class);
        Assertions.assertEquals(1,book.getId());
    }
}
