package hu.beni.amusementpark.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;

@Slf4j
@RestControllerAdvice
@ResponseStatus(HttpStatus.I_AM_A_TEAPOT)
public class AmusementParkRestAdvice {

    private static final String ERROR = "Error: ";

    @ExceptionHandler(Throwable.class)
    public String handleException(Throwable throwable) {
        log.error(ERROR, throwable);
        return "Unexpected error occured!";
    }

    @ExceptionHandler(AmusementParkException.class)
    public String handleAmusementParkException(AmusementParkException amusementParkException) {
        log.error(ERROR, amusementParkException);
        return amusementParkException.getMessage();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String handleMethodArgumentNotValidException(MethodArgumentNotValidException methodArgumentNotValidException) {
        log.error(ERROR, methodArgumentNotValidException);
        return methodArgumentNotValidException.getBindingResult().getFieldErrors().stream()
                .map(fe -> "Validation error: " + fe.getField() + " " + fe.getDefaultMessage() + ".").reduce(String::concat).get();
    }
}
