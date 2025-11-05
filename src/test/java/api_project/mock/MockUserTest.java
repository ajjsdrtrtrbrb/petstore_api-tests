package api_project.mock;

import api_project.api.ApiUser;
import api_project.model.*;
import api_project.utils.UserUtils;
import com.github.tomakehurst.wiremock.WireMockServer;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Класс тестирования Mock API пользователя.
 * Использует WireMock для мокирования REST-запросов и RestAssured для работы с HTTP.
 */
public class MockUserTest {

    private static WireMockServer wireMockServer; // Сервер WireMock
    private MockUserApi mockUserApi;              // Наш моковый API клиент
    private static int port;                      // Порт WireMock сервера

    /**
     * Настройка WireMock сервера перед всеми тестами
     */
    @BeforeAll
    public static void setUpServer() {
        wireMockServer = new WireMockServer(0); // 0 = выбрать случайный свободный порт
        wireMockServer.start();
        port = wireMockServer.port(); // сохраняем порт для RestAssured
        System.out.println("WireMock запущен на порту: " + port);
        configureFor("localhost", port); // конфигурируем WireMock для localhost
    }

    /**
     * Остановка WireMock сервера после всех тестов
     */
    @AfterAll
    public static void stopServer() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }

    /**
     * Настройка API клиента перед каждым тестом
     */
    @BeforeEach
    public void setUp() {
        RequestSpecification userSpec = new RequestSpecBuilder()
                .setBaseUri("http://localhost")
                .setBasePath("/user")
                .setPort(port)
                .setContentType(ContentType.JSON)
                .build();
        mockUserApi = new MockUserApi(userSpec, new ObjectMapper());
    }

    /**
     * Позитивный тест создания пользователя
     */
    @Test
    public void userCreatePositive() throws JsonProcessingException {
        User user = new UserBuilder()
                .id(1)
                .username("test")
                .firstName("user")
                .lastName("user")
                .email("user@gmail.com")
                .password("123")
                .userStatus(1)
                .build();

        // Сериализация пользователя в JSON
        String jsonBody = new ObjectMapper().writeValueAsString(user);

        // Настройка WireMock для POST /user
        stubFor(post(urlEqualTo("/user"))
                .withHeader("Content-type", equalTo("application/json"))
                .withRequestBody(equalToJson(jsonBody))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-type", "application/json")
                        .withBody(jsonBody)));

        // Вызов метода API
        User createdUser = mockUserApi.createUser(user, User.class);

        // Проверка результата
        Assertions.assertNotNull(createdUser);
        Assertions.assertEquals(user.getId(), createdUser.getId());
        Assertions.assertEquals(user.getUsername(), createdUser.getUsername());
        Assertions.assertEquals(user.getFirstName(), createdUser.getFirstName());
        Assertions.assertEquals(user.getLastName(), createdUser.getLastName());
        Assertions.assertEquals(user.getEmail(), createdUser.getEmail());
        Assertions.assertEquals(user.getPassword(), createdUser.getPassword());
        Assertions.assertEquals(user.getUserStatus(), createdUser.getUserStatus());

        // Проверка, что POST запрос действительно был вызван
        verify(postRequestedFor(urlEqualTo("/user"))
                .withRequestBody(equalToJson(jsonBody)));
    }

    /**
     * Негативный тест создания пользователя (пустые поля)
     */
    @Test
    public void userCreateNegative() throws JsonProcessingException {
        User user = new UserBuilder()
                .id(1)
                .username("")      // пустое имя пользователя
                .firstName("")     // пустое имя
                .lastName("user")
                .email("user@gmail.com")
                .userStatus(1)
                .build();

        String requestBody = new ObjectMapper().writeValueAsString(user);

        // Ожидаемый ответ API
        ApiResponse apiResponse = new ApiResponseBuilder()
                .code(400)
                .type("error")
                .message("bad request")
                .build();

        String expectedResponse = new ObjectMapper().writeValueAsString(apiResponse);

        // Настройка WireMock
        stubFor(post(urlEqualTo("/user"))
                .withHeader("Content-type", equalTo("application/json"))
                .withRequestBody(equalToJson(requestBody))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-type", "application/json")
                        .withBody(expectedResponse)));

        ApiResponse realResponse = mockUserApi.createUser(user, ApiResponse.class);

        Assertions.assertNotNull(realResponse, "real response is null");
        Assertions.assertEquals(apiResponse.getCode(), realResponse.getCode());
        Assertions.assertEquals(apiResponse.getType(), realResponse.getType());
        Assertions.assertEquals(apiResponse.getMessage(), realResponse.getMessage());

        // Проверка, что POST запрос был вызван
        verify(postRequestedFor(urlEqualTo("/user"))
                .withRequestBody(equalToJson(requestBody)));
    }

    /**
     * Позитивный параметризованный тест получения пользователя
     */
    @ParameterizedTest
    @ValueSource(strings = {"test1", "test2", "test3"})
    public void getUserPositive(String userName) throws JsonProcessingException {
        User user = new UserBuilder()
                .id(10)
                .username(userName)
                .firstName("dinamo")
                .lastName("kiev")
                .email("dinamo@gmail.com")
                .phone("123")
                .password("123")
                .userStatus(10)
                .build();

        String responseBody = new ObjectMapper().writeValueAsString(user);

        // Настройка WireMock для GET /user/{username}
        stubFor(get(urlEqualTo("/user/" + userName))
                .withHeader("Content-Type", equalTo("application/json"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseBody)));

        User getUser = mockUserApi.getUser(userName, User.class);

        Assertions.assertNotNull(getUser, "user is null");
        Assertions.assertAll("check get user fields",
                () -> assertEquals(user.getId(), getUser.getId()),
                () -> assertEquals(user.getUsername(), getUser.getUsername()),
                () -> assertEquals(user.getFirstName(), getUser.getFirstName()),
                () -> assertEquals(user.getLastName(), getUser.getLastName()),
                () -> assertEquals(user.getPhone(), getUser.getPhone()),
                () -> assertEquals(user.getPassword(), getUser.getPassword()),
                () -> assertEquals(user.getUserStatus(), getUser.getUserStatus()));

        verify(getRequestedFor(urlEqualTo("/user/" + userName))
                .withHeader("Content-type", equalTo("application/json")));
    }

    /**
     * Негативный параметризованный тест получения пользователя (не найден)
     */
    @ParameterizedTest
    @ValueSource(strings = {"aa", "ss", "dd"})
    public void getUserNegative(String userName) throws JsonProcessingException {
        ApiResponse apiResponse = new ApiResponseBuilder()
                .code(404)
                .type("error")
                .message("user not found")
                .build();

        String responseBody = new ObjectMapper().writeValueAsString(apiResponse);

        stubFor(get(urlEqualTo("/user/" + userName))
                .withHeader("Content-Type", equalTo("application/json"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseBody)));

        ApiResponse realResponse = mockUserApi.getUser(userName, ApiResponse.class);

        Assertions.assertNotNull(realResponse, "response is null");
        Assertions.assertEquals(apiResponse.getCode(), realResponse.getCode());
        Assertions.assertEquals(apiResponse.getMessage(), realResponse.getMessage());
        Assertions.assertEquals(apiResponse.getType(), realResponse.getType());

        verify(getRequestedFor(urlEqualTo("/user/" + userName))
                .withHeader("Content-Type", equalTo("application/json")));
    }


    /**
     * Позитивный тест обновления пользователя
     */
    @Test
    public void putUserPositive() throws JsonProcessingException {
        String userName = "testUser";
        User user = new UserBuilder()
                .id(10)
                .username(userName)
                .firstName("dinamo")
                .lastName("kiev")
                .email("dinamo@gmail.com")
                .phone("123")
                .password("123")
                .userStatus(10)
                .build();

        String jsonBody = new ObjectMapper().writeValueAsString(user);

        // Настройка WireMock для PUT /user/{username}
        stubFor(put(urlEqualTo("/user/" + userName))
                .withHeader("Content-Type", equalTo("application/json"))
                .withRequestBody(equalToJson(jsonBody))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(jsonBody)));

        User updatedUser = mockUserApi.putUser(user, userName, User.class);

        Assertions.assertNotNull(updatedUser, "updated user is null");
        Assertions.assertAll("check put user fields",
                () -> assertEquals(user.getId(), updatedUser.getId()),
                () -> assertEquals(user.getUsername(), updatedUser.getUsername()),
                () -> assertEquals(user.getFirstName(), updatedUser.getFirstName()),
                () -> assertEquals(user.getLastName(), updatedUser.getLastName()),
                () -> assertEquals(user.getPhone(), updatedUser.getPhone()),
                () -> assertEquals(user.getPassword(), updatedUser.getPassword()),
                () -> assertEquals(user.getUserStatus(), updatedUser.getUserStatus()));

        verify(putRequestedFor(urlEqualTo("/user/" + userName)));
    }

    /**
     * Негативный тест обновления пользователя
     */
    @Test
    public void putUserNegative() throws JsonProcessingException {
        String userName = "test";
        User user = new UserBuilder()
                .id(10)
                .username(userName)
                .firstName("dinamo")
                .lastName("kiev")
                .email("dinamo@gmail.com")
                .phone("123")
                .password("123")
                .userStatus(10)
                .build();

        String requestBody = new ObjectMapper().writeValueAsString(user);

        ApiResponse apiResponse = new ApiResponseBuilder()
                .code(400)
                .type("error")
                .message("bad request")
                .build();

        String responseBody = new ObjectMapper().writeValueAsString(apiResponse);

        stubFor(put(urlEqualTo("/user/" + userName))
                .withHeader("Content-Type", equalTo("application/json"))
                .withRequestBody(equalToJson(requestBody))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseBody)));

        ApiResponse realResponse = mockUserApi.putUser(user, userName, ApiResponse.class);

        Assertions.assertNotNull(realResponse, "realResponse is null");
        Assertions.assertEquals(apiResponse.getCode(), realResponse.getCode());
        Assertions.assertEquals(apiResponse.getType(), realResponse.getType());
        Assertions.assertEquals(apiResponse.getMessage(), realResponse.getMessage());

        verify(putRequestedFor(urlEqualTo("/user/" + userName)));
    }

    /**
     * Позитивный тест удаления пользователя
     */
    @Test
    public void deleteUserPositive() throws JsonProcessingException {
        String userName = "testUser";
        ApiResponse apiResponse = new ApiResponseBuilder()
                .code(200)
                .type("success")
                .message("user deleted")
                .build();

        String responseBody = new ObjectMapper().writeValueAsString(apiResponse);

        stubFor(delete(urlEqualTo("/user/" + userName))
                .withHeader("Content-Type", equalTo("application/json"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseBody)));

        ApiResponse realResponse = mockUserApi.deleteUser(userName);

        Assertions.assertNotNull(realResponse, "realResponse is null");
        Assertions.assertEquals(apiResponse.getCode(), realResponse.getCode());
        Assertions.assertEquals(apiResponse.getType(), realResponse.getType());
        Assertions.assertEquals(apiResponse.getMessage(), realResponse.getMessage());

        verify(deleteRequestedFor(urlEqualTo("/user/" + userName)));
    }

    /**
     * Негативный тест удаления пользователя
     */
    @Test
    public void deleteUserNegative() throws JsonProcessingException {
        String userName = "test";
        ApiResponse apiResponse = new ApiResponseBuilder()
                .code(400)
                .type("error")
                .message("bad request")
                .build();

        String requestBody = new ObjectMapper().writeValueAsString(apiResponse);

        stubFor(delete(urlEqualTo("/user/" + userName))
                .withHeader("Content-Type", equalTo("application/json"))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody(requestBody)));

        ApiResponse realResponse = mockUserApi.deleteUser(userName);

        Assertions.assertNotNull(realResponse, "realResponse is null");
        Assertions.assertEquals(apiResponse.getCode(), realResponse.getCode());
        Assertions.assertEquals(apiResponse.getType(), realResponse.getType());
        Assertions.assertEquals(apiResponse.getMessage(), realResponse.getMessage());

        verify(deleteRequestedFor(urlEqualTo("/user/" + userName)));
    }

    /**
     * Позитивный тест логина пользователя
     */
    @Test
    public void loginUserPositive() throws JsonProcessingException {
        String userName = "testUser";
        String password = "123";

        ApiResponse apiResponse = new ApiResponseBuilder()
                .code(200)
                .type("success")
                .message("user login")
                .build();

        String responseBody = new ObjectMapper().writeValueAsString(apiResponse);

        stubFor(get(urlPathEqualTo("/user/login"))
                .withQueryParam("username", equalTo(userName))
                .withQueryParam("password", equalTo(password))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseBody)));

        ApiResponse realResponse = mockUserApi.loginUser(userName, password);

        Assertions.assertNotNull(realResponse, "realResponse is null");
        Assertions.assertEquals(apiResponse.getCode(), realResponse.getCode());
        Assertions.assertEquals(apiResponse.getType(), realResponse.getType());
        Assertions.assertEquals(apiResponse.getMessage(), realResponse.getMessage());

        verify(getRequestedFor(urlPathEqualTo("/user/login")));
    }

    /**
     * Негативный тест логина пользователя
     */
    @Test
    public void loginUserNegative() throws JsonProcessingException {
        String userName = "test";
        String password = "1s4";

        ApiResponse apiResponse = new ApiResponseBuilder()
                .code(400)
                .type("error")
                .message("bad request")
                .build();

        String requestBody = new ObjectMapper().writeValueAsString(apiResponse);

        stubFor(get(urlPathEqualTo("/user/login"))
                .withQueryParam("username", equalTo(userName))
                .withQueryParam("password", equalTo(password))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody(requestBody)));

        ApiResponse realResponse = mockUserApi.loginUser(userName, password);

        Assertions.assertNotNull(realResponse, "realResponse is null");
        Assertions.assertEquals(apiResponse.getCode(), realResponse.getCode());
        Assertions.assertEquals(apiResponse.getType(), realResponse.getType());
        Assertions.assertEquals(apiResponse.getMessage(), realResponse.getMessage());

        verify(getRequestedFor(urlPathEqualTo("/user/login")));
    }

    /**
     * Позитивный тест выхода пользователя
     */
    @Test
    public void userLogoutPositive() throws JsonProcessingException {
        ApiResponse apiResponse = new ApiResponseBuilder()
                .code(200)
                .type("success")
                .message("user logout")
                .build();

        String requestBody = new ObjectMapper().writeValueAsString(apiResponse);

        stubFor(get(urlEqualTo("/user/logout"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(requestBody)));

        ApiResponse realResponse = mockUserApi.logout();

        Assertions.assertNotNull(realResponse, "realResponse is null");
        Assertions.assertEquals(apiResponse.getCode(), realResponse.getCode());
        Assertions.assertEquals(apiResponse.getType(), realResponse.getType());
        Assertions.assertEquals(apiResponse.getMessage(), realResponse.getMessage());

        verify(getRequestedFor(urlPathEqualTo("/user/logout")));
    }

    /**
     * Негативный тест выхода пользователя
     */
    @Test
    public void userLogoutNegative() throws JsonProcessingException {
        ApiResponse apiResponse = new ApiResponseBuilder()
                .code(400)
                .type("error")
                .message("bad request")
                .build();

        String requestBody = new ObjectMapper().writeValueAsString(apiResponse);

        stubFor(get(urlEqualTo("/user/logout"))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody(requestBody)));

        ApiResponse realResponse = mockUserApi.logout();

        Assertions.assertNotNull(realResponse, "realResponse is null");
        Assertions.assertEquals(apiResponse.getCode(), realResponse.getCode());
        Assertions.assertEquals(apiResponse.getType(), realResponse.getType());
        Assertions.assertEquals(apiResponse.getMessage(), realResponse.getMessage());

        verify(getRequestedFor(urlPathEqualTo("/user/logout")));
    }

    /**
     * Позитивный тест создания списка пользователей
     */
    @Test
    public void createWithListPositive() throws JsonProcessingException {
        ApiResponse apiResponse = new ApiResponseBuilder()
                .code(200)
                .type("ok")
                .message("Users created successfully")
                .build();

        String response = new ObjectMapper().writeValueAsString(apiResponse);

        stubFor(post(urlEqualTo("/user/createWithList"))
                .withRequestBody(containing("John")) // проверяем, что в теле есть имя
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(response)));

        List<User> users = UserUtils.generateListUsersRandomId(10);
        ApiResponse response1 = mockUserApi.createWithList(users);

        Assertions.assertEquals(200, response1.getCode());
        Assertions.assertEquals("ok", response1.getType());
        Assertions.assertEquals("Users created successfully", response1.getMessage());
    }

    /**
     * Негативный тест создания списка пользователей
     */
    @Test
    public void createWithListNegative() throws JsonProcessingException {
        ApiResponse apiResponse = new ApiResponseBuilder()
                .code(400)
                .type("error")
                .message("bad request")
                .build();

        String responseBody = new ObjectMapper().writeValueAsString(apiResponse);
        String requestBody = new ObjectMapper().writeValueAsString(new ArrayList<User>());

        stubFor(post(urlEqualTo("/user/createWithList"))
                .withRequestBody(equalToJson(requestBody))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseBody)));

        ApiResponse realResponse = mockUserApi.createWithList(new ArrayList<User>());

        Assertions.assertEquals(400, realResponse.getCode());
        Assertions.assertEquals("error", realResponse.getType());
        Assertions.assertEquals("bad request", realResponse.getMessage());
    }
}
