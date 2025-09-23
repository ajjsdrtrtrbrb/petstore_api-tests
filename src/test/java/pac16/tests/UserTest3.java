package pac16.tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import pac16.api.ApiUser;
import pac16.base.BaseClass;
import pac16.model.ApiResponse;
import pac16.model.User;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Epic("user api")
@Feature("create_delete_update_get")

public class UserTest3 extends BaseClass {


    @ParameterizedTest
    @ValueSource(strings = {"user1", "user2", "user3"})
    @Story("user api")
    @Description("user api")
    public void test1(String userName) throws JsonProcessingException {
        final ApiUser apiUser = new ApiUser(userSpec, userCreateWithList);
        step("creation with list", () -> {

            User user1 = new User(1, "user1", "John", "Doe", "john.doe@example.com", "password123", "1234567890", 1);
            User user2 = new User(2, "user2", "Jane", "Smith", "jane.smith@example.com", "password456", "0987654321", 1);
            User user3 = new User(3, "user3", "Bob", "Brown", "bob.brown@example.com", "password789", "1122334455", 1);
            User[] users = {user1, user2, user3};
            List<User> userList = Arrays.asList(users);
            try {
                ApiResponse response = apiUser.createWithList(userList);
                assertAll(
                        () -> assertEquals(200, response.getCode(), "Неправильный код ответа"),
                        () -> assertEquals("ok", response.getMessage(), "Неправильное сообщение")
                );
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
        step("get created users", () -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            User user = apiUser.getByUserName(userName);
            Assertions.assertTrue(user.getId() > 0);
            Assertions.assertEquals(userName, user.getUserName());
        });
        step("put user", () -> {
            User user = new User(1, "user1", "John", "Doe", "john.doe@example.com", "password123", "1234567890", 1);
            String userName2 = "user1";
            try {
                ApiResponse response=apiUser.putUser(userName2,user);
                assertAll("check",
                        () -> assertEquals(200, response.getCode(), "неправильный code"),
                        () -> assertFalse( response.getMessage().isEmpty(), "неправильный message"),
                        () -> assertEquals("unknown", response.getType(), "неправильный type"));
                Thread.sleep(500);
                User user1=apiUser.getByUserName(user.getUserName());
                Assertions.assertEquals(user1.getId(),1);
                Assertions.assertEquals(user1.getUserName(),userName2);

            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        });
        step("delete",()->{
            String userName2 = "user1";
            ApiResponse response=apiUser.deleteUser(userName2);
            assertAll("check",
                    () -> assertEquals(200, response.getCode(), "неправильный code"),
                    () -> assertEquals("ok", response.getMessage(), "неправильный message"),
                    () -> assertEquals("unknown", response.getType(), "неправильный type"));
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            apiUser.getAfterDelete(userName2);


        });


    }

}
