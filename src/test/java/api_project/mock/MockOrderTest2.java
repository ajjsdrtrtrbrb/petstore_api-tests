package api_project.mock;

import api_project.model.Order;
import api_project.model.OrderBuilder;
import api_project.model.ApiResponse;
import api_project.model.ApiResponseBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тесты для проверки Mock API заказов (Order)
 * Используется WireMock для мокирования сервера
 */
public class MockOrderTest2 {

    private static WireMockServer wireMockServer; // сервер WireMock
    private MockOrderApi mockOrderApi;           // клиент для работы с мок-API
    private static int port;                     // порт WireMock сервера

    /**
     * Запуск WireMock перед всеми тестами
     */
    @BeforeAll
    public static void setUpServer() {
        wireMockServer = new WireMockServer(0); // порт 0 — случайный свободный
        wireMockServer.start();
        port = wireMockServer.port();
        System.out.println("WireMock запущен на порту: " + port);
        configureFor("localhost", port);
    }

    /**
     * Остановка WireMock после всех тестов
     */
    @AfterAll
    public static void stop() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }

    /**
     * Настройка клиента перед каждым тестом
     */
    @BeforeEach
    public void setUp() {
        RequestSpecification spec = new RequestSpecBuilder()
                .setBaseUri("http://localhost")
                .setBasePath("/order")
                .setPort(port)
                .setContentType(ContentType.JSON)
                .build();
        mockOrderApi = new MockOrderApi(spec, new ObjectMapper());
    }

    /**
     * Позитивный тест создания заказа
     */
    @Test
    public void createOrderPositive() throws JsonProcessingException {
        Order order = new OrderBuilder()
                .id(10)
                .petId(10)
                .quantity(10)
                .status("1")
                .complete(true)
                .shipDate("2025-10-28T06:33:31.116Z")
                .build();

        String jsonBody = new ObjectMapper().writeValueAsString(order);

        // Настройка stub на POST /order
        stubFor(post(urlEqualTo("/order"))
                .withHeader("Content-Type", equalTo("application/json"))
                .withRequestBody(equalToJson(jsonBody))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(jsonBody)));

        Order created = mockOrderApi.createOrder(order, Order.class);

        assertThat(created)
                .usingRecursiveComparison()
                .isEqualTo(order);
    }

    /**
     * Негативный тест создания заказа с некорректным статусом
     */
    @Test
    public void createOrderNegative() throws JsonProcessingException {
        Order order = new OrderBuilder()
                .id(10)
                .petId(10)
                .quantity(10)
                .status("[") // некорректный статус
                .complete(true)
                .shipDate("2025-10-28T06:33:31.116Z")
                .build();

        ApiResponse apiResponse = new ApiResponseBuilder()
                .code(400)
                .type("error")
                .message("bad request")
                .build();

        String responseBody = new ObjectMapper().writeValueAsString(apiResponse);

        stubFor(post(urlEqualTo("/order"))
                .withHeader("Content-Type", equalTo("application/json"))
                .withRequestBody(matchingJsonPath("$.status", equalTo("[")))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseBody)));

        ApiResponse realResponse = mockOrderApi.createOrder(order, ApiResponse.class);

        assertThat(realResponse)
                .usingRecursiveComparison()
                .isEqualTo(apiResponse);
    }

    /**
     * Параметризованный позитивный тест получения заказа
     */
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    public void getOrderPositive(int id) throws JsonProcessingException {
        Order order = new OrderBuilder()
                .id(id)
                .petId(10)
                .quantity(10)
                .status("1")
                .complete(true)
                .shipDate("2025-10-28T06:33:31.116Z")
                .build();

        String jsonBody = new ObjectMapper().writeValueAsString(order);

        stubFor(get(urlEqualTo("/order/" + id))
                .withHeader("Content-Type", equalTo("application/json"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(jsonBody)));

        Order realOrder = mockOrderApi.getOrderById(id, Order.class);

        assertThat(realOrder)
                .usingRecursiveComparison()
                .isEqualTo(order);
    }

    /**
     * Параметризованный негативный тест получения заказа
     */
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    public void getOrderNegative(int id) throws JsonProcessingException {
        ApiResponse apiResponse = new ApiResponseBuilder()
                .code(400)
                .type("error")
                .message("bad request")
                .build();

        String responseBody = new ObjectMapper().writeValueAsString(apiResponse);

        stubFor(get(urlEqualTo("/order/" + id))
                .withHeader("Content-Type", equalTo("application/json"))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseBody)));

        ApiResponse realResponse = mockOrderApi.getOrderById(id, ApiResponse.class);

        assertThat(realResponse)
                .usingRecursiveComparison()
                .isEqualTo(apiResponse);
    }

    /**
     * Позитивный тест удаления заказа
     */
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    public void deleteOrderPositive(int id) throws JsonProcessingException {
        ApiResponse apiResponse = new ApiResponseBuilder()
                .code(200)
                .type("success")
                .message("order deleted")
                .build();

        String responseBody = new ObjectMapper().writeValueAsString(apiResponse);

        stubFor(delete(urlEqualTo("/order/" + id))
                .withHeader("Content-Type", equalTo("application/json"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseBody)));

        ApiResponse realResponse = mockOrderApi.deleteOrder(id, ApiResponse.class);

        assertThat(realResponse)
                .usingRecursiveComparison()
                .isEqualTo(apiResponse);
    }

    /**
     * Негативный тест удаления заказа
     */
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    public void deleteOrderNegative(int id) throws JsonProcessingException {
        ApiResponse apiResponse = new ApiResponseBuilder()
                .code(404)
                .type("error")
                .message("order not found")
                .build();

        String responseBody = new ObjectMapper().writeValueAsString(apiResponse);

        stubFor(delete(urlEqualTo("/order/" + id))
                .withHeader("Content-Type", equalTo("application/json"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseBody)));

        ApiResponse realResponse = mockOrderApi.deleteOrder(id, ApiResponse.class);

        assertThat(realResponse)
                .usingRecursiveComparison()
                .isEqualTo(apiResponse);
    }

    /**
     * Позитивный тест создания и удаления заказа
     */
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    public void createAndDeleteOrderPositive(int id) throws JsonProcessingException {
        // Создание заказа
        Order order = new OrderBuilder()
                .id(id)
                .petId(10)
                .quantity(10)
                .status("1")
                .complete(true)
                .shipDate("2025-10-28T06:33:31.116Z")
                .build();

        String createRequestBody = new ObjectMapper().writeValueAsString(order);

        stubFor(post(urlEqualTo("/order"))
                .withHeader("Content-Type", equalTo("application/json"))
                .withRequestBody(equalToJson(createRequestBody))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(createRequestBody)));

        Order createdOrder = mockOrderApi.createOrder(order, Order.class);

        assertThat(createdOrder)
                .usingRecursiveComparison()
                .isEqualTo(order);

        // Получение созданного заказа
        String getResponseBody = new ObjectMapper().writeValueAsString(order);
        stubFor(get(urlEqualTo("/order/" + id))
                .withHeader("Content-Type", equalTo("application/json"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(getResponseBody)));

        Order getOrder = mockOrderApi.getOrderById(id, Order.class);
        assertThat(getOrder)
                .usingRecursiveComparison()
                .isEqualTo(order);

        // Удаление заказа
        String deleteOrderObject = new ObjectMapper().writeValueAsString(order);
        stubFor(delete(urlEqualTo("/order/" + id))
                .withHeader("Content-Type", equalTo("application/json"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(deleteOrderObject)));

        Order deletedOrder = mockOrderApi.deleteOrder(id, Order.class);
        assertThat(deletedOrder)
                .usingRecursiveComparison()
                .isEqualTo(order);

        // Попытка получить после удаления
        ApiResponse getResponse = new ApiResponseBuilder()
                .code(404)
                .type("error")
                .message("order not found")
                .build();
        String getMockResponse = new ObjectMapper().writeValueAsString(getResponse);

        stubFor(get(urlEqualTo("/order/" + id))
                .withHeader("Content-Type", equalTo("application/json"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", "application/json")
                        .withBody(getMockResponse)));

        ApiResponse realGetResponse = mockOrderApi.getOrderById(id, ApiResponse.class);
        assertThat(realGetResponse)
                .usingRecursiveComparison()
                .isEqualTo(getResponse);

        // Попытка удалить уже удалённый заказ
        ApiResponse deleteResponse = new ApiResponseBuilder()
                .code(404)
                .type("error")
                .message("order not found")
                .build();
        String responseBodyForDelete = new ObjectMapper().writeValueAsString(deleteResponse);

        stubFor(delete(urlEqualTo("/order/" + id))
                .withHeader("Content-Type", equalTo("application/json"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseBodyForDelete)));

        ApiResponse realDeleteResponse = mockOrderApi.deleteOrder(id, ApiResponse.class);
        assertThat(realDeleteResponse)
                .usingRecursiveComparison()
                .isEqualTo(deleteResponse);
    }

    /**
     * Позитивный тест получения инвентаря
     */
    @Test
    public void inventoryPositive() throws JsonProcessingException {
        Map<String, Integer> inventoryMap = new HashMap<>();
        inventoryMap.put("sold", 45);
        inventoryMap.put("pending", 10);
        inventoryMap.put("available", 75);

        String responseInventoryBody = new ObjectMapper().writeValueAsString(inventoryMap);

        stubFor(get(urlEqualTo("/order/inventory"))
                .withHeader("Content-Type", equalTo("application/json"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseInventoryBody)));

        Map<String, Integer> realInventoryResponse = mockOrderApi.getInventory(Map.class);

        assertThat(realInventoryResponse)
                .usingRecursiveComparison()
                .isEqualTo(inventoryMap);
    }

    /**
     * Негативный тест получения инвентаря
     */
    @Test
    public void inventoryNegative() throws JsonProcessingException {
        ApiResponse apiResponse = new ApiResponseBuilder()
                .code(500)
                .type("error")
                .message("internal error")
                .build();

        String responseBody = new ObjectMapper().writeValueAsString(apiResponse);

        stubFor(get(urlEqualTo("/order/inventory"))
                .withHeader("Content-Type", equalTo("application/json"))
                .willReturn(aResponse()
                        .withStatus(500)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseBody)));

        ApiResponse realResponse = mockOrderApi.getInventory(ApiResponse.class);

        Assertions.assertAll("check",
                () -> Assertions.assertEquals(apiResponse.getCode(), realResponse.getCode()),
                () -> Assertions.assertEquals(apiResponse.getMessage(), realResponse.getMessage()),
                () -> Assertions.assertEquals(apiResponse.getType(), realResponse.getType())
        );
    }
}
