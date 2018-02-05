package hu.beni.tester.constant;

public class Constants {
	
	public static final String ADMIN = "admin";
	public static final String USER = "user";
	public static final String PASS = "pass";
	
	public static final String USERNAME = "username";
	public static final String PASSWORD = "password";
	
	public static final String SEMICOLON = ";";
	public static final String COOKIE = "Cookie";
	public static final String SET_COOKIE = "Set-Cookie";
	
	public static final String PAGE = "page";
	public static final String SIZE = "size";
	
	public static final int NUMBER_OF_PARKS_TO_CREATE_PER_ADMIN = 10;
	public static final int NUMBER_OF_MACHINES_TO_CREATE_FOR_EACH_PARK = 10;
	
	public static final int PAGE_SIZE = 10;
	public static final String GUEST_BOOK_REGISTRY_TEXT = "nice park";
	
	public static final String BASE_URL = "http://localhost:8080";
	public static final String LOGIN_URL = BASE_URL + "/login";
	public static final String LOGOUT_URL = BASE_URL + "/logout";
	public static final String AMUSEMENT_PARK_URL = BASE_URL + "/amusementPark";
	public static final String AMUSEMENT_PARK_DELETE_URL = AMUSEMENT_PARK_URL + "/{amusementParkId}";
	public static final String MACHINE_URL = AMUSEMENT_PARK_URL + "/{amusementParkId}/machine";
	public static final String VISITOR_URL = BASE_URL + "/visitor";
	public static final String ENTER_PARK_URL = AMUSEMENT_PARK_URL + "/{amusementParkId}/visitor/{visitorId}/enterPark";
	public static final String PAGED_AMUSEMENT_PARK_URL = AMUSEMENT_PARK_URL + "/paged";
	public static final String GET_ON_MACHINE_URL = MACHINE_URL + "/{machineId}/visitor/{visitorId}/getOnMachine";
	public static final String GET_OFF_MACHINE_URL = MACHINE_URL + "/{machineId}/visitor/{visitorId}/getOffMachine";
	public static final String ADD_GUEST_BOOK_REGISTRY = AMUSEMENT_PARK_URL + "/{amusementParkId}/visitor/{visitorId}/guestBookRegistry";
	public static final String LEAVE_PARK_URL = AMUSEMENT_PARK_URL + "/{amusementParkId}/visitor/{visitorId}/leavePark";
	public static final String VISITOR_DELETE_URL = VISITOR_URL + "/{visitorId}";
	
}
