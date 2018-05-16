package hu.beni.amusementpark.constants;

import java.util.stream.IntStream;

public class StringParamConstants {

	public static final String OPINION_ON_THE_PARK = "Amazing";

	public static final String STRING_EMPTY = "";
	public static final String STRING_WITH_2_LENGTH = "qw";
	public static final String STRING_WITH_4_LENGTH = STRING_WITH_2_LENGTH + "er";
	public static final String STRING_WITH_6_LENGTH = STRING_WITH_4_LENGTH + "tz";
	public static final String STRING_WITH_11_LENGTH = STRING_WITH_6_LENGTH + "uiopa";
	public static final String STRING_WITH_16_LENGTH = STRING_WITH_11_LENGTH + "sdfgh";
	public static final String STRING_WITH_21_LENGTH = STRING_WITH_16_LENGTH + "jklyx";
	public static final String STRING_WITH_26_LENGTH = STRING_WITH_21_LENGTH + "cvbnm";
	public static final String STRING_WITH_101_LENGTH;

	static {
		StringBuilder sb = new StringBuilder(STRING_WITH_26_LENGTH);
		IntStream.range(0, 3).forEach(i -> sb.append(STRING_WITH_21_LENGTH));
		sb.append(STRING_WITH_6_LENGTH);
		sb.append(STRING_WITH_4_LENGTH);
		sb.append(STRING_WITH_2_LENGTH);
		STRING_WITH_101_LENGTH = sb.toString();
	}

}
