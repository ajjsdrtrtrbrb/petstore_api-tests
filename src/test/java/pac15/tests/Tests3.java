package pac15.tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pac15.ApiService.ApiOrder;
import pac15.base.BaseClass;
import pac15.model.Order;
import com.fasterxml.jackson.databind.JsonNode;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.internal.RequestSpecificationImpl;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pac15.ApiService.ApiOrder;
import pac15.base.BaseClass;
import pac15.model.Order;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import org.checkerframework.checker.units.qual.A;
import org.checkerframework.checker.units.qual.PolyUnit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import pac15.model.Pet;
import pac15.ApiService.*;
import pac15.base.BaseClass;
import pac15.model.ApiResponse;
import pac15.model.Order;
import pac15.model.User;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

public class Tests3 extends BaseClass {
    @Test
    public void test1() throws JsonProcessingException {
        Order order = new Order(5, 5, 5, "2025-08-26T08:47:47.397Z", "placed", true);
        ApiOrder apiOrder=new ApiOrder(orderSpec);
        Order created=apiOrder.createOrder(order);
        System.out.println(order.equals(created));
        Order getById=apiOrder.getOrderById(5);
        Assertions.assertAll("проверка полей order",
                () -> assertEquals(5, created.getId(), "неправильный id"),
                () -> assertEquals(5, created.getPetId(), "неправильный petId"),
                () -> assertEquals(5, created.getQuantity(), "неправильный Quantity"));
        ApiResponse deleteResponse=apiOrder.deleteById(5);
        Assertions.assertAll("check",
                ()->assertEquals(200,deleteResponse.getCode()),
                ()->assertEquals("unknown",deleteResponse.getType()));
        given()
                .spec(orderSpec)
                .pathParam("id",5)
                .when()
                .get("{id}")
                .then()
                .statusCode(404);
    }

    @Test
    public void userTest() throws JsonProcessingException {
        User user = new User(101, "dinamo", "John", "Doe",
                "john.doe@example.com", "12345", "555-1234", "1");
        ApiUser apiUser=new ApiUser(userSpec);
        ApiResponse responseCreated=apiUser.createUser(user);
        Assertions.assertTrue(responseCreated.getCode()==200);
        User created=apiUser.getUserByUserName("dinamo");
        Assertions.assertEquals(user,created);
        User updateUser = new User(102, "dinamo2", "John", "Doe",
                "john.doe@example.com", "12345", "555-1234", "1");
        ApiResponse responseUpdated=apiUser.updateUser("dinamo",updateUser);
        Assertions.assertEquals(200,responseUpdated.getCode());
        User updated=apiUser.getUserByUserName("dinamo2");
        Assertions.assertEquals(updateUser,updated);
        ApiResponse deleteUpdated=apiUser.deleteUserByUserName("dinamo2");
        Assertions.assertEquals(200,deleteUpdated.getCode());
        given()
                .spec(userSpec)
                .pathParam("username","dinamo2")
                .when()
                .get("/{username}")
                .then()
                .statusCode(404);

    }
    @Test
    public void test6() throws JsonProcessingException {
        String json="{\n" +
                "  \"id\": 55,\n" +
                "  \"username\": \"dinamo\",\n" +
                "  \"firstName\": \"kiev\",\n" +
                "  \"lastName\": \"ukraine\",\n" +
                "  \"email\": \"string\",\n" +
                "  \"password\": \"string\",\n" +
                "  \"phone\": \"string\",\n" +
                "  \"userStatus\": 0\n" +
                "}";
        ObjectMapper mapper=new ObjectMapper();
        ApiUser apiUser=new ApiUser(userSpec);
        Response response=
                given()
                        .spec(userSpec)
                        .body(json)
                        .when()
                        .post()
                        .then()
                        .statusCode(200)
                        .extract().response();
        ApiResponse created=mapper.readValue(response.asString(),ApiResponse.class);
        Assertions.assertEquals(200,created.getCode());
        User getId=
                given()
                        .spec(userSpec)
                        .pathParam("username","dinamo")
                        .when()
                        .get("/{username}")
                        .then()
                        .statusCode(200)
                        .extract()
                        .as(User.class);
        User user = new User(55, "dinamo2", "John", "Doe",
                "john.doe@example.com", "12345", "555-1234", "1");
        ApiResponse response1=apiUser.createUser(user);
        Assertions.assertEquals(200,response1.getCode());
        User user1=apiUser.getUserByUserName("dinamo2");
        Assertions.assertEquals(user1,user);

        String json2="  {\n" +
                "    \"id\": 1,\n" +
                "    \"username\": \"dinamo\",\n" +
                "    \"firstName\": \"John\",\n" +
                "    \"lastName\": \"Doe\",\n" +
                "    \"email\": \"john.doe@example.com\",\n" +
                "    \"password\": \"12345\",\n" +
                "    \"phone\": \"555-1234\",\n" +
                "    \"userStatus\": 1\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 2,\n" +
                "    \"username\": \"spartak\",\n" +
                "    \"firstName\": \"Jane\",\n" +
                "    \"lastName\": \"Smith\",\n" +
                "    \"email\": \"jane.smith@example.com\",\n" +
                "    \"password\": \"password\",\n" +
                "    \"phone\": \"555-5678\",\n" +
                "    \"userStatus\": 0\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 3,\n" +
                "    \"username\": \"shakhtar\",\n" +
                "    \"firstName\": \"Alex\",\n" +
                "    \"lastName\": \"Ivanov\",\n" +
                "    \"email\": \"alex.ivanov@example.com\",\n" +
                "    \"password\": \"qwerty\",\n" +
                "    \"phone\": \"555-9012\",\n" +
                "    \"userStatus\": 1\n" +
                "  }\n" +
                "]";
        List<User>list=mapper.readValue(json2, new TypeReference<List<User>>() {
        });
        for(User s:list){
            Assertions.assertTrue(s.getId()>0);
        }


    }

}
