package pac16.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Assertions;
import pac16.base.BaseClass;
import pac16.model.ApiResponse;
import pac16.model.Order;
import pac16.model.User;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.restassured.RestAssured.given;

public class ApiOrder extends BaseClass {
    private RequestSpecification spec1;
    private RequestSpecification spec2;

    public ApiOrder() {

    }

    public ApiOrder(RequestSpecification spec1) {
        this.spec1 = spec1;
        mapper = new ObjectMapper();
    }

    public ApiOrder(RequestSpecification spec1, RequestSpecification spec2) {
        this.spec1 = spec1;
        this.spec2 = spec2;
        mapper = new ObjectMapper();
    }

    public Map<String, Integer> getInventory() throws JsonProcessingException {
        Response response =
                given()
                        .spec(spec2)
                        .when()
                        .get()
                        .then()
                        .statusCode(200)
                        .extract().response();
        Map<String, Integer> map = mapper.readValue(response.asString(), new TypeReference<Map<String, Integer>>() {
        });
        Set<String> set = Set.of("available", "pending", "sold");
        map.keySet().retainAll(set);
        return map;
    }


    public Order createOrder(Order order) throws JsonProcessingException {
        Response response =
                given()
                        .spec(spec1)
                        .accept(ContentType.JSON)
                        .body(order)
                        .when()
                        .post()
                        .then()
                        .extract().response();
        System.out.println(response.getStatusCode());
        System.out.println(response.asString());
        return mapper.readValue(response.asString(), Order.class);
    }

    public Order createOrderFromJson(String json) {
        return given()
                .spec(spec1)
                .body(json)
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract()
                .as(Order.class);
    }

    public Order getOrderById(int id) {
        return
                given()
                        .spec(spec1)
                        .pathParam("id", id)
                        .when()
                        .get("{id}")
                        .then()
                        .statusCode(200)
                        .extract()
                        .as(Order.class);
    }

    public ApiResponse deleteOrder(int id) {
        return given()
                .spec(spec1)
                .pathParam("id", id)
                .when()
                .delete("/{id}")
                .then()
                .statusCode(200)
                .extract()
                .as(ApiResponse.class);
    }

    public ApiResponse deleteNotFount(int id) {
        return
                given()
                        .spec(spec1)
                        .pathParam("id", id)
                        .when()
                        .delete("/{id}")
                        .then()
                        .statusCode(404)
                        .extract()
                        .as(ApiResponse.class);
    }

    @Step("Создаём заказ с id = {id}, petId = {petId}, quantity = {quantity}" +
            "Получаем заказ по id = {id}" +
            "Удаляем заказ по id = {id}")
    public void createAndGetAndDelete(int id, int petId, int quantity) {
        String shipDate = Instant.now().toString();
        ;
        String body = "{ " +
                "\"id\": " + id + "," +
                "\"petId\": " + petId + "," +
                "\"quantity\": " + quantity + "," +
                "\"shipDate\": \"" + shipDate + "\"," +
                "\"status\": \"placed\"," +
                "\"complete\": true" +
                " }";
        Order order =
                given()
                        .spec(spec1)
                        .body(body)
                        .when()
                        .post()
                        .then()
                        .statusCode(200)
                        .extract().as(Order.class);
        System.out.println(order);
        Assertions.assertTrue(order.getId() > 0);
        Assertions.assertEquals(petId, order.getPetId());
        Assertions.assertEquals(quantity, order.getQuantity());
        Order getOrder =
                given()
                        .spec(spec1)
                        .pathParam("id", id)
                        .when()
                        .get("/{id}")
                        .then()
                        .statusCode(200)
                        .extract()
                        .as(Order.class);
        ApiResponse response =
                given()
                        .spec(spec1)
                        .pathParam("id", id)
                        .when()
                        .delete("/{id}", id)
                        .then()
                        .statusCode(200)
                        .extract()
                        .as(ApiResponse.class);
        Assertions.assertEquals(200, response.getCode());


    }

    @Step("получение несуществующего заказа")
    public Response get404test(int id) {
        return given()
                .spec(spec1)
                .pathParam("id", id)
                .when()
                .get("/{id}")
                .then()
                .extract().response();
    }

    @Step("удаление несуществующего заказа")
    public Response deleteOrder404(int id) {
        return given()
                .spec(spec1)
                .pathParam("id", id)
                .when()
                .delete("/{id}")
                .then()
                .extract().response();
    }

    @Step("создание заказа с неправильными полями")
    public Response createWrong(String id, String petId, String quantity) {
        String shipDate = Instant.now().toString();
        String body = "{ " +
                "\"id\": " + id + "," +
                "\"petId\": " + petId + "," +
                "\"quantity\": " + quantity + "," +
                "\"shipDate\": \"" + shipDate + "\"," +
                "\"status\": \"placed\"," +
                "\"complete\": true" +
                " }";
        return given()
                .spec(spec1)
                .body(body)
                .when()
                .post()
                .then()
                .extract().response();
    }
}
