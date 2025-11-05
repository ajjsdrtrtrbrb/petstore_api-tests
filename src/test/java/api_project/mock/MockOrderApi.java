package api_project.mock;

import api_project.model.Order;
import api_project.model.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;
import java.util.List;

/**
 * Класс для работы с мок-версией Order API
 * Наследуется от BaseMockApi, чтобы использовать requestSpecification и конвертацию JSON
 */
public class MockOrderApi extends BaseMockApi {

    public MockOrderApi(RequestSpecification requestSpecification, ObjectMapper objectMapper) {
        super(requestSpecification, objectMapper);
    }

    /**
     * Получение заказа по ID
     * @param id ID заказа
     * @param clazz Класс объекта, в который конвертируем JSON
     * @param <T> Тип возвращаемого объекта
     * @return объект типа T (например, Order или ApiResponse)
     */
    @Step("get order by id = {id}")
    public <T> T getOrderById(int id, Class<T> clazz) {
        Response response =
                given()
                        .spec(requestSpecification)
                        .pathParam("id", id)
                        .log().all()
                        .when()
                        .get("/{id}")
                        .then()
                        .log().all()
                        .extract().response();
        return convert(response, clazz);
    }

    /**
     * Создание заказа
     */
    @Step("create order")
    public <T> T createOrder(Order order, Class<T> clazz) {
        Response response =
                given()
                        .spec(requestSpecification)
                        .log().all()
                        .body(order) // REST-assured умеет сериализовать POJO в JSON
                        .when()
                        .post()
                        .then()
                        .log().all()
                        .extract().response();
        return convert(response, clazz);
    }

    /**
     * Удаление заказа по ID
     */
    @Step("delete order with id = {id}")
    public <T> T deleteOrder(int id, Class<T> clazz) {
        Response response =
                given()
                        .spec(requestSpecification)
                        .pathParam("id", id)
                        .log().all()
                        .when()
                        .delete("/{id}")
                        .then()
                        .log().all()
                        .extract().response();
        return convert(response, clazz);
    }

    /**
     * Получение инвентаря заказов
     */
    @Step("get inventory")
    public <T> T getInventory(Class<T> clazz) {
        Response response =
                given()
                        .spec(requestSpecification)
                        .log().all()
                        .when()
                        .get("/inventory")
                        .then()
                        .log().all()
                        .extract().response();
        return convert(response, clazz);
    }
}
