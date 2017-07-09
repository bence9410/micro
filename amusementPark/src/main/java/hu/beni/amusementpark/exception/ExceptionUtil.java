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

    public static <T> void exceptionIfEqualsWithMessage(T t1, T t2, String message) {
        if (t1.equals(t2)) {
            throw new AmusementParkException(message);
        }
    }

}
