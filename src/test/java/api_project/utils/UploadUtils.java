package api_project.utils;
import static io.restassured.RestAssured.given;
import io.restassured.specification.RequestSpecification;
import java.io.File;
import java.io.IOException;

/**
 * Утилитный класс для загрузки изображений на сервер через API.
 * <p>
 * Используется в тестах Pet API (например, POST /pet/{petId}/uploadImage).
 * Оборачивает RestAssured-вызов для удобства и повторного использования.
 */

public class UploadUtils {

    /**
     * Загружает изображение для указанного питомца.
     *
     * @param spec     базовая спецификация запроса (RequestSpecification), содержащая baseURI, headers и т.д.
     * @param id       идентификатор питомца (petId), которому загружается изображение
     * @param file     объект файла, который нужно отправить
     * @param metaData дополнительная мета-информация, передаваемая вместе с изображением
     *
     * Пример использования:
     * <pre>
     * File image = new File("src/test/resources/pet.png");
     * UploadUtils.uploadImage(requestSpec, 123, image, "test image");
     * </pre>
     */
    public static void uploadImage(RequestSpecification spec,long id, File file,String metaData){

        given()
                .spec(spec)
                .pathParam("petId",id)
                .multiPart("additionalMetadata", metaData)
                .multiPart("file", file, "image/png")
                .when()
                .post("/{petId}/uploadImage")
                .then()
                .statusCode(200);
    }
}
