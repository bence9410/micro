package hu.beni.amusement.park.helper;

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
            	asserter.asserExceptiont(exceptionType.cast(throwable));
            } else {
                fail("Other exception was thrown! " + throwable.getClass().getName());
            }
        }
    }
    
    @FunctionalInterface
    public static interface ExceptionAsserter<T extends Throwable>{
    	
    	public void asserExceptiont(T t);
    	
    }
}