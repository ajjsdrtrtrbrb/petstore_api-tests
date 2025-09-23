package pac15.ApiService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Assertions;
import pac15.base.BaseClass;
import pac15.model.Pet;

import java.util.List;

import static io.restassured.RestAssured.given;

public class ApiPet extends BaseClass {
    private RequestSpecification specification;
    public ApiPet(RequestSpecification specification){
        this.specification=specification;
    }

    public List<Pet.Tags> getTagsFromCreatedPet(Pet pet) throws JsonProcessingException {
        Response response=
        given()
                .spec(specification)
                .body(pet)
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract().response();

        ObjectMapper mapper=new ObjectMapper();
        JsonNode root=mapper.readTree(response.asString());
        JsonNode tags=root.get("tags");
        Assertions.assertTrue(tags.isArray());
        List<Pet.Tags>list=mapper.convertValue(tags, new TypeReference<List<Pet.Tags>>() {
        });
        return list;
    }
    public Pet create(Pet pet){
        return
                given()
                        .spec(specification)
                        .body(pet)
                        .when()
                        .post()
                        .then()
                        .statusCode(200)
                        .extract()
                        .as(Pet.class);
    }
}
