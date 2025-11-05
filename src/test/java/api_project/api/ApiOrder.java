package api_project.api;

import api_project.base.BaseClass;
import api_project.model.ApiResponse;
import api_project.model.Order;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.junit.jupiter.api.Assertions;

import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Класс ApiOrder отвечает за работу с API раздела "Store" (заказы и инвентарь).
 * Содержит методы для:
 *  - проверки инвентаря,
 *  - создания заказа (через объект и JSON),
 *  - получения заказа по ID,
 *  - удаления заказа,
 *  - проверки негативных сценариев (400, 404, 500) и моков.
 * Использует REST-assured, Jackson и Allure для отчётности.
 */
public class ApiOrder extends BaseClass {

    private RequestSpecification specOrder;
    private RequestSpecification inventorySpec;
    private ResponseSpecification baseResponse;
    private ResponseSpecification orderResponse;
    private ResponseSpecification mockSuccessSpec;
    private ResponseSpecification mockBadRequestSpec;
    private ResponseSpecification mockNotFoundSpec;
    private ResponseSpecification mockInternalErrorSpec;
    private ObjectMapper mapper;

    /** Базовый конструктор с минимальным набором зависимостей. */
    public ApiOrder(RequestSpecification specOrder, RequestSpecification inventorySpec,
                    ResponseSpecification baseResponse, ResponseSpecification orderResponse,
                    ObjectMapper mapper) {
        if (specOrder == null || inventorySpec == null || baseResponse == null || orderResponse == null || mapper == null) {
            throw new IllegalArgumentException("Все спеки и mapper должны быть переданы в конструктор!");
        }
        this.specOrder = specOrder;
        this.inventorySpec = inventorySpec;
        this.baseResponse = baseResponse;
        this.orderResponse = orderResponse;
        this.mapper = mapper;
    }

    /** Полный конструктор с моками и дополнительными спецификациями. */
    public ApiOrder(RequestSpecification specOrder,
                    RequestSpecification inventorySpec,
                    ResponseSpecification baseResponseSpecification,
                    ResponseSpecification orderCreateResponseSpecification,
                    ResponseSpecification apiResponseSpecification,
                    ResponseSpecification mockBadRequestSpec,
                    ResponseSpecification mockNotFoundSpec,
                    ResponseSpecification mockInternalErrorSpec,
                    ObjectMapper mapper) {

        if (specOrder == null || inventorySpec == null || baseResponseSpecification == null
                || orderCreateResponseSpecification == null || apiResponseSpecification == null
                || mockBadRequestSpec == null || mockNotFoundSpec == null
                || mockInternalErrorSpec == null || mapper == null) {
            throw new IllegalArgumentException("Все спеки и mapper должны быть переданы в конструктор!");
        }

        this.specOrder = specOrder;
        this.inventorySpec = inventorySpec;
        this.baseResponseSpecification = baseResponseSpecification;
        this.orderCreateResponseSpecification = orderCreateResponseSpecification;
        this.apiResponseSpecification = apiResponseSpecification;
        this.mockBadRequestSpec = mockBadRequestSpec;
        this.mockNotFoundSpec = mockNotFoundSpec;
        this.mockInternalErrorSpec = mockInternalErrorSpec;
        this.mapper = mapper;
    }

    /** Конструктор только для моков (без inventory). */
    public ApiOrder(RequestSpecification specOrder,
                    ResponseSpecification mockSuccessSpec,
                    ResponseSpecification mockBadRequestSpec,
                    ResponseSpecification mockNotFoundSpec,
                    ResponseSpecification mockInternalErrorSpec,
                    ObjectMapper mapper) {
        if (specOrder == null || mockSuccessSpec == null || mockBadRequestSpec == null
                || mockNotFoundSpec == null || mockInternalErrorSpec == null || mapper == null) {
            throw new IllegalArgumentException("Все спеки и mapper должны быть переданы в конструктор!");
        }
        this.specOrder = specOrder;
        this.mockSuccessSpec = mockSuccessSpec;
        this.mockBadRequestSpec = mockBadRequestSpec;
        this.mockNotFoundSpec = mockNotFoundSpec;
        this.mockInternalErrorSpec = mockInternalErrorSpec;
        this.mapper = mapper;
    }

    /** Позволяет задать спецификацию для inventory-запросов отдельно. */
    public void setInventorySpec(RequestSpecification inventorySpec){
        this.inventorySpec = inventorySpec;
    }

    /** Проверка получения инвентаря без валидации содержимого. */
    @Step("get inventory")
    public void getInventory1() {
        RestAssured.given()
                .spec(inventorySpec)
                .when()
                .get()
                .then()
                .spec(baseResponseSpecification);
    }

    /** Получение инвентаря в виде карты Map<String, Integer>. */
    @Step("get inventory with checking fields")
    public Map<String, Integer> getInventory2() {
        Map<String, Integer> map =
                RestAssured.given()
                        .spec(inventorySpec)
                        .when()
                        .get()
                        .then()
                        .extract()
                        .as(new TypeRef<Map<String, Integer>>() {});
        return map;
    }

    /** Получение инвентаря с использованием JsonPath. */
    @Step("get inventory by Jsonpath")
    public JsonPath getInventory3() {
        return RestAssured.given()
                .spec(inventorySpec) // заменено с orderInventorySpec
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract().jsonPath();
    }

    /** Проверка, что ответ inventory содержит ключи "available", "pending", "sold". */
    @Step("get inventory keys check")
    public void getInventory4() {
        Response response = RestAssured.given()
                .spec(inventorySpec)
                .when()
                .get()
                .then()
                .spec(baseResponseSpecification)
                .extract().response();
        JsonPath jsonPath = response.jsonPath();
        assertNotNull(jsonPath.get("available"), "available is absent");
        assertNotNull(jsonPath.get("pending"), "pending is absent");
        assertNotNull(jsonPath.get("sold"), "sold is absent");
    }

    /** Альтернативная проверка ключей через body() и Hamcrest matchers. */
    @Step("get inventory keys check2")
    public void getInventory5() {
        RestAssured.given()
                .spec(inventorySpec)
                .when()
                .get()
                .then()
                .spec(baseResponseSpecification)
                .body("$", hasKey("pending"))
                .body("$", hasKey("sold"))
                .body("$", hasKey("available"));
    }

    /** Создание заказа, передавая объект Order в теле запроса. */
    @Step("Create order with object")
    public Order orderCreateWithObject(Order order) throws JsonProcessingException {
        Response response = RestAssured.given()
                .spec(specOrder)
                .body(order)
                .when()
                .post()
                .then()
                .spec(orderCreateResponseSpecification)
                .body("$", hasKey("id"))
                .body("$", hasKey("petId"))
                .extract().response();
        return mapper.readValue(response.asString(), Order.class);
    }

    /** Создание заказа, передавая JSON-строку. Проверяет код ответа и возвращает Order. */
    @Step("Create order with json")
    public Order orderCreateWithJson(String json) throws JsonProcessingException {
        Response response = RestAssured.given()
                .spec(specOrder)
                .body(json)
                .when()
                .post()
                .then()
                .extract().response();
        if(response.getStatusCode()!=200){ // для моков
            throw new RuntimeException(response.jsonPath().getString("message")+" code "+response.getStatusCode());
        }
        return mapper.readValue(response.asString(), Order.class);
    }

    /** Получает заказ по его ID. При ошибке выбрасывает исключение с сообщением. */
    @Step("get order")
    public Order getOrder(int id) throws JsonProcessingException {
        Response response = RestAssured.given()
                .spec(specOrder)
                .pathParam("id", id)
                .when()
                .get("/{id}")
                .then()
                .extract().response();
        if(response.getStatusCode()!=200){
            throw new RuntimeException(response.jsonPath().getString("message"));
        }
        return mapper.readValue(response.asString(), Order.class);
    }

    /** Удаляет заказ по ID. При моках допускается 404. */
    @Step("delete order")
    public ApiResponse orderDelete(int id) throws JsonProcessingException {
        Response response = RestAssured.given()
                .spec(specOrder)
                .pathParam("id", id)
                .when()
                .delete("/{id}")
                .then()
                .statusCode(anyOf(is(200),is(404))) // 404 добавлен для моков
                .extract().response();
        return mapper.readValue(response.asString(), ApiResponse.class);
    }

    /** Проверка удаления несуществующего заказа (404). */
    @Step("delete 404")
    public ApiResponse delete404(int id) throws JsonProcessingException {
        Response response = RestAssured.given()
                .spec(specOrder)
                .pathParam("id", id)
                .when()
                .delete("/{id}")
                .then()
                .spec(errorResponseResponseSpecification)
                .extract().response();
        return mapper.readValue(response.asString(), ApiResponse.class);
    }

    /** Проверка получения 404 при запросе несуществующего заказа. */
    @Step("get 404")
    public int get404(int id) {
        Response response = RestAssured.given()
                .spec(specOrder)
                .pathParam("id", id)
                .when()
                .get("/{id}")
                .then()
                .spec(errorResponseResponseSpecification)
                .extract().response();
        return response.getStatusCode();
    }

    /** Проверка 400 при создании заказа с некорректным телом запроса. */
    @Step("post 400")
    @Severity(SeverityLevel.CRITICAL)
    public int post400(String json) {
        Response response = RestAssured.given()
                .spec(specOrder)
                .body(json)
                .when()
                .post()
                .then()
                .extract().response();
        return response.getStatusCode();
    }
}
