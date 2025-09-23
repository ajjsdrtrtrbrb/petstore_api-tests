package pac2;

import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.requestSpecification;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class Tests extends BaseClass{
    @Test
    public void get(){
        given()
                .spec(spec)
                .basePath("/api/v1/Authors")
                .when()
                .get()
                .then()
                .statusCode(200)
                .body("[0].id",equalTo(1))
                .body("[1].id",equalTo(2));
    }
    @Test
    public void get2(){
       Response response= given()
                .spec(spec)
                .basePath("/api/v1/Authors")
                .when().get().andReturn();
      int statusCode= response.getStatusCode();
        System.out.println(statusCode);
        response.prettyPrint();
    }
    @Test
    public void getAuthorAndDeserialize(){
        Author author= given()
                .spec(spec)
                .basePath("/api/v1/Authors/1")
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract()
                .as(Author.class);
        Assertions.assertEquals(1,author.getId());
        Assertions.assertNotNull(author.getId());
        Assertions.assertEquals(1,author.getIdBook());
        Assertions.assertEquals("First Name 1",author.getFirstName());
        Assertions.assertEquals("Last Name 1",author.getLastName());
        System.out.println(author);



    }
    @Test
    public void getByDes(){
        Author author=
        given()
                .spec(spec)
                .basePath("/api/v1/Authors/2")
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract()
                .as(Author.class);
        Assertions.assertEquals(2,author.getId());
    }
    @ParameterizedTest
    @ValueSource(ints = {1,2,3,4,5})
    public void test(int id){
        given()
                .spec(spec)
                .basePath("/api/v1/Authors/"+id)
                .when()
                .get()
                .then()
                .statusCode(200)
                .body("id",equalTo(id));
    }
    @ParameterizedTest
    @ValueSource(ints = {6,7,8})
    public void test2(int id){
        given()
                .spec(spec)
                .basePath("/api/v1/Authors/"+id)
                .when()
                .get()
                .then()
                .statusCode(200)
                .body("id",equalTo(id))
                .body("idBook",notNullValue());
    }

    @ParameterizedTest
    @ValueSource(ints = {1,2})

    public void test3(int id){
        given()
                .spec(spec)
                .basePath("/api/v1/Authors/"+id)
                .when()
                .get()
                .then()
                .statusCode(200)
                .body("id",equalTo(id));

    }
    @Test
    public void test4(){
        Author author=
                given()
                        .spec(spec)
                        .basePath("/api/v1/Authors/1")
                        .when()
                        .get()
                        .then()
                        .statusCode(200)
                        .extract()
                        .as(Author.class);
        Assertions.assertEquals(1,author.getId());
    }
    @Test
    public void create(){
        Author author=new Author(55,88,"test1","test2");
        given()
                .spec(spec)
                .basePath("/api/v1/Authors/")
                .body(author)
                .when()
                .post()
                .then()
                .statusCode(200)
                .body("id",equalTo(55))
                .body("idBook",equalTo(88))
                .body("firstName",equalTo("test1"))
                .body("lastName",equalTo("test2"));
    }
    @Test
    public void put(){
        Author author=new Author(88,77,"fff","ggg");
        given()
                .spec(spec)
                .basePath("/api/v1/Authors/55")
                .body(author)
                .when()
                .put()
                .then()
                .statusCode(200)
                .body("id",equalTo(88))
                .body("idBook",equalTo(77))
                .body("firstName",equalTo("fff"))
                .body("lastName",equalTo("ggg"));
    }
    @ParameterizedTest
    @ValueSource(ints = {1,2,88})
    public void delete(int id){
        given()
                .spec(spec)
                .basePath("/api/v1/Authors/"+id)
                .when()
                .delete()
                .then()
                .statusCode(200);
        given()
                .spec(spec)
                .basePath("/api/v1/Authors/"+id)
                .when()
                .get()
                .then()
                .statusCode(200);
    }
}
