package api_project.mock;

import api_project.api.ApiOrder;
import api_project.model.Order;
import api_project.model.ApiResponse;
import api_project.model.OrderBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import io.qameta.allure.*;
import io.restassured.path.json.JsonPath;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.junit.jupiter.api.*;

import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

@Epic("Mock API Testing") // Эпик для Allure
@Feature("Order CRUD via Mock") // Фича для Allure
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // Чтобы тесты можно было упорядочить
public class MockOrderTest {

    private static WireMockServer wireMockServer;
    private static int port;
    private ApiOrder apiOrder;

    @BeforeAll
    public static void setUpServer() {
        wireMockServer = new WireMockServer(0); // WireMock на случайном порту
        wireMockServer.start();
        port = wireMockServer.port();
        System.out.println("WireMock запущен на порту: " + port);
        configureFor("localhost", port);
    }

    @AfterAll
    public static void stopServer() {
        wireMockServer.stop();
    }

    @BeforeEach
    public void setUp() {
        // Спецификация запроса для Order
        RequestSpecification specOrder = new RequestSpecBuilder()
                .setBaseUri("http://localhost")
                .setBasePath("/order")
                .setPort(port)
                .setContentType(ContentType.JSON)
                .build();

        // Стандартные ResponseSpecification для моков
        ResponseSpecification mockSuccessSpec = io.restassured.RestAssured.expect().statusCode(200);
        ResponseSpecification mockBadRequestSpec = io.restassured.RestAssured.expect().statusCode(400);
        ResponseSpecification mockNotFoundSpec = io.restassured.RestAssured.expect().statusCode(404);
        ResponseSpecification mockInternalErrorSpec = io.restassured.RestAssured.expect().statusCode(500);

        // ObjectMapper для сериализации/десериализации
        ObjectMapper mapper = new ObjectMapper();

        // Спецификация запроса для inventory
        RequestSpecification inventorySpec = new RequestSpecBuilder()
                .setBaseUri("http://localhost")
                .setBasePath("/store/inventory")
                .setPort(port)
                .setContentType(ContentType.JSON)
                .build();

        // Инициализация ApiOrder
        apiOrder = new ApiOrder(specOrder, inventorySpec,
                mockSuccessSpec, mockSuccessSpec, mockSuccessSpec,
                mockBadRequestSpec, mockNotFoundSpec, mockInternalErrorSpec,
                mapper);
    }

    // ===============================
    @Test
    @Story("Get inventory positive") // Шаг для Allure
    @Description("Проверка успешного получения inventory через мок")
    public void getInventoryPositive() {
        stubFor(get(urlEqualTo("/store/inventory"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"sold\":5,\"available\":10}")));

        Map<String, Integer> map = apiOrder.getInventory2();
        assertEquals(5, map.get("sold"));
        assertEquals(10, map.get("available"));
    }

    // ===============================
    @Test
    @Story("Get inventory negative")
    @Description("Проверка 404 при отсутствии inventory")
    public void getInventory404() {
        stubFor(get(urlEqualTo("/store/inventory"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"code\":404,\"message\":\"inventory not found\"}")));

        Exception exception = assertThrows(RuntimeException.class, () -> apiOrder.getInventory2());
        assertTrue(exception.getMessage().contains("inventory not found"));
    }

    // ===============================
    @Test
    @Story("Create order positive")
    @Description("Создание заказа через мок API")
    public void createOrderPositive() throws JsonProcessingException {
        Order order = new OrderBuilder()
                .id(55)
                .petId(55)
                .quantity(55)
                .shipDate("2025-10-17T04:17:41.247Z")
                .status("placed")
                .complete(true)
                .build();
        String json = new ObjectMapper().writeValueAsString(order);

        stubFor(post(urlEqualTo("/order"))
                .withRequestBody(equalToJson(json))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(json)));

        apiOrder.orderCreateWithJson(json);
        JsonPath jsonPath = JsonPath.from(json);
        int id = jsonPath.getInt("id");
        assertEquals(order.getId(), id);
    }

    // ===============================
    @Test
    @Story("Create order negative")
    @Description("Проверка ошибки 400 при неверном формате JSON")
    public void createOrderNegative() {
        String json = "{]";
        stubFor(post(urlEqualTo("/order"))
                .withRequestBody(containing(json))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"code\":400,\"message\":\"bad request\"}")));

        Exception exception = assertThrows(RuntimeException.class, () -> apiOrder.orderCreateWithJson(json));
        assertEquals("bad request", exception.getMessage());
    }

    // ===============================
    @Test
    @Story("Create order internal error")
    @Description("Проверка ошибки 500 при серверной ошибке")
    public void createNegative500() throws JsonProcessingException {
        Order created = new OrderBuilder()
                .id(55)
                .petId(55)
                .quantity(55)
                .shipDate("2025-10-17T04:17:41.247Z")
                .status(null)
                .complete(true)
                .build();
        String json = new ObjectMapper().writeValueAsString(created);

        stubFor(post(urlEqualTo("/order"))
                .withRequestBody(equalToJson(json))
                .willReturn(aResponse()
                        .withStatus(500)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"code\":500,\"message\":\"internal error\"}")));

        Exception exception = assertThrows(RuntimeException.class, () -> apiOrder.orderCreateWithJson(json));
        assertTrue(exception.getMessage().contains("internal error"));
        assertTrue(exception.getMessage().contains("code 500"));
    }

    // ===============================
    @Test
    @Story("Get order positive")
    @Description("Проверка успешного получения заказа по id")
    public void getOrderPositive() throws JsonProcessingException {
        Order expectedOrder = new OrderBuilder()
                .id(1)
                .petId(1)
                .quantity(1)
                .shipDate("2023-03-27T02:14:59.643+0000")
                .status("placed")
                .complete(true)
                .build();
        String json = new ObjectMapper().writeValueAsString(expectedOrder);

        stubFor(get(urlEqualTo("/order/1"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(json)));

        Order actualOrder = apiOrder.getOrder(1);

        assertEquals(expectedOrder.getId(), actualOrder.getId());
        assertEquals(expectedOrder.getPetId(), actualOrder.getPetId());
        assertEquals(expectedOrder.getQuantity(), actualOrder.getQuantity());
        assertEquals(expectedOrder.getShipDate(), actualOrder.getShipDate());
        assertEquals(expectedOrder.getStatus(), actualOrder.getStatus());
        assertEquals(expectedOrder.isComplete(), actualOrder.isComplete());
    }

    // ===============================
    @Test
    @Story("Get order negative")
    @Description("Проверка 404 при получении несуществующего заказа")
    public void getOrderNegative() {
        stubFor(get(urlMatching("/order/\\d+"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"code\":404,\"message\":\"order not found\"}")));

        Exception exception = assertThrows(RuntimeException.class, () -> apiOrder.getOrder(555));
        assertEquals("order not found", exception.getMessage());
    }

    // ===============================
    @Test
    @Story("Delete order positive")
    @Description("Проверка успешного удаления заказа")
    public void deleteOrderPositive() throws JsonProcessingException {
        stubFor(delete(urlMatching("/order/\\d+"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"code\":200,\"type\":\"success\",\"message\":\"order deleted\"}")));

        ApiResponse response = apiOrder.orderDelete(1);
        assertEquals(200, response.getCode());
        assertEquals("success", response.getType());
        assertEquals("order deleted", response.getMessage());
    }

    // ===============================
    @Test
    @Story("Delete order negative")
    @Description("Проверка 404 при удалении несуществующего заказа")
    public void deleteOrderNegative() throws JsonProcessingException {
        stubFor(delete(urlMatching("/order/\\d+"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"code\":404,\"type\":\"failed\",\"message\":\"order not found\"}")));

        ApiResponse response = apiOrder.orderDelete(10);
        assertEquals(404, response.getCode());
        assertEquals("failed", response.getType());
        assertEquals("order not found", response.getMessage());
    }
}
