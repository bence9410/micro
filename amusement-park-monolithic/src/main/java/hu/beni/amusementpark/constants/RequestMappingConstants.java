package hu.beni.amusementpark.constants;

public class RequestMappingConstants {

	public static final String SLASH = "/";
	public static final String WEBJARS = "/webjars/**";
	public static final String INDEX_JS = "/index.js";
	public static final String LINKS = "/links";
	public static final String ME = "/me";
	public static final String SIGN_UP = "/signUp";
	public static final String UPLOAD_MONEY = "/uploadMoney";

	public static final String AMUSEMENT_PARKS = "/amusement-parks";
	public static final String AN_AMUSEMENT_PARK = "/{amusementParkId}";

	public static final String IN_AN_AMUSEMENT_PARK_MACHINES = AMUSEMENT_PARKS + AN_AMUSEMENT_PARK + "/machines";
	public static final String MACHINE_ID = "/{machineId}";

	public static final String VISITORS = "/visitors";
	public static final String A_VISITOR = VISITORS + "/{visitorId}";

	private static final String IN_A_PARK_A_VISITOR = AMUSEMENT_PARKS + AN_AMUSEMENT_PARK + VISITORS;

	public static final String IN_A_PARK_A_VISITOR_ENTER_PARK = IN_A_PARK_A_VISITOR + "/enter-park";
	public static final String IN_A_PARK_A_VISITOR_LEAVE_PARK = IN_A_PARK_A_VISITOR + "/leave-park";

	private static final String IN_A_PARK_ON_A_MACHINE_A_VISITOR = IN_AN_AMUSEMENT_PARK_MACHINES + MACHINE_ID
			+ VISITORS;

	public static final String IN_A_PARK_ON_A_MACHINE_A_VISITOR_GET_ON = IN_A_PARK_ON_A_MACHINE_A_VISITOR
			+ "/get-on-machine";
	public static final String IN_A_PARK_ON_A_MACHINE_A_VISITOR_GET_OFF = IN_A_PARK_ON_A_MACHINE_A_VISITOR
			+ "/get-off-machine";

	public static final String IN_A_PARK_A_VISITOR_GUEST_BOOK_REGISTRIES = IN_A_PARK_A_VISITOR
			+ "/guest-book-registries";
	public static final String A_GUEST_BOOK_REGISTRY = "guest-book-registries/{guestBookRegistryId}";

	private RequestMappingConstants() {
		super();
	}

}
