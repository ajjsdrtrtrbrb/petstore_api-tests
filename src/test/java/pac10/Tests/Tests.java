package pac10.Tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import pac10.Base.BaseClass;
import pac10.Model.Pet;

import java.util.ArrayList;
import java.util.List;
import pac10.Model.Pet;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class Tests extends BaseClass {
    private String contentType="Content-Type";
    private String applicationJson="application/json";
    @Test
    @Order(1)
    public void post1() {
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
                .body(json)
                .when()
                .post()
                .then()
                .statusCode(200)
                .body("id", equalTo(5));
    }

    @Order(2)
    @ParameterizedTest
    @ValueSource(ints = {1})
    public void get1(int id) {
        given()
                .spec(spec1)
                .when()
                .get("{id}", id)
                .then()
                .statusCode(200)
                .body("name", equalTo("doggie"));
    }

    @Order(3)
    @Test
    public void post2() {
        List<String>photoUrl=new ArrayList<>();
        photoUrl.add("photoUrl1");
        List<Pet.Tags>tags=new ArrayList<>();
        Pet.Tags tags1=new Pet.Tags(1,"tag1");
        Pet.Tags tags2=new Pet.Tags(2,"tag2");
        tags.add(tags1);
        tags.add(tags2);
        Pet.Category category=new Pet.Category(1,"category1");
        Pet pet=new Pet(10,category,"category10",photoUrl,tags,"available");
        given()
                .spec(spec1)
                .header(contentType,applicationJson)
                .body(pet)
                .when()
                .post()
                .then()
                .statusCode(200)
                .body("id",equalTo(10));

    }
    @Order(4)
    @ParameterizedTest
    @ValueSource(ints = {10})
    public void get2(int id){
        Pet pet=
                given()
                        .spec(spec1)
                        .when()
                        .get("{id}",id)
                        .then()
                        .statusCode(200)
                        .extract()
                        .as(Pet.class);
        Assertions.assertEquals(10,pet.getId());



    }
    @Order(4)
    @Test
    public void get3() throws JsonProcessingException {
        Response response=
                given()
                        .spec(spec1)
                        .when()
                        .get("/10")
                        .then()
                        .statusCode(200)
                        .extract().response();
        String json=response.asString();
        ObjectMapper mapper=new ObjectMapper();
        Pet pet=mapper.readValue(json,Pet.class);
        Assertions.assertEquals(10,pet.getId());
        JsonNode node=mapper.readTree(json);
        JsonNode category=node.get("category");
        Pet.Category category1=mapper.treeToValue(category,Pet.Category.class);
        System.out.println(category1.getId());
        System.out.println(category1.getName());
    }
    @Order(5)
    @ParameterizedTest
    @ValueSource(ints = {10})
    public void getTag(int id) throws JsonProcessingException {
        Response response=
                given()
                        .spec(spec1)
                        .when()
                        .get("{id}",id)
                        .then()
                        .statusCode(200)
                        .extract().response();
        String json=response.asString();
        ObjectMapper mapper=new ObjectMapper();
        Pet pet=mapper.readValue(json,Pet.class);
        Assertions.assertEquals(10,pet.getId());
        JsonNode root=mapper.readTree(json);
        JsonNode tag=root.get("tags");
        Pet.Tags petTags=mapper.treeToValue(tag.get(0),Pet.Tags.class);
        Assertions.assertEquals("string",petTags.getName());

    }
    @Order(6)
    @ParameterizedTest
    @ValueSource(ints = {10})
    public void get5(int id){
        Pet pet=
                given()
                        .spec(spec1)
                        .when()
                        .get("{id}",id)
                        .then()
                        .statusCode(200)
                        .extract()
                        .as(Pet.class);
        Assertions.assertEquals(10,pet.getId());
        Assertions.assertEquals("doggie",pet.getName());
        Assertions.assertEquals("string",pet.getStatus());
        Assertions.assertEquals("string",pet.getTags().get(0).getName());
    }
    @Test
    public void test(){
        Pet.Category category=new Pet.Category(2,"cat2");
        List<Pet.Tags>list=List.of(new Pet.Tags(2,"tag2"));
        Pet pet=new Pet(20, category, "catPet", List.of("url1"), list, "available");
        given()
                .spec(spec1)
                .header(contentType,applicationJson)
                .body(pet)
                .when()
                .post()
                .then()
                .statusCode(200);
        Pet responce=given()
                .spec(spec1)
                .when()
                .get("/20")
                .then()
                .statusCode(200)
                .extract()
                .as(Pet.class);
        Assertions.assertEquals("catPet",responce.getName());
        Assertions.assertEquals("cat2",responce.getCategory().getName());
    }
    /*
    public class PetstoreJsonNodeArrayTest {

    @Test
    public void testGetPetsByStatus_UsingJsonNode() throws Exception {
        // 1. Отправляем GET-запрос
        Response response = RestAssured.given()
                .baseUri("https://petstore.swagger.io/v2")
                .queryParam("status", "available")
                .when()
                .get("/pet/findByStatus")
                .then()
                .statusCode(200)
                .extract()
                .response();

        // 2. Преобразуем тело ответа в JsonNode (массив)
        ObjectMapper mapper = new ObjectMapper();
        JsonNode petsArray = mapper.readTree(response.asString());

        // 3. Проверяем, что это массив и он не пустой
        Assertions.assertTrue(petsArray.isArray(), "Ответ не является массивом");
        Assertions.assertTrue(petsArray.size() > 0, "Список питомцев пуст");

        // 4. Проходимся по первым 5 элементам и проверяем значения
        int limit = Math.min(petsArray.size(), 5);
        for (int i = 0; i < limit; i++) {
            JsonNode pet = petsArray.get(i);

            // Проверка id
            Assertions.assertTrue(pet.has("id"), "Нет поля id у элемента #" + i);
            long id = pet.get("id").asLong();
            Assertions.assertTrue(id > 0, "Некорректный id: " + id);

            // Проверка name
            Assertions.assertTrue(pet.has("name"), "Нет поля name у элемента #" + i);
            String name = pet.get("name").asText();
            Assertions.assertFalse(name.isEmpty(), "Пустое имя у питомца #" + i);

            // Вывод для наглядности
            System.out.println("Pet #" + (i+1) + " -> id: " + id + ", name: " + name);
        }
    }
}
     */
}
