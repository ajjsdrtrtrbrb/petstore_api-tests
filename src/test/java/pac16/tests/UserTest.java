package pac16.tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pac16.api.ApiUser;
import pac16.base.BaseClass;
import pac16.model.ApiResponse;
import pac16.model.User;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserTest extends BaseClass {
    private  ApiUser apiUser;

    @BeforeEach
    public void setUp(){
        super.setAll();
        apiUser = new ApiUser(userSpec, userCreateWithList);
    }


    @Test
    public void test1() throws JsonProcessingException {

        List<User> users = new ArrayList<>();
        users.add(new User(101, "user1", "John", "Doe", "john@example.com", "12345", "555-1234", 1));
        users.add(new User(102, "user2", "Jane", "Doe", "jane@example.com", "54321", "555-5678", 2));
        ApiResponse apiResponse1 = apiUser.createWithList(users);
        Assertions.assertAll("check",
                () -> assertEquals(200, apiResponse1.getCode()),
                () -> assertEquals("unknown", apiResponse1.getType()),
                () -> assertEquals("ok", apiResponse1.getMessage()));

        step("проверка юзера по имени",
                ()->{
            String name=users.get(0).getUserName();
            User getUser=apiUser.getByUserName(name);
            Assertions.assertEquals("user1",name);
                });
    }
}
