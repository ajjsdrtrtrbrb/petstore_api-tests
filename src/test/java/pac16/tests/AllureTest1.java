package pac16.tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import pac16.api.ApiOrder;
import pac16.api.ApiUser;
import pac16.base.BaseClass;
import pac16.model.ApiResponse;
import pac16.model.Order;
import pac16.model.User;
import static org.awaitility.Awaitility.await;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.reset;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
@Epic("crud user")
@Feature("create get user")
public class AllureTest1 extends BaseClass{
    private ApiUser apiUser;
    @BeforeEach
    public  void setUp(){
        super.setAll();
        apiUser=new ApiUser(userSpec);
    }
    @Story("create user & get")
    @Description("user creattion")
    @Test
    public void createUser(){
        User newUser = new User(
                1234,
                "qa_test",
                "QA",
                "Engineer",
                "qa@test.com",
                "password123",
                "1234567890",
                1
        );
        String userName=newUser.getUserName();
        step("user creation",()->{
         ApiResponse response=apiUser.createUser(newUser);
            System.out.println(response);
         Assertions.assertEquals(200,response.getCode());
        });
        step("wait user",()->{
            await().atMost(30, TimeUnit.SECONDS)
                    .pollInterval(500,TimeUnit.MILLISECONDS)
                    .until(()->{
                        try {
                            User user=apiUser.getByUserName(userName);
                            return user!=null&&user.getId()>0;
                        }
                        catch (Exception e){
                            return false;
                        }
                    });
        });
        step("get user",()->{
            User getUser=apiUser.getByUserName(userName);
            System.out.println(getUser);
            Assertions.assertEquals(userName,getUser.getUserName());
        });
        step("delete user",()->{
            ApiResponse deleteResponse=apiUser.deleteUser(userName);
            System.out.println(deleteResponse);
            Assertions.assertEquals(200,deleteResponse.getCode());
        });


    }
}
