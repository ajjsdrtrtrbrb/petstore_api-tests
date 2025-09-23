package pac6.test;

import io.restassured.RestAssured;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import pac6.base.BaseClass;
import pac6.model.User;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class Tests extends BaseClass {
    @ParameterizedTest
    @ValueSource(ints = {1,2,3})
    public void get(int id){
        given()
                .spec(requestSpecification)
                .basePath("/api/v1/Users")
                .when()
                .get("/{id}", id)
                .then()
                .statusCode(200)
                .body("id",equalTo(id));
    }
    @ParameterizedTest
    @ValueSource(ints = {1})
    public void test1(int id){
        User user= RestAssured.given()
                .spec(requestSpecification)
                .basePath("/api/v1/Users")
                .when()
                .get("/{id}",id)
                .then()
                .statusCode(200)
                .extract()
                .as(User.class);
        System.out.println(user);
        Assertions.assertEquals(id,user.getId());
        Assertions.assertNotNull(user.getUserName());
        Assertions.assertNotNull(user.getPassword());

    }

    @Test
    public void test2(){
        List<User>list=given()
                .spec(requestSpecification)
                .basePath("/api/v1/Users")
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .jsonPath()
                .getList("",User.class);
        for(User a:list){
            System.out.println(a);
            Assertions.assertNotNull(a.getUserName());
            Assertions.assertNotNull(a.getPassword());
        }
    }
}
