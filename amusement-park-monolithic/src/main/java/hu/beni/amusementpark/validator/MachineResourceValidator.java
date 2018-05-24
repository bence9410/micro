package hu.beni.amusementpark.validator;

import static hu.beni.amusementpark.constants.FieldNameConstants.FANTASY_NAME;
import static hu.beni.amusementpark.constants.FieldNameConstants.MINIMUM_REQUIRED_AGE;
import static hu.beni.amusementpark.constants.FieldNameConstants.NUMBER_OF_SEATS;
import static hu.beni.amusementpark.constants.FieldNameConstants.PRICE;
import static hu.beni.amusementpark.constants.FieldNameConstants.SIZE;
import static hu.beni.amusementpark.constants.FieldNameConstants.TICKET_PRICE;
import static hu.beni.amusementpark.constants.FieldNameConstants.TYPE;
import static hu.beni.amusementpark.constants.ValidationMessageConstants.NOT_NULL_MESSAGE;
import static hu.beni.amusementpark.constants.ValidationMessageConstants.oneOfMessage;
import static hu.beni.amusementpark.validator.ValidatorUtil.validateForNotNullAndRange;
import static hu.beni.amusementpark.validator.ValidatorUtil.validateForNotNullAndSize;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.validation.Errors;

import hu.beni.amusementpark.enums.MachineType;
import hu.beni.clientsupport.resource.MachineResource;

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

		validateMachineType(machineResource.getType(), errors);
	}

	private void validateMachineType(String type, Errors errors) {
		if (type == null) {
			errors.rejectValue(TYPE, null, NOT_NULL_MESSAGE);
		} else if (!machineTypes.contains(type)) {
			errors.rejectValue(TYPE, null, oneOfMessage(machineTypes.toString()));
		}
	}
}
