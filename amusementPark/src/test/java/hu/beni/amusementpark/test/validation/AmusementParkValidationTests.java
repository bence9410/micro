package hu.beni.amusementpark.test.validation;

import org.junit.Before;
import org.junit.Test;

import hu.beni.amusementpark.entity.AmusementPark;
import static hu.beni.amusementpark.test.ValidEntityFactory.*;

public class AmusementParkValidationTests extends AbstractValidation<AmusementPark>{

	private AmusementPark amusementPark;
	
	@Before
	public void setUp() {
		amusementPark = createAmusementParkWithAddress();
	}
	
	@Test
	public void validAmusementPark() {
		validateAndAssertNoViolations(amusementPark);
	}
	
	
}
