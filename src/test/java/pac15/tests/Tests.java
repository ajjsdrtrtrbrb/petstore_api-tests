package pac15.tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import org.checkerframework.checker.units.qual.A;
import org.checkerframework.checker.units.qual.PolyUnit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import pac15.model.Pet;
import pac15.ApiService.*;
import pac15.base.BaseClass;
import pac15.model.ApiResponse;
import pac15.model.Order;
import pac15.model.User;


import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class Tests extends BaseClass {
    @ParameterizedTest
    @ValueSource(strings = {"test"})
    public void test1(String userName) throws JsonProcessingException {
        ApiUser apiUser = new ApiUser(userSpec);
        User user = new User(1, "testUser", "John", "Doe",
                "john.doe@example.com", "12345", "555-1234", "1");
        ApiResponse apiResponse = apiUser.createUser(user);
        Assertions.assertAll("проверка полей ариреспонс",
                () -> assertEquals(200, apiResponse.getCode(), "неправильный код"),
                () -> assertFalse(apiResponse.getMessage().isEmpty(), "месседж пустой"),
                () -> assertEquals("unknown", apiResponse.getType(), "неправильный тип"));
        User created = apiUser.getUserByUserName(userName);

        Assertions.assertAll("ПРОВЕРКА ВСЕХ ПОЛЕЙ ЮЗЕР",
                () -> assertEquals(2, created.getId(), "Id не совпадает"),
                () -> assertEquals("test", created.getUserName(), "Username не совпадает"),
                () -> assertEquals("test", created.getFirstName(), "FirstName не совпадает"),
                () -> assertEquals("test", created.getLastName(), "LastName не совпадает"),
                () -> assertEquals("test@example.com", created.getEmail(), "Email не совпадает"),
                () -> assertEquals("123456", created.getPassword(), "Password не совпадает"),
                () -> assertEquals("1234567890", created.getPhone()),
                () -> assertEquals("1", created.getUserStatus(), "Status не совпадает")
        );


    }

    @Test
    public void test2() throws JsonProcessingException {
        String usersJson = "[\n" +
                "  {\n" +
                "    \"id\": 1,\n" +
                "    \"username\": \"user1\",\n" +
                "    \"firstName\": \"John\",\n" +
                "    \"lastName\": \"Doe\",\n" +
                "    \"email\": \"john.doe@example.com\",\n" +
                "    \"password\": \"12345\",\n" +
                "    \"phone\": \"555-1111\",\n" +
                "    \"userStatus\": \"1\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 2,\n" +
                "    \"username\": \"user2\",\n" +
                "    \"firstName\": \"Jane\",\n" +
                "    \"lastName\": \"Smith\",\n" +
                "    \"email\": \"jane.smith@example.com\",\n" +
                "    \"password\": \"54321\",\n" +
                "    \"phone\": \"555-2222\",\n" +
                "    \"userStatus\": \"1\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": 3,\n" +
                "    \"username\": \"user3\",\n" +
                "    \"firstName\": \"Alice\",\n" +
                "    \"lastName\": \"Johnson\",\n" +
                "    \"email\": \"alice.johnson@example.com\",\n" +
                "    \"password\": \"abcd1234\",\n" +
                "    \"phone\": \"555-3333\",\n" +
                "    \"userStatus\": \"1\"\n" +
                "  }\n" +
                "]";

        Response response = Mockito.mock(Response.class);
        Mockito.when(response.statusCode()).thenReturn(200);
        Mockito.when(response.asString()).thenReturn(usersJson);
        ObjectMapper mapper = new ObjectMapper();
        Assertions.assertEquals(200, response.statusCode());
        List<User> list = mapper.readValue(response.asString(), new TypeReference<List<User>>() {
        });
        int limit = Math.min(list.size(), 3);
        int count = 0;
        for (int i = 0; i < limit; i++) {
            Assertions.assertEquals(++count, list.get(i).getId(), "wrong id");
        }
    }

    @Test
    public void test3() throws JsonProcessingException {
        String json = "[\n" +
                "  {\"id\":1, \"username\":\"user1\", \"firstName\":\"John\", \"lastName\":\"Doe\"},\n" +
                "  {\"id\":2, \"username\":\"user2\", \"firstName\":\"Jane\", \"lastName\":\"Smith\"}\n" +
                "]";
        ObjectMapper mapper = new ObjectMapper();
        List<User> list = mapper.readValue(json, new TypeReference<List<User>>() {
        });
        for (User s : list) {
            Assertions.assertTrue(s.getId() > 0);
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {1})
    public void orderTest(int id) throws JsonProcessingException {
        ApiOrder apiOrder = new ApiOrder(orderSpec);
        Order order = new Order(
                id,          // id
                id + 1,          // petId
                id + 2,          // quantity
                "2025-08-25T09:26:01.906Z", // shipDate
                "dinamo", // status
                true         // complete
        );
        Order created = apiOrder.createOrder(order);

        Assertions.assertAll("check",
                () -> assertEquals(1, created.getId(), "wrong id"),
                () -> assertEquals(2, created.getPetId()));

        Order getOrder = apiOrder.getOrderById(id);
        Assertions.assertEquals(1, getOrder.getId());
        int petId = apiOrder.getPetIdByOrderId(id);
        Assertions.assertEquals(2, petId);

    }

    @Test
    public void test4() throws JsonProcessingException {
        ApiUser apiUser = new ApiUser(userSpec);
        User user = new User(
                1001,
                "newUser",
                "Alice",
                "Wonder",
                "alice@example.com",
                "password123",
                "555-6789",
                "1"
        );
        ApiResponse response = apiUser.createUser(user);
        Assertions.assertAll("check response",
                () -> assertEquals(200, response.getCode(), "Код ответа не 200"),
                () -> assertFalse(response.getType().isEmpty(), "Тип пустой"),
                () -> assertEquals("unknown", response.getType(), "Тип не unknown")
        );
        User createdUser = apiUser.getUserByUserName("newUser");
        Assertions.assertAll("Проверка всех полей созданного User",
                () -> assertEquals(1001, createdUser.getId()),
                () -> assertEquals("newUser", createdUser.getUserName()),
                () -> assertEquals("Alice", createdUser.getFirstName()),
                () -> assertEquals("Wonder", createdUser.getLastName())
        );
    }

    @Test
    public void test5() throws JsonProcessingException {
        ApiUser apiUser = new ApiUser(userSpec);

        User updatedUser = new User(
                1001,
                "newUser",
                "AliceUpdated",
                "WonderUpdated",
                "alice.updated@example.com",
                "newpass",
                "555-9999",
                "1"
        );
        ApiResponse response = apiUser.updateUser("newUser", updatedUser);
        Assertions.assertAll("check response",
                () -> assertEquals(200, response.getCode()));
        User updatedUser2 = apiUser.getUserByUserName("newUser");
        Assertions.assertAll("check user",
                () -> assertEquals(1001, updatedUser2.getId()));
        ApiResponse apiResponse2 = apiUser.deleteUserByUserName("newUser");
        Assertions.assertAll("check response",
                () -> assertEquals(200, apiResponse2.getCode()));

    }

    @ParameterizedTest
    @ValueSource(ints = {5})
    public void deleteOrder(int id) {
        ApiOrder apiOrder = new ApiOrder(orderSpec);
        ApiResponse response = apiOrder.deleteById(id);
        Assertions.assertAll("Check delete",
                () -> assertEquals(200, response.getCode(), "Code should be 200"),
                () -> assertFalse(response.getMessage().isEmpty(), "Message should not be empty"),
                () -> assertEquals("unknown", response.getType(), "Type should be 'unknown'")
        );
    }
    @Test
    public void testPet1() throws JsonProcessingException {
        Pet.Category category = new Pet.Category();


        category.setId(1);
        category.setName("category1");

        Pet.Tags tag = new Pet.Tags();
        tag.setId(1);
        tag.setName("tag1");

        Pet pet = new Pet();
        pet.setId(101);
        pet.setName("dinamo");
        pet.setCategory(category);
        pet.setPhotoUrls(List.of("https://example.com/photo1.jpg"));
        pet.setTags(List.of(tag));
        pet.setStatus("available");
        ApiPet apiPet=new ApiPet(petSpec);
        List<Pet.Tags>list=apiPet.getTagsFromCreatedPet(pet);
        Assertions.assertEquals(1,list.get(0).getId());
        Pet pet1=apiPet.create(pet);
        Assertions.assertAll("check",
                ()->assertEquals(101,pet1.getId()));
    }

}
