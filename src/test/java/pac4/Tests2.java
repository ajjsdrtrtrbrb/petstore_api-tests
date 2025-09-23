package pac4;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class Tests2 extends BaseClass2{
    @ParameterizedTest
    @ValueSource(ints = {1,2,3,4,5})
    public void get(int id){
        given()
                .spec(requestSpecification)
                .basePath("/api/v1/Users/"+id)
                .when()
                .get()
                .then()
                .statusCode(200)
                .body("id",equalTo(id));
    }
}
