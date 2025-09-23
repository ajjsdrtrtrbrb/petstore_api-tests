package pac8.base;


import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;

public class BaseClass {

   protected RequestSpecification spec1;
    @BeforeEach
    public  void setUp1(){
        spec1=new RequestSpecBuilder()
                .setBaseUri("https://fakerestapi.azurewebsites.net")
                .setContentType(ContentType.JSON)
                .build();
    }
    protected RequestSpecification spec2;
            @BeforeEach
    public void setUp2(){
                RestAssured.baseURI="https://fakerestapi.azurewebsites.net";
        spec2=RestAssured
                .given()
                .header("Content-type","application/json")
                .log().all();
            }

}
