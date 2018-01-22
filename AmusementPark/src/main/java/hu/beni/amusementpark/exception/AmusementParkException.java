package hu.beni.amusementpark.exception;

public class AmusementParkException extends RuntimeException {

    private static final long serialVersionUID = 9136108464735214111L;

	public AmusementParkException(String message) {
        super(message);
    }

    public AmusementParkException(String message, Throwable cause) {
        super(message, cause);
    }
}
