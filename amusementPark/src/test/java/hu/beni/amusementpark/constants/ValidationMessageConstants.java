package hu.beni.amusementpark.constants;

public class ValidationMessageConstants {
	
	private static final String SIZE_MESSAGE = "size must be between %d and %d";
	private static final String RANGE_MESSAGE = "must be between %d and %d";
	
	public static final String NOT_NULL_MESSAGE = "may not be null";
	
	public static final String NOT_EMPTY_MESSAGE = "may not be empty";
	
	public static final String SIZE_0_5_MESSAGE = String.format(SIZE_MESSAGE, 0, 5);
	public static final String SIZE_3_10_MESSAGE = String.format(SIZE_MESSAGE, 3, 10);
	public static final String SIZE_3_15_MESSAGE = String.format(SIZE_MESSAGE, 3, 15);
	public static final String SIZE_5_20_MESSAGE = String.format(SIZE_MESSAGE, 5, 20);
	public static final String SIZE_5_25_MESSAGE = String.format(SIZE_MESSAGE, 5, 25);
	public static final String SIZE_5_100_MESSAGE = String.format(SIZE_MESSAGE, 5, 100);
	
	public static final String RANGE_0_21_MESSAGE = String.format(RANGE_MESSAGE, 0, 21);
	public static final String RANGE_5_30_MESSAGE = String.format(RANGE_MESSAGE, 5, 30);
	public static final String RANGE_5_200_MESSAGE = String.format(RANGE_MESSAGE, 5, 200);
	public static final String RANGE_20_200_MESSAGE = String.format(RANGE_MESSAGE, 20, 200);
	public static final String RANGE_50_2000_MESSAGE = String.format(RANGE_MESSAGE, 50, 2000);
	public static final String RANGE_500_50000_MESSAGE = String.format(RANGE_MESSAGE, 500, 50000);

}
