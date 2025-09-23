package pac15.ApiService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import pac15.base.BaseClass;
import pac15.model.ApiResponse;
import pac15.model.User;

import java.io.IOException;
import java.util.Optional;

import static io.restassured.RestAssured.given;

public class ApiUser extends BaseClass {
    private RequestSpecification specification;
    public ApiUser(RequestSpecification specification){
        if(specification==null){
            throw new IllegalArgumentException("spec is null");
        }
        else {
            this.specification=specification;
        }
    }
    public ApiResponse createUser(User user){
        return given()
                .spec(specification)
                .body(user)
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract()
                .as(ApiResponse.class);
    }
    public User getUserByUserName(String userName) throws JsonProcessingException {
        Response response=
                given()
                        .spec(specification)
                        .pathParam("userName",userName)
                        .when()
                        .get("/{userName}")
                        .then()
                        .statusCode(200)
                        .extract().response();
        ObjectMapper mapper=new ObjectMapper();
        return mapper.readValue(response.asString(),User.class);
    }
    public ApiResponse updateUser(String userName, User user) throws JsonProcessingException {
        Response response=
                given()
                        .spec(specification)
                        .pathParam("username",userName)
                        .body(user)
                        .when()
                        .put("/{username}")
                        .then()
                        .statusCode(200)
                        .extract().response();
        ObjectMapper mapper=new ObjectMapper();
        ApiResponse response1=mapper.readValue(response.asString(),ApiResponse.class);
        return response1;


    }
    public ApiResponse deleteUserByUserName(String userName){
        return given()
                .spec(specification)
                .pathParam("username",userName)
                .when()
                .delete("/{username}")
                .then()
                .statusCode(200)
                .extract()
                .as(ApiResponse.class);
    }

//    public Optional<User>get200or404(String userName){
//        Response response=
//                given()
//                        .spec(specification)
//                        .pathParam("username",userName)
//                        .when()
//                        .get("/{username}")
//                        .then()
//                        .extract().response();
//        int code=response.getStatusCode();
//        if(code==200){
//            try {
//                User user = new ObjectMapper().readValue(response.asString(), User.class);
//                return Optional.of(user);
//            }
//            catch (IOException e){
//                throw new RuntimeException("error desirialization");
//            }
//        }
//        else if(code==404){
//            System.out.println("user not found");
//            return Optional.empty();
//        }
//        else {
//            throw new RuntimeException("unexpected code");
//        }
//    }
}
