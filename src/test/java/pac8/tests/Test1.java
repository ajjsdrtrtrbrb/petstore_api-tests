package pac8.tests;

import org.junit.jupiter.api.Order;
import pac7.base.BaseClass;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import pac7.base.BaseClass;
import pac7.model.User;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;

public class Test1 extends pac8.base.BaseClass {
    @Test
    @Order(1)
    public void get1() {
        given()
                .spec(spec1)
                .basePath("/api/v1/Users/1")
                .when()
                .get()
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("userName", equalTo("User 1"))
                .body("password", notNullValue())
                .body("password", equalTo("Password1"));

    }

    @ParameterizedTest
    @ValueSource(ints = {1})
    public void get2(int id) {
        given()
                .spec(spec2)
                .basePath("/api/v1/Users")
                .when()
                .get("{id}", id)
                .then()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("userName", notNullValue());

    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    public void get3(int id) {
        given()
                .spec(spec2)
                .basePath("/api/v1/Users")
                .when()
                .get("{id}", id)
                .then()
                .statusCode(200)
                .body("id", equalTo(id));

    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    public void get4(int id) {
        User user =
                given()
                        .spec(spec1)
                        .basePath("/api/v1/Users")
                        .when()
                        .get("{id}", id)
                        .then()
                        .statusCode(200)
                        .extract()
                        .as(User.class);
        Assertions.assertEquals(id, user.getId());
    }

    @Test
    public void get5() {
        Response response = RestAssured.given()
                .spec(spec1)
                .basePath("/api/v1/Users")
                .when()
                .get();
        List<User> list = response.as(new TypeRef<List<User>>() {
        });
        assertEquals(200, response.statusCode());
        assertNotEquals(!list.isEmpty(), "list is empty");
        int count = 0;
        for (User s : list) {
            System.out.println(s);
            assertEquals(++count, s.getId());
        }
    }

    @Test
    public void get6() {
        Response response =
                given()
                        .spec(spec1)
                        .basePath("/api/v1/Users")
                        .get()
                        .then()
                        .statusCode(200)
                        .extract().response();

        response.prettyPrint();
    }

    @Test
    public void get7() {
        Response response = given()
                .spec(spec1)
                .basePath("/api/v1/Users")
                .when()
                .get()
                .andReturn();
        response.prettyPrint();

    }

    @Test
    public void get8() {
        given()
                .spec(spec1)
                .basePath("/api/v1/Userssss")
                .when()
                .get()
                .then()
                .statusCode(404);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    public void get9(int id) {
        User user = given()
                .spec(spec1)
                .basePath("/api/v1/Users")
                .when()
                .get("{id}", id)
                .then()
                .statusCode(200)
                .extract()
                .as(User.class);
        System.out.println(user);
        String s = "User";
        String s1 = " " + id;
        String s2 = s + s1;
        System.out.println(s2);
        Assertions.assertEquals(id, user.getId());
        Assertions.assertEquals(s2, user.getName());
    }

    @Test
    public void get10() {
        Response response =
                given()
                        .spec(spec1)
                        .basePath("/api/v1/Users")
                        .when()
                        .get();
        List<User> list = response.as(new TypeRef<List<User>>() {
        });
        assertEquals(200, response.statusCode());
        assertNotEquals(!list.isEmpty(), "empty");
        int count = 0;
        for (User s : list) {
            assertEquals(++count, s.getId());
            assertTrue(s.getId() > 0);
            System.out.println(s);
        }

    }

    @Test
    public void get11() {
        List<User> list =
                given()
                        .spec(spec1)
                        .basePath("/api/v1/Users")
                        .when()
                        .get()
                        .then()
                        .statusCode(200)
                        .extract()
                        .body()
                        .jsonPath()
                        .getList("", User.class);
        for (User s : list) {
            assertTrue(s.getId() > 0);
            assertNotNull(s.getName());
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {1})
    public void get12(int id) {
        User user = given()
                .spec(spec1)
                .basePath("/api/v1/Users")
                .when()
                .get("{id}", id)
                .then()
                .statusCode(200)
                .extract()
                .as(User.class);
        assertTrue(user.getId() > 0);
        assertNotNull(user.getId());
        assertEquals(id, user.getId());
    }

    @Test
    public void get13() {
        List<User> list =
                given()
                        .spec(spec2)
                        .basePath("/api/v1/Users")
                        .when()
                        .get()
                        .then()
                        .statusCode(200)
                        .extract()
                        .body()
                        .jsonPath()
                        .getList("", User.class);
        System.out.println(list);
        for (User s : list) {
            assertTrue(s.getId() > 0);
        }
    }

    @Test
    public void get14() {
        Response response = given()
                .spec(spec1)
                .basePath("/api/v1/Users")
                .when()
                .get();
        List<User> list = response.as(new TypeRef<List<User>>() {
        });
        int count = 0;
        for (User s : list) {
            assertTrue(s.getId() > 0);
            assertEquals(++count, s.getId());
        }
    }

    @Test
    public void post1() {
        User user = new User(55, "user1", "password1");
        given()
                .spec(spec1)
                .basePath("/api/v1/Users")
                .body(user)
                .when()
                .post()
                .then()
                .statusCode(200)
                .body("id", equalTo(55))
                .body("userName", equalTo("user1"))
                .body("password", equalTo("password1"));
        User user1 = new User(56, "user2", "password2");
        Response response = given()
                .spec(spec1)
                .basePath("/api/v1/Users/55")
                .body(user1)
                .when()
                .put()
                .then()
                .statusCode(200)
                .extract().response();
        User user2 = response.as(User.class);
        assertEquals(user1, user2);
        assertEquals(user1.getId(), user2.getId());
        assertEquals(user1.getName(), user2.getName());
        assertEquals(user1.getPassword(), user2.getPassword());

    }
    @Test
    public void post2(){
        String json="{\n" +
                "  \"id\": 33,\n" +
                "  \"userName\": \"user33\",\n" +
                "  \"password\": \"password33\"\n" +
                "}";
      Response response=  given()
                .spec(spec2)
                .basePath("/api/v1/Users")
                .body(json)
                .when()
                .post()
                .then()
                .statusCode(200)
              .extract().response();
      User user1=response.as(User.class);
      assertEquals(33,user1.getId());
      assertEquals("user33",user1.getName());
      assertEquals("password33",user1.getPassword());
      Map<String,Object>map=new HashMap<>();
      map.put("id",99);
      map.put("userName","user55");
      map.put("password","password55");


      User user2=given()
              .spec(spec1)
              .basePath("/api/v1/Users/55")
              .body(map)
              .when()
              .put()
              .then()
              .statusCode(200)
              .extract()
              .as(User.class);

      assertEquals(user2.getId(),map.get("id"));
      assertEquals(user2.getName(),map.get("userName"));
      assertEquals(user2.getPassword(),map.get("password"));
    }
    @Test
    public void delete(){
        given()
                .spec(spec1)
                .basePath("/api/v1/Users/55")
                .when()
                .delete()
                .then()
                .statusCode(200);
    }
}
