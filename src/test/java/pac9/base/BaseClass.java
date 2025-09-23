package pac9.base;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;

import static io.restassured.RestAssured.given;

public class BaseClass {
    protected RequestSpecification spec1;
    protected RequestSpecification spec2;
    @BeforeEach
    public void setUp1(){
        spec1=new RequestSpecBuilder()
                .setBaseUri("https://fakerestapi.azurewebsites.net")
                .setBasePath("/api/v1/Users")
                .setContentType(ContentType.JSON)
                .build();
    }
    @BeforeEach
    public void setUp2(){
        RestAssured.baseURI="https://fakerestapi.azurewebsites.net";
        RestAssured.basePath="/api/v1/Users";
        spec2= given()
                .header("Content_type","application/json")
                .log().all();
    }
}
