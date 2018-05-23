package hu.beni.amusementpark.validator;

import static hu.beni.amusementpark.constants.FieldNameConstants.ADDRESS;
import static hu.beni.amusementpark.constants.FieldNameConstants.CAPITAL;
import static hu.beni.amusementpark.constants.FieldNameConstants.CITY_IN_ADDRESS;
import static hu.beni.amusementpark.constants.FieldNameConstants.COUNTRY_IN_ADDRESS;
import static hu.beni.amusementpark.constants.FieldNameConstants.ENTRANCE_FEE;
import static hu.beni.amusementpark.constants.FieldNameConstants.HOUSE_NUMBER_IN_ADDRESS;
import static hu.beni.amusementpark.constants.FieldNameConstants.NAME;
import static hu.beni.amusementpark.constants.FieldNameConstants.STREET_IN_ADDRESS;
import static hu.beni.amusementpark.constants.FieldNameConstants.TOTAL_AREA;
import static hu.beni.amusementpark.constants.FieldNameConstants.ZIP_CODE_IN_ADDRESS;
import static hu.beni.amusementpark.constants.ValidationMessageConstants.NOT_NULL_MESSAGE;
import static hu.beni.amusementpark.validator.ValidatorUtil.validateForNotEmptyAndSize;
import static hu.beni.amusementpark.validator.ValidatorUtil.validateForNotNullAndRange;
import static hu.beni.amusementpark.validator.ValidatorUtil.validateForNotNullAndSize;

import org.springframework.validation.Errors;

import hu.beni.clientsupport.dto.AddressDTO;
import hu.beni.clientsupport.resource.AmusementParkResource;

public class AmusementParkResourceValidator extends AbstractValidator<AmusementParkResource> {

	public AmusementParkResourceValidator() {
		super(AmusementParkResource.class);
	}

	@Override
	public void validate(Object target, Errors errors) {
		AmusementParkResource amusementParkResource = cast(target);

		validateForNotNullAndSize(amusementParkResource.getName(), NAME, 5, 20, errors);

		validateForNotNullAndRange(amusementParkResource.getCapital(), CAPITAL, 500, 50000, errors);

		validateForNotNullAndRange(amusementParkResource.getTotalArea(), TOTAL_AREA, 50, 2000, errors);

		validateForNotNullAndRange(amusementParkResource.getEntranceFee(), ENTRANCE_FEE, 5, 200, errors);

		validateAddressDTO(amusementParkResource.getAddress(), errors);
	}

	private void validateAddressDTO(AddressDTO addressDTO, Errors errors) {
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
