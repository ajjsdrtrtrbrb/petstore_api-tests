package api_project.test;

import api_project.api.ApiOrder;
import api_project.base.BaseClass;
import api_project.model.ApiResponse;
import api_project.model.Order;
import api_project.model.OrderBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Epic("CRUD ORDER") // Эпик для Allure
@Feature("Order create get delete") // Фича для Allure
@Tag("api") // Теги для CI, фильтруют тесты
@Tag("order")
public class OrderTest extends BaseClass {

    private final ApiOrder apiOrder = new ApiOrder(orderSpec, orderInventorySpec,
            baseResponseSpecification, orderCreateResponseSpecification, mapper);

    // ================================
    @Story("get inventory")
    @Description("Получение инвентаря")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Получение инвентаря")
    @Test
    public void getInventory1() {
        apiOrder.getInventory1();
    }

    @Story("get inventory with checking fields")
    @Description("Получение инвентаря с проверкой значений")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Получение инвентаря с проверкой значений")
    @Test
    public void getInventory2() {
        Map<String, Integer> map = apiOrder.getInventory2();
        map.forEach((key, value) -> assertTrue(value > 0, "Значение меньше 0 для " + key));
    }

    @Story("get inventory by JsonPath")
    @Description("Получение инвентаря через JsonPath")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Получение инвентаря через JsonPath")
    @Test
    public void getInventory3() {
        var jsonPath = apiOrder.getInventory3();
        Map<String, Integer> map = jsonPath.getMap("");
        assertTrue(map.containsKey("available"), "ключ available отсутствует");
        assertTrue(map.containsKey("pending"), "ключ pending отсутствует");
        assertTrue(map.containsKey("sold"), "ключ sold отсутствует");

        int available = jsonPath.getInt("available");
        int pending = jsonPath.getInt("pending");
        int sold = jsonPath.getInt("sold");

        assertTrue(available > 0, "available < 0");
        assertTrue(pending > 0, "pending < 0");
        assertTrue(sold > 0, "sold < 0");

        System.out.println("available " + available);
        System.out.println("pending " + pending);
        System.out.println("sold " + sold);
    }

    @Story("get inventory with check keys")
    @Description("Проверка ключей инвентаря (вариант 1)")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Проверка ключей инвентаря (вариант 1)")
    @Test
    public void getInventory4() {
        apiOrder.getInventory4();
    }

    @Story("get inventory with check keys2")
    @Description("Проверка ключей инвентаря (вариант 2)")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Проверка ключей инвентаря (вариант 2)")
    @Test
    public void getInventory5() {
        apiOrder.getInventory5();
    }

    // ================================
    @Story("create order with object")
    @Description("Создание заказа через объект Order")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Создание заказа через объект Order")
    @Test
    public void createOrder1() throws JsonProcessingException {
        Order order = new OrderBuilder()
                .id(55)
                .petId(55)
                .quantity(55)
                .shipDate("2025-10-07T05:48:58.949+0000")
                .status("placed")
                .complete(true)
                .build();

        Order created = apiOrder.orderCreateWithObject(order);

        assertAll("Проверка полей заказа",
                () -> assertTrue(created.getId() > 0),
                () -> assertEquals(55, created.getPetId()),
                () -> assertEquals(55, created.getQuantity()),
                () -> assertNotNull(created.getShipDate()),
                () -> assertEquals("placed", created.getStatus()),
                () -> assertTrue(created.isComplete())
        );
    }

    @Story("create order with json")
    @Description("Создание заказа через JSON")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Создание заказа через JSON")
    @Test
    public void createOrder2() throws JsonProcessingException {
        String json = "{\n" +
                "  \"id\": 55,\n" +
                "  \"petId\": 55,\n" +
                "  \"quantity\": 55,\n" +
                "  \"shipDate\": \"2025-10-07T05:48:58.949+0000\",\n" +
                "  \"status\": \"placed\",\n" +
                "  \"complete\": true\n" +
                "}";

        Order created = apiOrder.orderCreateWithJson(json);

        assertAll("Проверка полей заказа",
                () -> assertTrue(created.getId() > 0),
                () -> assertEquals(55, created.getPetId()),
                () -> assertEquals(55, created.getQuantity()),
                () -> assertNotNull(created.getShipDate()),
                () -> assertEquals("placed", created.getStatus()),
                () -> assertTrue(created.isComplete())
        );
    }

    // ================================
    @Story("get order by id")
    @Description("Получение заказа по ID")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Получение заказа по ID")
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    public void get1(int id) throws JsonProcessingException {
        Order order = apiOrder.getOrder(id);
        assertEquals(id, order.getId());
    }

    @Story("create delete order")
    @Description("Создание и удаление заказа")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Создание и удаление заказа")
    @Test
    public void testCreateDelete() throws JsonProcessingException {
        int orderId = 10;
        Order order = new OrderBuilder().id(orderId).build();
        Order created = apiOrder.orderCreateWithObject(order);
        assertEquals(orderId, created.getId());

        ApiResponse response = apiOrder.orderDelete(orderId);
        assertAll("Проверка удаления заказа",
                () -> assertEquals(200, response.getCode()),
                () -> assertEquals("unknown", response.getType()),
                () -> assertNotNull(response.getMessage())
        );
    }

    // ================================
    @Story("delete 404")
    @Description("Попытка удалить несуществующий заказ")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Удаление несуществующего заказа")
    @ParameterizedTest
    @ValueSource(ints = {4584556, 85455, 89686765})
    public void delete404(int id) throws JsonProcessingException {
        ApiResponse response = apiOrder.delete404(id);
        assertAll("Проверка кода 404",
                () -> assertEquals(404, response.getCode()),
                () -> assertEquals("unknown", response.getType()),
                () -> assertEquals("Order Not Found", response.getMessage())
        );
    }

    @Story("get 404")
    @Description("Попытка получить несуществующий заказ")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Получение несуществующего заказа")
    @ParameterizedTest
    @ValueSource(ints = {5586536, 8868953, 115786786})
    public void get404(int id) {
        int code = apiOrder.get404(id);
        assertEquals(404, code);
    }

    @Story("post 400")
    @Description("Создание заказа с неверным JSON")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Создание заказа с неверным JSON")
    @ParameterizedTest
    @ValueSource(strings = {"{..........}"})
    public void post400(String json) {
        int code = apiOrder.post400(json);
        assertEquals(400, code);
    }
}
