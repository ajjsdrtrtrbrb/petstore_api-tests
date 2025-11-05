package api_project.utils;
import api_project.model.User;
import api_project.model.UserBuilder;
import io.restassured.specification.Argument;
import org.junit.jupiter.params.provider.Arguments;

import java.io.IOException;
import java.nio.file.*;
import java.util.stream.Stream;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class UserUtils {
    // Генератор случайных чисел для id и других случайных данных
    private static final Random random=new Random();
    /**
     * Генерирует список пользователей со случайными ID.
     * Используется для тестов, где важна уникальность данных.
     * @param count количество пользователей в списке
     * @return список объектов User с разными случайными id
     */
    public static List<User>generateListUsersRandomId(int count){
        return IntStream.rangeClosed(1,count)
                .mapToObj(i-> {
                    int id = random.nextInt(1000) + 1;
                   return new UserBuilder()
                            .id(id)
                            .username("user" + id)
                            .email("user" + id + "@example.com")

                            .build();
                }).collect(Collectors.toList());
    }
    /**
     * Генерирует список пользователей с последовательными ID (фиксированными).
     * Удобно использовать для параметризованных тестов, где нужно предсказуемое значение.
     * @param startId начальное значение id
     * @param count количество пользователей
     * @return список объектов User с id от startId до startId + count
     */
    public static List<User>generateListUserWithFixedId(int startId,int count){
        return IntStream.range(startId,startId+count)
                .mapToObj(i->{
                    return new UserBuilder()
                            .id(i)
                            .username("user"+i)
                            .email("user"+i+"@example.com")
                            .build();
                }).collect(Collectors.toList());
    }
    /**
     * Возвращает поток JSON-строк с тестовыми пользователями.
     * Применяется в тестах для проверки сериализации/десериализации JSON.
     * @return Stream<String> с JSON-представлением пользователей
     */
    public static Stream<String>userJsonStream(){
        return Stream.of( """
                {
                  "id": 501,
                  "username": "alpha",
                  "firstName": "John",
                  "lastName": "Doe",
                  "email": "john.doe@example.com",
                  "password": "12345",
                  "phone": "+380501234567",
                  "userStatus": 1
                }
                """,
                """
                {
                  "id": 502,
                  "username": "bravo",
                  "firstName": "Jane",
                  "lastName": "Smith",
                  "email": "jane.smith@example.com",
                  "password": "qwerty",
                  "phone": "+380671112233",
                  "userStatus": 1
                }
                """,
                """
                {
                  "id": 503,
                  "username": "charlie",
                  "firstName": "Mike",
                  "lastName": "Johnson",
                  "email": "mike.j@example.com",
                  "password": "pass123",
                  "phone": "+380931234567",
                  "userStatus": 1
                }
                """);
    }
    /**
     * Возвращает поток параметров (Arguments) для PUT-запросов с валидными пользователями.
     * Используется в @ParameterizedTest с @MethodSource("userPutStream").
     * @return Stream<Arguments> с тестовыми данными для обновления пользователя
     */
    public static Stream<Arguments> userPutStream(){
        return Stream.of(
                Arguments.of( "test1",
                        new User(1, "acsa", "First", "Last", "acsa@mail.com", "12345", "1234567890", 0)),
                Arguments.of("test2",new User(2, "testUser", "Test", "User", "test@mail.com", "pass", "0987654321", 1))
        );
    }
    /**
     * Возвращает поток параметров (Arguments) для PUT-запросов с невалидными данными.
     * Используется для тестирования обработки ошибок и негативных сценариев API.
     * @return Stream<Arguments> с некорректными JSON-строками
     */
    public static Stream<Arguments>userInvalidPut(){
       return Stream.of(
                Arguments.of("[","{invalid}"),
                Arguments.of("=","{username:}")
        );
    }
}
