package hu.beni.amusementpark.constants;

public class ValidationMessageConstants {

	private static final String SIZE_MESSAGE = "size must be between %d and %d";
	private static final String RANGE_MESSAGE = "must be between %d and %d";
	private static final String MUST_BE_ONE_OF = "must be one of %s";

	public static final String NOT_NULL_MESSAGE = "must not be null";
	public static final String NOT_EMPTY_MESSAGE = "must not be empty";
	public static final String PAST_MESSAGE = "must be a past date";
	public static final String EMAIL_MESSAGE = "must be a well-formed email address";

	public static String sizeMessage(long min, long max) {
		return String.format(SIZE_MESSAGE, min, max);
	}

	public static String rangeMessage(long min, long max) {
		return String.format(RANGE_MESSAGE, min, max);
	}

	public static String oneOfMessage(String values) {
		return String.format(MUST_BE_ONE_OF, values);
	}

	private ValidationMessageConstants() {
		super();
	}

}
