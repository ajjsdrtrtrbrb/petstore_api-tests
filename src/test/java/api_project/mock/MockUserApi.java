package api_project.mock;

import api_project.model.ApiResponse;
import api_project.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

import java.util.List;

// Класс для работы с моками API User, наследуется от BaseMockApi
// BaseMockApi содержит общий функционал: requestSpecification и метод convert()
public class MockUserApi extends BaseMockApi {

    // Конструктор, передаем RequestSpecification и ObjectMapper
    public MockUserApi(RequestSpecification requestSpecification, ObjectMapper objectMapper) {
        super(requestSpecification, objectMapper);
    }

    // ===============================
    @Step("Get user with username: {userName}") // Шаг для Allure
    public <T> T getUser(String userName, Class<T> clazz) {
        // GET-запрос на получение пользователя по username
        Response response = given()
                .spec(requestSpecification) // Базовая спецификация
                .log().all()                // Логируем запрос
                .when()
                .get("/" + userName)        // GET /{username}
                .then()
                .extract().response();      // Получаем Response
        return convert(response, clazz);     // Конвертируем Response в нужный класс
    }

    // ===============================
    @Step("Create user (generic response)") // Создание пользователя
    public <T> T createUser(User user, Class<T> clazz) {
        try {
            String json = objectMapper.writeValueAsString(user); // Сериализация объекта User в JSON
            Response response = given()
                    .spec(requestSpecification)
                    .log().all()
                    .body(json) // Передаем JSON в тело запроса
                    .when()
                    .post()     // POST-запрос
                    .then()
                    .extract().response();
            return convert(response, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    // ===============================
    @Step("Put user with username: {userName}") // Обновление пользователя
    public <T> T putUser(User user, String userName, Class<T> clazz) {
        try {
            String json = objectMapper.writeValueAsString(user);
            Response response = given()
                    .spec(requestSpecification)
                    .log().all()
                    .body(json)
                    .when()
                    .put("/" + userName) // PUT /{username}
                    .then()
                    .extract().response();
            return convert(response, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    // ===============================
    @Step("Delete user with username: {userName}") // Удаление пользователя
    public ApiResponse deleteUser(String userName) {
        Response response = given()
                .spec(requestSpecification)
                .log().all()
                .when()
                .delete("/" + userName) // DELETE /{username}
                .then()
                .extract().response();
        return convert(response, ApiResponse.class);
    }

    // ===============================
    @Step("Login user with username: {userName} with password: {password}") // Логин пользователя
    public ApiResponse loginUser(String userName, String password) {
        Response response = given()
                .spec(requestSpecification)
                .log().all()
                .queryParam("username", userName) // Параметры запроса
                .queryParam("password", password)
                .when()
                .get("/login") // GET /login
                .then()
                .extract().response();
        return convert(response, ApiResponse.class);
    }

    // ===============================
    @Step("Logout user") // Логаут пользователя
    public ApiResponse logout() {
        Response response = given()
                .spec(requestSpecification)
                .log().all()
                .when()
                .get("/logout") // GET /logout
                .then()
                .extract().response();
        return convert(response, ApiResponse.class);
    }

    // ===============================
    @Step("Create with userList") // Создание нескольких пользователей
    public ApiResponse createWithList(List<User> list) {
        Response response = given()
                .spec(requestSpecification)
                .log().all()
                .body(list) // Передаем список пользователей в теле запроса
                .when()
                .post("/createWithList") // POST /createWithList
                .then()
                .extract().response();
        return convert(response, ApiResponse.class);
    }
}
