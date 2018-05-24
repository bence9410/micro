package hu.beni.amusementpark.validator;

import static hu.beni.amusementpark.constants.FieldNameConstants.DATE_OF_BIRTH;
import static hu.beni.amusementpark.constants.FieldNameConstants.NAME;
import static hu.beni.amusementpark.constants.FieldNameConstants.SPENDING_MONEY;
import static hu.beni.amusementpark.constants.FieldNameConstants.STATE;
import static hu.beni.amusementpark.constants.ValidationMessageConstants.oneOfMessage;
import static hu.beni.amusementpark.validator.ValidatorUtil.validateForNotNullAndPast;
import static hu.beni.amusementpark.validator.ValidatorUtil.validateForNotNullAndRange;
import static hu.beni.amusementpark.validator.ValidatorUtil.validateForNotNullAndSize;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.validation.Errors;

import hu.beni.amusementpark.enums.VisitorState;
import hu.beni.clientsupport.resource.VisitorResource;

public class VisitorResourceValidator extends AbstractValidator<VisitorResource> {

	private static final Set<String> visitorStates = Stream.of(VisitorState.values()).map(VisitorState::toString)
			.collect(Collectors.toSet());

	public VisitorResourceValidator() {
		super(VisitorResource.class);
	}

	@Override
	public void validate(Object target, Errors errors) {
		VisitorResource visitorResource = cast(target);

		validateForNotNullAndSize(visitorResource.getName(), NAME, 5, 25, errors);

		validateForNotNullAndPast(visitorResource.getDateOfBirth(), DATE_OF_BIRTH, errors);

		validateForNotNullAndRange(visitorResource.getSpendingMoney(), SPENDING_MONEY, 50, Integer.MAX_VALUE, errors);

		validateVisitorState(visitorResource.getState(), errors);
	}

	private void validateVisitorState(String state, Errors errors) {
		if (state != null && !visitorStates.contains(state)) {
			errors.rejectValue(STATE, null, oneOfMessage(visitorStates.toString()));
		}
	}

}
