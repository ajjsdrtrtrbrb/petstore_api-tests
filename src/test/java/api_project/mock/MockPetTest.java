package api_project.mock;

import api_project.api.ApiPet;
import api_project.base.BaseClass;
import api_project.enums.PetStatus;
import api_project.model.ErrorResponse;
import api_project.model.Pet;
import api_project.model.ApiResponse;
import api_project.model.PetBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для мок-сервера Pet API
 * Используется WireMock для имитации API
 */
public class MockPetTest {

    private static WireMockServer wireMockServer;
    private ApiPet apiPet;
    private static int port;

    /** Запуск WireMock перед всеми тестами */
    @BeforeAll
    public static void setUpServer() {
        wireMockServer = new WireMockServer(0); // порт 0 = случайный свободный
        wireMockServer.start();
        port = wireMockServer.port();
        System.out.println("WireMock запущен на порту: " + port);
        configureFor("localhost", port);
    }

    /** Остановка WireMock после всех тестов */
    @AfterAll
    public static void stopServer() {
        wireMockServer.stop();
    }

    /** Настройка клиента ApiPet перед каждым тестом */
    @BeforeEach
    public void setUp() {
        RequestSpecification specification = new RequestSpecBuilder()
                .setBaseUri("http://localhost")
                .setBasePath("/pet")
                .setPort(port)
                .setContentType(ContentType.JSON)
                .build();
        apiPet = new ApiPet(specification);
    }

    /** Позитивный тест получения питомца по ID */
    @Test
    public void getPetSuccess() throws JsonProcessingException {
        stubFor(get(urlEqualTo("/1"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\":1,\"name\":\"MockPet\",\"status\":\"available\"}")));

        Pet pet = apiPet.getById(1);

        assertEquals(1, pet.getId());
        assertEquals("MockPet", pet.getName());
        assertEquals("available", pet.getStatus());
    }

    /** Негативный тест: питомец не найден (404) */
    @Test
    public void get404() {
        stubFor(get(urlEqualTo("/999"))
                .willReturn(aResponse()
                        .withStatus(404)));

        Exception exception = assertThrows(RuntimeException.class, () -> apiPet.getById(999));
        assertTrue(exception.getMessage().contains("404"));
    }

    /** Позитивный тест поиска питомцев по статусу */
    @Test
    public void findByStatusPositive() throws JsonProcessingException {
        String jsonResponse = "[{\"id\":1,\"name\":\"Doggy\",\"status\":\"available\"}]";

        stubFor(get(urlPathEqualTo("/findByStatus"))
                .withQueryParam("status", equalTo("available"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(jsonResponse)));

        List<Pet> list = apiPet.getByStatus(PetStatus.available);

        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals("available", list.get(0).getStatus());
    }

    /** Негативный тест поиска питомцев по статусу (нет результатов) */
    @Test
    public void findByStatusNegative() {
        stubFor(get(urlPathEqualTo("/findByStatus"))
                .withQueryParam("status", equalTo("sold"))
                .willReturn(aResponse()
                        .withStatus(404)));

        Exception exception = assertThrows(RuntimeException.class,
                () -> apiPet.getByStatus(PetStatus.sold));
        assertTrue(exception.getMessage().contains("pet with status sold not found"));
    }

    /** Позитивный тест создания питомца */
    @Test
    public void createPositive() throws JsonProcessingException {
        Pet pet = new PetBuilder()
                .id(10)
                .name("pet")
                .status("available")
                .build();

        String body = new ObjectMapper().writeValueAsString(pet);

        stubFor(post(urlEqualTo("/pet"))
                .withHeader("Content-Type", equalTo("application/json"))
                .withRequestBody(equalTo(body))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(body)));

        apiPet.setResponseSpecification(BaseClass.mockSuccessSpec);

        Pet created = apiPet.createPetWithJson(body);

        assertEquals(pet.getId(), created.getId());
        assertEquals(pet.getName(), created.getName());
        assertEquals(pet.getStatus(), created.getStatus());
    }

    /** Негативный тест создания питомца: 400 Bad Request */
    @Test
    public void createNegative400() throws JsonProcessingException {
        Pet invalidPet = new PetBuilder()
                .id(1)
                .name("test")
                .status("pending")
                .build();

        String json = new ObjectMapper().writeValueAsString(invalidPet);

        stubFor(post(urlEqualTo("/pet"))
                .withRequestBody(equalTo(json))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"code\":400, \"message\":\"name is required\"}")));

        ErrorResponse errorResponse = apiPet.mockCreate404(json);

        assertEquals(400, errorResponse.getCode());
        assertEquals("name is required", errorResponse.getMessage());
    }

    /** Негативный тест создания питомца: 500 Internal Server Error */
    @Test
    public void createNegative500() throws JsonProcessingException {
        Pet pet = new PetBuilder()
                .id(5555)
                .name("[")
                .status("hhh")
                .build();

        String json = new ObjectMapper().writeValueAsString(pet);

        stubFor(post(urlEqualTo("/pet"))
                .withRequestBody(equalTo(json))
                .willReturn(aResponse()
                        .withStatus(500)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"code\":500, \"message\":\"Internal server error\"}")));

        ErrorResponse errorResponse = apiPet.mockCreate500(json);

        assertEquals(500, errorResponse.getCode());
        assertEquals("Internal server error", errorResponse.getMessage());
    }

    /** Негативный тест обновления питомца: 400 Bad Request */
    @Test
    public void mockPutNegative400() throws JsonProcessingException {
        Pet pet = new PetBuilder()
                .id(10)
                .name("test")
                .status("jjkrer")
                .build();

        String json = new ObjectMapper().writeValueAsString(pet);

        stubFor(put(urlEqualTo("/pet"))
                .withRequestBody(equalTo(json))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"code\":400, \"message\":\"status invalid\"}")));

        ErrorResponse errorResponse = apiPet.mockPut400(json);

        assertEquals(400, errorResponse.getCode());
        assertEquals("status invalid", errorResponse.getMessage());
    }

    /** Позитивный тест обновления питомца */
    @Test
    public void mockPutPositive() throws JsonProcessingException {
        Pet pet = new PetBuilder()
                .id(10)
                .name("test")
                .status("available")
                .build();

        String json = new ObjectMapper().writeValueAsString(pet);

        stubFor(put(urlPathEqualTo("/pet"))
                .withRequestBody(equalToJson(json))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(json)));

        apiPet.setResponseSpecification(BaseClass.mockSuccessSpec);

        Pet put = apiPet.putPet(pet);

        assertEquals(pet.getId(), put.getId());
        assertEquals(pet.getName(), put.getName());
        assertEquals(pet.getStatus(), put.getStatus());
    }

    /** Позитивный тест удаления питомца */
    @Test
    public void mockDeletePositive() throws JsonProcessingException {
        stubFor(delete(urlMatching("/pet/\\d+"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"code\":200,\"type\":\"success\",\"message\":\"deleted\"}")));

        ApiResponse apiResponse = apiPet.deletePet(5, "test");

        assertEquals(200, apiResponse.getCode());
        assertEquals("success", apiResponse.getType());
        assertEquals("deleted", apiResponse.getMessage());
    }

    /** Негативный тест удаления питомца */
    @Test
    public void mockDeleteNegative() throws JsonProcessingException {
        stubFor(delete(urlMatching("/pet/\\d+"))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"code\":400,\"message\":\"bad request\"}")));

        ErrorResponse errorResponse = apiPet.mockDelete400(55);

        assertEquals(400, errorResponse.getCode());
        assertEquals("bad request", errorResponse.getMessage());
    }
}
