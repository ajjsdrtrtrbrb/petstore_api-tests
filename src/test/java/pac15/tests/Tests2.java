package pac15.tests;

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

public class Tests2 extends BaseClass {
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void test1() throws JsonProcessingException {
        ApiOrder apiOrder = new ApiOrder(orderSpec);
        Order order = new Order(5, 5, 5, "2025-08-26T08:47:47.397Z", "placed", true);
        Order created = apiOrder.createOrder(order);
        Assertions.assertAll("проверка полей order",
                () -> assertEquals(5, created.getId(), "неправильный id"),
                () -> assertEquals(5, created.getPetId(), "неправильный petId"),
                () -> assertEquals(5, created.getQuantity(), "неправильный Quantity"),
                () -> assertEquals("2025-08-26T08:47:47.397+0000", created.getShipDate(), "wrong Quantity"),
                () -> assertEquals("placed", created.getStatus(), "неправильній статус"),
                () -> assertTrue(created.isComplete(), "wrong"));
        int id = created.getId();
        Order orderByGet = apiOrder.getOrderById(order.getId());
        Assertions.assertAll("проверка полей order",
                () -> assertEquals(5, orderByGet.getId(), "неправильный id"),
                () -> assertEquals(5, orderByGet.getPetId(), "неправильный petId"),
                () -> assertEquals(5, orderByGet.getQuantity(), "неправильный Quantity"),
                () -> assertEquals("2025-08-26T08:47:47.397+0000", orderByGet.getShipDate(), "wrong Quantity"),
                () -> assertEquals("placed", orderByGet.getStatus(), "неправильній статус"),
                () -> assertTrue(orderByGet.isComplete(), "wrong"));
        ApiResponse deleteResponse = apiOrder.deleteById(order.getId());
        Assertions.assertEquals(200, deleteResponse.getCode());
        Assertions.assertEquals("unknown", deleteResponse.getType());
        Assertions.assertFalse(deleteResponse.getMessage().isEmpty());
        String json = "{\n" +
                "  \"id\": 8,\n" +
                "  \"petId\": 8,\n" +
                "  \"quantity\": 8,\n" +
                "  \"shipDate\": \"2025-08-26T08:47:47.397Z\",\n" +
                "  \"status\": \"placed\",\n" +
                "  \"complete\": true\n" +
                "}";
        Response response =
                given()
                        .spec(orderSpec)
                        .body(json)
                        .when()
                        .post()
                        .then()
                        .statusCode(200)
                        .extract().response();
        Order createdByJson = mapper.readValue(response.asString(), Order.class);
        Assertions.assertEquals(8, createdByJson.getId());

       given()
                .spec(orderSpec)
                .pathParam("id", createdByJson.getId())
                .when()
                .delete("/{id}")
                .then()
                .statusCode(200);

        Response response1 =
                given()
                        .spec(orderSpec)
                        .pathParam("id", createdByJson.getId())
                        .when()
                        .get("/{id}")
                        .then()
                        .extract().response();
        if(response1.getStatusCode()==404){
            assertEquals(404, response1.statusCode());
        }
        else if(response1.getStatusCode()==200) {
            Order order1=mapper.readValue(response1.asString(),Order.class);
            Assertions.assertAll("check",
                    () -> assertEquals(8, order1.getId()),
                    () -> assertEquals(8,order1.getPetId()),
                    () -> assertEquals(8, order1.getQuantity())
            );
        }
        else {
            throw new RuntimeException("unknown status");
        }

        Response response2 =
                given()
                        .spec(orderSpec)
                        .pathParam("id", createdByJson.getId())
                        .when()
                        .get("/{id}")
                        .then()
                        .extract()
                        .response();
        Assertions.assertEquals(404,response2.statusCode());



    }
    @Test
    public void test2() throws JsonProcessingException {
        Order order = new Order(5, 5, 5,
                "2025-08-26T08:47:47.397Z", "placed", true);
        Response response=
                given()
                        .spec(orderSpec)
                        .body(order)
                        .when()
                        .post()
                        .then()
                        .statusCode(200)
                        .extract().response();
        JsonNode node=mapper.readTree(response.asString());
        Assertions.assertTrue(node.has("id"));
        int id=node.get("id").asInt();
        Assertions.assertEquals(id,response.as(Order.class).getId());
        String array="[\n" +
                "  {\n" +
                "    \"id\": 1,\n" +
                "    \"petId\": 101,\n" +
                "    \"quantity\": 2,\n" +
                "    \"shipDate\": \"2025-08-26T09:00:00.000Z\",\n" +
                "    \"status\": \"placed\",\n" +
                "    \"complete\": true\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 2,\n" +
                "    \"petId\": 102,\n" +
                "    \"quantity\": 1,\n" +
                "    \"shipDate\": \"2025-08-27T15:30:00.000Z\",\n" +
                "    \"status\": \"approved\",\n" +
                "    \"complete\": false\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 3,\n" +
                "    \"petId\": 103,\n" +
                "    \"quantity\": 5,\n" +
                "    \"shipDate\": \"2025-08-28T12:15:00.000Z\",\n" +
                "    \"status\": \"delivered\",\n" +
                "    \"complete\": true\n" +
                "  }\n" +
                "]\n";
        List<Order>list=mapper.readValue(array, new TypeReference<List<Order>>() {
        });
        for(Order s:list){
            Assertions.assertTrue(s.getId()>0);
            Assertions.assertTrue(s.getQuantity()>0);
        }
        Response response1=Mockito.mock(Response.class);
        Mockito.when(response1.getStatusCode()).thenReturn(200);
        Mockito.when(response1.asString()).thenReturn(array);
        Assertions.assertEquals(200,response1.getStatusCode());
        Assertions.assertEquals(array,response1.asString());


    }
    @ParameterizedTest
    @ValueSource(ints = {5})
    public void test3(int id){
        ApiOrder apiOrder=new ApiOrder(orderSpec);
        Order order = new Order(5, 5, 5, "2025-08-26T08:47:47.397Z", "placed", true);

        Order created=apiOrder.createOrder(order);
        Assertions.assertEquals(order,created);
        given()
                .spec(orderSpec)
                .pathParam("id",id)
                .when()
                .get("/{id}")
                .then()
                .statusCode(200);
        given()
                .spec(orderSpec)
                .pathParam("id",created.getId())
                .when()
                .delete("/{id}")
                .then()
                .statusCode(200);
        Response response=
                given()
                        .spec(orderSpec)
                        .pathParam("id",id)
                        .when()
                        .get("/{id}")
                        .then()
                        .extract().response();
        int code=response.getStatusCode();
        if(code==200){
            Assertions.assertAll("check",
                    ()->assertEquals(5,new ObjectMapper().readValue(response.asString(),Order.class).getId()),
                    ()->assertEquals(5,new ObjectMapper().readValue(response.asString(),Order.class).getPetId()),
                    ()->assertEquals(5,new ObjectMapper().readValue(response.asString(),Order.class).getQuantity())
                    );
        }
        else {
            System.out.println(code);
            Assertions.assertEquals(404,code);
        }
    }
    @Test
    public void test4(){
        String json="{\n" +
                "  \"id\": 0,\n" +
                "  \"petId\": test,\n" +
                "  \"quantity\": -1,\n" +
                "  \"shipDate\": \"2025-08-26T08:47:47.397Z\",\n" +
                "  \"status\": \"placed\",\n" +
                "  \"complete\": true\n" +
                "}";
        Response response=
        given()
                .spec(orderSpec)
                .body(json)
                .when()
                .post()
                .then()
                .extract().response();
        int code=response.getStatusCode();
        System.out.println(code);
        System.out.println(response.asString());
        Assertions.assertEquals(400,code);
    }
    @ParameterizedTest
    @ValueSource(ints = {1,2,3,4,5,6,7,8,9,10})
    public void test5(int id) throws JsonProcessingException {
        ApiOrder apiOrder=new ApiOrder(orderSpec);
        List<Order>list=new ArrayList<>();
        Order order=apiOrder.getOrderById(id);
        list.add(order);
        for(Order s:list){
            Assertions.assertEquals(id,s.getId());
        }
    }
    @Test
    public void test6() throws JsonProcessingException {
        RestAssured.baseURI="https://petstore.swagger.io";
        RestAssured.basePath="/v2/store/inventory";
        Response response=
                given()
                        .when()
                        .get()
                        .then()
                        .statusCode(200)
                        .extract().response();
        Map<String,Integer>map=mapper.readValue(response.asString(), new TypeReference<Map<String, Integer>>() {
        });
        Assertions.assertTrue(!map.isEmpty(),"should be not empty");
        Assertions.assertTrue(map.containsKey("available"));
        Assertions.assertTrue(map.get("available")>0);
    }
}
