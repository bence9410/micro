package hu.beni.amusementpark.constants;

public class ErrorMessageConstants {

	private static final String VALIDATION_ERROR_FIELD_MESSAGE = "Validation error: %s %s.";

	public static final String ERROR = "Error:";
	public static final String UNEXPECTED_ERROR_OCCURED = "Unexpected error occured!";
	public static final String COULD_NOT_GET_VALIDATION_MESSAGE = "Validation error occurred, but could not get error message.";
	public static final String COULD_NOT_FIND_USER = "Could not find user with username: %s.";

	public static final String NO_AMUSEMENT_PARK_WITH_ID = "No amusement park with the given id!";
	public static final String NO_MACHINE_IN_PARK_WITH_ID = "No machine in the park with the given id!";
	public static final String NO_VISITOR_IN_PARK_WITH_ID = "No visitor in the park with the given id!";
	public static final String NO_VISITOR_ON_MACHINE_WITH_ID = "No visitor on machine with the given id!";
	public static final String NO_GUEST_BOOK_REGISTRY_WITH_ID = "No guest book registry with the given id!";
	public static final String MACHINE_IS_TOO_EXPENSIVE = "Machine is too expensive!";
	public static final String MACHINE_IS_TOO_BIG = "Machine is too big!";
	public static final String VISITORS_ON_MACHINE = "Visitors on machine!";
	public static final String NOT_ENOUGH_MONEY = "Not enough money!";
	public static final String VISITOR_IS_ON_A_MACHINE = "Visitor is on a machine!";
	public static final String VISITOR_IS_TOO_YOUNG = "Visitor is too young!";
	public static final String NO_FREE_SEAT_ON_MACHINE = "No free seat on machine!";
	public static final String VISITOR_NOT_SIGNED_UP = "Visitor not signed up!";
	public static final String VISITOR_IS_IN_A_PARK = "Visitor is in a park!";
	public static final String VISITORS_IN_PARK = "Visitors in the park!";
	public static final String NO_ARCHIVE_SEND_TYPE = "Could not send park to archive. No acrhive send type specified.";

	public static String validationError(String field, String message) {
		return String.format(VALIDATION_ERROR_FIELD_MESSAGE, field, message);
	}

	private ErrorMessageConstants() {
		super();
	}
}
