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

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import pac10.Model.Pet;
import pac12.Exception.MyException;


import java.util.*;

public class Tests2 extends BaseClass {
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
        Response response =
                given()
                        .spec(spec1)
                        .body(json)
                        .when()
                        .post()
                        .then()
                        .statusCode(200)
                        .extract().response();
        String s = response.asString();
        ObjectMapper mapper = new ObjectMapper();
        Pet pet = mapper.readValue(s, Pet.class);
        Assertions.assertFalse(pet.getName().isEmpty());
        Assertions.assertEquals("benia", pet.getName());
        JsonNode root = mapper.readTree(s);
        Assertions.assertTrue(root.has("tags"));
        JsonNode tagsNode = root.get("tags");
        List<Pet.Tags> tagsList = Arrays.asList(mapper.readValue(tagsNode.toString(), Pet.Tags[].class));
        Assertions.assertEquals(15, tagsList.get(0).getId());
        Assertions.assertEquals("tag15", tagsList.get(0).getName());
        JsonNode node2 = root.get("category");
        Pet.Category category = mapper.treeToValue(node2, Pet.Category.class);
        Assertions.assertEquals(5, category.getId());
        Assertions.assertEquals("category5", category.getName());
        JsonNode node3 = root.get("photoUrls");
        List<String> photoUrlsList = Arrays.asList(mapper.readValue(node3.toString(), String[].class));
        Assertions.assertEquals("photoBenia", photoUrlsList.get(0));
        JsonNode node4 = root.get("tags");
        for (JsonNode sss : node4) {
            int id = sss.get("id").asInt();
            String name = sss.get("name").asText();
            System.out.println(id + " " + name);
            Assertions.assertNotNull(id);
            Assertions.assertTrue(!name.isEmpty());
        }
    }

    @ParameterizedTest
    @ValueSource(ints = 1)
    public void test2(int id) throws JsonProcessingException, MyException {
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
        Pet pet = new Pet(1, category, "benia", photoUrls, tags, "available");
        Response response =
                given()
                        .spec(spec1)
                        .body(pet)
                        .when()
                        .post()
                        .then()
                        .statusCode(200)
                        .extract().response();
        Pet pet1 = response.as(Pet.class);
        Assertions.assertTrue(pet1.getId() > 0);
        Assertions.assertEquals(1, pet1.getId());
        Assertions.assertEquals("benia", pet1.getName());
        Assertions.assertEquals("available", pet1.getStatus());
        List<Pet.Tags> petTags1 = pet1.getTags();
        int count = 0;
        for (Pet.Tags s : petTags1) {
            Assertions.assertEquals(++count, s.getId());
        }
        ObjectMapper mapper = new ObjectMapper();
        Pet pet2 = mapper.readValue(response.asString(), Pet.class);
        Assertions.assertEquals(1, pet1.getId());
        Assertions.assertEquals("benia", pet1.getName());
        List<String> listUrl = pet2.getPhotoUrls();
        Assertions.assertEquals("url1", listUrl.get(0));
        //-------------------------
        JsonNode root = mapper.readTree(response.asString());
        Pet pet3 = mapper.treeToValue(root, Pet.class);
        Assertions.assertEquals(1, pet3.getId());
        JsonNode tagss = root.get("tags");
        for (JsonNode s : tagss) {
            Assertions.assertTrue(s.has("id"));
            int id1 = s.get("id").asInt();
            String name = s.get("name").asText();
            System.out.println(id + " " + name);

        }
        List<Pet.Tags> list = Arrays.asList(mapper.readValue(tagss.toString(), Pet.Tags[].class));
        Assertions.assertTrue(tagss.isArray());
        int countt = 0;
        for (Pet.Tags s : list) {
            Assertions.assertEquals(++countt, s.getId());
        }
        Pet pet4 = given()
                .spec(spec1)
                .when()
                .get("{id}", id)
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .extract()
                .as(Pet.class);
        String json = mapper.writeValueAsString(pet4);
        System.out.println(json);
        Pet pet5 = mapper.readValue(json, Pet.class);
        Assertions.assertEquals(1, pet5.getId());
        Response response1 =
                given()
                        .queryParam("status", "available")
                        .when()
                        .get("findByStatus")
                        .then()
                        .statusCode(200)
                        .extract().response();
        JsonNode node2 = mapper.readTree(response1.asString());
        if (node2.isObject() || node2.isValueNode()) {
            throw new MyException("NOT ARRAY");
        }
        Assertions.assertTrue(node2.isArray(),"not array");
        Assertions.assertTrue(node2.size()>0,"is empty");
        int limit=Math.min(node2.size(),5);
        for(int i=0;i<limit;i++){
            JsonNode node=node2.get(i);
            Assertions.assertTrue(node.has("id"));
            Assertions.assertTrue(node.has("name"));
            int idd=node.get("id").asInt();
            String name=node.get("name").asText();
            System.out.println("id "+id+" name "+name);
        }


    }
}
