package pac16.tests;

import org.junit.jupiter.api.Test;
import pac16.api.ApiPet;
import pac16.base.BaseClass;

public class PetTest2 extends BaseClass {
    @Test
    public void test(){
        ApiPet apiPet=new ApiPet(petSpec,formSpec);

    }
}
