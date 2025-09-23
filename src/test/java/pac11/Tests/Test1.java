package pac11.Tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import pac10.Model.Pet;
import pac11.Base.BaseClass;

import java.util.Arrays;
import java.util.List;

public class Test1 extends BaseClass {
    private String contentType="Content-Type";
    private String applicationJson="application/json";
    @Order(1)
    @Test
    public void get1() {
        Response response =
                given()
                        .spec(spec2)
                        .when()
                        .get("/1")
                        .then()
                        .statusCode(200)
                        .extract().response();
        String object = response.asString();
        System.out.println(object);
    }

    @Order(2)
    @Test
    public void get2() throws JsonProcessingException {
        Response response
                = given()
                .spec(spec2)
                .when()
                .get("/1")
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .extract().response();
        ObjectMapper mapper = new ObjectMapper();
        Pet pet = mapper.readValue(response.asString(), Pet.class);
        Assertions.assertEquals(1, pet.getId());
        Assertions.assertEquals("doggie", pet.getName());

    }

    @Order(3)
    @ParameterizedTest
    @ValueSource(ints = {1})
    public void get3(int id) {
        Pet pet =
                given()
                        .spec(spec1)
                        .when()
                        .get("{id}", id)
                        .then()
                        .statusCode(200)
                        .extract()
                        .as(Pet.class);
        Assertions.assertEquals(1, pet.getId());
    }

    @Order(4)
    @ParameterizedTest
    @ValueSource(ints = {1})
    public void get5(int id) throws JsonProcessingException {
        Response response =
                given()
                        .spec(spec1)
                        .when()
                        .get("{id}",id)
                        .then()
                        .statusCode(200)
                        .extract().response();
        ObjectMapper mapper=new ObjectMapper();
        JsonNode root=mapper.readTree(response.asString());
        JsonNode tags=root.get("tags");
        JsonNode photoUrls=root.get("photoUrls");
        List<Pet.Tags>list1=mapper.convertValue(tags, new TypeReference<List<Pet.Tags>>() {});
        String[]list2=mapper.treeToValue(photoUrls,String[].class);
        List<String>list3= Arrays.asList(list2);
        System.out.println(list1);
        System.out.println(list2);

    }
    @Order(5)
    @ParameterizedTest
    @ValueSource(ints = {2})
    public void get6(int id){
        given()
                .spec(spec1)
                .when()
                .get("{id}",id)
                .then()
                .statusCode(404);

    }
    @Order(6)
    @Test
    public void post1() throws JsonProcessingException {
        String json = "{\n" +
                "  \"id\": 5,\n" +
                "  \"category\": {\n" +
                "    \"id\": 5,\n" +
                "    \"name\": \"category 5\"\n" +
                "  },\n" +
                "  \"name\": \"doggie\",\n" +
                "  \"photoUrls\": [\n" +
                "    \"string\"\n" +
                "  ],\n" +
                "  \"tags\": [\n" +
                "    {\n" +
                "      \"id\": 5,\n" +
                "      \"name\": \"tags5\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"status\": \"available\"\n" +
                "}";
        given()
                .spec(spec1)
                .contentType(ContentType.JSON)
                .body(json)
                .when()
                .post()
                .then()
                .statusCode(200);
        Pet pet=
                given()
                        .spec(spec1)
                        .when()
                        .get("/5")
                        .then()
                        .statusCode(200)
                        .extract()
                        .as(Pet.class);
        Assertions.assertEquals(5,pet.getId());
        Assertions.assertEquals("doggie",pet.getName());
        Response response=
        given()
                .spec(spec1)
                .when()
                .get("/5")
                .then()
                .statusCode(200)
                .extract().response();
        ObjectMapper mapper=new ObjectMapper();
        JsonNode node=mapper.readTree(response.asString());
        JsonNode category=node.get("category");
        Pet.Category category1=mapper.treeToValue(category,Pet.Category.class);
        Assertions.assertEquals(5,category1.getId());
        Assertions.assertEquals("category 5",category1.getName());
    }

}
