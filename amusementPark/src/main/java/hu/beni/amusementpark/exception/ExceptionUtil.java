package hu.beni.amusementpark.exception;

public class ExceptionUtil {

    public static void exceptionIfFirstLessThanSecondWithMessage(Integer first, Integer second, String message) {
        if (first < second) {
            throw new AmusementParkException(message);
        }
    }

    public static void exceptionIfNotZeroWithMessage(Long value, String message) {
        if (value != 0) {
            throw new AmusementParkException(message);
        }
    }

}
