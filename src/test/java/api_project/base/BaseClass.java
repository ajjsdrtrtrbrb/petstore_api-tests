package api_project.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Allure;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.IsEqual.equalTo;

import org.junit.jupiter.api.BeforeAll;

/**
 * BaseClass — базовый класс для всех API-классов.
 * Содержит:
 *  - общие RequestSpecification для всех эндпоинтов (Pet, User, Store)
 *  - общие ResponseSpecification для стандартной валидации ответов
 *  - ObjectMapper для сериализации/десериализации JSON
 *  - метод step() для Allure шагов
 */
public class BaseClass {

    // RequestSpecification для заказа
    protected static RequestSpecification orderSpec;

    // RequestSpecification для получения инвентаря
    protected static RequestSpecification orderInventorySpec;

    // RequestSpecification для User
    protected static RequestSpecification userSpec;

    // RequestSpecification для создания пользователей списком
    protected static RequestSpecification userCreateWithList;

    // RequestSpecification для создания пользователей массивом
    protected static RequestSpecification userCreateWithArray;

    // RequestSpecification для Pet
    protected static RequestSpecification petSpec;

    // RequestSpecification для загрузки изображения питомца
    protected static RequestSpecification petSpecUploadImage;

    // Базовая ResponseSpecification (стандартная проверка 200)
    protected static ResponseSpecification baseResponseSpecification;

    // ResponseSpecification для стандартного API-ответа
    protected static ResponseSpecification apiResponseSpecification;

    // ResponseSpecification для ошибок (400/404/500)
    protected static ResponseSpecification errorResponseResponseSpecification;

    // ResponseSpecification для создания Pet
    protected static ResponseSpecification petCreateResponseSpecification;

    // ResponseSpecification для создания Order
    protected static ResponseSpecification orderCreateResponseSpecification;

    // ResponseSpecification для создания User
    protected static ResponseSpecification userCreateResponseSpecification;

    // Моки для успешного и ошибочного ответа
    public static ResponseSpecification mockSuccessSpec = RestAssured.expect().statusCode(200);
    public static ResponseSpecification mockBadRequestSpec = RestAssured.expect().statusCode(400);
    public static ResponseSpecification mockNotFoundSpec = RestAssured.expect().statusCode(404);
    public static ResponseSpecification mockInternalErrorSpec = RestAssured.expect().statusCode(500);

    // ObjectMapper для работы с JSON
    public static ObjectMapper mapper;

    /**
     * Инициализация всех спецификаций и фильтров для RestAssured.
     * Вызывается один раз перед всеми тестами.
     */
    @BeforeAll
    public static void setAll() {
        // Инициализация RequestSpecification для заказов
        orderSpec = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setBaseUri("https://petstore.swagger.io")
                .setBasePath("/v2/store/order")
                .build();

        // RequestSpecification для инвентаря
        orderInventorySpec = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setBaseUri("https://petstore.swagger.io")
                .setBasePath("/v2/store/inventory")
                .build();

        // RequestSpecification для пользователя
        userSpec = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setBaseUri("https://petstore.swagger.io")
                .setBasePath("/v2/user")
                .build();

        // Создание пользователей списком
        userCreateWithList = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setBaseUri("https://petstore.swagger.io")
                .setBasePath("/v2/user/createWithList")
                .build();

        // Создание пользователей массивом
        userCreateWithArray = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setBaseUri("https://petstore.swagger.io")
                .setBasePath("/v2/user/createWithArray")
                .build();

        // RequestSpecification для Pet
        petSpec = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setBaseUri("https://petstore.swagger.io")
                .setBasePath("/v2/pet")
                .build();

        // Загрузка изображения питомца
        petSpecUploadImage = new RequestSpecBuilder()
                .setBaseUri("https://petstore.swagger.io/v2")
                .setBasePath("/pet/{id}/uploadImage")
                .build();

        // Базовая проверка ответа 200 и JSON
        baseResponseSpecification = new ResponseSpecBuilder()
                .expectContentType(ContentType.JSON)
                .expectStatusCode(200)
                .build();

        // Стандартная проверка кода 200 и полей code/type/message
        apiResponseSpecification = new ResponseSpecBuilder()
                .expectStatusCode(200)
                .expectContentType(ContentType.JSON)
                .expectBody("code", equalTo(200))
                .expectBody("type", notNullValue())
                .expectBody("message", notNullValue())
                .build();

        // Проверка ошибок 400/404/500
        errorResponseResponseSpecification = new ResponseSpecBuilder()
                .expectContentType(ContentType.JSON)
                .expectStatusCode(anyOf(is(400), is(404), is(500)))
                .expectBody("code", notNullValue())
                .expectBody("message", notNullValue())
                .build();

        // Проверка создания питомца
        petCreateResponseSpecification = new ResponseSpecBuilder()
                .expectContentType(ContentType.JSON)
                .expectStatusCode(200)
                .expectBody("id", greaterThan(0))
                .expectBody("category", notNullValue())
                .expectBody("name", notNullValue())
                .expectBody("status", notNullValue())
                .expectBody("tags", notNullValue())
                .expectBody("photoUrls", notNullValue())
                .build();

        // Проверка создания заказа
        orderCreateResponseSpecification = new ResponseSpecBuilder()
                .expectContentType(ContentType.JSON)
                .expectStatusCode(200)
                .expectBody("id", greaterThan(0))
                .expectBody("petId", notNullValue())
                .expectBody("quantity", notNullValue())
                .expectBody("shipDate", notNullValue())
                .expectBody("status", notNullValue())
                .expectBody("complete", notNullValue())
                .build();

        // Проверка создания пользователя
        userCreateResponseSpecification = new ResponseSpecBuilder()
                .expectContentType(ContentType.JSON)
                .expectStatusCode(200)
                .expectBody("code", equalTo(200))
                .expectBody("message", notNullValue())
                .expectBody("type", notNullValue())
                .build();

        // Инициализация ObjectMapper
        mapper = new ObjectMapper();

        // Добавление фильтра Allure для RestAssured
        RestAssured.filters(new AllureRestAssured());
    }

    /**
     * Обертка для Allure.step()
     * Позволяет обернуть любой код в шаг Allure
     *
     * @param name   название шага
     * @param action код, который выполняется внутри шага
     */
    protected void step(String name, Runnable action) {
        Allure.step(name, () -> {
            try {
                action.run();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
