package pac6.base;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;

public class BaseClass {
   protected RequestSpecification requestSpecification;
    @BeforeEach
    public void setUp(){
        RestAssured.baseURI="https://fakerestapi.azurewebsites.net";
        requestSpecification=RestAssured.given()
                .header("Content-type","application/json")
                .log().all();
    }
}
