package pac15.ApiService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Assertions;
import pac15.base.BaseClass;
import pac15.model.ApiResponse;
import pac15.model.Order;

import static io.restassured.RestAssured.given;

public class ApiOrder extends BaseClass {
  private   RequestSpecification specification;
  public ApiOrder(RequestSpecification specification){
      if(specification==null){
          throw new IllegalArgumentException("spec is null");
      }
      else {
          this.specification=specification;
      }
  }
  public Order createOrder(Order order){
   return    given()
              .spec(specification)
              .body(order)
              .when()
              .post()
              .then()
              .statusCode(200)
              .extract()
              .as(Order.class);
  }
  public Order getOrderById(int id) throws JsonProcessingException {
      Response response=
      given()
              .spec(specification)
              .pathParam("id",id)
              .when()
              .get("/{id}")
              .then()
              .statusCode(200)
              .extract().response();
      ObjectMapper mapper=new ObjectMapper();
      Order order=mapper.readValue(response.asString(),Order.class);
      return order;
  }
  public int getPetIdByOrderId(int id) throws JsonProcessingException {
      Response response=
              given()
                      .spec(specification)
                      .pathParam("id",id)
                      .when()
                      .get("/{id}")
                      .then()
                      .statusCode(200)
                      .extract().response();
      ObjectMapper mapper=new ObjectMapper();
      JsonNode root=mapper.readTree(response.asString());
      Assertions.assertTrue(root.has("petId"));
      int petId=root.get("petId").asInt();
      return petId;
  }
  public ApiResponse deleteById(int id){
      return given()
              .spec(specification)
              .pathParam("id",id)
              .when()
              .delete("/{id}")
              .then()
              .statusCode(200)
              .extract().as(ApiResponse.class);
  }


}
