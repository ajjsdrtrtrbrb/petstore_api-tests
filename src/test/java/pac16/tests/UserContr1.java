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

import static org.junit.jupiter.api.Assertions.assertEquals;

@Epic("user api")
@Feature("user create get update delete login logout create with list")
public class UserContr1 extends BaseClass {
    private ApiUser apiUser;

    @BeforeEach
    public void setUp() {
        super.setAll();
        apiUser = new ApiUser(userSpec, userCreateWithList);
    }

    @ParameterizedTest
    @ValueSource(strings = {"user1", "user2"})
    @Story("create user list")
    @Description("create users")
    public void test1(String userName) throws JsonProcessingException {
        List<User> users = new ArrayList<>();
        users.add(new User(101, "user1", "John", "Doe", "john@example.com", "12345", "555-1234", 1));
        users.add(new User(102, "user2", "Jane", "Doe", "jane@example.com", "54321", "555-5678", 2));
        ApiResponse apiResponse = apiUser.createWithList(users);
        Assertions.assertAll("check",
                () -> assertEquals(200, apiResponse.getCode()),
                () -> assertEquals("unknown", apiResponse.getType()),
                () -> assertEquals("ok", apiResponse.getMessage()));
        User getUser = apiUser.getByUserName(userName);
        Assertions.assertTrue(getUser.getId() > 0);
        Assertions.assertEquals(userName, getUser.getUserName());


    }

    @Story("user login logout")
    @Description("user login logout")
    @Test
    public void loginLogout() {
        step("login", () -> {
            String userName = "user1";
            String password = "123";
            ApiResponse response1 = apiUser.login(userName, password);

            Assertions.assertAll("check",
                    () -> assertEquals(200, response1.getCode()),
                    () -> assertEquals("unknown", response1.getType())

            );
        });

        step("logout", () -> {
            ApiResponse response = apiUser.logout();

            Assertions.assertAll("check",
                    () -> assertEquals(200, response.getCode()),
                    () -> assertEquals("unknown", response.getType()),
                    () -> assertEquals("ok", response.getMessage())
            );
        });
    }

    @Story("create get update delete")
    @Description("create get update delete")
    @Test
    public void test3() {
        User user = new User(101, "user1", "John", "Doe", "john@example.com", "12345", "555-1234", 1);
        step("create", () -> {
            ApiResponse createResponse = apiUser.createUser(user);
            Assertions.assertEquals(200, createResponse.getCode());
        });
        step("get", () -> {
            User getUser = apiUser.getByUserName("user1");
            Assertions.assertEquals("user1", getUser.getUserName());
        });
        step("update", () -> {
            User user2 = new User(102, "user1", "John8", "Doe", "john@example.com", "12345", "555-1234", 1);
            try {
                ApiResponse response1 = apiUser.putUser("user1", user2);
                Assertions.assertEquals(200, response1.getCode());
                User user1 = apiUser.getByUserName("user1");
                Assertions.assertEquals("John", user1.getFirstName());
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }


        });
        step("delete", () -> {
            ApiResponse response = apiUser.deleteUser("user1");
            Assertions.assertEquals(200, response.getCode());
            apiUser.getAfterDelete("user1ffffff");
        });



    }
    @Story("404 get")
    @Description("404 get")
    @ParameterizedTest()
    @ValueSource(strings = {"shdhcscd"})
    public void get404(String userName){
        Response response=apiUser.get404(userName);
        Assertions.assertEquals(404,response.getStatusCode());

    }
    @Story("create list 500")
    @Description("create list 500")
    @Test
    public void createList500(){

        Response response=apiUser.createWithList500();
        int code=response.getStatusCode();
        Assertions.assertEquals(500,code);
    }
 @Story("404 post")
 @Description("404 post")
 @ParameterizedTest
 @ValueSource(strings = {"{\n" +
         "  \"id\": 12345@,\n" +
        // "  \"username\": \"@@@lllalclccs\",\n" +
       //  "  \"firstName\": \"Test\",\n" +
         "  \"lastName\": \"User\",\n" +
         "  \"emafffil\": \"testuser@example.com\",\n" +
         "  \"password\": \"password123\",\n" +
         "  \"phone\": \"+1234567890\",\n" +
         "  \"userStatus\": 1\n" +
         "}"})
    public void test2(String json){
        apiUser.post400(json);

 }
 @Test
    public void test() throws JsonProcessingException {
     String usersJson = "[\n" +
             "  {\n" +
             "    \"id\": 1,\n" +
             "    \"username\": \"user1\",\n" +
             "    \"firstName\": \"John\",\n" +
             "    \"lastName\": \"Doe\",\n" +
             "    \"email\": \"john.doe@example.com\",\n" +
             "    \"password\": \"12345\",\n" +
             "    \"phone\": \"555-1111\",\n" +
             "    \"userStatus\": \"1\"\n" +
             "  },\n" +
             "  {\n" +
             "    \"id\": 2,\n" +
             "    \"username\": \"user2\",\n" +
             "    \"firstName\": \"Jane\",\n" +
             "    \"lastName\": \"Smith\",\n" +
             "    \"email\": \"jane.smith@example.com\",\n" +
             "    \"password\": \"54321\",\n" +
             "    \"phone\": \"555-2222\",\n" +
             "    \"userStatus\": \"1\"\n" +
             "  },\n" +
             "  {\n" +
             "    \"id\": 3,\n" +
             "    \"username\": \"user3\",\n" +
             "    \"firstName\": \"Alice\",\n" +
             "    \"lastName\": \"Johnson\",\n" +
             "    \"email\": \"alice.johnson@example.com\",\n" +
             "    \"password\": \"abcd1234\",\n" +
             "    \"phone\": \"555-3333\",\n" +
             "    \"userStatus\": \"1\"\n" +
             "  }\n" +
             "]";
     Response response = Mockito.mock(Response.class);
     Mockito.when(response.statusCode()).thenReturn(200);
     Mockito.when(response.asString()).thenReturn(usersJson);
     ObjectMapper mapper = new ObjectMapper();
     Assertions.assertEquals(200, response.statusCode());
     List<pac15.model.User> list = mapper.readValue(response.asString(), new TypeReference<List<pac15.model.User>>() {
     });
     int limit = Math.min(list.size(), 3);
     int count = 0;
     for (int i = 0; i < limit; i++) {
         Assertions.assertEquals(++count, list.get(i).getId(), "wrong id");
     }

 }
}
