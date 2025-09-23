package pac7.base;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;

public class BaseClass {
    protected RequestSpecification specification;
    @BeforeEach
    public void setUp1(){
        specification=new RequestSpecBuilder()
                .setBaseUri("https://fakerestapi.azurewebsites.net")
                .setContentType(ContentType.JSON)
                .build();
    }
    protected RequestSpecification requestSpecification;
    @BeforeEach
    public void setUp2(){
        RestAssured.baseURI="https://fakerestapi.azurewebsites.net";
        requestSpecification=RestAssured.given()
                .header("Content-type","application/json")
                .log().all();
    }
}
