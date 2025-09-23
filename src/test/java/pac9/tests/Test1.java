package pac9.tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import pac9.base.BaseClass;
import pac9.model.User;

import java.util.List;
import java.util.Objects;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class Test1 extends BaseClass {
    @ParameterizedTest
    @ValueSource(ints = {1})
    public void test1(int id){
        User user= given()
                .spec(spec2)
                .when()
                .get("{id}",id)
                .then()
                .statusCode(200)
                .extract()
                .as(User.class);
        Assertions.assertEquals(id,user.getId());
        Assertions.assertEquals("User 1",user.getName());
        Assertions.assertEquals("Password1",user.getPassword());
    }
    @ParameterizedTest
    @ValueSource(ints = {1,2,3})
    public void test2(int id){
        given()
                .spec(spec2)
                .when()
                .get("{id}",id)
                .then()
                .statusCode(200)
                .body("id",equalTo(id))
                .body("userName",notNullValue());
    }
    @Test
    public void test3(){
        List<User> list =
                given()
                        .spec(spec1)
                        .when()
                        .get()
                        .then()
                        .statusCode(200)
                        .extract()
                        .body()
                        .jsonPath()
                        .getList("",User.class);
        Assertions.assertFalse(list.isEmpty(),"is empty");
        int count=0;
        for(User s:list){
            Assertions.assertEquals(++count,s.getId());
            Assertions.assertTrue(s.getId()>0);
            Assertions.assertNotNull(s.getName());
            Assertions.assertNotNull(s.getPassword());
        }
    }
    @Test
    public void test4() throws JsonProcessingException {
        Response response=RestAssured.given()
                .spec(spec1)
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract().response();
        ObjectMapper mapper=new ObjectMapper();
        List<User>list=mapper.readValue(response.asString(), new TypeReference<List<User>>() {});
        Assertions.assertFalse(list.isEmpty(),"empty");
        Assertions.assertEquals(1,list.get(0).getId(),"not 1");
    }
    @Test
    public void test5() throws JsonProcessingException {
        Response response=RestAssured.given()
                .spec(spec1)
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract().response();
        ObjectMapper mapper=new ObjectMapper();
        List<User>list=mapper.readValue(response.asString(), new TypeReference<List<User>>() {});
        for(User s:list){
            Assertions.assertTrue(s.getId()>0);
            Assertions.assertTrue(s.getName()!=null);
        }
    }
}
