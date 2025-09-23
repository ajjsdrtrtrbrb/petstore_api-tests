package pac16.tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import pac16.api.ApiOrder;
import pac16.base.BaseClass;
import pac16.model.ApiResponse;
import pac16.model.Order;

import java.util.Map;

@Epic("order api")
@Feature("negative test")
public class OrderContrNegative extends BaseClass {
    private ApiOrder apiOrder;

    @BeforeEach
    public void setUp() {
        apiOrder = new ApiOrder(orderSpec);
    }

    @ParameterizedTest
    @ValueSource(ints = {9999})
    @Story("получение несуществующего пользователя")
    @Description("wrong order")
    public void get404(int id) {
        Response response = apiOrder.get404test(id);
        Assertions.assertEquals(404, response.getStatusCode());
    }

    @ParameterizedTest
    @ValueSource(ints = {9999})
    @Story("удаление несуществующего пользователя")
    @Description("wrong order")
    public void delete404(int id) {
        Response response = apiOrder.deleteOrder404(id);
        Assertions.assertEquals(404, response.getStatusCode());
    }

    @Test
    @Story("wrong creation")
    @Description("wrong creation")
    public void wrongCreate(){
        Response response=apiOrder.createWrong("dddd","Fffff","wqd");
        Assertions.assertEquals(400,response.getStatusCode());

    }
}
