package hu.beni.amusementpark.test.validation;

import static hu.beni.amusementpark.constants.StringParamConstants.STRING_WITH_26_LENGTH;
import static hu.beni.amusementpark.constants.StringParamConstants.STRING_WITH_4_LENGTH;
import static hu.beni.amusementpark.constants.ValidationMessageConstants.NOT_NULL_MESSAGE;
import static hu.beni.amusementpark.constants.ValidationMessageConstants.rangeMessage;
import static hu.beni.amusementpark.constants.ValidationMessageConstants.sizeMessage;
import static hu.beni.amusementpark.helper.ValidEntityFactory.createMachine;

import org.junit.Before;
import org.junit.Test;

import hu.beni.amusementpark.entity.Machine;

public class MachineValidationTests extends AbstractValidation<Machine> {

	private static final String FANTASY_NAME = "fantasyName";
	private static final String SIZE = "size";
	private static final String PRICE = "price";
	private static final String NUMBER_OF_SEATS = "numberOfSeats";
	private static final String MINIMUM_REQUIRED_AGE = "minimumRequiredAge";
	private static final String TICKET_PRICE = "ticketPrice";
	private static final String TYPE = "type";

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
		assertInvalidValueAndPropertyNameAndMessageEquals(machine.getFantasyName(), FANTASY_NAME, sizeMessage(5, 25));

		machine.setFantasyName(STRING_WITH_26_LENGTH);
		validateAndAssertViolationsSizeIsOne(machine);
		assertInvalidValueAndPropertyNameAndMessageEquals(machine.getFantasyName(), FANTASY_NAME, sizeMessage(5, 25));
	}

	@Test
	public void invalidSize() {
		machine.setSize(null);
		validateAndAssertViolationsSizeIsOne(machine);
		assertInvalidValueAndPropertyNameAndMessageEquals(machine.getSize(), SIZE, NOT_NULL_MESSAGE);

		machine.setSize(19);
		validateAndAssertViolationsSizeIsOne(machine);
		assertInvalidValueAndPropertyNameAndMessageEquals(machine.getSize(), SIZE, rangeMessage(20, 200));

		machine.setSize(201);
		validateAndAssertViolationsSizeIsOne(machine);
		assertInvalidValueAndPropertyNameAndMessageEquals(machine.getSize(), SIZE, rangeMessage(20, 200));
	}

	@Test
	public void invalidPrice() {
		machine.setPrice(null);
		validateAndAssertViolationsSizeIsOne(machine);
		assertInvalidValueAndPropertyNameAndMessageEquals(machine.getPrice(), PRICE, NOT_NULL_MESSAGE);

		machine.setPrice(49);
		validateAndAssertViolationsSizeIsOne(machine);
		assertInvalidValueAndPropertyNameAndMessageEquals(machine.getPrice(), PRICE, rangeMessage(50, 2000));

		machine.setPrice(2001);
		validateAndAssertViolationsSizeIsOne(machine);
		assertInvalidValueAndPropertyNameAndMessageEquals(machine.getPrice(), PRICE, rangeMessage(50, 2000));
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
				rangeMessage(5, 30));

		machine.setNumberOfSeats(31);
		validateAndAssertViolationsSizeIsOne(machine);
		assertInvalidValueAndPropertyNameAndMessageEquals(machine.getNumberOfSeats(), NUMBER_OF_SEATS,
				rangeMessage(5, 30));
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
				rangeMessage(0, 21));

		machine.setMinimumRequiredAge(22);
		validateAndAssertViolationsSizeIsOne(machine);
		assertInvalidValueAndPropertyNameAndMessageEquals(machine.getMinimumRequiredAge(), MINIMUM_REQUIRED_AGE,
				rangeMessage(0, 21));
	}

	@Test
	public void invalidTicketPrice() {
		machine.setTicketPrice(null);
		validateAndAssertViolationsSizeIsOne(machine);
		assertInvalidValueAndPropertyNameAndMessageEquals(machine.getTicketPrice(), TICKET_PRICE, NOT_NULL_MESSAGE);

		machine.setTicketPrice(4);
		validateAndAssertViolationsSizeIsOne(machine);
		assertInvalidValueAndPropertyNameAndMessageEquals(machine.getTicketPrice(), TICKET_PRICE, rangeMessage(5, 30));

		machine.setTicketPrice(31);
		validateAndAssertViolationsSizeIsOne(machine);
		assertInvalidValueAndPropertyNameAndMessageEquals(machine.getTicketPrice(), TICKET_PRICE, rangeMessage(5, 30));
	}

	@Test
	public void invalidType() {
		machine.setType(null);
		validateAndAssertViolationsSizeIsOne(machine);
		assertInvalidValueAndPropertyNameAndMessageEquals(machine.getType(), TYPE, NOT_NULL_MESSAGE);
	}
}