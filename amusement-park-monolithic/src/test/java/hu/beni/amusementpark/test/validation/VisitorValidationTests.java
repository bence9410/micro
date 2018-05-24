package hu.beni.amusementpark.test.validation;

import static hu.beni.amusementpark.constants.FieldNameConstants.DATE_OF_BIRTH;
import static hu.beni.amusementpark.constants.FieldNameConstants.NAME;
import static hu.beni.amusementpark.constants.FieldNameConstants.SPENDING_MONEY;
import static hu.beni.amusementpark.constants.FieldNameConstants.USERNAME;
import static hu.beni.amusementpark.constants.StringParamConstants.STRING_WITH_26_LENGTH;
import static hu.beni.amusementpark.constants.StringParamConstants.STRING_WITH_4_LENGTH;
import static hu.beni.amusementpark.constants.ValidationMessageConstants.NOT_NULL_MESSAGE;
import static hu.beni.amusementpark.constants.ValidationMessageConstants.PAST_MESSAGE;
import static hu.beni.amusementpark.constants.ValidationMessageConstants.rangeMessage;
import static hu.beni.amusementpark.constants.ValidationMessageConstants.sizeMessage;
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
		assertInvalidValueAndPropertyNameAndMessageEquals(visitor.getName(), NAME, sizeMessage(5, 25));

		visitor.setName(STRING_WITH_26_LENGTH);
		validateAndAssertViolationsSizeIsOne(visitor);
		assertInvalidValueAndPropertyNameAndMessageEquals(visitor.getName(), NAME, sizeMessage(5, 25));
	}

	@Test
	public void invalidUsername() {
		visitor.setUsername(null);
		validateAndAssertViolationsSizeIsOne(visitor);
		assertInvalidValueAndPropertyNameAndMessageEquals(visitor.getUsername(), USERNAME, NOT_NULL_MESSAGE);

		visitor.setUsername(STRING_WITH_4_LENGTH);
		validateAndAssertViolationsSizeIsOne(visitor);
		assertInvalidValueAndPropertyNameAndMessageEquals(visitor.getUsername(), USERNAME, sizeMessage(5, 25));

		visitor.setUsername(STRING_WITH_26_LENGTH);
		validateAndAssertViolationsSizeIsOne(visitor);
		assertInvalidValueAndPropertyNameAndMessageEquals(visitor.getUsername(), USERNAME, sizeMessage(5, 25));
	}

	@Test
	public void invalidDateOfBirth() {
		visitor.setDateOfBirth(null);
		validateAndAssertViolationsSizeIsOne(visitor);
		assertInvalidValueAndPropertyNameAndMessageEquals(visitor.getDateOfBirth(), DATE_OF_BIRTH, NOT_NULL_MESSAGE);

		visitor.setDateOfBirth(LocalDate.now());
		validateAndAssertViolationsSizeIsOne(visitor);
		assertInvalidValueAndPropertyNameAndMessageEquals(visitor.getDateOfBirth(), DATE_OF_BIRTH, PAST_MESSAGE);
	}

	@Test
	public void invalidSpendingMoney() {
		visitor.setSpendingMoney(null);
		validateAndAssertViolationsSizeIsOne(visitor);
		assertInvalidValueAndPropertyNameAndMessageEquals(visitor.getSpendingMoney(), SPENDING_MONEY, NOT_NULL_MESSAGE);

		visitor.setSpendingMoney(49);
		validateAndAssertViolationsSizeIsOne(visitor);
		assertInvalidValueAndPropertyNameAndMessageEquals(visitor.getSpendingMoney(), SPENDING_MONEY,
				rangeMessage(50, Integer.MAX_VALUE));
	}
}
