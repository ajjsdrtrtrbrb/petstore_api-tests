package pac16.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pac16.base.BaseClass;
import pac16.model.ApiResponse;
import pac16.model.User;

import java.util.List;

import static io.restassured.RestAssured.given;

public class ApiUser extends BaseClass {
    private RequestSpecification specification1;
    private RequestSpecification specification2;


    public ApiUser(RequestSpecification userSpec, RequestSpecification userCreateWithList) {
        this.specification1 = userSpec;
        this.specification2 = userCreateWithList;
        this.mapper=new ObjectMapper();
    }
    public ApiUser(RequestSpecification userSpec){
        this.specification1 = userSpec;
        this.mapper=new ObjectMapper();
    }

    public ApiResponse createWithList(List<User> list) throws JsonProcessingException {
        Response response =
                given()
                        .spec(specification2)
                        .body(list)
                        .log().all()
                        .when()
                        .post()
                        .then()
                        .log().all()
                        .statusCode(200)
                        .extract()
                        .response();

        ApiResponse apiResponse = mapper.readValue(response.asString(), ApiResponse.class);


        return apiResponse;
    }
    public ApiResponse create2(String json){
        return given()
                .spec(specification1)
                .body(json)
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract()
                .as(ApiResponse.class);
    }

    public User getByUserName(String userName){
        return given()
                .spec(specification1)
                .pathParam("username", userName)
                .when()
                .get("/{username}")  // имена совпадают!
                .then()
                .statusCode(200)
                .extract()
                .as(User.class);
    }
    public ApiResponse putUser(String userName, User user) throws JsonProcessingException {
        Response response=
                given()
                        .spec(specification1)
                        .pathParam("username",userName)
                        .body(user)
                        .when()
                        .put("/{username}")
                        .then()
                        .statusCode(200)
                        .extract().response();
        ApiResponse apiResponse=mapper.readValue(response.asString(),ApiResponse.class);
        return apiResponse;

    }
    public ApiResponse deleteUser(String userName){
        return
        given()
                .spec(specification1)
                .pathParam("username",userName)
                .when()
                .delete("/{username}")
                .then()
                .statusCode(200)
                .extract()
                .as(ApiResponse.class);
    }
    public ApiResponse login(String userName,String password){
        return
                given()
                        .spec(specification1)
                        .queryParam("username",userName)
                        .queryParam("password",password)
                        .when()
                        .get("/login")
                        .then()
                        .statusCode(200)
                        .extract()
                        .as(ApiResponse.class);
    }
    public ApiResponse logout(){
        return
                given()
                        .spec(specification1)
                        .when()
                        .get("/logout")
                        .then()
                        .statusCode(200)
                        .extract()
                        .as(ApiResponse.class);

    }
    public ApiResponse createUser(User user){
        return
                given()
                        .spec(specification1)
                        .body(user)
                        .when()
                        .post()
                        .then()
                        .statusCode(200)
                        .extract()
                        .as(ApiResponse.class);
    }
    public void getAfterDelete(String userName){

                given()
                        .spec(specification1)
                        .pathParam("username",userName)
                        .when()
                        .get("/{username}")
                        .then()
                        .statusCode(404);

    }
    public Response get404(String userName){
        return given()
                .spec(specification1)
                .pathParam("username",userName)
                .when()
                .get("/{username}")
                .andReturn();
    }
    @Test
    public Response createWithList500(){
        return
        given()
                .spec(specification2)
                .body("{}")
                .when()
                .post()
                .andReturn();
    }
    public void post400(String json){
        Response response=
                given()
                        .spec(specification1)
                        .body(json)
                        .when()
                        .post()
                        .andReturn();
        int code=response.getStatusCode();
        Assertions.assertEquals(400,code);
    }
}
