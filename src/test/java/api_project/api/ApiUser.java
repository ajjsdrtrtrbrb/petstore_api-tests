package api_project.api;

import api_project.base.BaseClass;
import api_project.model.ApiResponse;
import api_project.model.ErrorResponse;
import api_project.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;


public class ApiUser extends BaseClass {

    // --- Поля класса ---

    private RequestSpecification userSpec;                 // Основная спецификация для работы с /user эндпоинтом
    private RequestSpecification userCreateWithList;       // Спецификация для эндпоинта /user/createWithList
    private RequestSpecification userCreateWithArray;      // Спецификация для эндпоинта /user/createWithArray
    private ResponseSpecification baseResponseSpecification; // Базовая спецификация для стандартных ответов (200 OK)
    private ResponseSpecification userCreateResponseSpecification; // Спецификация для ответов при создании пользователя
    private ObjectMapper mapper;                           // Jackson ObjectMapper для десериализации JSON

    // --- Конструкторы ---

    /**
     * Основной конструктор, принимает все RequestSpecification и ResponseSpecification.
     * Используется, когда тесты требуют разных настроек для эндпоинтов /user, /createWithList и т.д.
     */
    public ApiUser(RequestSpecification userSpec,
                   RequestSpecification userCreateWithList,
                   RequestSpecification userCreateWithArray,
                   ResponseSpecification baseResponseSpecification,
                   ResponseSpecification userCreateResponseSpecification,
                   ObjectMapper mapper) {
        if (userSpec == null || userCreateWithList == null || userCreateWithArray == null
                || baseResponseSpecification == null || userCreateResponseSpecification == null
                || mapper == null) {
            throw new IllegalArgumentException("Все спеки и mapper должны быть переданы в конструктор!");
        }
        this.userSpec = userSpec;
        this.userCreateWithList = userCreateWithList;
        this.userCreateWithArray = userCreateWithArray;
        this.baseResponseSpecification = baseResponseSpecification;
        this.userCreateResponseSpecification = userCreateResponseSpecification;
        this.mapper = mapper;
    }

    /**
     * Упрощённый конструктор: принимает только userSpec и mapper.
     * Создаёт дефолтные спецификации ответов (200 OK, JSON).
     */
    public ApiUser(RequestSpecification userSpec, ObjectMapper mapper) {
        if (userSpec == null || mapper == null) {
            throw new IllegalArgumentException("Все спеки и mapper должны быть переданы в конструктор!");
        }
        this.userSpec = userSpec;
        this.mapper = mapper;

        this.baseResponseSpecification = RestAssured.expect()
                .statusCode(200)
                .contentType("application/json");

        this.userCreateResponseSpecification = RestAssured.expect()
                .statusCode(200)
                .contentType("application/json");
    }

    // --- Методы API ---

    /**
     * POST /user
     * Создаёт нового пользователя из объекта User.
     */
    @Step("create user {user.username}")
    public ApiResponse createUser(User user) throws JsonProcessingException {
        Response response = RestAssured.given()
                .spec(userSpec)
                .body(user)
                .when()
                .post()
                .then()
                .spec(baseResponseSpecification)
                .extract().response();
        return mapper.readValue(response.asString(), ApiResponse.class);
    }

    /**
     * POST /user
     * Создаёт пользователя, передавая тело запроса в виде JSON-строки.
     */
    @Step("create user with json {user.username}")
    public ApiResponse createWithJson(String json) throws JsonProcessingException {
        Response response = RestAssured.given()
                .spec(userSpec)
                .body(json)
                .when()
                .post()
                .then()
                .spec(userCreateResponseSpecification)
                .extract().response();
        return mapper.readValue(response.asString(), ApiResponse.class);
    }

    /**
     * GET /user/{username}
     * Возвращает объект User по имени пользователя.
     */
    @Step("get user {username}")
    public User getUser(String username) throws JsonProcessingException {
        Response response = RestAssured.given()
                .spec(userSpec)
                .pathParam("username", username)
                .when()
                .get("/{username}")
                .then()
                .statusCode(200)
                .extract().response();
        return mapper.readValue(response.asString(), User.class);
    }

    /**
     * GET /user/{username}
     * Получение несуществующего пользователя, возвращает ErrorResponse.
     */
    @Step("get nonexistent user {username}")
    public ErrorResponse getNonExistentUser(String username) {
        return RestAssured.given()
                .spec(userSpec)
                .pathParam("username", username)
                .when()
                .get("/{username}")
                .then()
                .extract().as(ErrorResponse.class);
    }

    /**
     * PUT /user/{username}
     * Обновляет данные пользователя.
     */
    @Step("put user {user.username}")
    public ApiResponse putUser(String username, User user) throws JsonProcessingException {
        Response response = RestAssured.given()
                .spec(userSpec)
                .pathParam("username", username)
                .body(user)
                .when()
                .put("/{username}")
                .then()
                .spec(baseResponseSpecification)
                .extract().response();
        return mapper.readValue(response.asString(), ApiResponse.class);
    }

    /**
     * DELETE /user/{username}
     * Удаляет пользователя по username.
     */
    @Step("delete user {username}")
    public ApiResponse deleteUser(String username) throws JsonProcessingException {
        Response response = RestAssured.given()
                .spec(userSpec)
                .pathParam("username", username)
                .when()
                .delete("/{username}")
                .then()
                .spec(baseResponseSpecification)
                .extract().response();
        return mapper.readValue(response.asString(), ApiResponse.class);
    }

    /**
     * GET /user/login
     * Логин пользователя с username и password (query-параметры).
     */
    @Step("login user")
    public ApiResponse loginUser(String username, String password) throws JsonProcessingException {
        Response response = RestAssured.given()
                .spec(userSpec)
                .queryParam("username", username)
                .queryParam("password", password)
                .when()
                .get("/login")
                .then()
                .spec(baseResponseSpecification)
                .extract().response();
        return mapper.readValue(response.asString(), ApiResponse.class);
    }

    /**
     * GET /user/logout
     * Выход пользователя из системы.
     */
    @Step("logout")
    public ApiResponse logout() throws JsonProcessingException {
        Response response = RestAssured.given()
                .spec(userSpec)
                .when()
                .get("/logout")
                .then()
                .spec(baseResponseSpecification)
                .extract().response();
        return mapper.readValue(response.asString(), ApiResponse.class);
    }

    /**
     * POST /user/createWithArray
     * Создание пользователей из массива объектов User.
     */
    @Step("Создать пользователей списком (array)")
    public ApiResponse createWithArray(List<User> list) throws JsonProcessingException {
        Response response = RestAssured.given()
                .spec(userCreateWithArray)
                .body(list)
                .when()
                .post()
                .then()
                .spec(baseResponseSpecification)
                .extract().response();
        return mapper.readValue(response.asString(), ApiResponse.class);
    }

    /**
     * POST /user/createWithList
     * Создание пользователей списком (List<User>).
     */
    @Step("Создать пользователей списком (list)")
    public ApiResponse createWithList(List<User> list) throws JsonProcessingException {
        Response response = RestAssured.given()
                .spec(userCreateWithList)
                .body(list)
                .when()
                .post()
                .then()
                .spec(baseResponseSpecification)
                .extract().response();
        return mapper.readValue(response.asString(), ApiResponse.class);
    }

    /**
     * GET /user/{username}
     * Проверка несуществующего пользователя (ожидается код 404).
     */
    @Step("получение несуществующего юзера, код 404")
    public ApiResponse get404(String userName) throws JsonProcessingException {
        Response response = RestAssured.given()
                .spec(userSpec)
                .pathParam("username", userName)
                .when()
                .get("/{username}")
                .then()
                .statusCode(404)
                .extract().response();
        return mapper.readValue(response.asString(), ApiResponse.class);
    }

    /**
     * DELETE /user/{username}
     * Удаление несуществующего пользователя (возврат Response без десериализации).
     */
    @Step("удаление несуществующего юзера")
    public Response delete404(String userName) {
        return RestAssured.given()
                .spec(userSpec)
                .pathParam("username", userName)
                .when()
                .delete("/{username}")
                .then()
                .extract().response();
    }

    /**
     * POST /user
     * Создание пользователя с некорректным JSON. Проверка, что возвращается 400.
     */
    @Step("создание юзера с некорректным json")
    public int createUserWithInvalidJsonReturn400(String json) {
        return RestAssured.given()
                .spec(userSpec)
                .body(json)
                .when()
                .post()
                .then()
                .extract().response().getStatusCode();
    }

    /**
     * PUT /user/{username}
     * Обновление пользователя с некорректным JSON и username.
     * Проверка, что возвращается 400.
     */
    @Step("обновление юзера с некорректным json и username")
    public int putUser400(String userName, String json) {
        Response response = RestAssured.given()
                .spec(userSpec)
                .pathParam("username", userName)
                .body(json)
                .when()
                .put("/{username}")
                .then()
                .extract().response();
        return response.getStatusCode();
    }
}
