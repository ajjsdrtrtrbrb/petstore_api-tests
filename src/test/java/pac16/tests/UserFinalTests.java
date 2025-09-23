package pac16.tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import pac16.api.ApiOrder;
import pac16.api.ApiUser;
import pac16.base.BaseClass;
import pac16.model.ApiResponse;
import pac16.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Epic("crud user")
@Feature("user create/update/get/delete")
public class UserFinalTests extends BaseClass {
    private ApiUser apiUser;

    @BeforeEach
    public void setUp() {
        super.setAll();
        apiUser = new ApiUser(userSpec, userCreateWithList);
    }

    @Story("create user")
    @Description("user create")
    @Test
    @Order(1)
    public void createUser1() throws InterruptedException {
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
        ApiResponse apiResponse = apiUser.createUser(newUser);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Assertions.assertAll("check",
                () -> assertEquals(200, apiResponse.getCode()),
                () -> assertEquals("unknown", apiResponse.getType()),
                () -> assertTrue(!apiResponse.getMessage().isEmpty()));
        User getUser=apiUser.getByUserName("qa_test");
        Assertions.assertAll("checkUser",
                ()->assertTrue(getUser.getId()>0),
                () -> assertEquals("qa_test", getUser.getUserName()),
                () -> assertEquals("QA", getUser.getFirstName()),
                () -> assertEquals("Engineer",getUser.getLastName()),
                () -> assertEquals("qa@test.com",getUser.getEmail()),
                () -> assertEquals("password123",getUser.getPassword()),
                () -> assertEquals("1234567890",getUser.getPhone()),
                () -> assertEquals(1,getUser.getUserStatus())

        );
        step("delete",()->{
            ApiResponse response=apiUser.deleteUser("qa_test");
            Assertions.assertAll("check",
                    () -> assertEquals(200, apiResponse.getCode()),
                    () -> assertEquals("unknown", apiResponse.getType()),
                    () -> assertTrue(!apiResponse.getMessage().isEmpty()));
        });


    }
    @Order(2)
    @Test
    public void test2(){
        Response response=apiUser.get404("qa_test");
        int code=response.getStatusCode();
        Assertions.assertEquals(404,code);
    }
    @Order(3)
    @Story("put user")
    @Description("put user")
    @ParameterizedTest
    @ValueSource(strings = "qa_test")
    public void test3(String userName) throws InterruptedException, JsonProcessingException {
        String json="{\n" +
                "  \"id\": 1234,\n" +
                "  \"username\": \"qa_test\",\n" +
                "  \"firstName\": \"QA\",\n" +
                "  \"lastName\": \"Engineer\",\n" +
                "  \"email\": \"qa@test.com\",\n" +
                "  \"password\": \"password123\",\n" +
                "  \"phone\": \"1234567890\",\n" +
                "  \"userStatus\": 1\n" +
                "}";
        ApiResponse apiResponse=apiUser.create2(json);
        Assertions.assertAll("check",
                () -> assertEquals(200, apiResponse.getCode()),
                () -> assertEquals("unknown", apiResponse.getType()),
                () -> assertTrue(!apiResponse.getMessage().isEmpty()));
        Thread.sleep(500);
        User get=apiUser.getByUserName("qa_test");
        get.setEmail("qa@test.coml");
        Thread.sleep(500);
        ApiResponse response=apiUser.putUser(userName,get);
        Assertions.assertEquals(200,response.getCode());
        Thread.sleep(500);
        User get2=apiUser.getByUserName("qa_test");
        Assertions.assertEquals("qa@test.coml",get2.getEmail());
    }




    @Story("get user")
    @Description("get user")
    @Test
    public void test4() {
        step("create", () -> {
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
            ApiResponse response = apiUser.createUser(newUser);
            Assertions.assertAll("check",
                    () -> assertEquals(200, response.getCode()),
                    () -> assertEquals("unknown", response.getType()),
                    () -> assertTrue(!response.getMessage().isEmpty()));
        });

        step("get (wait for user)", () -> {
            // Ждём появления пользователя до 5 секунд
            await().atMost(5, TimeUnit.SECONDS)
                    .pollInterval(500, TimeUnit.MILLISECONDS)
                    .until(() -> {
                        try {
                            User getUser = apiUser.getByUserName("qa_test");
                            return getUser != null && getUser.getId() > 0;
                        } catch (Exception e) {
                            // Если пока 404, ждём дальше
                            return false;
                        }
                    });
        });

        step("get and check", () -> {
            User getUser = apiUser.getByUserName("qa_test");
            Assertions.assertAll("checkUser",
                    () -> assertTrue(getUser.getId() > 0),
                    () -> assertEquals("qa_test", getUser.getUserName()),
                    () -> assertEquals("QA", getUser.getFirstName()),
                    () -> assertEquals("Engineer", getUser.getLastName()),
                    () -> assertEquals("qa@test.com", getUser.getEmail()),
                    () -> assertEquals("password123", getUser.getPassword()),
                    () -> assertEquals("1234567890", getUser.getPhone()),
                    () -> assertEquals(1, getUser.getUserStatus())
            );
        });
    }
    @Story("create get user")
    @Description("create gate user")
    @Test
    public void test5(){
        step("create user",()->{
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
            ApiResponse response=apiUser.createUser(newUser);
            Assertions.assertEquals(200,response.getCode());
        });
        step("await user",()->{
            await().atMost(5,TimeUnit.SECONDS)
                    .pollInterval(500,TimeUnit.MILLISECONDS)
                    .until(()->{
                        try {
                            User user=apiUser.getByUserName("qa_test");
                            return user!=null&&user.getId()>0;
                        }
                        catch (Exception e){
                            return false;
                        }
                    });
        });
        step("get user",()->{
            User user=apiUser.getByUserName("qa_test");
            Assertions.assertEquals("qa_test",user.getUserName());
        });
    }

   @Story("get user")
   @Description("get user")
   @Test
   public void test6(){
        step("create user",()->{
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
            ApiResponse response=apiUser.createUser(newUser);
            Assertions.assertEquals(200,response.getCode());
        });
        step("wait user",()->{
            await().atMost(5,TimeUnit.SECONDS)
                    .pollInterval(500,TimeUnit.MILLISECONDS)
                    .until(()->{
                        try{
                            User user=apiUser.getByUserName("qa_test");
                            return user!=null&&user.getId()>0;
                        }
                        catch (Exception e){
                            return false;
                        }
                    });
        });
        step("get user",()->{
            User user=apiUser.getByUserName("qa_test");
            Assertions.assertEquals("qa_test",user.getUserName());
        });
   }


}
