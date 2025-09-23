package pac16.tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import pac16.api.ApiPet;
import pac16.base.BaseClass;
import pac16.model.Pet;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.List;

@Epic("pet api")
@Feature("get user by status")

public class PetTestStatuses extends BaseClass {
    @ParameterizedTest
    @ValueSource(strings = {"available","sold","pending"})
    public void getByStatus(String status){
        ApiPet apiPet=new ApiPet(petSpec);
        List<Pet>list=apiPet.get(status);
        assertThat(list,is(not(empty())));
        assertThat(list.stream().map(s->s.getStatus()).toList(),everyItem(equalTo(status)));
    }
    @Test
    @Story("get pet by statuses")
    @Description("получение пет по статусам")
    public void test(){
        ApiPet apiPet=new ApiPet(petSpec);
        step("get available status",()->{
            List<String>list=apiPet.getPetWithAvailableStatus();
            assertThat(list,is(not(empty())));
            assertThat(list,everyItem(equalTo("available")));
        });
        step("get sold status",()->{
            List<String>list=apiPet.getPetWithSoldStatus();
            assertThat(list,is(not(empty())));
            assertThat(list,everyItem(equalTo("sold")));
        });
        step("get pending status",()->{
            List<String>list=apiPet.getPetWithPendingStatus();
            assertThat(list,is(not(empty())));
            assertThat(list,everyItem(equalTo("pending")));
        });
    }
}
