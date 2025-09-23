package pac16.tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.checkerframework.checker.units.qual.PolyUnit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pac16.api.ApiOrder;
import pac16.base.BaseClass;
import pac16.model.Order;

import java.util.Map;
@Epic("order api")
@Feature("order crud")

public class OrderTest1 extends BaseClass {
    @Test
    @DisplayName("Проверка /store/inventory возвращает валидные статусы")
    public void getInventory() throws JsonProcessingException {
        ApiOrder apiOrder=new ApiOrder(orderSpec,inventorySpec);
        Map<String,Integer>map=apiOrder.getInventory();
        Assertions.assertFalse(map.isEmpty(),"map is empty");
        Assertions.assertTrue(map.containsKey("available"));
        map.forEach((status,value)->
                Assertions.assertTrue(value>0,"У статуса " + status + " отрицательное значение: " + value)
        );
    }
    @Test
    @Feature("create/get order")
    @Description("creation/getting order")
    @DisplayName("создание/получение заказа")
    public void getTest(){
        ApiOrder apiOrder=new ApiOrder(orderSpec,inventorySpec);
        Order order=new Order(1,1,1,"2025-09-03T07:05:24.778+0000","placed",true);
        step("oreder create",()->{
            try {
                Order created=apiOrder.createOrder(order);
                Assertions.assertEquals(order,created);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });



       // Order order=apiOrder.getOrderById(1);
    }
}
