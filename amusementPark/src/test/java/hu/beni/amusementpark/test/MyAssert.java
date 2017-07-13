package hu.beni.amusementpark.test;

import static org.junit.Assert.fail;

public class MyAssert {

    public static void assertThrows(Runnable exceptionThrower, Class<? extends Throwable> exceptionType, String exceptionMessage) {
        try {
            exceptionThrower.run();
            fail("No exception was thrown!");
        } catch (Throwable throwable) {
            if (exceptionType.isInstance(throwable)) {
                if (!exceptionMessage.equals(throwable.getMessage())) {
                    fail("The expected exception was thrown but with different message! " + throwable.getMessage());
                }
            } else {
                fail("Other exception was thrown! " + throwable.getClass().getName());
            }
        }
    }
}
