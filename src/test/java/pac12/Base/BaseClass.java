package pac12.Base;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;

import static io.restassured.RestAssured.given;

public class BaseClass {
    protected RequestSpecification spec1;
    protected RequestSpecification spec2;
    protected RequestSpecification specForUser;
    @BeforeEach
    public void setUp1(){
        spec1=new RequestSpecBuilder()
                .setBaseUri("https://petstore.swagger.io")
                .setBasePath("/v2/pet")
                .setContentType(ContentType.JSON)
                .build();
    }
    @BeforeEach
    public void setUp2(){
        RestAssured.baseURI="https://petstore.swagger.io";
        RestAssured.basePath="/v2/pet";
        spec2= given()
                .header("Content-Type","application/json")
                .log().all();
    }
    @BeforeEach
    public void setUp3(){
        specForUser=new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setBasePath("/v2/user")
                .setBaseUri("https://petstore.swagger.io")
                .build();
    }
}
