package api_project.mock;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

/**
 * Базовый класс для работы с мок-сервисами (Mock API)
 * Содержит общие методы для всех API
 */
public abstract class BaseMockApi {

    // Объект RequestSpecification из REST-assured — хранит базовые настройки запроса (baseUri, headers и т.д.)
    protected final RequestSpecification requestSpecification;

    // Jackson ObjectMapper — для конвертации JSON в объекты и обратно
    protected final ObjectMapper objectMapper;

    /**
     * Конструктор, инициализирует спецификацию запроса и ObjectMapper
     *
     * @param requestSpecification REST-assured спецификация запроса
     * @param objectMapper Jackson ObjectMapper
     */
    protected BaseMockApi(RequestSpecification requestSpecification, ObjectMapper objectMapper){
        this.requestSpecification = requestSpecification;
        this.objectMapper = objectMapper;
    }

    /**
     * Универсальный метод конвертации Response в объект указанного класса
     *
     * @param response объект ответа от REST-assured
     * @param clazz    класс, в который нужно конвертировать JSON
     * @param <T>      тип возвращаемого объекта
     * @return объект типа T
     * @throws RuntimeException если произошла ошибка конвертации JSON
     */
    protected <T> T convert(Response response, Class<T> clazz){
        try {
            return objectMapper.readValue(response.asString(), clazz);
        } catch (JsonProcessingException e) {
            // Пробрасываем RuntimeException, чтобы тест падал при некорректном JSON
            throw new RuntimeException(e);
        }
    }
}
