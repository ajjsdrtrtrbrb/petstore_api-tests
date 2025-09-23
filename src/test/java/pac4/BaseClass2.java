package pac4;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;

public class BaseClass2 {
    RequestSpecification requestSpecification;
    @BeforeEach
    public void setUp(){
        requestSpecification =new RequestSpecBuilder()
                .setBaseUri("https://fakerestapi.azurewebsites.net")
                .setContentType("application/json")
                .build();
    }
}
