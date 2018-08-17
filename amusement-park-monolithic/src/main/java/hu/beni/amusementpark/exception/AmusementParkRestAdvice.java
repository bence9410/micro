package hu.beni.amusementpark.exception;

import static hu.beni.amusementpark.constants.ErrorMessageConstants.COULD_NOT_GET_VALIDATION_MESSAGE;
import static hu.beni.amusementpark.constants.ErrorMessageConstants.ERROR;
import static hu.beni.amusementpark.constants.ErrorMessageConstants.UNEXPECTED_ERROR_OCCURED;
import static hu.beni.amusementpark.constants.ErrorMessageConstants.validationError;

import java.util.stream.Stream;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
@ResponseStatus(HttpStatus.I_AM_A_TEAPOT)
public class AmusementParkRestAdvice {

	@ExceptionHandler(Throwable.class)
	public String handleException(Throwable throwable) {
		log.error(ERROR, throwable);
		return UNEXPECTED_ERROR_OCCURED;
	}

	@ExceptionHandler(AmusementParkException.class)
	public String handleAmusementParkException(AmusementParkException amusementParkException) {
		log.error(ERROR, amusementParkException);
		return amusementParkException.getMessage();
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public String handleMethodArgumentNotValidException(
			MethodArgumentNotValidException methodArgumentNotValidException) {
		log.error(ERROR, methodArgumentNotValidException);
		BindingResult bindingResult = methodArgumentNotValidException.getBindingResult();
		return Stream
				.concat(bindingResult.getFieldErrors().stream().map(this::convertToMessage),
						bindingResult.getGlobalErrors().stream().map(this::convertToMessage))
				.reduce(String::concat).orElse(COULD_NOT_GET_VALIDATION_MESSAGE);
	}

	private String convertToMessage(FieldError fieldError) {
		return validationError(fieldError.getField(), fieldError.getDefaultMessage());
	}

	private String convertToMessage(ObjectError objectError) {
		return validationError(objectError.getDefaultMessage());
	}
}
