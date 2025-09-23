package pac13.model;

import io.restassured.specification.RequestSpecification;
import pac13.base.BaseClass;

import static io.restassured.RestAssured.given;

public class OrderApi extends BaseClass {
    RequestSpecification spec;
   public OrderApi(RequestSpecification spec){
        this.spec=spec;
    }
    public Order createOrder(Order order){
        return
                given()
                        .spec(spec)
                        .body(order)
                        .when()
                        .post()
                        .then()
                        .statusCode(200)
                        .extract()
                        .as(Order.class);
    }
    public ApiResponse deleteOrder(int id){
        return given()
                .spec(spec)
                .pathParam("id",id)
                .when()
                .delete("/{id}")
                .then()
                .statusCode(200)
                .extract()
                .as(ApiResponse.class);
    }
    public Order getOrder(int id){
        return
                given()
                        .spec(spec)
                        .pathParam("id",id)
                        .when()
                        .get("/{id}")
                        .then()
                        .statusCode(200)
                        .extract()
                        .response()
                        .as(Order.class);
    }

}
