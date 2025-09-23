package pac15.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import io.qameta.allure.Allure;

import java.lang.Runnable;


public class BaseClass {
    protected ObjectMapper mapper;
    protected RequestSpecification userSpec;
    protected RequestSpecification orderSpec;
    protected RequestSpecification petSpec;
    protected RequestSpecification inventorySpec;

    @BeforeEach
    public void setUpAll() {
        mapper = new ObjectMapper();
        userSpec = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setBaseUri("https://petstore.swagger.io")
                .setBasePath("/v2/user")
                .build();
        orderSpec = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setBaseUri("https://petstore.swagger.io")
                .setBasePath("/v2/store/order")
                .build();
        petSpec = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setBaseUri("https://petstore.swagger.io")
                .setBasePath("/v2/pet")
                .build();
        inventorySpec = new RequestSpecBuilder()
                .setBaseUri("https://petstore.swagger.io")
                .setBasePath("/v2/store/inventory")
                .setContentType(ContentType.JSON)
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
