package hu.beni.amusementpark.validator;

import static hu.beni.amusementpark.constants.ValidationMessageConstants.NOT_EMPTY_MESSAGE;
import static hu.beni.amusementpark.constants.ValidationMessageConstants.NOT_NULL_MESSAGE;
import static hu.beni.amusementpark.constants.ValidationMessageConstants.PAST_MESSAGE;
import static hu.beni.amusementpark.constants.ValidationMessageConstants.RANGE_MESSAGE;
import static hu.beni.amusementpark.constants.ValidationMessageConstants.SIZE_MESSAGE;

import java.time.LocalDate;

import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;

public class ValidatorUtil {

	private ValidatorUtil() {
		super();
	}

	public static void validateForNotNullAndSize(String value, String field, int min, int max, Errors errors) {
		if (value == null) {
			errors.rejectValue(field, null, NOT_NULL_MESSAGE);
		} else {
			int length = value.length();
			if (length < min || length > max) {
				errors.rejectValue(field, null, String.format(SIZE_MESSAGE, min, max));
			}
		}
	}

	public static void validateForNotEmptyAndSize(String value, String field, int max, Errors errors) {
		if (StringUtils.isEmpty(value)) {
			errors.rejectValue(field, null, NOT_EMPTY_MESSAGE);
		} else {
			if (value.length() > max) {
				errors.rejectValue(field, null, String.format(SIZE_MESSAGE, 0, max));
			}
		}
	}

	public static void validateForNotNullAndRange(Integer value, String field, int min, int max, Errors errors) {
		if (value == null) {
			errors.rejectValue(field, null, NOT_NULL_MESSAGE);
		} else {
			if (value < min || value > max) {
				errors.rejectValue(field, null, String.format(RANGE_MESSAGE, min, max));
			}
		}
	}

	public static void validateForNotNullAndPast(LocalDate value, String field, Errors errors) {
		if (value == null) {
			errors.rejectValue(field, null, NOT_NULL_MESSAGE);
		} else {
			if (value.isBefore(LocalDate.now())) {
				errors.rejectValue(field, null, PAST_MESSAGE);
			}
		}
	}

}
