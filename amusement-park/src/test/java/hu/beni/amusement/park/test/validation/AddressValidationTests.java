package hu.beni.amusement.park.test.validation;


import static hu.beni.amusement.park.constants.FieldNameConstants.*;
import static hu.beni.amusement.park.constants.StringParamConstants.*;
import static hu.beni.amusement.park.constants.ValidationMessageConstants.*;
import static hu.beni.amusement.park.helper.ValidEntityFactory.createAddress;

import org.junit.Before;
import org.junit.Test;

import hu.beni.amusement.park.entity.Address;

public class AddressValidationTests extends AbstractValidation<Address> {

	private Address address;

	@Before
	public void setUp() {
		address = createAddress();
	}

	@Test
	public void validAddress() {
		validateAndAssertNoViolations(address);
	}

	@Test
	public void invalidCountry() {
		address.setCountry(null);
		validateAndAssertViolationsSizeIsOne(address);
		assertInvalidValueAndPropertyNameAndMessageEquals(address.getCountry(), COUNTRY, NOT_NULL_MESSAGE);

		address.setCountry(STRING_WITH_2_LENGTH);
		validateAndAssertViolationsSizeIsOne(address);
		assertInvalidValueAndPropertyNameAndMessageEquals(address.getCountry(), COUNTRY, SIZE_3_15_MESSAGE);

		address.setCountry(STRING_WITH_16_LENGTH);
		validateAndAssertViolationsSizeIsOne(address);
		assertInvalidValueAndPropertyNameAndMessageEquals(address.getCountry(), COUNTRY, SIZE_3_15_MESSAGE);
	}

	@Test
	public void invalidZipCode() {
		address.setZipCode(null);
		validateAndAssertViolationsSizeIsOne(address);
		assertInvalidValueAndPropertyNameAndMessageEquals(address.getZipCode(), ZIP_CODE, NOT_NULL_MESSAGE);

		address.setZipCode(STRING_WITH_2_LENGTH);
		validateAndAssertViolationsSizeIsOne(address);
		assertInvalidValueAndPropertyNameAndMessageEquals(address.getZipCode(), ZIP_CODE, SIZE_3_10_MESSAGE);

		address.setZipCode(STRING_WITH_11_LENGTH);
		validateAndAssertViolationsSizeIsOne(address);
		assertInvalidValueAndPropertyNameAndMessageEquals(address.getZipCode(), ZIP_CODE, SIZE_3_10_MESSAGE);
	}

	@Test
	public void invalidCity() {
		address.setCity(null);
		validateAndAssertViolationsSizeIsOne(address);
		assertInvalidValueAndPropertyNameAndMessageEquals(address.getCity(), CITY, NOT_NULL_MESSAGE);

		address.setCity(STRING_WITH_2_LENGTH);
		validateAndAssertViolationsSizeIsOne(address);
		assertInvalidValueAndPropertyNameAndMessageEquals(address.getCity(), CITY, SIZE_3_15_MESSAGE);

		address.setCity(STRING_WITH_16_LENGTH);
		validateAndAssertViolationsSizeIsOne(address);
		assertInvalidValueAndPropertyNameAndMessageEquals(address.getCity(), CITY, SIZE_3_15_MESSAGE);
	}

	@Test
	public void invalidStreet() {
		address.setStreet(null);
		validateAndAssertViolationsSizeIsOne(address);
		assertInvalidValueAndPropertyNameAndMessageEquals(address.getStreet(), STREET, NOT_NULL_MESSAGE);

		address.setStreet(STRING_WITH_4_LENGTH);
		validateAndAssertViolationsSizeIsOne(address);
		assertInvalidValueAndPropertyNameAndMessageEquals(address.getStreet(), STREET, SIZE_5_25_MESSAGE);

		address.setStreet(STRING_WITH_26_LENGTH);
		validateAndAssertViolationsSizeIsOne(address);
		assertInvalidValueAndPropertyNameAndMessageEquals(address.getStreet(), STREET, SIZE_5_25_MESSAGE);
	}

	@Test
	public void invalidHouseNumber() {
		address.setHouseNumber(null);
		validateAndAssertViolationsSizeIsOne(address);
		assertInvalidValueAndPropertyNameAndMessageEquals(address.getHouseNumber(), HOUSE_NUMBER, NOT_EMPTY_MESSAGE);

		address.setHouseNumber(STRING_EMPTY);
		validateAndAssertViolationsSizeIsOne(address);
		assertInvalidValueAndPropertyNameAndMessageEquals(address.getHouseNumber(), HOUSE_NUMBER, NOT_EMPTY_MESSAGE);
		
		address.setHouseNumber(STRING_WITH_6_LENGTH);
		validateAndAssertViolationsSizeIsOne(address);
		assertInvalidValueAndPropertyNameAndMessageEquals(address.getHouseNumber(), HOUSE_NUMBER, SIZE_0_5_MESSAGE);
	}

}
