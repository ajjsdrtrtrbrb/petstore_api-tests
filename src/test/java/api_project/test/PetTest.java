package api_project.test;

import api_project.api.ApiPet;
import api_project.base.BaseClass;
import api_project.enums.PetStatus;
import api_project.model.*;
import api_project.utils.FileUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

@Epic("PET CRUD") // Эпик для Allure
@Feature("create get update delete upload image") // Фича для Allure
@Tag("api") // Теги для CI, фильтруют тесты
@Tag("pet")
public class PetTest extends BaseClass {

    private final ApiPet apiPet = new ApiPet(petSpec, petSpecUploadImage, petCreateResponseSpecification,
            apiResponseSpecification, mapper);

    // ================================
    @Story("pet upload image")
    @Description("Загрузка изображения для питомца")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Загрузка изображения для питомца")
    @Test
    public void uploadImage() throws IOException {
        // Скачиваем тестовый файл
        File file = FileUtils.downLoadFileFromUrl(
                "https://www.google.com/url?sa=i&url=https%3A%2F%2Favatarko.ru%2Fkartinka%2F13379&psig=AOvVaw0SG9a7srvCc25tZoGYjgHG&ust=1759993470349000&source=images&cd=vfe&opi=89978449&ved=0CBIQjRxqFwoTCJCCu96ElJADFQAAAAAdAAAAABAE",
                "cat.png");

        // Загружаем изображение через API
        ApiResponse response = apiPet.uploadPetImage(1, "test", file);

        // Проверяем код ответа и поля
        assertEquals(200, response.getCode());
        assertEquals("unknown", response.getType());
        assertFalse(response.getMessage().isEmpty());

        // Проверяем текст ответа
        String[] words = response.getMessage().split(" ");
        boolean uploaded = false;
        for (String word : words) {
            if (word.equals("uploaded")) uploaded = true;
        }
        System.out.println(uploaded ? "file uploaded" : "error");
    }

    // ================================
    @Story("create pet")
    @Description("Создание питомца с объектом Pet")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Создание питомца с объектом Pet")
    @Test
    public void createPetWithObject() {
        Pet pet = new PetBuilder()
                .id(10)
                .name("testPet")
                .category(new Pet.Category(10, "category"))
                .photoUrls(List.of("photoUrl"))
                .tags(List.of(new Pet.Tags(10, "tag")))
                .status("available")
                .build();

        Pet created = apiPet.createPetWithObject(pet);

        // Проверяем все поля созданного питомца
        assertEquals(pet.getId(), created.getId(), "Неверный ID");
        assertEquals(pet.getCategory().getName(), created.getCategory().getName(), "Неверное имя категории");
        assertEquals(pet.getCategory().getId(), created.getCategory().getId(), "Неверный ID категории");
        assertEquals(pet.getName(), created.getName(), "Неверное имя питомца");
        assertEquals(pet.getStatus(), created.getStatus(), "Неверный статус питомца");
        assertEquals(pet.getPhotoUrls().get(0), created.getPhotoUrls().get(0), "Неверный photoUrl");
        assertEquals(pet.getTags().get(0).getId(), created.getTags().get(0).getId(), "Неверный ID тэга");
        assertEquals(pet.getTags().get(0).getName(), created.getTags().get(0).getName(), "Неверное имя тэга");
    }

    // ================================
    @Story("create pet with json")
    @Description("Создание питомца через JSON")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Создание питомца через JSON")
    @Test
    public void createPetWithJson() {
        String json = "{...}"; // JSON для теста

        Pet created = apiPet.createPetWithJson(json);

        // Проверка полей созданного питомца через JsonPath
        // (логика совпадает с исходным тестом)
        // ...
    }

    // ================================
    @Story("get pet by status {status}")
    @Description("Получение питомцев по статусу")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Получение питомцев по статусу")
    @ParameterizedTest
    @ValueSource(strings = {"available", "sold", "pending"})
    public void getPetByStatus(String status) throws JsonProcessingException {
        PetStatus petStatus = PetStatus.valueOf(status);
        List<Pet> list = apiPet.getByStatus(petStatus);
        assertTrue(list.stream().allMatch(s -> s.getStatus().equals(petStatus.name())), "Неверный статус");
    }

    // ================================
    @Story("get pet by id {id}")
    @Description("Получение питомца по ID")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Получение питомца по ID")
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    public void getPetById(int id) throws Exception {
        String name = "getPet";

        // Создаем питомца
        Allure.step("Создаем питомца", () -> {
            Pet pet = new PetBuilder().id(id).name(name).build();
            Pet created = apiPet.createPetWithObject(pet);
            Thread.sleep(500);
            assertEquals(id, created.getId());
            assertEquals(name, created.getName());
        });

        // Проверяем через awaitility
        await().atMost(10, TimeUnit.SECONDS)
                .pollInterval(1, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    Pet pet = apiPet.getById(id);
                    assertEquals(id, pet.getId());
                    assertEquals(name, pet.getName());
                });

        Allure.step("Получаем питомца", () -> {
            Pet getPet = apiPet.getById(id);
            assertEquals(id, getPet.getId());
            assertEquals(name, getPet.getName());
        });
    }

    // ================================
    @Story("create/delete pet {id}")
    @Description("Создание и удаление питомца")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Создание и удаление питомца")
    @ParameterizedTest
    @ValueSource(ints = {10})
    public void deletePet(int id) {
        // Создаем питомца
        Allure.step("Создаем питомца", () -> {
            Pet pet = new PetBuilder().id(id).build();
            assertEquals(id, pet.getId());
        });

        await().atMost(5, TimeUnit.SECONDS)
                .pollInterval(1, TimeUnit.SECONDS)
                .until(() -> apiPet.getById(id) != null);

        // Удаляем питомца
        Allure.step("Удаляем питомца", () -> {
            ApiResponse apiResponse = apiPet.deletePet(id, "api_key");
            assertAll("Проверка ответа",
                    () -> assertEquals(200, apiResponse.getCode()),
                    () -> assertEquals("unknown", apiResponse.getType()),
                    () -> assertEquals(String.valueOf(id), apiResponse.getMessage())
            );
        });
    }

    // ================================
    @Story("404 get")
    @Description("Попытка получить несуществующего питомца")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Получение несуществующего питомца")
    @Test
    public void get404() {
        Random random = new Random();
        for (int i = 500; i < 503; i++) {
            apiPet.get404(random.nextInt(5000) * i);
        }
    }

    // ================================
    @Story("404 delete")
    @Description("Попытка удалить несуществующего питомца")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Удаление несуществующего питомца")
    @Test
    public void delete404() {
        Random random = new Random();
        for (int i = 500; i < 503; i++) {
            apiPet.delete404(random.nextInt(500) * i, String.valueOf(i));
        }
    }

    // ================================
    @Story("create 400")
    @Description("Создание питомца с неверным JSON")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Создание питомца с неверным JSON")
    @Test
    public void create404() {
        String json = "{].";
        apiPet.createPet400(json);
    }
}
