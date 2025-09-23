package pac12.Tests;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.junit.jupiter.api.Tags;
import pac12.Base.BaseClass;
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
import pac12.Exception.MyException;


import java.util.*;

public class Test1 extends BaseClass {

    @Order(1)
    @Test
    public void test1() throws JsonProcessingException {
        String json = "{\n" +
                "  \"id\": 8,\n" +
                "  \"category\": {\n" +
                "    \"id\": 5,\n" +
                "    \"name\": \"category5\"\n" +
                "  },\n" +
                "  \"name\": \"benia\",\n" +
                "  \"photoUrls\": [\n" +
                "    \"photoBenia\"\n" +
                "  ],\n" +
                "  \"tags\": [\n" +
                "    {\n" +
                "      \"id\": 15,\n" +
                "      \"name\": \"tag15\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"status\": \"available\"\n" +
                "}";
        Response response = given()
                .spec(spec1)
                .body(json)
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract().response();
        String responseBody = response.asString();
        ObjectMapper mapper = new ObjectMapper();
        Pet pet = mapper.readValue(responseBody, Pet.class);
        Assertions.assertEquals(8, pet.getId());
        Assertions.assertEquals("benia", pet.getName());
        JsonNode node1 = mapper.readTree(responseBody);
        Assertions.assertTrue(node1.has("id"));
        int id = node1.get("id").asInt();
        Assertions.assertEquals(id, pet.getId());
        JsonNode node2 = node1.get("tags");
        List<Pet.Tags> tags = mapper.readValue(node2.toString(), new TypeReference<List<Pet.Tags>>() {
        });
        Assertions.assertEquals(15, tags.get(0).getId());
        Assertions.assertEquals("tag15", tags.get(0).getName());
        JsonNode node3 = node1.get("photoUrls");
        List<String> photoUrls = mapper.readValue(node3.toString(), new TypeReference<List<String>>() {
        });
        Assertions.assertEquals("photoBenia", photoUrls.get(0));
        Pet pet2 = mapper.readValue(responseBody, Pet.class);
        Assertions.assertEquals("benia", pet2.getName());

    }

    @Order(2)
    @Test
    public void test2() {
        String json = "{\n" +
                "  \"id\": 8jjjjj,\n" +
                "  \"category\": {\n" +
                "    \"id\": 5,\n" +
                "    \"name\": \"category5\"\n" +
                "  },\n" +
                "  \"name\": \"benia\",\n" +
                "  \"photoUrls\": [\n" +
                "    \"photoBenia\"\n" +
                "  ],\n" +
                "  \"tags\": [\n" +
                "    {\n" +
                "      \"id\": 15,\n" +
                "      \"name\": \"tag15\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"status\": \"available\"\n" +
                "}";
        given()
                .spec(spec1)
                .body(json)
                .when()
                .post()
                .then()
                .statusCode(400);

    }

    @Order(3)
    @ParameterizedTest
    @ValueSource(ints = {777})
    public void test3(int id) {
        given()
                .spec(spec1)
                .when()
                .get("{id}", id)
                .then()
                .statusCode(404);
    }

    @Order(4)
    @ParameterizedTest
    @ValueSource(ints = {1})
    public void test4(int id) throws JsonProcessingException {

        Pet pet = given()
                .spec(spec1)
                .when()
                .get("{id}", id)
                .then()
                .statusCode(200)
                .extract()
                .as(Pet.class);
        Assertions.assertNotNull(pet.getId());
        Assertions.assertFalse(pet.getName().isEmpty());
        Response response =
                given()
                        .spec(spec1)
                        .when()
                        .get("/1")
                        .then()
                        .statusCode(200)
                        .extract().response();
        String body = response.asString();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode treeNode = mapper.readTree(body);
        JsonNode node2 = treeNode.get("tags");

        List<Pet.Tags> tags = mapper.readValue(node2.toString(), new TypeReference<List<Pet.Tags>>() {
        });
        Assertions.assertEquals(1, tags.get(0).getId());
        Assertions.assertEquals("string", tags.get(0).getName());
        JsonNode node3 = treeNode.get("photoUrls");
        List<String> photoUrl = mapper.readValue(node3.toString(), new TypeReference<List<String>>() {
        });
        Assertions.assertEquals("string", photoUrl.get(0));
    }

    @Order(5)
    @Test
    public void test5() throws JsonProcessingException {
        Pet.Category category = new Pet.Category(1, "category1");
        List<String> photoUrls = new ArrayList<>();
        String url1 = "url1";
        String url2 = "url2";
        photoUrls.add(url1);
        photoUrls.add(url2);
        Pet.Tags tags1 = new Pet.Tags(1, "tag1");
        Pet.Tags tags2 = new Pet.Tags(2, "tag2");
        List<Pet.Tags> tags = new ArrayList<>();
        tags.add(tags1);
        tags.add(tags2);
        Pet pet = new Pet(11, category, "benia", photoUrls, tags, "available");
        Response response =
                given()
                        .spec(spec1)
                        .body(pet)
                        .when()
                        .post()
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();
        ObjectMapper mapper = new ObjectMapper();
        Pet pet1 = mapper.readValue(response.asString(), Pet.class);
        Assertions.assertEquals(11, pet1.getId());
        Assertions.assertEquals("benia", pet1.getName());
        JsonNode root = mapper.readTree(response.asString());
        JsonNode tagsResponse = root.get("tags");
        List<Pet.Tags> tagsList = mapper.readValue(tagsResponse.toString(), new TypeReference<List<Pet.Tags>>() {
        });
        JsonNode photoUrlsResponse = root.get("photoUrls");
        List<String> photoUrlsList = mapper.readValue(photoUrlsResponse.toString(), new TypeReference<List<String>>() {
        });
        Assertions.assertEquals(1, tagsList.get(0).getId());
        Assertions.assertEquals("tag1", tagsList.get(0).getName());

        given()
                .spec(spec2)
                .when()
                .delete("/1")
                .then()
                .statusCode(200);
        given()
                .spec(spec2)
                .when()
                .get("/1")
                .then()
                .statusCode(404);
    }
    @Order(6)
    @Test
    public void test6(){
        Pet pet=
                given()
                        .spec(spec1)
                        .when()
                        .get("/2")
                        .then()
                        .statusCode(200)
                        .extract()
                        .as(Pet.class);
        Assertions.assertEquals(2,pet.getId());
    }
    @Order(7)
    @Test
    public void test7() throws JsonProcessingException {
        Pet.Category category=new Pet.Category(1,"category");
        List<Pet.Tags>tags=List.of(new Pet.Tags(1,"tag"));
        List<String>photoUrls=List.of("photoUrl");

        Map<String, Object>map=new HashMap<>();
        map.put("id",1);
        map.put("name","benia");
        map.put("category",category);
        map.put("status","available");
        map.put("tags",tags);
        map.put("photoUrls",photoUrls);
        given()
                .spec(spec1)
                .body(map)
                .when()
                .post()
                .then()
                .statusCode(200);
        ObjectMapper mapper=new ObjectMapper();
        Response response=
                given()
                        .spec(spec1)
                        .when()
                        .get("/1")
                        .then()
                        .statusCode(200)
                        .extract().response();
        Pet pet1=mapper.readValue(response.asString(),Pet.class);
        Assertions.assertEquals(1,pet1.getId());
        Assertions.assertEquals("benia",pet1.getName());
        Assertions.assertEquals("available",pet1.getStatus());
        JsonNode root=mapper.readTree(response.asString());
        JsonNode node1=root.get("tags");
        List<Pet.Tags>petTags=mapper.readValue(node1.toString(), new TypeReference<List<Pet.Tags>>() {
        });
        Assertions.assertEquals(1,petTags.get(0).getId());
        Assertions.assertEquals("tag",petTags.get(0).getName());

    }
    @Order(8)
    @Test
    public void test8() throws JsonProcessingException, MyException {
        Response response=
                given()
                        .spec(spec1)
                        .queryParam("status","available")
                        .when()
                        .get("/findByStatus")
                        .then()
                        .statusCode(200)
                        .extract().response();
        ObjectMapper mapper=new ObjectMapper();
        JsonNode root=mapper.readTree(response.asString());
        if(root.isObject()){
            throw new MyException("is object");
        }
        if(root.isValueNode()){
            throw new MyException("is valueNode");
        }
        Assertions.assertTrue(root.isArray(),"not array");
        Assertions.assertTrue(root.size()>0,"is empty");
        int limit = Math.min(5,root.size());
        for(int i=0;i<limit;i++){
            JsonNode node=root.get(i);
            Assertions.assertTrue(node.has("id"),"id doesnt exist id "+i);
            long idd=node.get("id").asLong();
            Assertions.assertTrue(idd>0,"wong id");
            Assertions.assertTrue(node.has("name"),"name doesnt exist");
            String name=node.get("name").asText();
            Assertions.assertFalse(name.isEmpty(),"name is empty");
            System.out.println("pet id "+(i+1)+" id "+idd+" name "+name);
        }

    }

}
