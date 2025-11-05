package api_project.mock;

import api_project.model.ApiResponse;
import api_project.model.ApiResponseBuilder;
import api_project.model.Pet;
import api_project.model.PetBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для проверки Mock API питомцев (Pet)
 * Используется WireMock для мокирования сервера
 */
public class MockPetTest2 {

    private static WireMockServer wireMockServer; // сервер WireMock
    private static int port;                      // порт WireMock сервера
    private MockPetApi mockPetApi;                // клиент для работы с мок-API

    /**
     * Запуск WireMock перед всеми тестами
     */
    @BeforeAll
    public static void setServer() {
        wireMockServer = new WireMockServer(0); // порт 0 — случайный свободный
        wireMockServer.start();
        port = wireMockServer.port();
        System.out.println("WireMock запущен на порту: " + port);
        configureFor("localhost", port);
    }

    /**
     * Остановка WireMock после всех тестов
     */
    @AfterAll
    public static void stop() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }

    /**
     * Настройка клиента перед каждым тестом
     */
    @BeforeEach
    public void setUp() {
        RequestSpecification requestSpecification = new RequestSpecBuilder()
                .setBaseUri("http://localhost")
                .setBasePath("/pet")
                .setPort(port)
                .setContentType(ContentType.JSON)
                .build();
        mockPetApi = new MockPetApi(requestSpecification, new ObjectMapper());
    }

    /**
     * Позитивный тест создания питомца
     */
    @Test
    public void petCreatePositive() throws JsonProcessingException {
        Pet pet = new PetBuilder()
                .id(10)
                .name("pet")
                .category(new Pet.Category(10, "category"))
                .status("available")
                .photoUrls(List.of("photo"))
                .tags(List.of(new Pet.Tags(10, "tag1"), new Pet.Tags(11, "tag2")))
                .build();

        String responseBody = new ObjectMapper().writeValueAsString(pet);

        // Настройка WireMock для POST-запроса
        stubFor(post(urlEqualTo("/pet"))
                .withHeader("Content-Type", equalTo("application/json"))
                .withRequestBody(equalToJson(responseBody))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseBody)));

        // Вызов метода клиента
        Pet createdPet = mockPetApi.createPet(pet, Pet.class);

        // Проверка результата
        assertNotNull(createdPet);
        assertEquals(pet.getId(), createdPet.getId());
        assertEquals(pet.getName(), createdPet.getName());
        assertEquals(pet.getCategory().getName(), createdPet.getCategory().getName());
        assertEquals(pet.getStatus(), createdPet.getStatus());

        // Проверка что запрос был отправлен
        verify(postRequestedFor(urlEqualTo("/pet")));
    }

    /**
     * Негативный тест создания питомца с ошибкой 400
     */
    @Test
    public void petCreateNegative() throws JsonProcessingException {
        Pet pet = new PetBuilder()
                .id(10)
                .name("pet")
                .category(new Pet.Category(10, "category"))
                .status("test")
                .photoUrls(List.of("photo"))
                .tags(List.of(new Pet.Tags(10, "tag1"), new Pet.Tags(11, "tag2")))
                .build();

        String requestBody = new ObjectMapper().writeValueAsString(pet);

        ApiResponse apiResponse = new ApiResponseBuilder()
                .code(400)
                .type("error")
                .message("bad request")
                .build();
        String responseBody = new ObjectMapper().writeValueAsString(apiResponse);

        stubFor(post(urlEqualTo("/pet"))
                .withHeader("Content-Type", equalTo("application/json"))
                .withRequestBody(equalToJson(requestBody))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseBody)));

        ApiResponse realResponse = mockPetApi.createPet(pet, ApiResponse.class);

        assertNotNull(realResponse);
        assertEquals(apiResponse.getCode(), realResponse.getCode());
        assertEquals(apiResponse.getType(), realResponse.getType());
        assertEquals(apiResponse.getMessage(), realResponse.getMessage());

        verify(postRequestedFor(urlEqualTo("/pet")));
    }

    /**
     * Позитивный тест обновления питомца
     */
    @Test
    public void petPutPositive() throws JsonProcessingException {
        Pet pet = new PetBuilder()
                .id(10)
                .name("pet")
                .category(new Pet.Category(10, "category"))
                .status("available")
                .photoUrls(List.of("photo"))
                .tags(List.of(new Pet.Tags(10, "tag1"), new Pet.Tags(11, "tag2")))
                .build();

        String responseBody = new ObjectMapper().writeValueAsString(pet);

        stubFor(put(urlEqualTo("/pet"))
                .withHeader("Content-Type", equalTo("application/json"))
                .withRequestBody(equalToJson(responseBody))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseBody)));

        Pet updatedPet = mockPetApi.updatePet(pet, Pet.class);

        assertNotNull(updatedPet);
        assertEquals(pet.getId(), updatedPet.getId());
        assertEquals(pet.getName(), updatedPet.getName());
        assertEquals(pet.getCategory().getName(), updatedPet.getCategory().getName());
        assertEquals(pet.getStatus(), updatedPet.getStatus());

        verify(putRequestedFor(urlEqualTo("/pet")));
    }

    /**
     * Негативный тест обновления питомца с ошибкой 400
     */
    @Test
    public void petPutNegative() throws JsonProcessingException {
        Pet pet = new PetBuilder()
                .id(10)
                .name("pet")
                .category(new Pet.Category(10, "category"))
                .status("test")
                .photoUrls(List.of("photo"))
                .tags(List.of(new Pet.Tags(10, "tag1"), new Pet.Tags(11, "tag2")))
                .build();

        String requestBody = new ObjectMapper().writeValueAsString(pet);

        ApiResponse apiResponse = new ApiResponseBuilder()
                .code(400)
                .type("error")
                .message("bad request")
                .build();
        String responseBody = new ObjectMapper().writeValueAsString(apiResponse);

        stubFor(put(urlEqualTo("/pet"))
                .withHeader("Content-Type", equalTo("application/json"))
                .withRequestBody(equalToJson(requestBody))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseBody)));

        ApiResponse realResponse = mockPetApi.updatePet(pet, ApiResponse.class);

        assertNotNull(realResponse);
        assertEquals(apiResponse.getCode(), realResponse.getCode());
        assertEquals(apiResponse.getType(), realResponse.getType());
        assertEquals(apiResponse.getMessage(), realResponse.getMessage());

        verify(putRequestedFor(urlEqualTo("/pet")));
    }

    /**
     * Позитивный параметризованный тест поиска питомцев по статусу
     */
    @ParameterizedTest
    @ValueSource(strings = {"available", "pending", "sold"})
    public void petFindByStatusPositive(String status) throws JsonProcessingException {
        List<Pet> pets = List.of(
                new PetBuilder()
                        .id(1)
                        .name("dog")
                        .status(status)
                        .category(new Pet.Category(1, "dogs"))
                        .photoUrls(List.of("url1"))
                        .tags(List.of(new Pet.Tags(1, "tag1")))
                        .build(),
                new PetBuilder()
                        .id(2)
                        .name("cat")
                        .status(status)
                        .category(new Pet.Category(2, "cats"))
                        .photoUrls(List.of("url2"))
                        .tags(List.of(new Pet.Tags(2, "tag2")))
                        .build()
        );

        String responseBody = new ObjectMapper().writeValueAsString(pets);

        stubFor(get(urlPathEqualTo("/pet/findByStatus"))
                .withQueryParam("status", equalTo(status))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseBody)));

        List<Pet> realResponse = mockPetApi.positiveFindByStatus(status);

        assertNotNull(realResponse);
        assertEquals(pets.size(), realResponse.size());
        for (int i = 0; i < realResponse.size(); i++) {
            assertEquals(pets.get(i).getStatus(), realResponse.get(i).getStatus());
        }

        verify(getRequestedFor(urlPathEqualTo("/pet/findByStatus"))
                .withQueryParam("status", equalTo(status)));
    }

    /**
     * Негативный параметризованный тест поиска питомцев по несуществующему статусу
     */
    @ParameterizedTest
    @ValueSource(strings = {"dd", "gg", "rr"})
    public void petFindByStatusNegative(String status) throws JsonProcessingException {
        ApiResponse apiResponse = new ApiResponseBuilder()
                .code(400)
                .type("error")
                .message("bad request")
                .build();
        String responseBody = new ObjectMapper().writeValueAsString(apiResponse);

        stubFor(get(urlPathEqualTo("/pet/findByStatus"))
                .withQueryParam("status", equalTo(status))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseBody)));

        ApiResponse realResponse = mockPetApi.negativeFindByStatus(status);

        assertNotNull(realResponse);
        assertEquals(apiResponse.getCode(), realResponse.getCode());
        assertEquals(apiResponse.getType(), realResponse.getType());
        assertEquals(apiResponse.getMessage(), realResponse.getMessage());

        verify(getRequestedFor(urlPathEqualTo("/pet/findByStatus"))
                .withQueryParam("status", equalTo(status)));
    }

    /**
     * Позитивный параметризованный тест получения питомца по ID
     */
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    public void getPetByIdPositive(int id) throws JsonProcessingException {
        Pet pet = new PetBuilder()
                .id(id)
                .status("available")
                .name("pet")
                .category(new Pet.Category(10, "category"))
                .photoUrls(List.of("photo"))
                .tags(List.of(new Pet.Tags(10, "tag")))
                .build();

        String responseBody = new ObjectMapper().writeValueAsString(pet);

        stubFor(get(urlPathEqualTo("/pet/" + id))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseBody)));

        Pet getPet = mockPetApi.getById(id, Pet.class);

        Assertions.assertEquals(id, getPet.getId());
        Assertions.assertEquals(pet.getName(), getPet.getName());
        Assertions.assertEquals(pet.getStatus(), getPet.getStatus());
        Assertions.assertEquals(pet.getCategory().getId(), getPet.getCategory().getId());
        Assertions.assertEquals(pet.getCategory().getName(), getPet.getCategory().getName());
        Assertions.assertEquals(pet.getPhotoUrls().get(0), getPet.getPhotoUrls().get(0));
        Assertions.assertEquals(pet.getTags().get(0).getId(), getPet.getTags().get(0).getId());
        Assertions.assertEquals(pet.getTags().get(0).getName(), getPet.getTags().get(0).getName());
    }

    /**
     * Негативный параметризованный тест получения питомца по ID
     */
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    public void getPetByIdNegative(int id) throws JsonProcessingException {
        ApiResponse apiResponse = new ApiResponseBuilder()
                .code(400)
                .type("error")
                .message("bad request")
                .build();

        String responseBody = new ObjectMapper().writeValueAsString(apiResponse);

        stubFor(get(urlPathEqualTo("/pet/" + id))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseBody)));

        Assertions.assertEquals(400, apiResponse.getCode());
        Assertions.assertEquals("error", apiResponse.getType());
        Assertions.assertEquals("bad request", apiResponse.getMessage());
    }

    /**
     * Позитивный параметризованный тест удаления питомца по ID
     */
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    public void deletePetByIdPositive(int id) throws JsonProcessingException {
        Pet pet = new PetBuilder()
                .id(id)
                .status("available")
                .name("pet")
                .category(new Pet.Category(10, "category"))
                .photoUrls(List.of("photo"))
                .tags(List.of(new Pet.Tags(10, "tag")))
                .build();

        String responseBody = new ObjectMapper().writeValueAsString(pet);

        stubFor(delete(urlPathEqualTo("/pet/" + id))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseBody)));

        Pet deletePet = mockPetApi.deleteById(id, Pet.class);

        Assertions.assertEquals(id, deletePet.getId());
        Assertions.assertEquals(pet.getName(), deletePet.getName());
        Assertions.assertEquals(pet.getStatus(), deletePet.getStatus());
        Assertions.assertEquals(pet.getCategory().getId(), deletePet.getCategory().getId());
        Assertions.assertEquals(pet.getCategory().getName(), deletePet.getCategory().getName());
        Assertions.assertEquals(pet.getPhotoUrls().get(0), deletePet.getPhotoUrls().get(0));
        Assertions.assertEquals(pet.getTags().get(0).getId(), deletePet.getTags().get(0).getId());
        Assertions.assertEquals(pet.getTags().get(0).getName(), deletePet.getTags().get(0).getName());
    }

    /**
     * Негативный параметризованный тест удаления питомца по ID
     */
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    public void deletePetByIdNegative(int id) throws JsonProcessingException {
        ApiResponse apiResponse = new ApiResponseBuilder()
                .code(400)
                .type("error")
                .message("bad request")
                .build();

        String responseBody = new ObjectMapper().writeValueAsString(apiResponse);

        stubFor(delete(urlPathEqualTo("/pet/" + id))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseBody)));

        Assertions.assertEquals(400, apiResponse.getCode());
        Assertions.assertEquals("error", apiResponse.getType());
        Assertions.assertEquals("bad request", apiResponse.getMessage());
    }
}
