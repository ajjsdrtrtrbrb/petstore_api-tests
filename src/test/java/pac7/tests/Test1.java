package pac7.tests;

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

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

public class Test1 extends BaseClass {
    @ParameterizedTest
    @ValueSource(ints = {1,2,3})
    public void test1(int id){
        given()
                .spec(specification)
                .basePath("/api/v1/Users")
                .when()
                .get("{id}",id)
                .then()
                .statusCode(200)
                .body("id",equalTo(id));
    }
    @ParameterizedTest
    @ValueSource(ints = {1})
    public void test2(int id){
        User user= RestAssured.given()
                .spec(specification)
                .basePath("/api/v1/Users")
                .when()
                .get("{id}",id)
                .then()
                .statusCode(200)
                .extract()
                .as(User.class);
        System.out.println(user);
        Assertions.assertEquals(1,user.getId());
        assertNotNull(user.getName());
        assertNotNull(user.getPassword());
    }
    @Test
    public void test3(){
        List<User> list=RestAssured.given()
                .spec(specification)
                .basePath("/api/v1/Users")
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .jsonPath()
                .getList("", User.class);
        for(User s:list){
            System.out.println(s);
            assertNotNull(s.getName());
            assertNotNull(s.getPassword());
        }
    }
    @ParameterizedTest
    @ValueSource(ints = {1})
    public void test4(int id){
        User user=given()
                .spec(specification)
                .basePath("/api/v1/Users")
                .when()
                .get("{id}",id)
                .then()
                .statusCode(200)
                .extract()
                .as(User.class);
        System.out.println(user);
        assertNotNull(user.getName());
        Assertions.assertEquals(1,user.getId());
    }
    @Test
    public void test5() throws JsonProcessingException {
        Response response=RestAssured.given()
                .spec(specification)
                .basePath("/api/v1/Users")
                .when()
                .get();
        List<User> users = response.as(new TypeRef<List<User>>() {});


        assertFalse(users.isEmpty(), "Список пользователей не должен быть пуст");
        assertEquals(200, response.statusCode(), "Ожидался статус 200");

        for (User user : users) {
            assertNotNull(user.getName(), "Имя пользователя не должно быть null");
            assertNotNull(user.getPassword(), "Email пользователя не должен быть null");
            assertTrue(user.getId() > 0, "ID должен быть положительным");
        }

    }

}
