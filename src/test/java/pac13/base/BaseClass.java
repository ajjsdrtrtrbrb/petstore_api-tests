package pac13.base;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;

import static io.restassured.RestAssured.given;

public class BaseClass {
    protected RequestSpecification spec;
    protected RequestSpecification specOrder;
    protected RequestSpecification specPet;
    @BeforeEach
    public void setUpAll(){
        spec=new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setBaseUri("https://petstore.swagger.io")
                .setBasePath("/v2/user")
                .build();

        RestAssured.baseURI="https://petstore.swagger.io";
        RestAssured.basePath="/v2/store";
        given()
                .header("Content-type","application/json");


        specPet =new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setBaseUri("https://petstore.swagger.io")
                .setBasePath("/v2/pet")
                .build();

        specOrder=new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setBaseUri("https://petstore.swagger.io")
                .setBasePath("/v2/store/order")
                .build();
    }
}
