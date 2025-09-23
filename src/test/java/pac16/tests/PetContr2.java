package pac16.tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.internal.shadowed.jackson.annotation.JsonTypeInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pac16.api.ApiPet;
import pac16.base.BaseClass;
import pac16.model.ApiResponse;
import pac16.model.Pet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

@Epic("pet api")
@Feature("careate update get delete")

public class PetContr2 extends BaseClass {
   private ApiPet apiPet;
   @BeforeEach
   private void setUP(){
       super.setAll();
       apiPet=new ApiPet(petSpec,formSpec);
   }
   @Story("create get")
    @Description("get create")
    @Test
    public void test1(){
       String json = "{\n" +
               "  \"id\": 123,\n" +
               "  \"category\": {\n" +
               "    \"id\": 1,\n" +
               "    \"name\": \"Dogs\"\n" +
               "  },\n" +
               "  \"name\": \"Rex\",\n" +
               "  \"photoUrls\": [\n" +
               "    \"https://example.com/photo/rex1.jpg\"\n" +
               "  ],\n" +
               "  \"tags\": [\n" +
               "    {\n" +
               "      \"id\": 101,\n" +
               "      \"name\": \"friendly\"\n" +
               "    },\n" +
               "    {\n" +
               "      \"id\": 102,\n" +
               "      \"name\": \"trained\"\n" +
               "    }\n" +
               "  ],\n" +
               "  \"status\": \"available\"\n" +
               "}";
       step("create pet",()->{
           try {
               Pet pet=apiPet.createPetWithJson(json);
               Assertions.assertEquals("Rex",pet.getName());
               Assertions.assertEquals("Dogs",pet.getCategory().getName());
               Assertions.assertEquals("friendly",pet.getTags().get(0).getName());
           } catch (JsonProcessingException e) {
               throw new RuntimeException(e);
           }
       });

       step("get pet",()->{
           Pet pet=apiPet.getPetById(123);
           Assertions.assertEquals(123,pet.getId());
           Assertions.assertEquals("Rex",pet.getName());
       });

   }
   @Story("pet get create")
    @Description("pet get create")
    @Test
    public void test2(){
       Pet.Category category = new Pet.Category();
       category.setId(1);
       category.setName("category");
       Pet.Tags tag1 = new Pet.Tags();
       tag1.setId(1);
       tag1.setName("tag1");
       Pet.Tags tag2=new Pet.Tags(2,"tag2");
       List<Pet.Tags> tags=new ArrayList<>(Arrays.asList(new Pet.Tags[]{tag1,tag2}));
       String photo="photo";
       List<String>photoUrls=new ArrayList<>();
       photoUrls.add(photo);
       long id=7;
       Pet pet=new Pet(id,category,"dinamo",photoUrls,tags,"available");
       step("create pet",()->{
           try {
               Pet cretaetdPet=apiPet.createPet(pet);
               Assertions.assertTrue(cretaetdPet.getId()>0);
               Assertions.assertEquals("dinamo",cretaetdPet.getName());
               Assertions.assertEquals("available",cretaetdPet.getStatus());
               Assertions.assertTrue(!cretaetdPet.getTags().isEmpty());
               Assertions.assertEquals(1,cretaetdPet.getTags().get(0).getId());
               Assertions.assertEquals("tag1",cretaetdPet.getTags().get(0).getName());
               Assertions.assertEquals(2,cretaetdPet.getTags().get(1).getId());
               Assertions.assertEquals("tag2",cretaetdPet.getTags().get(1).getName());
               Assertions.assertEquals("photo",cretaetdPet.getPhotoUrls().get(0));
           } catch (JsonProcessingException e) {
               throw new RuntimeException(e);
           }
       });
       step("wait pet",()->{
           await().atMost(5,TimeUnit.SECONDS)
                   .pollInterval(500,TimeUnit.MILLISECONDS)
                   .until(()->{
                       try{
                           Pet fetchedPet=apiPet.getPetById(id);
                           return fetchedPet!=null&&fetchedPet.getId()>0;
                       }
                       catch (Exception e){
                           return false;
                       }
                   });
       });
       step("get pet",()->{
           Pet createdPet=apiPet.getPetById(id);
           System.out.println(createdPet);
           Assertions.assertTrue(createdPet.getId()>0);
           Assertions.assertEquals("dinamo",createdPet.getName());
           Assertions.assertEquals("available",createdPet.getStatus());
           Assertions.assertTrue(!createdPet.getTags().isEmpty());
           Assertions.assertEquals("tag1",createdPet.getTags().get(0).getName());
       });
   }
}
