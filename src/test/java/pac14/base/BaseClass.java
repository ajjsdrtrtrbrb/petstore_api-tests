package pac14.base;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.internal.RequestSpecificationImpl;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;

public class BaseClass {
    protected RequestSpecification orderSpec;
    protected RequestSpecification userSpec;
    protected RequestSpecification petSpec;
    @BeforeEach
    public void setUp(){
        orderSpec=new RequestSpecBuilder()
                .setBaseUri("https://petstore.swagger.io")
                .setBasePath("/v2/store/order")
                .setContentType(ContentType.JSON)
                .build();

        userSpec=new RequestSpecBuilder()
                .setBaseUri("https://petstore.swagger.io")
                .setBasePath("/v2/user")
                .setContentType(ContentType.JSON)
                .build();
        petSpec=new RequestSpecBuilder()
                .setBaseUri("https://petstore.swagger.io")
                .setBasePath("/v2/pet")
                .setContentType(ContentType.JSON)
                .build();

    }
}
