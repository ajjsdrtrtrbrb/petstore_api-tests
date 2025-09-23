package pac16.api;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Assertions;
import pac16.base.BaseClass;
import pac16.model.ApiResponse;
import pac16.model.Pet;
import pac16.model.User;

import java.util.List;

import static io.restassured.RestAssured.given;

public class ApiPet extends BaseClass {
    private RequestSpecification spec1;
    private RequestSpecification spec2;
    public ApiPet(RequestSpecification spec1){
        this.spec1=spec1;
        mapper=new ObjectMapper();
    }
    public ApiPet(RequestSpecification spec1,RequestSpecification spec2){
        this.spec1=spec1;
        this.spec2=spec2;
        mapper=new ObjectMapper();
    }
    public Pet createPet(Pet pet) throws JsonProcessingException {
       Response response=
               given()
                       .spec(spec1)
                       .body(pet)
                       .when()
                       .post()
                       .then()
                       .statusCode(200)
                       .extract().response();
       Pet pet1=mapper.readValue(response.asString(),Pet.class);
       return pet1;
    }
    public Pet createPetWithJson(String json) throws JsonProcessingException {
        Response response=
                given()
                        .spec(spec1)
                        .body(json)
                        .when()
                        .post()
                        .then()
                        .statusCode(200)
                        .extract().response();
        return mapper.readValue(response.asString(),Pet.class);
    }
    public Pet putPet(Pet pet){
        return
                given()
                        .spec(spec1)
                        .body(pet)
                        .when()
                        .put()
                        .then()
                        .statusCode(200)
                        .extract()
                        .as(Pet.class);
    }
    public List<String> getPetWithAvailableStatus(){
        List<String>list=
                given()
                        .spec(spec1)
                        .queryParam("status","available")
                        .when()
                        .get("/findByStatus")
                        .then()
                        .statusCode(200)
                        .extract()
                        .jsonPath()
                        .getList("status");
        return list;
    }
    public List<String> getPetWithPendingStatus(){
        List<String>list=
                given()
                        .spec(spec1)
                        .queryParam("status","pending")
                        .when()
                        .get("/findByStatus")
                        .then()
                        .statusCode(200)
                        .extract()
                        .jsonPath()
                        .getList("status");
        return list;
    }
    public List<String> getPetWithSoldStatus(){
        List<String>list=
                given()
                        .spec(spec1)
                        .queryParam("status","sold")
                        .when()
                        .get("/findByStatus")
                        .then()
                        .statusCode(200)
                        .extract()
                        .jsonPath()
                        .getList("status");
        return list;
    }
    public List<Pet> get(String status){
        List<Pet>list=
        given()
                .spec(spec1)
                .queryParam("status",status)
                .when()
                .get("/findByStatus")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList("",Pet.class);
        return list;
    }
    public Pet getPetById(long id){
        return  given()
                .spec(spec1)
                .pathParam("id",id)
                .when()
                .get("/{id}")
                .then()
                .statusCode(200)
                .extract()
                .as(Pet.class);
    }
   public ApiResponse getFor404Status(int id){
        return
                given()
                        .spec(spec1)
                        .when()
                        .pathParam("id",id)
                        .when()
                        .get("/{id}")
                        .then()
                        .statusCode(404)
                        .extract()
                        .as(ApiResponse.class);
    }
   public ApiResponse updatePetWithForm(long id, String name, String status){
        return
                given()
                        .spec(spec2)
                        .formParam("name",name)
                        .formParam("status",status)
                        .when()
                        .post("{id}",id)
                        .then()
                        .statusCode(200)
                        .extract()
                        .as(ApiResponse.class);
    }
   public ApiResponse deletePet(long id){
        return
                given()
                        .spec(spec1)
                        .pathParam("id",id)
                        .when()
                        .delete("/{id}")
                        .then()
                        .statusCode(200)
                        .extract()
                        .as(ApiResponse.class);
    }
}
