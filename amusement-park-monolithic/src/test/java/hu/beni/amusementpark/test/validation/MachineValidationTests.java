package hu.beni.amusementpark.test.validation;

import static hu.beni.amusementpark.constants.FieldNameConstants.*;
import static hu.beni.amusementpark.constants.StringParamConstants.*;
import static hu.beni.amusementpark.constants.ValidationMessageConstants.*;

import org.junit.Before;
import org.junit.Test;

import hu.beni.amusementpark.entity.Machine;

import static hu.beni.amusementpark.helper.ValidEntityFactory.createMachine;

public class MachineValidationTests extends AbstractValidation<Machine> {

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

	@Test
	public void invalidSize() {
		machine.setSize(null);
		validateAndAssertViolationsSizeIsOne(machine);
		assertInvalidValueAndPropertyNameAndMessageEquals(machine.getSize(), SIZE, NOT_NULL_MESSAGE);

		machine.setSize(19);
		validateAndAssertViolationsSizeIsOne(machine);
		assertInvalidValueAndPropertyNameAndMessageEquals(machine.getSize(), SIZE, RANGE_20_200_MESSAGE);

		machine.setSize(201);
		validateAndAssertViolationsSizeIsOne(machine);
		assertInvalidValueAndPropertyNameAndMessageEquals(machine.getSize(), SIZE, RANGE_20_200_MESSAGE);
	}

	@Test
	public void invalidPrice() {
		machine.setPrice(null);
		validateAndAssertViolationsSizeIsOne(machine);
		assertInvalidValueAndPropertyNameAndMessageEquals(machine.getPrice(), PRICE, NOT_NULL_MESSAGE);

		machine.setPrice(49);
		validateAndAssertViolationsSizeIsOne(machine);
		assertInvalidValueAndPropertyNameAndMessageEquals(machine.getPrice(), PRICE, RANGE_50_2000_MESSAGE);

		machine.setPrice(2001);
		validateAndAssertViolationsSizeIsOne(machine);
		assertInvalidValueAndPropertyNameAndMessageEquals(machine.getPrice(), PRICE, RANGE_50_2000_MESSAGE);
	}

	@Test
	public void invalidNumberOfSeats() {
		machine.setNumberOfSeats(null);
		validateAndAssertViolationsSizeIsOne(machine);
		assertInvalidValueAndPropertyNameAndMessageEquals(machine.getNumberOfSeats(), NUMBER_OF_SEATS,
				NOT_NULL_MESSAGE);

		machine.setNumberOfSeats(4);
		validateAndAssertViolationsSizeIsOne(machine);
		assertInvalidValueAndPropertyNameAndMessageEquals(machine.getNumberOfSeats(), NUMBER_OF_SEATS,
				RANGE_5_30_MESSAGE);

		machine.setNumberOfSeats(31);
		validateAndAssertViolationsSizeIsOne(machine);
		assertInvalidValueAndPropertyNameAndMessageEquals(machine.getNumberOfSeats(), NUMBER_OF_SEATS,
				RANGE_5_30_MESSAGE);
	}

	@Test
	public void invalidMinimumRequiredAge() {
		machine.setMinimumRequiredAge(null);
		validateAndAssertViolationsSizeIsOne(machine);
		assertInvalidValueAndPropertyNameAndMessageEquals(machine.getMinimumRequiredAge(), MINIMUM_REQUIRED_AGE,
				NOT_NULL_MESSAGE);

		machine.setMinimumRequiredAge(-1);
		validateAndAssertViolationsSizeIsOne(machine);
		assertInvalidValueAndPropertyNameAndMessageEquals(machine.getMinimumRequiredAge(), MINIMUM_REQUIRED_AGE,
				RANGE_0_21_MESSAGE);

		machine.setMinimumRequiredAge(22);
		validateAndAssertViolationsSizeIsOne(machine);
		assertInvalidValueAndPropertyNameAndMessageEquals(machine.getMinimumRequiredAge(), MINIMUM_REQUIRED_AGE,
				RANGE_0_21_MESSAGE);
	}

	@Test
	public void invalidTicketPrice() {
		machine.setTicketPrice(null);
		validateAndAssertViolationsSizeIsOne(machine);
		assertInvalidValueAndPropertyNameAndMessageEquals(machine.getTicketPrice(), TICKET_PRICE, NOT_NULL_MESSAGE);

		machine.setTicketPrice(4);
		validateAndAssertViolationsSizeIsOne(machine);
		assertInvalidValueAndPropertyNameAndMessageEquals(machine.getTicketPrice(), TICKET_PRICE, RANGE_5_30_MESSAGE);

		machine.setTicketPrice(31);
		validateAndAssertViolationsSizeIsOne(machine);
		assertInvalidValueAndPropertyNameAndMessageEquals(machine.getTicketPrice(), TICKET_PRICE, RANGE_5_30_MESSAGE);
	}

	@Test
	public void invalidType() {
		machine.setType(null);
		validateAndAssertViolationsSizeIsOne(machine);
		assertInvalidValueAndPropertyNameAndMessageEquals(machine.getType(), TYPE, NOT_NULL_MESSAGE);
	}
}