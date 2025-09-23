package pac10.Base;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;

public class BaseClass {
    protected RequestSpecification spec1;
    protected RequestSpecification spec2;
    @BeforeEach
    public void setSpec1(){
        spec1=new RequestSpecBuilder()
                .setBaseUri("https://petstore.swagger.io")
                .setBasePath("/v2/pet")
                .setContentType(ContentType.JSON)
                .build();
    }
    @BeforeEach
    public void setSpec2(){
        RestAssured.baseURI="https://petstore.swagger.io";
        RestAssured.basePath="/v2/pet";
        spec2=RestAssured.given()
                .header("Content-Type","application/json")
                .log().all();
    }
}
