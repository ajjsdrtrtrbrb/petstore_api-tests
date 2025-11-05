package api_project.mock;

import api_project.model.ApiResponse;
import api_project.model.Pet;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;

// Класс для работы с моками API Pet, наследуется от BaseMockApi
// BaseMockApi содержит общий функционал: requestSpecification и метод convert()
public class MockPetApi extends BaseMockApi {

    // Конструктор, передаем RequestSpecification и ObjectMapper
    public MockPetApi(RequestSpecification requestSpecification, ObjectMapper objectMapper) {
        super(requestSpecification, objectMapper);
    }

    // ===============================
    @Step("create pet") // Шаг для Allure отчета
    public <T> T createPet(Pet pet, Class<T> clazz) {
        // POST-запрос на создание питомца
        Response response = given()
                .spec(requestSpecification)       // Используем базовую спецификацию
                .contentType("application/json") // Указываем, что тело запроса JSON
                .body(pet)                        // Передаем объект Pet в тело запроса
                .log().all()                      // Логируем все детали запроса
                .when()
                .post()                           // POST-запрос
                .then()
                .log().all()                      // Логируем ответ
                .extract().response();            // Получаем Response

        return convert(response, clazz);       // Конвертируем Response в нужный класс
    }

    // ===============================
    @Step("update pet") // Шаг для Allure
    public <T> T updatePet(Pet pet, Class<T> clazz) {
        // PUT-запрос на обновление питомца
        Response response = given()
                .spec(requestSpecification)
                .contentType("application/json")
                .body(pet)
                .log().all()
                .when()
                .put()
                .then()
                .log().all()
                .extract().response();
        return convert(response, clazz);
    }

    // ===============================
    @Step("find by status: {status}") // Общий метод поиска питомцев по статусу
    public Response findByStatus(String status) {
        Response response = given()
                .spec(requestSpecification)
                .queryParam("status", status) // Передаем статус как query параметр
                .log().all()
                .when()
                .get("/findByStatus")         // GET-запрос
                .then()
                .log().all()
                .extract().response();
        return response;
    }

    // ===============================
    @Step("Positive: find pets by valid status: {status}") // Возвращает список питомцев при валидном статусе
    public List<Pet> positiveFindByStatus(String status) {
        Response response = findByStatus(status); // Используем общий метод поиска
        try {
            Pet[] pets = objectMapper.readValue(response.asString(), Pet[].class); // Десериализация JSON в массив объектов Pet
            return Arrays.asList(pets); // Преобразуем массив в List
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    // ===============================
    @Step("Negative: find pets by invalid status: {status}") // Обработка невалидного статуса
    public ApiResponse negativeFindByStatus(String status) {
        Response response = findByStatus(status);
        return convert(response, ApiResponse.class); // Возвращаем ApiResponse с ошибкой
    }

    // ===============================
    @Step("get by id: {id}") // Получение питомца по id
    public <T> T getById(int id, Class<T> clazz) {
        Response response = given()
                .spec(requestSpecification)
                .pathParam("id", id) // Передаем id как path параметр
                .log().all()
                .when()
                .get("/{id}")       // GET-запрос по id
                .then()
                .log().all()
                .extract().response();
        return convert(response, clazz);
    }

    // ===============================
    @Step("delete by id: {id}") // Удаление питомца по id
    public <T> T deleteById(int id, Class<T> clazz) {
        Response response = given()
                .spec(requestSpecification)
                .pathParam("id", id) // Передаем id как path параметр
                .log().all()
                .when()
                .delete("/{id}")    // DELETE-запрос
                .then()
                .log().all()
                .extract().response();
        return convert(response, clazz);
    }
}
