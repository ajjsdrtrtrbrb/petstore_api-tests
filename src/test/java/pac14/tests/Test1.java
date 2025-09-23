package pac14.tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.internal.common.assertion.Assertion;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;
import pac14.base.BaseClass;
import pac14.model.ApiPet;
import pac14.model.ApiResponse;
import pac14.model.MyEx;
import pac14.model.Pet;
import pac14.model.Order;
import pac14.model.OrderApi;

import org.junit.jupiter.api.Test;

import java.util.List;

public class Test1 extends BaseClass {

    @Test
    public void test1() throws JsonProcessingException, MyEx {

        ApiPet apiPet = new ApiPet(petSpec);
        Pet.Category category = new Pet.Category();


        category.setId(1);
        category.setName("category1");

        Pet.Tag tag = new Pet.Tag();
        tag.setId(1);
        tag.setName("tag1");

        Pet pet = new Pet();
        pet.setId(101);
        pet.setName("dinamo");
        pet.setCategory(category);
        pet.setPhotoUrls(List.of("https://example.com/photo1.jpg"));
        pet.setTags(List.of(tag));
        pet.setStatus("available");
        Pet pet1 = apiPet.createPetByObject(pet);
        Assertions.assertEquals(101, pet1.getId());
        Assertions.assertEquals("dinamo", pet1.getName());
        Assertions.assertEquals("available", pet1.getStatus());
        Assertions.assertTrue(!pet1.getPhotoUrls().isEmpty());
        Assertions.assertTrue(!(pet1.getTags().size() == 0));

        String json = "{\n" +
                "  \"id\": 102,\n" +
                "  \"category\": {\n" +
                "    \"id\": 102,\n" +
                "    \"name\": \"category102\"\n" +
                "  },\n" +
                "  \"name\": \"barsa\",\n" +
                "  \"photoUrls\": [\n" +
                "    \"photo\"\n" +
                "  ],\n" +
                "  \"tags\": [\n" +
                "    {\n" +
                "      \"id\": 102,\n" +
                "      \"name\": \"tag102\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"status\": \"available\"\n" +
                "}";
        Pet pet2 = apiPet.createByJson(json);
        Assertions.assertEquals(102, pet2.getId());
        Assertions.assertEquals("barsa", pet2.getName());
        Assertions.assertEquals("available", pet2.getStatus());
        List<String> list = pet2.getPhotoUrls();
        System.out.println(list);
        List<Pet.Tag> list1 = pet2.getTags();
        System.out.println(list1);
        Assertions.assertEquals(102, list1.get(0).getId());

        Pet pet3 = apiPet.createPetByObject(pet);
        pet3.setId(88);
        Pet updatePet = apiPet.updatePetByPut(pet3);
        Assertions.assertEquals(88, updatePet.getId());

        ApiResponse apiResponse = apiPet.updatePetByIdWithApiResponseResponse(102, "real", "sold");
        Assertions.assertEquals(200, apiResponse.getCode());
        Pet pet4 = apiPet.getPetById(102);
        Assertions.assertEquals("barsa", pet4.getName());
        Assertions.assertEquals("sold", pet4.getStatus());

        List<Pet> statuses = apiPet.getByStatus("available");
        for (Pet s : statuses) {
            Assertions.assertEquals("available", s.getStatus());
        }
        ApiResponse apiResponse1 = apiPet.deletePet(102, "test");
        Assertions.assertEquals(200, apiResponse1.getCode());
        Pet.Category category1 = apiPet.getCategoryForPetId(101);
        Assertions.assertEquals(1, category1.getId());
        Assertions.assertEquals("category1", category1.getName());

        List<String> list2 = apiPet.getPhotoForPetByPetId(101);
        Assertions.assertTrue(!list2.isEmpty());
        List<Pet.Tag> list3 = apiPet.getPetTugsByPetId(101);
        Assertions.assertEquals(1, list3.get(0).getId());
        Assertions.assertEquals("tag1", list3.get(0).getName());

    }

    @Test
    public void test2() throws JsonProcessingException {
        OrderApi orderApi = new OrderApi(orderSpec);
        String json = "{\n" +
                "  \"id\": 100,\n" +
                "  \"petId\": 100,\n" +
                "  \"quantity\": 100,\n" +
                "  \"shipDate\": \"2025-08-22T09:26:34.143Z\",\n" +
                "  \"status\": \"placed\",\n" +
                "  \"complete\": true\n" +
                "}";
        Order order = orderApi.createOrder(json);
        Assertions.assertEquals(100, order.getId());
        Assertions.assertEquals(100, order.getPetId());
        Assertions.assertEquals(100, order.getQuantity());
        Assertions.assertEquals("placed", order.getStatus());
        Assertions.assertEquals(true, order.isComplete());
        Order order1 = orderApi.getById(100);
        System.out.println(order.equals(order1));
        Assertions.assertEquals(order.getId(), order1.getId());

        JsonNode node = orderApi.getNodeById(100);
        ObjectMapper mapper = new ObjectMapper();
        Order order3 = mapper.readValue(node.toString(), Order.class);
        Assertions.assertEquals(100, order3.getId());
        int id = node.get("id").asInt();
        String status = node.get("status").asText();
        Assertions.assertEquals(id, order3.getId());
        Assertions.assertEquals(status, order3.getStatus());

    }

    @Test
    public void test3() throws JsonProcessingException {
        String json = "[\n" +
                "  {\n" +
                "    \"id\": 1,\n" +
                "    \"petId\": 101,\n" +
                "    \"quantity\": 2,\n" +
                "    \"shipDate\": \"2025-08-22T14:30:00.000+0000\",\n" +
                "    \"status\": \"placed\",\n" +
                "    \"complete\": true\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 2,\n" +
                "    \"petId\": 102,\n" +
                "    \"quantity\": 1,\n" +
                "    \"shipDate\": \"2025-08-23T10:00:00.000+0000\",\n" +
                "    \"status\": \"approved\",\n" +
                "    \"complete\": false\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 3,\n" +
                "    \"petId\": 103,\n" +
                "    \"quantity\": 5,\n" +
                "    \"shipDate\": \"2025-08-24T16:45:00.000+0000\",\n" +
                "    \"status\": \"delivered\",\n" +
                "    \"complete\": true\n" +
                "  }\n" +
                "]";
        Response response = Mockito.mock(Response.class);
        Mockito.when(response.getStatusCode()).thenReturn(200);
        Mockito.when(response.asString()).thenReturn(json);
        ObjectMapper mapper = new ObjectMapper();
        List<Order> list = mapper.readValue(response.asString(), new TypeReference<List<Order>>() {
        });
        Assertions.assertFalse(list.isEmpty());
        int limit=Math.min(list.size(),3);
        int count=0;
        for(int i=1;i<limit+1;i++){
            Assertions.assertEquals(i,list.get(count++).getId());
        }
    }
}
