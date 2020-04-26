package hu.beni.amusementpark.test.validation;

import static hu.beni.amusementpark.constants.StringParamConstants.STRING_WITH_21_LENGTH;
import static hu.beni.amusementpark.constants.StringParamConstants.STRING_WITH_4_LENGTH;
import static hu.beni.amusementpark.constants.ValidationMessageConstants.NOT_NULL_MESSAGE;
import static hu.beni.amusementpark.constants.ValidationMessageConstants.rangeMessage;
import static hu.beni.amusementpark.constants.ValidationMessageConstants.sizeMessage;
import static hu.beni.amusementpark.helper.ValidEntityFactory.createAmusementPark;

import org.junit.Before;
import org.junit.Test;

import hu.beni.amusementpark.entity.AmusementPark;

public class AmusementParkValidationTests extends AbstractValidation<AmusementPark> {

	private static final String NAME = "name";
	private static final String CAPITAL = "capital";
	private static final String TOTAL_AREA = "totalArea";
	private static final String ENTRANCE_FEE = "entranceFee";

	private AmusementPark amusementPark;

	@Before
	public void setUp() {
		amusementPark = createAmusementPark();
	}

	@Test
	public void validAmusementPark() {
		validateAndAssertNoViolations(amusementPark);
	}

	@Test
	public void invalidName() {
		amusementPark.setName(null);
		validateAndAssertViolationsSizeIsOne(amusementPark);
		assertInvalidValueAndPropertyNameAndMessageEquals(amusementPark.getName(), NAME, NOT_NULL_MESSAGE);

		amusementPark.setName(STRING_WITH_4_LENGTH);
		validateAndAssertViolationsSizeIsOne(amusementPark);
		assertInvalidValueAndPropertyNameAndMessageEquals(amusementPark.getName(), NAME, sizeMessage(5, 20));

		amusementPark.setName(STRING_WITH_21_LENGTH);
		validateAndAssertViolationsSizeIsOne(amusementPark);
		assertInvalidValueAndPropertyNameAndMessageEquals(amusementPark.getName(), NAME, sizeMessage(5, 20));
	}

	@Test
	public void invalidCapital() {
		amusementPark.setCapital(null);
		validateAndAssertViolationsSizeIsOne(amusementPark);
		assertInvalidValueAndPropertyNameAndMessageEquals(amusementPark.getCapital(), CAPITAL, NOT_NULL_MESSAGE);

		amusementPark.setCapital(99);
		validateAndAssertViolationsSizeIsOne(amusementPark);
		assertInvalidValueAndPropertyNameAndMessageEquals(amusementPark.getCapital(), CAPITAL,
				rangeMessage(500, 50000));

		amusementPark.setCapital(50001);
		validateAndAssertViolationsSizeIsOne(amusementPark);
		assertInvalidValueAndPropertyNameAndMessageEquals(amusementPark.getCapital(), CAPITAL,
				rangeMessage(500, 50000));
	}

	@Test
	public void invalidTotalArea() {
		amusementPark.setTotalArea(null);
		validateAndAssertViolationsSizeIsOne(amusementPark);
		assertInvalidValueAndPropertyNameAndMessageEquals(amusementPark.getTotalArea(), TOTAL_AREA, NOT_NULL_MESSAGE);

		amusementPark.setTotalArea(49);
		validateAndAssertViolationsSizeIsOne(amusementPark);
		assertInvalidValueAndPropertyNameAndMessageEquals(amusementPark.getTotalArea(), TOTAL_AREA,
				rangeMessage(50, 5000));

		amusementPark.setTotalArea(5001);
		validateAndAssertViolationsSizeIsOne(amusementPark);
		assertInvalidValueAndPropertyNameAndMessageEquals(amusementPark.getTotalArea(), TOTAL_AREA,
				rangeMessage(50, 5000));
	}

	@Test
	public void invalidEntranceFee() {
		amusementPark.setEntranceFee(null);
		validateAndAssertViolationsSizeIsOne(amusementPark);
		assertInvalidValueAndPropertyNameAndMessageEquals(amusementPark.getEntranceFee(), ENTRANCE_FEE,
				NOT_NULL_MESSAGE);

		amusementPark.setEntranceFee(4);
		validateAndAssertViolationsSizeIsOne(amusementPark);
		assertInvalidValueAndPropertyNameAndMessageEquals(amusementPark.getEntranceFee(), ENTRANCE_FEE,
				rangeMessage(5, 200));

		amusementPark.setEntranceFee(201);
		validateAndAssertViolationsSizeIsOne(amusementPark);
		assertInvalidValueAndPropertyNameAndMessageEquals(amusementPark.getEntranceFee(), ENTRANCE_FEE,
				rangeMessage(5, 200));
	}
}