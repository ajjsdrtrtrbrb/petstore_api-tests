package pac13.model;

import io.restassured.response.Response;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.checkerframework.checker.units.qual.PolyUnit;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import pac13.model.Pet;
import pac13.model.User;
import pac13.model.Order;
import pac13.model.ApiResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;

import org.junit.jupiter.api.Test;
import pac13.base.BaseClass;
import io.restassured.response.Response;
import org.mockito.Mockito;

import static org.mockito.Mockito.when;


import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import static io.restassured.RestAssured.given;

public class ApiUser extends BaseClass{
    private RequestSpecification spec;

    public ApiUser(RequestSpecification spec) {
        this.spec = spec;
    }
    public ApiResponse createUser(User user){
        ApiResponse response=new ApiResponse();
        return response=
                given()
                        .spec(spec)
                        .body(user)
                        .when()
                        .post()
                        .then()
                        .statusCode(200)
                        .extract().as(ApiResponse.class);


    }
    public User getUser(String userName){
        return given()
                .spec(spec)
                .pathParam("username",userName)
                .log().all()
                .when()
                .get("/{username}")
                .then()
                .log().all()
                .statusCode(200)
                .extract()
                .as(User.class);
    }
    public ApiResponse deleteUser(String userName){
         ApiResponse response=
                given()
                        .spec(spec)
                        .when()
                        .delete("/"+userName)
                        .then()
                        .statusCode(200)
                        .extract()
                        .response()
                        .as(ApiResponse.class);
         return response;
    }
}
