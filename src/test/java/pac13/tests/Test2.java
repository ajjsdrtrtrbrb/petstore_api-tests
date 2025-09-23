package pac13.tests;

import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import pac13.model.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;

import org.junit.jupiter.api.Test;
import pac13.base.BaseClass;
import io.restassured.response.Response;
import org.mockito.Mockito;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import static io.restassured.RestAssured.given;

public class Test2 extends BaseClass {
    private  ApiUser apiUser;
    private OrderApi orderApi;

    @BeforeEach
    public void init(){
        apiUser = new ApiUser(spec);

    }
    @BeforeEach
    public void init2(){
        orderApi=new OrderApi(specOrder);
    }
    @Test
    public void test1() throws JsonProcessingException {
        String json = "{\n" +
                "  \"id\": 88,\n" +
                "  \"username\": \"dinamo\",\n" +
                "  \"firstName\": \"kiev\",\n" +
                "  \"lastName\": \"ukraine\",\n" +
                "  \"email\": \"dinamo@gmail.com\",\n" +
                "  \"password\": \"12345\",\n" +
                "  \"phone\": \"12345\",\n" +
                "  \"userStatus\": 88\n" +
                "}";
        ApiResponse createResponse =
                given()
                        .spec(spec)
                        .body(json)
                        .when()
                        .post()
                        .then()
                        .log().all()
                        .statusCode(200)
                        .extract()
                        .as(ApiResponse.class);
        Assertions.assertEquals(200, createResponse.getCode());


        Response getResponse=
                given()
                        .spec(spec)
                        .when()
                        .get("/dinamo")
                        .then()
                        .statusCode(200)
                        .extract().response();
        ObjectMapper mapper=new ObjectMapper();
        User getUser=mapper.readValue(getResponse.asString(),User.class);
        Assertions.assertEquals("dinamo",getUser.getUserName());
        Assertions.assertEquals(88,getUser.getId());
        Assertions.assertEquals("dinamo@gmail.com",getUser.getEmail());

        Response response2=
                given()
                        .spec(spec)
                        .when()
                        .get("/dinamo")
                        .then()
                        .statusCode(200)
                        .extract().response();
        JsonNode root=mapper.readTree(response2.asString());
        User user2=mapper.treeToValue(root,User.class);
        System.out.println(user2);
        Assertions.assertEquals(88,user2.getId());
        Assertions.assertEquals("dinamo",user2.getUserName());
        Assertions.assertEquals("kiev",user2.getFirstName());
        Assertions.assertEquals("ukraine",user2.getLastName());
        Assertions.assertEquals("dinamo@gmail.com",user2.getEmail());
        Assertions.assertEquals("12345",user2.getPassword());
        Assertions.assertEquals("12345",user2.getPhone());
        Assertions.assertEquals("88",user2.getUserStatus());

    }
    @Test
    public void test2(){

        User user = new User(88, "dinamo", "kiev", "ukraine",
                "dinamo@gmail.com", "12345", "12345", "88");
        ApiResponse responseCreateUser=apiUser.createUser(user);
        Assertions.assertEquals(200,responseCreateUser.getCode());
        User user2=apiUser.getUser("dinamo");
        Assertions.assertEquals(88,user2.getId());
        Assertions.assertEquals("dinamo",user2.getUserName());
        Assertions.assertEquals("kiev",user2.getFirstName());
        Assertions.assertEquals("ukraine",user2.getLastName());
        Assertions.assertEquals("dinamo@gmail.com",user2.getEmail());
        Assertions.assertEquals("12345",user2.getPassword());
        Assertions.assertEquals("12345",user2.getPhone());
        Assertions.assertEquals("88",user2.getUserStatus());
        ApiResponse apiResponse=apiUser.deleteUser("dinamo");
        Assertions.assertEquals(200,apiResponse.getCode());


    }
    @Test
    public void test3(){
        Order order=new Order(1,1,1,"2023-03-27T02:14:59.643+0000","placed",true);
        Order created=orderApi.createOrder(order);
        Assertions.assertEquals(1,created.getId());
        Order getOrder=orderApi.getOrder(1);
        Assertions.assertEquals(1,getOrder.getPetId());
        ApiResponse responseDelete=orderApi.deleteOrder(1);
        Assertions.assertEquals(200,responseDelete.getCode());
    }
    @Test
    public void test4() throws JsonProcessingException {
        int orderId=5;
        String json = "{\n" +
                "  \"id\": 5,\n" +
                "  \"petId\": 5,\n" +
                "  \"quantity\": 5,\n" +
                "  \"shipDate\": \"2023-03-27T02:14:59.643+0000\",\n" +
                "  \"status\": \"placed\",\n" +
                "  \"complete\": true\n" +
                "}";
        Order order=
                given()
                        .spec(specOrder)
                        .body(json)
                        .when()
                        .post()
                        .then()
                        .statusCode(200)
                        .extract()
                        .as(Order.class);
        Assertions.assertEquals(5,order.getId());
        Response order2=
                given()
                        .spec(specOrder)
                        .pathParams("id",orderId)
                        .when()
                        .get("/{id}")
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();
        ObjectMapper mapper=new ObjectMapper();
        Order order3=mapper.readValue(order2.asString(),Order.class);
        Assertions.assertEquals(5,order3.getId());

        ApiResponse response=
                given()
                        .spec(specOrder)
                        .pathParams("id",orderId)
                        .when()
                        .delete("/{id}")
                        .then()
                        .statusCode(200)
                        .extract()
                        .response()
                        .as(ApiResponse.class);
        Assertions.assertEquals(200,response.getCode());

    }
    @Test
    public void test5() throws JsonProcessingException {
     String json=   "[\n" +
                "  {\"id\":1,\"username\":\"user1\",\"firstName\":\"John\"},\n" +
                "  {\"id\":2,\"username\":\"user2\",\"firstName\":\"Jane\"}\n" +
                "]";
     Response response=Mockito.mock(Response.class);
     Mockito.when(response.getStatusCode()).thenReturn(200);
     Mockito.when(response.asString()).thenReturn(json);
     ObjectMapper mapper=new ObjectMapper();
     List<User>list=mapper.readValue(response.asString(), new TypeReference<List<User>>() {
     });
     for(User s:list){
         Assertions.assertTrue(s.getId()>0);
     }
    }
}
