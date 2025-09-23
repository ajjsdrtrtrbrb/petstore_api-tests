package pac14.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Assertions;
import pac14.base.BaseClass;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;

public class ApiPet extends BaseClass {
    private final RequestSpecification spec;

    public ApiPet(RequestSpecification spec) {
        if (spec == null) {
            throw new IllegalArgumentException("RequestSpecification cannot be null");
        }
        this.spec = spec;
    }

    public Pet createPetByObject(Pet pet) { ///
        return given()
                .spec(spec)
                .body(pet)
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract()
                .as(Pet.class);

    }

    public Pet createByJson(String json) throws JsonProcessingException {///
        ObjectMapper mapper = new ObjectMapper();
        Response response =
                given()
                        .spec(spec)
                        .body(json)
                        .when()
                        .post()
                        .then()
                        .statusCode(200)
                        .extract().response();
        return mapper.readValue(response.asString(), Pet.class);
    }

    public Pet updatePetByPut(Pet pet) {//
        return given()
                .spec(spec)
                .body(pet)
                .when()
                .put()
                .then()
                .statusCode(200)
                .extract()
                .as(Pet.class);
    }

    ////////////

    public ApiResponse updatePetByIdWithApiResponseResponse(int id, String name, String status) {
        ApiResponse apiResponse = new ApiResponse();
        Response response =
                given()
                        .spec(spec)
                        .contentType("application/x-www-form-urlencoded")
                        .formParam("name", name)
                        .formParam("status", status)
                        .pathParam("id", id)
                        .when()
                        .post("/{id}")
                        .then()
                        .statusCode(200)
                        .extract().response();
        apiResponse = response.as(ApiResponse.class);
        return apiResponse;
    }

    public List<Pet> getByStatus(String status) throws JsonProcessingException {///
        List<Pet> list = new ArrayList<>();
        Response response =
                given()
                        .spec(spec)
                        .queryParam("status", status)
                        .when()
                        .get("/findByStatus")
                        .then()
                        .statusCode(200)
                        .extract().response();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(response.asString());
        Assertions.assertTrue(node.isArray(), "response is not array");
        list = mapper.readValue(response.asString(), new TypeReference<List<Pet>>() {
        });
        return list;
    }

    public Pet getPetById(int id) throws JsonProcessingException {//////
        Response response =
                given()
                        .spec(spec)
                        .when()
                        .get("/{id}", id)
                        .then()
                        .statusCode(200)
                        .extract().response();
        ObjectMapper mapper = new ObjectMapper();
        Pet pet = mapper.readValue(response.asString(), Pet.class);
        return pet;
    }

    public ApiResponse deletePet(int id, String apiKey) { //
        return
                given()
                        .spec(spec)
                        .pathParam("id", id)
                        .header("api_key", apiKey)
                        .when()
                        .delete("/{id}")
                        .then()
                        .statusCode(200)
                        .extract()
                        .as(ApiResponse.class);
    }

    public Pet.Category getCategoryForPetId(int id) throws JsonProcessingException, MyEx {//
        Pet.Category category = new Pet.Category();
        Response response = given()
                .spec(spec)
                .pathParam("id", id)
                .when()
                .get("/{id}")
                .then()
                .statusCode(200)
                .extract()
                .response();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response.asString());
        JsonNode node=root.get("category");
        if(node!=null){
        category=mapper.treeToValue(node,Pet.Category.class);}
        else {
            throw new MyEx("null category");
        }
        return category;
    }
    public List<String> getPhotoForPetByPetId(int id) throws JsonProcessingException {//
        List<String>list=new ArrayList<>();
        Response response=
                given()
                        .spec(spec)
                        .pathParam("id",id)
                        .when()
                        .get("/{id}")
                        .then()
                        .statusCode(200)
                        .extract().response();
        ObjectMapper mapper=new ObjectMapper();
        Pet pet=mapper.readValue(response.asString(), Pet.class);
        list=pet.getPhotoUrls();
        return list;
    }
    public List<Pet.Tag> getPetTugsByPetId(int id) throws JsonProcessingException {//
        List<Pet.Tag>list;
        Response response=
                given()
                        .spec(spec)
                        .pathParam("id",id)
                        .when()
                        .get("/{id}")
                        .then()
                        .statusCode(200)
                        .extract().response();
        ObjectMapper mapper=new ObjectMapper();
        JsonNode root=mapper.readTree(response.asString());
        JsonNode node=root.get("tags");
        if(node!=null&&node.isArray()){
        list=mapper.readValue(node.toString(), new TypeReference<List<Pet.Tag>>() {
        });}
        else {
            list=new ArrayList<>();
        }
        return list;
    }
}
