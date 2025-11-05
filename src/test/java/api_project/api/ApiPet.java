package api_project.api;

import api_project.base.BaseClass;
import api_project.enums.PetStatus;
import api_project.model.ApiResponse;
import api_project.model.ErrorResponse;
import api_project.model.Pet;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.junit.jupiter.api.Assertions;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Класс ApiPet содержит методы для работы с API PetStore:
 * создание, обновление, получение, удаление питомцев, а также
 * тесты на негативные сценарии и проверки с моками.
 * Использует REST-assured, Jackson (ObjectMapper) и Allure для шагов отчёта.
 */
public class ApiPet extends BaseClass {
    private RequestSpecification petSpec;
    private RequestSpecification petSpecUploadImage;
    private ResponseSpecification petCreateResponseSpecification;
    private ResponseSpecification apiResponseSpecification;
    private ObjectMapper mapper;

    /**
     * Конструктор с минимальным набором параметров.
     * Используется, если не требуется загрузка изображений или специальные спецификации.
     */
    public ApiPet(RequestSpecification petSpec) {
        if (petSpec == null) {
            throw new IllegalArgumentException("Все спеки и mapper должны быть переданы в конструктор!");
        }
        this.petSpec = petSpec;
        mapper = new ObjectMapper();
    }

    /**
     * Конструктор с полным набором зависимостей.
     * Используется для полного контроля над спецификациями и сериализацией.
     */
    public ApiPet(RequestSpecification petSpec, RequestSpecification petSpecUploadImage,
                  ResponseSpecification petCreateResponseSpecification, ResponseSpecification apiResponseSpecification,
                  ObjectMapper mapper) {
        if (petSpec == null || petSpecUploadImage == null
                || petCreateResponseSpecification == null || apiResponseSpecification == null
                || mapper == null) {
            throw new IllegalArgumentException("Все спеки и mapper должны быть переданы в конструктор!");
        }
        this.petSpec = petSpec;
        this.petSpecUploadImage = petSpecUploadImage;
        this.petCreateResponseSpecification = petCreateResponseSpecification;
        this.apiResponseSpecification = apiResponseSpecification;
        this.mapper = mapper;
    }

    /** Устанавливает ResponseSpecification для создания питомца. */
    public void setResponseSpecification(ResponseSpecification responseSpecification) {
        this.petCreateResponseSpecification = responseSpecification;
    }

    /**
     * Загружает изображение для питомца.
     * Отправляет multipart-запрос с метаданными и файлом.
     */
    @Step("Upload image for pet with id = {id}")
    public ApiResponse uploadPetImage(int id, String additionalMetadata, File file) throws JsonProcessingException {
        Response response = RestAssured.given()
                .spec(petSpecUploadImage)
                .pathParam("id", id)
                .multiPart("additionalMetadata", additionalMetadata)
                .multiPart("file", file, "image/png")
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract().response();
        return mapper.readValue(response.asString(), ApiResponse.class);
    }

    /**
     * Создаёт питомца, передавая объект Pet в теле запроса.
     * Проверяет совпадение имени и статуса.
     */
    @Step("create pet with id = {pet.id}")
    public Pet createPetWithObject(Pet pet) {
        Response response = RestAssured.given()
                .spec(petSpec)
                .contentType(ContentType.JSON)
                .body(pet)
                .log().all()
                .when()
                .post()
                .then()
                .log().all()
                .statusCode(200)
                .spec(petCreateResponseSpecification)
                .extract().response();

        // Проверяем поля name и status в ответе
        Assertions.assertEquals(response.jsonPath().getString("name"), pet.getName());
        Assertions.assertEquals(response.jsonPath().getString("status"), pet.getStatus());

        return response.as(Pet.class);
    }

    /**
     * Создаёт питомца, передавая JSON-строку.
     * Извлекает id, name и status из JSON перед отправкой.
     */
    @Step("create pet with id extracted from json")
    public Pet createPetWithJson(String json) {
        Pet createdPet = RestAssured.given()
                .spec(petSpec)
                .body(json)
                .when()
                .post()
                .then()
                .statusCode(200)
                .spec(petCreateResponseSpecification)
                .extract().as(Pet.class);
        return createdPet;
    }

    /**
     * Обновляет питомца методом PUT.
     */
    @Step("put pet with id {pet.id}")
    public Pet putPet(Pet pet) {
        return RestAssured.given()
                .spec(petSpec)
                .body(pet)
                .when()
                .put()
                .then()
                .statusCode(200)
                .spec(petCreateResponseSpecification)
                .extract().as(Pet.class);
    }

    /**
     * Получает список питомцев по статусу (available, pending, sold).
     * В случае 404 или другого кода — выбрасывает исключение.
     */
    @Step("get pet by status: {status}")
    public List<Pet> getByStatus(PetStatus status) throws JsonProcessingException {
        Response response = RestAssured.given()
                .spec(petSpec)
                .queryParam("status", status.name())
                .when()
                .get("/findByStatus")
                .andReturn();

        if (response.getStatusCode() == 404)
            throw new RuntimeException("pet with status " + status + " not found");
        else if (response.getStatusCode() != 200)
            throw new RuntimeException("unexpected statusCode " + response.getStatusCode());

        return Arrays.asList(mapper.readValue(response.asString(), Pet[].class));
    }

    /**
     * Получает питомца по его id.
     * Возвращает объект Pet, либо выбрасывает исключение при ошибке.
     */
    @Step("get pet by id: {id}")
    public Pet getById(int id) throws JsonProcessingException {
        Response response = RestAssured.given()
                .spec(petSpec)
                .pathParam("id", id)
                .when()
                .get("/{id}")
                .andReturn();

        if (response.getStatusCode() == 404)
            throw new RuntimeException("pet with " + id + " not found");
        else if (response.getStatusCode() != 200)
            throw new RuntimeException("Unexpected status code: " + response.getStatusCode());

        return mapper.readValue(response.asString(), Pet.class);
    }

    /**
     * Создаёт питомца и затем обновляет его через form-data.
     * Проверяет, что обновление прошло успешно.
     */
    @Step("Сreate/Update pet with id: {id}, name: {name}, status: {status}")
    public void createUpdateWithFormPet(Pet pet, int id, String name, PetStatus status) throws JsonProcessingException {
        Pet created = createPetWithObject(pet);
        Response response = RestAssured.given()
                .spec(petSpec)
                .contentType("application/x-www-form-urlencoded")
                .pathParam("id", id)
                .formParam("name", name)
                .formParam("status", status.name())
                .when()
                .post("/{id}")
                .then()
                .statusCode(200)
                .extract().response();

        ApiResponse apiResponse = response.as(ApiResponse.class);

        Assertions.assertAll("check",
                () -> assertEquals(200, apiResponse.getCode()),
                () -> assertEquals("unknown", apiResponse.getType()),
                () -> assertEquals(String.valueOf(id), apiResponse.getMessage())
        );

        Pet updated = getById(id);
        Assertions.assertAll("check pet",
                () -> assertEquals(id, updated.getId()),
                () -> assertEquals(name, updated.getName()),
                () -> assertEquals(status, updated.getStatus())
        );
    }

    /** Удаляет питомца по id, возвращая ApiResponse. */
    @Step("Delete pet with id: {id}")
    public ApiResponse deletePet(int id, String apiKey) throws JsonProcessingException {
        Response response = RestAssured.given()
                .spec(petSpec)
                .pathParam("id", id)
                .header("api_key", apiKey)
                .when()
                .delete("/{id}")
                .then()
                .statusCode(200)
                .extract().response();
        return mapper.readValue(response.asString(), ApiResponse.class);
    }

    /** Проверка получения 404 при запросе несуществующего питомца. */
    @Step("get 404 pet with {id}")
    public void get404(int id) {
        Response response = RestAssured.given()
                .spec(petSpec)
                .pathParam("id", id)
                .when()
                .get("/{id}")
                .then()
                .statusCode(404)
                .extract().response();
        ApiResponse apiResponse = response.as(ApiResponse.class);
        Assertions.assertAll("check",
                () -> assertEquals(1, apiResponse.getCode()),
                () -> assertEquals("error", apiResponse.getType()),
                () -> assertEquals("Pet not found", apiResponse.getMessage())
        );
    }

    /** Проверка создания питомца с некорректным телом запроса (ожидается 400). */
    @Step("Create pet 400")
    public void createPet400(String json) {
        RestAssured.given()
                .spec(petSpec)
                .body(json)
                .log().all()
                .when()
                .post()
                .then()
                .log().all()
                .statusCode(400);
    }

    /** Мок для проверки ответа 400 при создании питомца. */
    @Step("create pet 400 for mock")
    public ErrorResponse mockCreate404(String json) throws JsonProcessingException {
        Response response = RestAssured.given()
                .spec(petSpec)
                .body(json)
                .when()
                .post()
                .then()
                .spec(BaseClass.mockBadRequestSpec)
                .extract().response();
        return mapper.readValue(response.asString(), ErrorResponse.class);
    }

    /** Мок для проверки ответа 500 при создании питомца. */
    @Step("create pet for mock 500")
    public ErrorResponse mockCreate500(String json) throws JsonProcessingException {
        Response response = RestAssured.given()
                .spec(petSpec)
                .body(json)
                .when()
                .post()
                .then()
                .spec(BaseClass.mockInternalErrorSpec)
                .extract().response();
        return mapper.readValue(response.asString(), ErrorResponse.class);
    }

    /** Мок для проверки 400 при запросе PUT. */
    @Step("put 400 for mock")
    public ErrorResponse mockPut400(String json) throws JsonProcessingException {
        Response response = RestAssured.given()
                .spec(petSpec)
                .body(json)
                .with()
                .put()
                .then()
                .spec(BaseClass.mockBadRequestSpec)
                .extract().response();
        return mapper.readValue(response.asString(), ErrorResponse.class);
    }

    /** Мок для проверки 400 при удалении питомца. */
    @Step("delete 400 for mock")
    public ErrorResponse mockDelete400(int id) throws JsonProcessingException {
        Response response = RestAssured.given()
                .spec(petSpec)
                .pathParam("id", id)
                .when()
                .delete("/{id}")
                .then()
                .spec(BaseClass.mockBadRequestSpec)
                .extract().response();
        return mapper.readValue(response.asString(), ErrorResponse.class);
    }

    /** Проверка, что при удалении несуществующего питомца возвращается 404. */
    @Step("delete pet 404")
    public void delete404(int id, String api_key) {
        Response response = RestAssured.given()
                .spec(petSpec)
                .header("api_key", api_key)
                .pathParam("id", id)
                .when()
                .get("/{id}")
                .then()
                .extract().response();
        Assertions.assertEquals(404, response.getStatusCode());
    }
}
