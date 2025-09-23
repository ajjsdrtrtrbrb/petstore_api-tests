package pac13.tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import pac13.model.Pet;
import pac13.model.User;
import pac13.model.Order;
import pac13.model.ApiResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;

import org.junit.jupiter.api.Test;
import pac13.base.BaseClass;
import io.restassured.response.Response;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;


import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class Test1 extends BaseClass {


    @Test
    public void test1() throws JsonProcessingException {
        String json = "{\n" +
                "  \"id\": 11,\n" +
                "  \"username\": \"dinamo\",\n" +
                "  \"firstName\": \"kiev\",\n" +
                "  \"lastName\": \"fc\",\n" +
                "  \"email\": \"dinamo\",\n" +
                "  \"password\": \"12345\",\n" +
                "  \"phone\": \"12345\",\n" +
                "  \"userStatus\": \"1\"\n" +
                "}";
        Response response =
                given()
                        .spec(spec)
                        .body(json)
                        .when()
                        .post()
                        .then()
                        .statusCode(200)
                        .extract().response();
        ObjectMapper mapper = new ObjectMapper();
        ApiResponse response1 = mapper.readValue(response.asString(), ApiResponse.class);
        System.out.println(response1);
        Assertions.assertEquals(200, response1.getCode());
    }

    @ParameterizedTest
    @ValueSource(strings = {"dinamo"})
    public void test2(String username) {
        User user =
                given()
                        .spec(spec)
                        .when()
                        .get("{username}", username)
                        .then()
                        .statusCode(200)
                        .body("lastName", equalTo("fc"))
                        .extract().as(User.class);
        System.out.println(user);
        Assertions.assertEquals("kiev", user.getFirstName());
        Assertions.assertEquals("dinamo", user.getUserName());
    }


    @ParameterizedTest
    @ValueSource(strings = "jjjj")
    public void test3(String userName){
        given()
                .spec(spec)
                .when()
                .get("{username}",userName)
                .then()
                .statusCode(404);

    }
    @Test
    public void test4(){
        String json= "{\n" +
                "  \"id\": 15,\n" +
                "  \"username\": \"dinamo\",\n" +
                "  \"firstName\": \"kiev\",\n" +
                "  \"lastName\": \"fc\",\n" +
                "  \"email\": \"dinamo\",\n" +
                "  \"password\": \"12345\",\n" +
                "  \"phone\": \"12345\",\n" +
                "  \"userStatus\": \"test\"\n" +
                "}";
        given()
                .spec(spec)
                .body(json)
                .when()
                .post()
                .then()
                .statusCode(500);
    }
    @Test
    public void test5() throws JsonProcessingException {
        String json="{\n" +
                "  \"id\": 20,\n" +
                "  \"username\": \"dinamo\",\n" +
                "  \"firstName\": \"kiev\",\n" +
                "  \"lastName\": \"fc\",\n" +
                "  \"email\": \"dinamo\",\n" +
                "  \"password\": \"12345\",\n" +
                "  \"phone\": \"12345\",\n" +
                "  \"userStatus\": \"1\"\n" +
                "}";
        Response response=
                given()
                        .spec(spec)
                        .body(json)
                        .when()
                        .post()
                        .then()
                        .statusCode(200)
                        .extract().response();
        ObjectMapper mapper=new ObjectMapper();
        JsonNode root=mapper.readTree(response.asString());
        Assertions.assertTrue(root.isObject());
        int code=root.get("code").asInt();
        Assertions.assertEquals(200,code);
        String type=root.get("type").asText();
        System.out.println(type);
        String message=root.get("message").asText();
        System.out.println(message);

        Response response2=
                given()
                        .spec(spec)

                        .when()
                        .get("/dinamo")
                        .then()
                        .statusCode(200)
                        .extract().response();
        User user=mapper.readValue(response2.asString(),User.class);
        System.out.println(user);
        Assertions.assertEquals("dinamo",user.getUserName());

        String json2="{\n" +
                "  \"id\": 21,\n" +
                "  \"username\": \"dinamooo\",\n" +
                "  \"firstName\": \"test\",\n" +
                "  \"lastName\": \"fc\",\n" +
                "  \"email\": \"dinamo\",\n" +
                "  \"password\": \"12345\",\n" +
                "  \"phone\": \"12345\",\n" +
                "  \"userStatus\": \"1\"\n" +
                "}";
        Response response3=
                given()
                        .spec(spec)
                        .body(json2)
                        .when()
                        .put("/dinamo")
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();
       ApiResponse d=mapper.readValue(response3.asString(),ApiResponse.class);
       Assertions.assertEquals(200,d.getCode());

       given()
               .spec(spec)
               .when()
               .delete("/dinamooo")
               .then()
               .statusCode(200);
       given()
               .spec(spec)
               .when()
               .get("/dinamooo")
               .then()
               .statusCode(404);
    }
    @Test
    public void test6() throws JsonProcessingException {
        String jsonList = "[\n" +
                "  {\"id\":1,\"username\":\"user1\",\"firstName\":\"John\"},\n" +
                "  {\"id\":2,\"username\":\"user2\",\"firstName\":\"Jane\"}\n" +
                "]";
        ObjectMapper mapper=new ObjectMapper();
        List<User> list=mapper.readValue(jsonList, new TypeReference<List<User>>() {
        });
        Assertions.assertEquals(2,list.size());
        Assertions.assertEquals(1,list.get(0).getId());

    }
    @Test
    public void test7() throws JsonProcessingException {
        String json=  "[\n" +
                "  {\"id\":101,\"petId\":201,\"quantity\":2,\"shipDate\":\"2025-08-20T12:00:00\",\"status\":\"placed\",\"complete\":true},\n" +
                "  {\"id\":102,\"petId\":202,\"quantity\":1,\"shipDate\":\"2025-08-21T15:30:00\",\"status\":\"approved\",\"complete\":false}\n" +
                "]";
        Response response=Mockito.mock(Response.class);
        Mockito.when(response.getStatusCode()).thenReturn(200);
        Mockito.when(response.asString()).thenReturn(json);
        ObjectMapper mapper=new ObjectMapper();
        List<Order>list=mapper.readValue(response.asString(), new TypeReference<List<Order>>() {
        });
        Assertions.assertEquals(101,list.get(0).getId());
    }
    @Test
    public void test8() throws JsonProcessingException {
        String json="[\n" +
                "  {\"id\":1,\"username\":\"user1\",\"firstName\":\"John\"},\n" +
                "  {\"id\":2,\"username\":\"user2\",\"firstName\":\"Jane\"}\n" +
                "]";
        ObjectMapper mapper=new ObjectMapper();
        List<User>list=mapper.readValue(json, new TypeReference<List<User>>() {
        });
        for(User s:list){
            Assertions.assertTrue(s.getId()>0);
        }
    }
    @Test
    public void test9() throws JsonProcessingException {
        String json="[\n" +
                "  {\"id\":101,\"petId\":201,\"quantity\":2,\"shipDate\":\"2025-08-20T12:00:00\",\"status\":\"placed\",\"complete\":true},\n" +
                "  {\"id\":102,\"petId\":202,\"quantity\":1,\"shipDate\":\"2025-08-21T15:30:00\",\"status\":\"approved\",\"complete\":false}\n" +
                "]";
        ObjectMapper mapper=new ObjectMapper();
        Response response=Mockito.mock(Response.class);
        Mockito.when(response.getStatusCode()).thenReturn(200);
        Mockito.when(response.asString()).thenReturn(json);
        List<Order>list=mapper.readValue(response.asString(), new TypeReference<List<Order>>() {
        });
        for(Order s:list){
            Assertions.assertTrue(s.getId()>0);
        }

    }
    @Test
    public void test10() throws JsonProcessingException {
        String json="{\n" +
                "  \"id\": 1,\n" +
                "  \"category\": {\n" +
                "    \"id\": 1,\n" +
                "    \"name\": \"category\"\n" +
                "  },\n" +
                "  \"name\": \"dinamo\",\n" +
                "  \"photoUrls\": [\n" +
                "    \"photo\"\n" +
                "  ],\n" +
                "  \"tags\": [\n" +
                "    {\n" +
                "      \"id\": 1,\n" +
                "      \"name\": \"tag\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"status\": \"available\"\n" +
                "}";
        Response response=
                given()
                        .spec(specPet)
                        .body(json)
                        .when().post()
                        .then()
                        .statusCode(200)
                        .extract().response();
        ObjectMapper mapper=new ObjectMapper();
        Pet pet=mapper.readValue(response.asString(),Pet.class);
        Assertions.assertEquals(1,pet.getId());
        Assertions.assertEquals("dinamo",pet.getName());
        JsonNode root=mapper.readTree(response.asString());
        JsonNode tags=root.get("tags");
        List<Pet.Tags>list= Arrays.asList(mapper.readValue(tags.toString(),Pet.Tags[].class));
        for(Pet.Tags s:list){
            Assertions.assertEquals(1,s.getId());
            Assertions.assertEquals("tag",s.getName());
        }
        JsonNode photo=root.get("photoUrls");
        List<String>list1=Arrays.asList(mapper.readValue(photo.toString(),String[].class));
        Assertions.assertEquals("photo",list1.get(0));
        JsonNode category=root.get("category");
        Pet.Category category1=mapper.treeToValue(category,Pet.Category.class);
        Assertions.assertEquals(1,category1.getId());
        Assertions.assertEquals("category",category1.getName());

    }
    @Test
    public void test11() throws JsonProcessingException {
        User user = new User(1001, "testUser",
                "John", "Doe", "john@example.com",
                "12345", "1234567890", "1");
        Response response=
                given()
                        .spec(spec)
                        .body(user)
                        .when()
                        .post()
                        .then()
                        .statusCode(200)
                        .extract().response();
        ObjectMapper mapper=new ObjectMapper();
        ApiResponse apiResponse=mapper.readValue(response.asString(),ApiResponse.class);
        Assertions.assertEquals(200,apiResponse.getCode());

        Response response1=
                given()
                        .spec(spec)
                        .when()
                        .get("{username}",user.getUserName())
                        .then()
                        .statusCode(200)
                        .extract().response();
        User user1=mapper.readValue(response1.asString(),User.class);
        Assertions.assertEquals(user.getUserName(),user1.getUserName());
        JsonNode root=mapper.readTree(response1.asString());
        Assertions.assertEquals(user.getId(),root.get("id").asInt());

    }
    @Test
    public void test12() throws JsonProcessingException {
        Order order = new Order(5001, 101, 2,
                "2025-08-21T12:00:00", "placed", true);
        Response response1=
                given()
                        .spec(specOrder)
                        .body(order)
                        .when()
                        .post()
                        .then()
                        .statusCode(200)
                        .extract().response();
        ObjectMapper mapper=new ObjectMapper();
        Order apiResponse=mapper.readValue(response1.asString(),Order.class);
        Assertions.assertEquals(5001,order.getId());

        Response response2=
                given()
                        .spec(specOrder)
                        .when()
                        .get("{id}",order.getId())
                        .then()
                        .statusCode(200)
                        .extract().response();
        Order order1=mapper.readValue(response2.asString(),Order.class);
        Assertions.assertEquals(order1.getId(),order.getId());
        JsonNode root=mapper.readTree(response2.asString());
        Assertions.assertEquals(order.getPetId(),root.get("petId").asInt());



    }
    @ParameterizedTest
    @ValueSource(strings = {"placed","approved","delivered"})
    public void getStatus(String status) throws JsonProcessingException {
        Order order = new Order(5000 + (int)(Math.random()*100), 200
                , 1, "2025-08-22T12:00:00", status, true);
        Response response1=
                given()
                        .spec(specOrder)
                        .body(order)
                        .when()
                        .post()
                        .then()
                        .statusCode(200)
                        .extract().response();
        ObjectMapper mapper=new ObjectMapper();
        Order order1=mapper.readValue(response1.asString(), Order.class);
        Assertions.assertEquals(order.getId(),order1.getId());
        Response response2=
                given()
                        .spec(specOrder)
                        .when()
                        .get("{id}",order.getId())
                        .then()
                        .statusCode(200)
                        .extract().response();
        Order order2=mapper.readValue(response2.asString(),Order.class);
        Assertions.assertEquals(order.getId(),order2.getId());
        JsonNode root=mapper.readTree(response2.asString());
        Assertions.assertEquals(order.getStatus(),root.get("status").asText());

    }

}
