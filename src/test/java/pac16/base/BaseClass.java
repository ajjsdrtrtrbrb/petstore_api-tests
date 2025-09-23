package pac16.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.internal.RequestSpecificationImpl;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import io.qameta.allure.Allure;
import java.lang.Runnable;

import static io.restassured.RestAssured.given;

public class BaseClass {
    protected RequestSpecification userSpec;
    protected RequestSpecification petSpec;
    protected RequestSpecification orderSpec;
    protected ObjectMapper mapper;
    protected RequestSpecification inventorySpec;
    protected RequestSpecification userCreateWithList;
    protected RequestSpecification formSpec;
    @BeforeEach
    public void setAll(){
        userSpec=new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setBaseUri("https://petstore.swagger.io")
                .setBasePath("/v2/user")
                .build();
        userCreateWithList=new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setBaseUri("https://petstore.swagger.io")
                .setBasePath("/v2/user/createWithList")
                .build();
        petSpec=new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setBaseUri("https://petstore.swagger.io")
                .setBasePath("/v2/pet")
                .build();
        RestAssured.baseURI="https://petstore.swagger.io";
        RestAssured.basePath="/v2/store/order";
        given()
                .header("Content-Type","application/json");
        inventorySpec=new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setBaseUri("https://petstore.swagger.io")
                .setBasePath("/v2/store/inventory")
                .build();

        mapper=new ObjectMapper();
        orderSpec=new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setBaseUri("https://petstore.swagger.io")
                .setBasePath("/v2/store/order")
                .build();
        formSpec=new RequestSpecBuilder()
                .setBaseUri("https://petstore.swagger.io")
                .setBasePath("/v2/pet")
                .setContentType("application/x-www-form-urlencoded")
                .setAccept("application/json")
                .build();
    }
    protected void step(String name, Runnable action) {
        Allure.step(name, () -> {
            try {
                action.run();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

    }
}
