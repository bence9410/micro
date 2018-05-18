package hu.beni.amusementpark.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import hu.beni.clientsupport.dto.AddressDTO;
import hu.beni.clientsupport.resource.AmusementParkResource;

import static hu.beni.amusementpark.constants.FieldNameConstants.*;
import static hu.beni.amusementpark.validator.ValidatorUtil.*;
import static hu.beni.amusementpark.constants.ValidationMessageConstants.NOT_NULL_MESSAGE;

public class AmusementParkResourceValidator implements Validator {

	private static final Class<AmusementParkResource> TARGET_CLASS = AmusementParkResource.class;

	@Override
	public boolean supports(Class<?> clazz) {
		return TARGET_CLASS.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		AmusementParkResource amusementParkResource = TARGET_CLASS.cast(target);

		validateForNotNullAndSize(amusementParkResource.getName(), NAME, 5, 20, errors);

		validateForNotNullAndRange(amusementParkResource.getCapital(), CAPITAL, 500, 50000, errors);

		validateForNotNullAndRange(amusementParkResource.getTotalArea(), TOTAL_AREA, 50, 2000, errors);

		validateForNotNullAndRange(amusementParkResource.getEntranceFee(), ENTRANCE_FEE, 5, 200, errors);

		AddressDTO addressDTO = amusementParkResource.getAddress();

		if (addressDTO == null) {
			errors.rejectValue(ADDRESS, null, NOT_NULL_MESSAGE);
		} else {
			validateForNotNullAndSize(addressDTO.getCountry(), COUNTRY_IN_ADDRESS, 3, 15, errors);

			validateForNotNullAndSize(addressDTO.getZipCode(), ZIP_CODE_IN_ADDRESS, 3, 15, errors);

			validateForNotNullAndSize(addressDTO.getCity(), CITY_IN_ADDRESS, 3, 15, errors);

			validateForNotNullAndSize(addressDTO.getStreet(), STREET_IN_ADDRESS, 5, 25, errors);

			validateForNotEmptyAndSize(addressDTO.getHouseNumber(), HOUSE_NUMBER_IN_ADDRESS, 5, errors);
		}
	}
}
