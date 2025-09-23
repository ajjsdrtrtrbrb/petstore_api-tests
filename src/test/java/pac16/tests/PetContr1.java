package pac16.tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import pac16.api.ApiPet;
import pac16.base.BaseClass;
import pac16.model.ApiResponse;
import pac16.model.Pet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Epic("pet api")
@Feature("pet crud")

public class PetContr1 extends BaseClass {
    private ApiPet apiPet;

    @BeforeEach
    public void setUp() {
        super.setAll();
        apiPet = new ApiPet(petSpec, formSpec);
    }

    @Story("create pet by json")
    @Description("create pet by json")
    @Test
    public void creationTest() throws JsonProcessingException {
        String json = "{\n" +
                "  \"id\": 123,\n" +
                "  \"category\": {\n" +
                "    \"id\": 1,\n" +
                "    \"name\": \"Dogs\"\n" +
                "  },\n" +
                "  \"name\": \"Rex\",\n" +
                "  \"photoUrls\": [\n" +
                "    \"https://example.com/photo/rex1.jpg\"\n" +
                "  ],\n" +
                "  \"tags\": [\n" +
                "    {\n" +
                "      \"id\": 101,\n" +
                "      \"name\": \"friendly\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": 102,\n" +
                "      \"name\": \"trained\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"status\": \"available\"\n" +
                "}";
        step("creation by json", () -> {
            try {
                Pet pet = apiPet.createPetWithJson(json);
                Assertions.assertEquals(1, pet.getCategory().getId());
                Assertions.assertEquals(101, pet.getTags().get(0).getId());
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

        });
        step("get by id", () -> {
            Pet pet = apiPet.getPetById(123);
            Assertions.assertTrue( pet.getId()>0);
            Assertions.assertEquals("available", pet.getStatus());
        });


    }
    @Test
    public void get404(){
        ApiResponse response=apiPet.getFor404Status(85555);
        Assertions.assertEquals(1,response.getCode());
    }
    @Test
    public void test2(){
            Pet.Category category = new Pet.Category();
            category.setId(1);
            category.setName("category");
            Pet.Tags tag1 = new Pet.Tags();
            tag1.setId(1);
            tag1.setName("tag1");
            Pet.Tags tag2=new Pet.Tags(2,"tag2");
            List<Pet.Tags>tags=new ArrayList<>(Arrays.asList(new Pet.Tags[]{tag1,tag2}));
            String photo="photo";
            List<String>photoUrls=new ArrayList<>();
            photoUrls.add(photo);
            long id=System.currentTimeMillis();
            Pet pet=new Pet(id,category,"dinamo",photoUrls,tags,"available");
            try {
                Pet created=apiPet.createPet(pet);
                Assertions.assertEquals("dinamo",created.getName());
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }


    }
    @Test
    public void test3(){
        Pet.Category category = new Pet.Category();
        category.setId(1);
        category.setName("category");
        Pet.Tags tag1 = new Pet.Tags();
        tag1.setId(1);
        tag1.setName("tag1");
        Pet.Tags tag2=new Pet.Tags(2,"tag2");
        List<Pet.Tags>tags=new ArrayList<>(Arrays.asList(new Pet.Tags[]{tag1,tag2}));
        String photo="photo";
        List<String>photoUrls=new ArrayList<>();
        photoUrls.add(photo);
        long id=System.currentTimeMillis();
        Pet pet=new Pet(id,category,"dinamo",photoUrls,tags,"available");
        Pet pet1=apiPet.putPet(pet);
        Assertions.assertEquals("dinamo",pet1.getName());

    }
    @Test
    public void test4(){
        List<Pet>list=apiPet.get("available");
        for(Pet s:list){
            Assertions.assertEquals("available",s.getStatus());
        }
    }
    @ParameterizedTest
    @ValueSource(strings = {"sold","available","pending"})
    public void test5(String status){
        List<Pet>list=
                given()
                        .queryParam("status",status)
                        .spec(petSpec)
                        .when()
                        .get("/findByStatus")
                        .then()
                        .statusCode(200)
                        .extract()
                        .jsonPath()
                        .getList("",Pet.class);
        for(Pet s:list){
            Assertions.assertEquals(s.getStatus(),status);
        }
    }
    @Test
    @Story("get all statuses")
    @Description("get all statuses")
    public void test5(){
        List<String>available=apiPet.getPetWithAvailableStatus();
        for(String s:available){
            Assertions.assertEquals("available",s);
        }
        List<String>sold=apiPet.getPetWithSoldStatus();
        for(String s:sold){
            Assertions.assertEquals("sold",s);
        }
        List<String>pending=apiPet.getPetWithPendingStatus();
        for(String s:pending){
            Assertions.assertEquals("pending",s);
        }
        List<String>available2=
                given()
                        .queryParam("status","available")
                        .spec(petSpec)
                        .when()
                        .get("/findByStatus")
                        .then()
                        .extract()
                        .jsonPath()
                        .getList("status");
        for(String s:available2){
            Assertions.assertEquals("available",s);
        }
    }
    @Test
    public void test6(){
        long id=System.currentTimeMillis();
        step("1",()->{
            Pet.Category category = new Pet.Category();
            category.setId(1);
            category.setName("category");
            Pet.Tags tag1 = new Pet.Tags();
            tag1.setId(1);
            tag1.setName("tag1");
            Pet.Tags tag2=new Pet.Tags(2,"tag2");
            List<Pet.Tags>tags=new ArrayList<>(Arrays.asList(new Pet.Tags[]{tag1,tag2}));
            String photo="photo";
            List<String>photoUrls=new ArrayList<>();
            photoUrls.add(photo);
            Pet pet=new Pet(5,category,"dinamo",photoUrls,tags,"available");
            try {
                Pet created=apiPet.createPet(pet);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
        step("2",()->{
            Pet pet=
            given()
                    .spec(petSpec)
                    .pathParam("id",5)
                    .when()
                    .get("/{id}")
                    .then()
                    .statusCode(200)
                    .extract()
                    .as(Pet.class);
            List<Pet.Tags>list=pet.getTags();
            int count=0;
            for(Pet.Tags s:list){
                Assertions.assertEquals(++count,s.getId());
            }
        });
    }
    @Test
    public void test7() throws JsonProcessingException {
        Response response=
                given()
                        .spec(petSpec)
                        .pathParam("id",1)
                        .when()
                        .get("/{id}")
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();
        JsonNode root=mapper.readTree(response.asString());
        Assertions.assertTrue(root.has("tags"));
        JsonNode tags=root.get("tags");
        List<Pet.Tags>list=mapper.convertValue(tags, new TypeReference<List<Pet.Tags>>() {
        });
        Assertions.assertTrue(!list.isEmpty());
        Assertions.assertTrue(root.has("photoUrls"));
        JsonNode photoUrl=root.get("photoUrls");
        List<String>photoList=mapper.convertValue(photoUrl, new TypeReference<List<String>>() {
        });
        Assertions.assertTrue(!photoList.isEmpty());


    }
}
