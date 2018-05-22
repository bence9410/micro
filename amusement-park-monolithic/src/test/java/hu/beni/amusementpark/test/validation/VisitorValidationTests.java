package hu.beni.amusementpark.test.validation;

import static hu.beni.amusementpark.constants.FieldNameConstants.DATE_OF_BIRTH;
import static hu.beni.amusementpark.constants.FieldNameConstants.NAME;
import static hu.beni.amusementpark.constants.FieldNameConstants.SPENDING_MONEY;
import static hu.beni.amusementpark.constants.FieldNameConstants.USERNAME;
import static hu.beni.amusementpark.constants.StringParamConstants.STRING_WITH_26_LENGTH;
import static hu.beni.amusementpark.constants.StringParamConstants.STRING_WITH_4_LENGTH;
import static hu.beni.amusementpark.constants.ValidationMessageConstants.NOT_NULL_MESSAGE;
import static hu.beni.amusementpark.constants.ValidationMessageConstants.RANGE_50_INTEGER_MAX_VALUE_MESSAGE;
import static hu.beni.amusementpark.constants.ValidationMessageConstants.SIZE_5_25_MESSAGE;
import static hu.beni.amusementpark.helper.ValidEntityFactory.createVisitor;

import java.time.LocalDate;

import org.junit.Before;
import org.junit.Test;

import hu.beni.amusementpark.entity.Visitor;

public class VisitorValidationTests extends AbstractValidation<Visitor> {

	private Visitor visitor;

	@Before
	public void setUp() {
		visitor = createVisitor();
	}

	@Test
	public void validVisitor() {
		validateAndAssertNoViolations(visitor);
	}

	@Test
	public void invalidName() {
		visitor.setName(null);
		validateAndAssertViolationsSizeIsOne(visitor);
		assertInvalidValueAndPropertyNameAndMessageEquals(visitor.getName(), NAME, NOT_NULL_MESSAGE);

		visitor.setName(STRING_WITH_4_LENGTH);
		validateAndAssertViolationsSizeIsOne(visitor);
		assertInvalidValueAndPropertyNameAndMessageEquals(visitor.getName(), NAME, SIZE_5_25_MESSAGE);

		visitor.setName(STRING_WITH_26_LENGTH);
		validateAndAssertViolationsSizeIsOne(visitor);
		assertInvalidValueAndPropertyNameAndMessageEquals(visitor.getName(), NAME, SIZE_5_25_MESSAGE);
	}

	@Test
	public void invalidUsername() {
		visitor.setUsername(null);
		validateAndAssertViolationsSizeIsOne(visitor);
		assertInvalidValueAndPropertyNameAndMessageEquals(visitor.getUsername(), USERNAME, NOT_NULL_MESSAGE);

		visitor.setUsername(STRING_WITH_4_LENGTH);
		validateAndAssertViolationsSizeIsOne(visitor);
		assertInvalidValueAndPropertyNameAndMessageEquals(visitor.getUsername(), USERNAME, SIZE_5_25_MESSAGE);

		visitor.setUsername(STRING_WITH_26_LENGTH);
		validateAndAssertViolationsSizeIsOne(visitor);
		assertInvalidValueAndPropertyNameAndMessageEquals(visitor.getUsername(), USERNAME, SIZE_5_25_MESSAGE);
	}

	@Test
	public void invalidDateOfBirth() {
		visitor.setDateOfBirth(null);
		validateAndAssertViolationsSizeIsOne(visitor);
		assertInvalidValueAndPropertyNameAndMessageEquals(visitor.getDateOfBirth(), DATE_OF_BIRTH, NOT_NULL_MESSAGE);

		visitor.setDateOfBirth(LocalDate.now());
		validateAndAssertViolationsSizeIsOne(visitor);
		assertInvalidValueAndPropertyNameAndMessageEquals(visitor.getDateOfBirth(), DATE_OF_BIRTH,
				"must be a past date");
	}

	@Test
	public void invalidSpendingMoney() {
		visitor.setSpendingMoney(null);
		validateAndAssertViolationsSizeIsOne(visitor);
		assertInvalidValueAndPropertyNameAndMessageEquals(visitor.getSpendingMoney(), SPENDING_MONEY, NOT_NULL_MESSAGE);

		visitor.setSpendingMoney(49);
		validateAndAssertViolationsSizeIsOne(visitor);
		assertInvalidValueAndPropertyNameAndMessageEquals(visitor.getSpendingMoney(), SPENDING_MONEY,
				RANGE_50_INTEGER_MAX_VALUE_MESSAGE);
	}
}
