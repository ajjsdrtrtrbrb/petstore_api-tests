package pac16.tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pac16.api.ApiUser;
import pac16.base.BaseClass;
import pac16.model.ApiResponse;
import pac16.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Epic("user api")
@Feature("authorization")
public class UserTest2 extends BaseClass {
    private ApiUser apiUser;

    @BeforeEach
    public void setUp() {
        super.setAll();
        apiUser = new ApiUser(userSpec);
    }

    @Test
    @Story("auth logout/creation/updating")
    @Description("логин лoгаут создание обновление")
    public void test() {
        String userName = "user7";
        step("user creation", () -> {
            User user = new User(7, userName, "dinamo", "kiev", "john@example.com", "123", "123", 123);
            ApiResponse apiResponse = apiUser.createUser(user);
            Assertions.assertAll("check",
                    () -> assertEquals(200, apiResponse.getCode()),
                    () -> assertEquals("unknown", apiResponse.getType()),
                    () -> assertTrue(!apiResponse.getMessage().isEmpty()));

        });
        step("getUser", () -> {
            User user = apiUser.getByUserName(userName);
            Assertions.assertAll("check",
                    () -> assertEquals(7, user.getId()),
                    () -> assertEquals("user7", user.getUserName()),
                    () -> assertEquals("dinamo", user.getFirstName()),
                    () -> assertEquals("kiev", user.getLastName()),
                    () -> assertEquals("john@example.com", user.getEmail()),
                    () -> assertEquals("123", user.getPassword()),
                    () -> assertEquals("123", user.getPhone()),
                    () -> assertEquals(123, user.getUserStatus())
            );
        });
        step("updating", () -> {
            User userUpdate = new User(7, userName, "dinamo1", "kiev1", "john@example.com", "123", "123", 123);
            try {
                ApiResponse apiResponse = apiUser.putUser(userName, userUpdate);
                Assertions.assertAll("check",
                        () -> assertEquals(200, apiResponse.getCode()),
                        () -> assertEquals("unknown", apiResponse.getType()),
                        () -> assertTrue(!apiResponse.getMessage().isEmpty()));
                User user = apiUser.getByUserName(userName);
                Assertions.assertAll("check",
                        () -> assertEquals(7, user.getId()),
                        () -> assertEquals("user7", user.getUserName()),
                        () -> assertEquals("dinamo1", user.getFirstName()),
                        () -> assertEquals("kiev1", user.getLastName()),
                        () -> assertEquals("john@example.com", user.getEmail()),
                        () -> assertEquals("123", user.getPassword()),
                        () -> assertEquals("123", user.getPhone()),
                        () -> assertEquals(123, user.getUserStatus())
                );

            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

        });
        step("login", () -> {
            ApiResponse apiResponse = apiUser.login(userName, "123");
            Assertions.assertAll("check",
                    () -> assertEquals(200, apiResponse.getCode()),
                    () -> assertEquals("unknown", apiResponse.getType()),
                    () -> assertTrue(!apiResponse.getMessage().isEmpty()));
        });
        step("logout",()->{
            ApiResponse apiResponse=apiUser.logout();
            Assertions.assertAll("check",
                    () -> assertEquals(200, apiResponse.getCode()),
                    () -> assertEquals("unknown", apiResponse.getType()),
                    () -> assertTrue(!apiResponse.getMessage().isEmpty()));

        });
        step("delete",()->{
            ApiResponse apiResponse=apiUser.deleteUser(userName);
            Assertions.assertAll("check",
                    () -> assertEquals(200, apiResponse.getCode()),
                    () -> assertEquals("unknown", apiResponse.getType()),
                    () -> assertTrue(!apiResponse.getMessage().isEmpty()));
            apiUser.getAfterDelete(userName);

        });
        step("create/compare",()->{
            User user = new User(7, userName, "dinamo", "kiev", "john@example.com", "123", "123", 123);
            ApiResponse apiResponse1=apiUser.createUser(user);
            User getUser=apiUser.getByUserName(userName);
            Assertions.assertEquals(user,getUser);

        });


    }


}
