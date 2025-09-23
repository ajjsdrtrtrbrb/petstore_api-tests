package pac5;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;


public class Tests extends BaseClass {
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    public void get(int id) {
        given()
                .spec(requestSpecification)
                .basePath("/api/v1/Users" )
                .when()
                .get("/{id}", id)
                .then()
                .statusCode(200)
                .body("id", equalTo(id));
    }

    @Test
    public void test1() {
        User user = given()
                .spec(requestSpecification)
                .basePath("/api/v1/Users/1")
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract()
                .as(User.class);
        System.out.println(user);
        Assertions.assertEquals(1, user.getId());
        Assertions.assertNotNull(user.getUserName());
    }

    @Test
    public void test2() {
        List<User> list =
                given()
                        .spec(requestSpecification)
                        .basePath("/api/v1/Users")
                        .when()
                        .get()
                        .then()
                        .statusCode(200)
                        .extract()
                        .body()
                        .jsonPath()
                        .getList("", User.class);
        for (User user : list) {
            System.out.println(user);
            Assertions.assertNotNull(user.getUserName());
        }
    }
}
