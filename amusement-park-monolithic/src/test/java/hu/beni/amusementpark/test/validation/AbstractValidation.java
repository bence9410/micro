package hu.beni.amusementpark.test.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

public abstract class AbstractValidation<T> {

	private final Validator validator;
	private Object invalidValue;
	private String propertyName;
	private String message;

	protected AbstractValidation() {
		validator = Validation.buildDefaultValidatorFactory().getValidator();
	}

	protected void validateAndAssertNoViolations(T t) {
		assertTrue(validator.validate(t).isEmpty());
	}

	protected void validateAndAssertViolationsSizeIsOne(T t) {
		Set<ConstraintViolation<T>> violations = validator.validate(t);
		assertTrue(violations.size() == 1);
		ConstraintViolation<T> violation = violations.iterator().next();
		invalidValue = violation.getInvalidValue();
		message = violation.getMessage();
		propertyName = violation.getPropertyPath().toString();
	}

	protected void assertInvalidValueAndPropertyNameAndMessageEquals(Object invalidValue, String propertyName,
			String message) {
		assertEquals(invalidValue, this.invalidValue);
		assertEquals(propertyName, this.propertyName);
		assertEquals(message, this.message);
	}

}
