package pac16.tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import pac16.api.ApiOrder;
import pac16.base.BaseClass;
import pac16.model.ApiResponse;
import pac16.model.Order;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Epic("order api")
@Feature("create update delete get")
public class OrderContr1 extends BaseClass {

    @Test
    @Feature("get inventory")
    @Description("получение инвентори")
    public void getInventoryTest() throws JsonProcessingException {
         ApiOrder apiOrder=new ApiOrder(orderSpec,inventorySpec);
        Map<String,Integer>map=apiOrder.getInventory();
        map.forEach((status,value)->
                Assertions.assertTrue(value>0,"status <=0"));
    }
    @Test
    @Feature("create order and get by id")
    @Description("создание и получение")
    public void getAndCreate(){
        ApiOrder apiOrder=new ApiOrder(orderSpec,inventorySpec);
        Order order=new Order(1,1,1,"2025-09-03T07:05:24.778+0000","placed",true);
        step("creation",()->{
            try {
                Order created=apiOrder.createOrder(order);
                System.out.println(created);
                Assertions.assertAll("check",
                        () -> assertTrue(created.getId()>0),
                        () -> assertEquals(order.getPetId(), created.getPetId()),
                        () -> assertEquals(order.getQuantity(), created.getQuantity()),
                        () -> assertEquals(order.getShipDate(), created.getShipDate()),
                        () -> assertEquals(order.getStatus(), created.getStatus()),
                        () -> assertEquals(order.isComplete(), created.isComplete()


                        ));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
        step("getting",()->{

            ApiResponse response=apiOrder.deleteOrder(1);
            Assertions.assertAll("check",
                    ()->assertEquals(200,response.getCode()),
                    ()->assertEquals("unknown",response.getType()),
                    ()->assertEquals("1",response.getMessage()));
        });

    }
    @ParameterizedTest
    @ValueSource(ints = {1,2,3})
    public void test1(int id){
        ApiOrder apiOrder=new ApiOrder(orderSpec,inventorySpec);
        Order order=apiOrder.getOrderById(id);
        Assertions.assertFalse(order.getId()==0);
    }

    @Test
    public void test2(){
        ApiOrder order=new ApiOrder(orderSpec,inventorySpec);
        ApiResponse response=order.deleteNotFount(575);
        Assertions.assertFalse(response.getCode()==0);
    }
    @Test
    public void test3(){
        String json="{\n" +
                "  \"id\": 55,\n" +
                "  \"petId\": 0,\n" +
                "  \"quantity\": 0,\n" +
                "  \"shipDate\": \"2025-09-08T06:54:59.310Z\",\n" +
                "  \"status\": \"placed\",\n" +
                "  \"complete\": true\n" +
                "}";
        ApiOrder apiOrder=new ApiOrder(orderSpec,inventorySpec);
        Order order=apiOrder.createOrderFromJson(json);
        Assertions.assertTrue(order.getId()>0);
        Assertions.assertEquals(0,order.getPetId());
    }
    @Test
    public void test4(){
        ApiOrder apiOrder=new ApiOrder(orderSpec);
        apiOrder.createAndGetAndDelete(5,5,5);
    }

}
