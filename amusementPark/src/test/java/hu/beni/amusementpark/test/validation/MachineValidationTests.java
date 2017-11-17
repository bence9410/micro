package hu.beni.amusementpark.test.validation;

import static hu.beni.amusementpark.constants.FieldNameConstants.*;
import static hu.beni.amusementpark.constants.StringParamConstants.*;
import static hu.beni.amusementpark.constants.ValidationMessageConstants.*;

import org.junit.Before;
import org.junit.Test;

import hu.beni.amusementpark.entity.Machine;

import static hu.beni.amusementpark.helper.ValidEntityFactory.createMachine;

public class MachineValidationTests extends AbstractValidation<Machine>{

	private Machine machine;
	
	@Before
	public void setUp() {
		machine = createMachine();
	}

	@Test
	public void validAddress() {
		validateAndAssertNoViolations(machine);
	}

	@Test
	public void invalidFantasyName() {
		machine.setFantasyName(null);
		validateAndAssertViolationsSizeIsOne(machine);
		assertInvalidValueAndPropertyNameAndMessageEquals(machine.getFantasyName(), FANTASY_NAME, NOT_NULL_MESSAGE);

		machine.setFantasyName(STRING_WITH_4_LENGTH);
		validateAndAssertViolationsSizeIsOne(machine);
		assertInvalidValueAndPropertyNameAndMessageEquals(machine.getFantasyName(), FANTASY_NAME, SIZE_5_25_MESSAGE);

		machine.setFantasyName(STRING_WITH_26_LENGTH);
		validateAndAssertViolationsSizeIsOne(machine);
		assertInvalidValueAndPropertyNameAndMessageEquals(machine.getFantasyName(), FANTASY_NAME, SIZE_5_25_MESSAGE);
	}
	
	
}
