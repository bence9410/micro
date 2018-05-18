package hu.beni.amusementpark.validator;

import org.springframework.hateoas.ResourceSupport;
import org.springframework.validation.Validator;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractValidator<T extends ResourceSupport> implements Validator {

	private final Class<T> targetClass;

	@Override
	public boolean supports(Class<?> clazz) {
		return targetClass.equals(clazz);
	}

	protected T cast(Object target) {
		return targetClass.cast(target);
	}

}
