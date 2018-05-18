package hu.beni.amusementpark.validator;

import org.springframework.validation.Errors;

import hu.beni.amusementpark.enums.MachineType;
import hu.beni.clientsupport.resource.MachineResource;

import static hu.beni.amusementpark.constants.FieldNameConstants.*;
import static hu.beni.amusementpark.validator.ValidatorUtil.*;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static hu.beni.amusementpark.constants.ValidationMessageConstants.NOT_NULL_MESSAGE;
import static hu.beni.amusementpark.constants.ValidationMessageConstants.MUST_BE_ONE_OF;

public class MachineResourceValidator extends AbstractValidator<MachineResource> {

	private static final Set<String> machineTypes = Stream.of(MachineType.values()).map(value -> value.toString())
			.collect(Collectors.toSet());

	public MachineResourceValidator() {
		super(MachineResource.class);
	}

	@Override
	public void validate(Object target, Errors errors) {
		MachineResource machineResource = cast(target);

		validateForNotNullAndSize(machineResource.getFantasyName(), FANTASY_NAME, 5, 25, errors);

		validateForNotNullAndRange(machineResource.getSize(), SIZE, 20, 200, errors);

		validateForNotNullAndRange(machineResource.getPrice(), PRICE, 50, 2000, errors);

		validateForNotNullAndRange(machineResource.getNumberOfSeats(), NUMBER_OF_SEATS, 5, 30, errors);

		validateForNotNullAndRange(machineResource.getMinimumRequiredAge(), MINIMUM_REQUIRED_AGE, 0, 21, errors);

		validateForNotNullAndRange(machineResource.getTicketPrice(), TICKET_PRICE, 5, 30, errors);

		String type = machineResource.getType();

		if (type == null) {
			errors.rejectValue(TYPE, null, NOT_NULL_MESSAGE);
		} else if (!machineTypes.contains(type)) {
			errors.rejectValue(TYPE, null, String.format(MUST_BE_ONE_OF, machineTypes));
		}
	}
}
