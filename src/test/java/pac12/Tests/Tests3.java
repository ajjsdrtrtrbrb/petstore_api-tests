package pac12.Tests;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import pac12.Base.BaseClass;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.junit.jupiter.api.Tags;
import pac12.Base.BaseClass;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import pac10.Model.Pet;
import pac12.Exception.MyException;
import pac12.Model.User;


import java.util.*;

import static io.restassured.path.xml.XmlPath.given;

public class Tests3 extends BaseClass {
    @Test
    public void test1() throws JsonProcessingException {
        String json="{\n" +
                "  \"id\": 101,\n" +
                "  \"username\": \"john_doe\",\n" +
                "  \"firstName\": \"John\",\n" +
                "  \"lastName\": \"Doe\",\n" +
                "  \"email\": \"john@example.com\",\n" +
                "  \"password\": \"12345\",\n" +
                "  \"phone\": \"123-456-7890\",\n" +
                "  \"userStatus\": 1\n" +
                "}";
        Response response
                =given()
                .spec(specForUser)
                .body(json)
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract().response();
        User user1=response.as(User.class);
        Assertions.assertTrue(user1.getId()>0);
        ObjectMapper mapper=new ObjectMapper();
        User user2=mapper.readValue(response.asString(),User.class);
        Assertions.assertEquals(101,user2.getId());
        Assertions.assertEquals("John",user2.getFirstName());
        JsonNode root=mapper.readTree(response.asString());
        Assertions.assertTrue(root.has("id"));
        int id=root.get("id").asInt();
        Assertions.assertEquals(id,user2.getId());
        User user3=mapper.treeToValue(root,User.class);
        Assertions.assertTrue(user3.getId()>0);
    }
}
