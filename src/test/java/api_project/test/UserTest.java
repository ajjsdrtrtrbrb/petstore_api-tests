package api_project.test;

import api_project.base.BaseClass;
import api_project.model.ApiResponse;
import api_project.model.User;
import api_project.api.ApiUser;
import api_project.model.UserBuilder;
import api_project.utils.UserUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Epic("USER CRUD") // Эпик для Allure
@Feature("user create get update delete login logout create with list") // Фича для Allure
@Tag("api") // Тег для CI, фильтрует тесты
@Tag("user")
public class UserTest extends BaseClass {

    private final ApiUser apiUser = new ApiUser(userSpec, userCreateWithList, userCreateWithArray,
            baseResponseSpecification, userCreateResponseSpecification, mapper);

    // ================================
    @Story("creation user")
    @Description("Создание пользователя с объектом User")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Создание пользователя с объектом User")
    @Test
    public void userCreateWithObject() throws JsonProcessingException {
        User user = new User(55, "testuser", "firstname", "lastname", "testuser@gmail.com", "123", "123", 1);
        ApiResponse response = apiUser.createUser(user);

        // Проверяем код ответа и обязательные поля
        assertAll("Проверка полей ответа",
                () -> assertEquals(200, response.getCode()),
                () -> assertEquals("unknown", response.getType()),
                () -> assertTrue(!response.getMessage().isEmpty())
        );
    }

    // ================================
    @Story("create get delete")
    @Description("Создание, получение и удаление пользователя")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Создание, получение и удаление пользователя")
    @Test
    public void createGetDelete() throws JsonProcessingException {
        String username = "dinamo";

        // Генерируем уникальный ID пользователя
        int id = (int) System.currentTimeMillis() % 100000;
        User user = new UserBuilder()
                .id(id)
                .username(username)
                .firstName("First")
                .lastName("Last")
                .email(username + "@gmail.com")
                .password("123")
                .phone("123")
                .userStatus(1)
                .build();

        // Создаем пользователя
        ApiResponse createResp = apiUser.createUser(user);
        assertEquals(200, createResp.getCode());

        // Получаем пользователя по username и проверяем все поля
        User getUser = apiUser.getUser(username);
        assertAll("Проверка полей пользователя",
                () -> assertEquals(username, getUser.getUsername(), "Username"),
                () -> assertEquals("First", getUser.getFirstName(), "FirstName"),
                () -> assertEquals("Last", getUser.getLastName(), "LastName"),
                () -> assertEquals(username + "@gmail.com", getUser.getEmail(), "Email"),
                () -> assertEquals("123", getUser.getPassword(), "Password"),
                () -> assertEquals("123", getUser.getPhone(), "Phone"),
                () -> assertEquals(1, getUser.getUserStatus(), "UserStatus")
        );

        // Удаляем пользователя
        ApiResponse delResp = apiUser.deleteUser(username);
        assertEquals(200, delResp.getCode());
    }

    // ================================
    @Story("удаление несуществующего юзера")
    @Description("Удаление несуществующего пользователя")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Удаление несуществующего пользователя")
    @ParameterizedTest
    @ValueSource(strings = {"sfwefefwf"})
    public void deleteTest404(String username) {
        int code = apiUser.delete404(username).getStatusCode();
        assertEquals(404, code);
    }

    // ================================
    @Story("получение несуществующего юзера")
    @Description("Попытка получить несуществующего пользователя")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Получение несуществующего пользователя")
    @ParameterizedTest
    @ValueSource(strings = {"sfwefefwf"})
    public void get404(String userName) throws JsonProcessingException {
        ApiResponse response = apiUser.get404(userName);
        assertEquals("User not found", response.getMessage());
    }

    // ================================
    @Story("логин юзер")
    @Description("Логин пользователя")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Логин пользователя")
    @ParameterizedTest(name = "Login user {0} with password {1}")
    @CsvSource({"test,123", "test2,456", "test3,789"})
    public void loginTest(String userName, String password) throws JsonProcessingException {
        ApiResponse apiResponse = apiUser.loginUser(userName, password);
        assertAll("Проверка полей ответа",
                () -> assertEquals(200, apiResponse.getCode()),
                () -> assertEquals("unknown", apiResponse.getType()),
                () -> assertNotNull(apiResponse.getMessage())
        );
    }

    // ================================
    @Story("логаут юзер")
    @Description("Логаут пользователя")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Логаут пользователя")
    @Test
    public void logoutTest() throws JsonProcessingException {
        ApiResponse apiResponse = apiUser.logout();
        assertAll("Проверка полей ответа",
                () -> assertEquals(200, apiResponse.getCode()),
                () -> assertEquals("unknown", apiResponse.getType()),
                () -> assertNotNull(apiResponse.getMessage())
        );
    }

    // ================================
    @Story("create user with json")
    @Description("Создание пользователя с JSON")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Создание пользователя с JSON")
    @ParameterizedTest(name = "create user #{index}")
    @MethodSource("api_project.utils.UserUtils#userJsonStream")
    public void createUserWithJson(String json) throws JsonProcessingException {
        ApiResponse apiResponse = apiUser.createWithJson(json);
        assertAll("Проверка полей ответа",
                () -> assertEquals(200, apiResponse.getCode()),
                () -> assertEquals("unknown", apiResponse.getType()),
                () -> assertNotNull(apiResponse.getMessage())
        );
    }

    // ================================
    @Story("put user")
    @Description("Обновление пользователя")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Обновление пользователя")
    @ParameterizedTest(name = "user put #{0}")
    @MethodSource("api_project.utils.UserUtils#userPutStream")
    public void putUser(String userName, User user) throws JsonProcessingException {
        ApiResponse apiResponse = apiUser.putUser(userName, user);
        assertAll("Проверка полей ответа",
                () -> assertEquals(200, apiResponse.getCode()),
                () -> assertEquals("unknown", apiResponse.getType()),
                () -> assertNotNull(apiResponse.getMessage())
        );
    }

    // ================================
    @Story("create user with list")
    @Description("Создание пользователей списком")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Создание пользователей списком")
    @Test
    public void createUserWithList() throws JsonProcessingException {
        List<User> users1 = UserUtils.generateListUsersRandomId(5);
        List<User> users2 = UserUtils.generateListUserWithFixedId(1,5);

        ApiResponse apiResponse1 = apiUser.createWithList(users1);
        ApiResponse apiResponse2 = apiUser.createWithList(users2);

        assertEquals(200, apiResponse1.getCode());
        assertEquals(200, apiResponse2.getCode());
    }

    // ================================
    @Story("create user with array")
    @Description("Создание пользователей массивом")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Создание пользователей массивом")
    @Test
    public void createUserWithArray() throws JsonProcessingException {
        List<User> list = UserUtils.generateListUserWithFixedId(1,5);
        ApiResponse apiResponse = apiUser.createWithArray(list);
        assertEquals(200, apiResponse.getCode());
    }

    // ================================
    @Story("create user with invalid json")
    @Description("Попытка создать пользователя с неверным JSON")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Создание пользователя с неверным JSON")
    @ParameterizedTest
    @ValueSource(strings = {"{]", "{invalid}", "{username:}"})
    public void response400(String json) {
        int code = apiUser.createUserWithInvalidJsonReturn400(json);
        assertEquals(400, code);
    }

    // ================================
    @Story("invalid user put")
    @Description("Попытка обновить пользователя неверным JSON")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Обновление пользователя неверным JSON")
    @ParameterizedTest(name = "invalid put user #{0}")
    @MethodSource("api_project.utils.UserUtils#userInvalidPut")
    public void invalidPut(String userName, String json) {
        int code = apiUser.putUser400(userName, json);
        assertEquals(400, code);
    }
}
