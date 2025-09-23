package pac14.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import pac14.base.BaseClass;

import static io.restassured.RestAssured.given;

public class OrderApi extends BaseClass {
    RequestSpecification requestSpecification;
    public OrderApi(RequestSpecification requestSpecification){
        this.requestSpecification=requestSpecification;
    }

    public Order createOrder(String json) throws JsonProcessingException {
        Response response=
                given()
                        .spec(requestSpecification)
                        .body(json)
                        .when()
                        .post()
                        .then()
                        .statusCode(200)
                        .extract().response();
        ObjectMapper mapper=new ObjectMapper();
        Order order=mapper.readValue(response.asString(),Order.class);
        return order;

    }
    public Order getById(int id){
        return given()
                .spec(requestSpecification)
                .pathParam("id",id)
                .when()
                .get("/{id}")
                .then()
                .statusCode(200)
                .extract().as(Order.class);
    }
    public JsonNode getNodeById(int id) throws JsonProcessingException {
        JsonNode node;
        Response response=
                given()
                        .spec(requestSpecification)
                        .pathParam("id",id)
                        .when()
                        .get("/{id}")
                        .then()
                        .statusCode(200)
                        .extract().response();
        ObjectMapper mapper=new ObjectMapper();
        node=mapper.readTree(response.asString());
        return node;
    }
}
