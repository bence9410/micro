package hu.beni.amusementpark.helper;

import static org.junit.Assert.fail;

public class MyAssert {

    public static <T extends Throwable> void assertThrows(Runnable exceptionThrower,
    		Class<T> exceptionType, ExceptionAsserter<T> asserter) {
        try {
            exceptionThrower.run();
            fail("No exception was thrown!");
        }catch(AssertionError assertionError){
            throw assertionError;
        } catch (Throwable throwable) {
            if (exceptionType.isInstance(throwable)) {
            	asserter.assertExceptiont(exceptionType.cast(throwable));
            } else {
                fail("Other exception was thrown! " + throwable.getClass().getName());
            }
        }
    }
    
    @FunctionalInterface
    public static interface ExceptionAsserter<T extends Throwable>{
    	
    	public void assertExceptiont(T t);
    	
    }
}