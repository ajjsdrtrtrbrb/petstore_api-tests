package pac15.tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pac15.ApiService.ApiUser;
import pac15.base.BaseClass;
import pac15.model.ApiResponse;
import pac15.model.User;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Epic("user api")
@Feature("crud user")
public class UserTest extends BaseClass {
    @Test
    @Story("creation user")
    @Description("создание юзера")
    public void createAndGetUserTest(){
        ApiUser apiUser=new ApiUser(userSpec);
        User user = new User(101, "dinamo", "John", "Doe",
                "john.doe@example.com", "12345", "555-1234", "1");
        step("cоздание юзера",()->{

                ApiResponse response=apiUser.createUser(user);
            assertEquals(200,response.getCode());
        });
        step("получение юзера по юзернейм",()->{
            try {
                User getUser=apiUser.getUserByUserName("dinamo");
                assertEquals("dinamo", getUser.getUserName());
                assertEquals("John", getUser.getFirstName());
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
        step("Удалить пользователя",()->{
            ApiResponse response=apiUser.deleteUserByUserName("dinamo");
            Assertions.assertEquals(200,response.getCode());
        });
        step("проверить удалилися ли пользователь",()->{

            given()
                    .spec(userSpec)
                    .pathParam("username",user.getUserName())
                    .when()
                    .get("/{username}")
                    .then()
                    .statusCode(404);
        });
    }
    @Test
    public void test(){
        given()
                .spec(userSpec)
                .pathParam("username", "dinamo")
                .when()
                .delete("/{username}")
                .then()
                .statusCode(200); // или 204
    }
}
