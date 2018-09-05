package hu.beni.amusementpark.test.validation;

import static hu.beni.amusementpark.constants.FieldNameConstants.AUTHORITY;
import static hu.beni.amusementpark.constants.FieldNameConstants.DATE_OF_BIRTH;
import static hu.beni.amusementpark.constants.FieldNameConstants.EMAIL;
import static hu.beni.amusementpark.constants.FieldNameConstants.PASSWO;
import static hu.beni.amusementpark.constants.FieldNameConstants.SPENDING_MONEY;
import static hu.beni.amusementpark.constants.StringParamConstants.STRING_WITH_26_LENGTH;
import static hu.beni.amusementpark.constants.StringParamConstants.STRING_WITH_4_LENGTH;
import static hu.beni.amusementpark.constants.StringParamConstants.STRING_WITH_59_LENGTH;
import static hu.beni.amusementpark.constants.StringParamConstants.STRING_WITH_61_LENGTH;
import static hu.beni.amusementpark.constants.ValidationMessageConstants.EMAIL_MESSAGE;
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
	public void invalidEmail() {
		visitor.setEmail(null);
		validateAndAssertViolationsSizeIsOne(visitor);
		assertInvalidValueAndPropertyNameAndMessageEquals(visitor.getEmail(), EMAIL, NOT_NULL_MESSAGE);

		visitor.setEmail(STRING_WITH_4_LENGTH);
		validateAndAssertViolationsSizeIsOne(visitor);
		assertInvalidValueAndPropertyNameAndMessageEquals(visitor.getEmail(), EMAIL, EMAIL_MESSAGE);
	}

	@Test
	public void invalidPassword() {
		visitor.setPassword(null);
		validateAndAssertViolationsSizeIsOne(visitor);
		assertInvalidValueAndPropertyNameAndMessageEquals(visitor.getPassword(), PASSWO, NOT_NULL_MESSAGE);

		visitor.setPassword(STRING_WITH_59_LENGTH);
		validateAndAssertViolationsSizeIsOne(visitor);
		assertInvalidValueAndPropertyNameAndMessageEquals(visitor.getPassword(), PASSWO, sizeMessage(60, 60));

		visitor.setPassword(STRING_WITH_61_LENGTH);
		validateAndAssertViolationsSizeIsOne(visitor);
		assertInvalidValueAndPropertyNameAndMessageEquals(visitor.getPassword(), PASSWO, sizeMessage(60, 60));
	}

	@Test
	public void invalidAuthority() {
		visitor.setAuthority(null);
		validateAndAssertViolationsSizeIsOne(visitor);
		assertInvalidValueAndPropertyNameAndMessageEquals(visitor.getAuthority(), AUTHORITY, NOT_NULL_MESSAGE);

		visitor.setAuthority(STRING_WITH_4_LENGTH);
		validateAndAssertViolationsSizeIsOne(visitor);
		assertInvalidValueAndPropertyNameAndMessageEquals(visitor.getAuthority(), AUTHORITY, sizeMessage(5, 25));

		visitor.setAuthority(STRING_WITH_26_LENGTH);
		validateAndAssertViolationsSizeIsOne(visitor);
		assertInvalidValueAndPropertyNameAndMessageEquals(visitor.getAuthority(), AUTHORITY, sizeMessage(5, 25));
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
