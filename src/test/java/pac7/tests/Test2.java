package pac7.tests;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import pac7.base.BaseClass;
import pac7.model.User;

import java.util.*;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.sessionId;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;
public class Test2 extends BaseClass {
    @Test
    @Order(1)
    public void getAllUsers1(){
        Response response=
                given()
                        .spec(requestSpecification)
                        .basePath("/api/v1/Users")
                        .when()
                        .get()
                        .andReturn();
        response.prettyPrint();
    }
    @Test
    @Order(2)
    public void getAllUsers2(){
        given()
                .spec(specification)
                .basePath("/api/v1/Users")
                .when()
                .get()
                .then()
                .statusCode(200);

    }
    @Test
    public void create1(){
        String json="{\n" +
                "  \"id\": 5,\n" +
                "  \"userName\": \"string1\",\n" +
                "  \"password\": \"string2\"\n" +
                "}";
        given()
                .spec(specification)
                .basePath("/api/v1/Users")
                .body(json)
                .when()
                .post()
                .then()
                .statusCode(200)
                        .body("id",notNullValue())
                .body("id",equalTo(5))
                .body("userName",equalTo("string1"))
                .body("password",equalTo("string2"));
    }
    @Test
    public void create2(){
        Map<String,Object>map=new HashMap<>();
        map.put("id",55);
        map.put("userName","jjj");
        map.put("password","ggg");
        Response response=RestAssured
                .given()
                .spec(specification)
                .basePath("/api/v1/Users")
                .body(map)
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract().response();
        User user=response.as(User.class);
        Assertions.assertEquals(55,user.getId());
        Assertions.assertEquals("jjj",user.getName());
        Assertions.assertEquals("ggg",user.getPassword());
    }
    @Test
    public void put(){
        User user=new User(77,"dinamo","kiev");
        given()
                .spec(specification)
                .basePath("/api/v1/Users/55")
                .body(user)
                .when()
                .put()
                .then()
                .statusCode(200)
                .body("id",equalTo(77))
                .body("userName",equalTo("dinamo"))
                .body("password",equalTo("kiev"));

    }
    @Test
    public void put2(){
        User user=new User(77,"dinamo","kiev");
        Response response=
                given()
                        .spec(specification)
                        .basePath("/api/v1/Users/77")
                        .body(user)
                        .when()
                        .put()
                        .then()
                        .statusCode(200)
                        .extract().response();
        User user2=response.as(User.class);
        System.out.println(user.equals(user2));

    }
    @ParameterizedTest
    @ValueSource(ints = {77})
    public void delete(int id){
        given()
                .spec(specification)
                .basePath("/api/v1/Users")
                .when()
                .delete("{id}",id)
                .then()
                .statusCode(200);
    }
    @ParameterizedTest
    @ValueSource(ints = {1})
    public void get1(int id){
        given()
                .spec(specification)
                .basePath("/api/v1/Users")
                .when()
                .get("{id}",id)
                .then()
                .statusCode(200)
                .body("id",equalTo(1));
    }
    @ParameterizedTest
    @ValueSource(ints = {1,2,3})
    public void get2(int id){

        User user=given()
                .spec(specification)
                .basePath("/api/v1/Users")
                .when()
                .get("{id}",id)
                .then()
                .statusCode(200)
                .extract()
                .as(User.class);
        Assertions.assertEquals(id,user.getId());
    }
    @ParameterizedTest
    @ValueSource(ints = {1})
    public void get3(int id){
        User user=
                given()
                        .spec(specification)
                        .basePath("/api/v1/Users")
                        .when()
                        .get("{id}",id)
                        .then()
                        .statusCode(200)
                        .extract()
                        .as(User.class);
        Assertions.assertEquals(id,user.getId());
        Assertions.assertNotNull("userName",user.getName());
    }
    @Test
    public void test(){
        List<User>list=
                given()
                        .spec(specification)
                        .basePath("/api/v1/Users")
                        .when()
                        .get()
                        .then()
                        .statusCode(200)
                        .extract()
                        .body()
                        .jsonPath()
                        .getList("",User.class);
        for(User s:list){
            Assertions.assertNotNull(s.getId());
        }
    }
    @Test
    public void test2(){
        Response response=
                given()
                        .spec(specification)
                        .basePath("/api/v1/Users")
                        .when()
                        .get();
        List<User>list=response.as(new TypeRef<List<User>>() {});
        assertFalse(list.isEmpty(), "Список пользователей не должен быть пустым");
        for(User s:list){
            assertTrue(s.getId()>0);
            assertNotNull(s.getName());
        }
    }

}
