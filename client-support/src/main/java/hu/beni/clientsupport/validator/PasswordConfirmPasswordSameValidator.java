package hu.beni.clientsupport.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import hu.beni.clientsupport.constraint.PasswordConfirmPasswordSameConstraint;
import hu.beni.clientsupport.resource.VisitorResource;

public class PasswordConfirmPasswordSameValidator
		implements ConstraintValidator<PasswordConfirmPasswordSameConstraint, VisitorResource> {

	@Override
	public boolean isValid(VisitorResource value, ConstraintValidatorContext context) {
		return value.getPassword().equals(value.getConfirmPassword());
	}

}
