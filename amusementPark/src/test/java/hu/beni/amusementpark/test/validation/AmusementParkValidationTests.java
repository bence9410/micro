package hu.beni.amusementpark.test.validation;

import org.junit.Before;
import org.junit.Test;

import hu.beni.amusementpark.entity.Address;
import hu.beni.amusementpark.entity.AmusementPark;

import static hu.beni.amusementpark.constants.StringParamConstants.*;
import static hu.beni.amusementpark.constants.ValidationMessageConstants.*;
import static hu.beni.amusementpark.constants.FieldNameConstants.*;
import static hu.beni.amusementpark.test.ValidEntityFactory.*;

public class AmusementParkValidationTests extends AbstractValidation<AmusementPark>{

	private AmusementPark amusementPark;
	
	@Before
	public void setUp() {
		amusementPark = createAmusementParkWithAddress();
	}
	
	@Test
	public void validAmusementPark() {
		validateAndAssertNoViolations(amusementPark);
	}
	
	@Test
	public void invalidName() {
		amusementPark.setName(null);
		validateAndAssertViolationsSizeIsOne(amusementPark);
		assertInvalidValueAndPropertyNameAndMessageEquals(amusementPark.getName(), NAME, NOT_NULL_MESSAGE);
		
		amusementPark.setName(STRING_WITH_4_LENGTH);
		validateAndAssertViolationsSizeIsOne(amusementPark);
		assertInvalidValueAndPropertyNameAndMessageEquals(amusementPark.getName(), NAME, SIZE_5_20_MESSAGE);
		
		amusementPark.setName(STRING_WITH_21_LENGTH);
		validateAndAssertViolationsSizeIsOne(amusementPark);
		assertInvalidValueAndPropertyNameAndMessageEquals(amusementPark.getName(), NAME, SIZE_5_20_MESSAGE);
	}
	
	@Test
	public void invalidCapital() {
		amusementPark.setCapital(null);
		validateAndAssertViolationsSizeIsOne(amusementPark);
		assertInvalidValueAndPropertyNameAndMessageEquals(amusementPark.getCapital(), CAPITAL, NOT_NULL_MESSAGE);
		
		amusementPark.setCapital(99);
		validateAndAssertViolationsSizeIsOne(amusementPark);
		assertInvalidValueAndPropertyNameAndMessageEquals(amusementPark.getCapital(), CAPITAL, RANGE_500_50000_MESSAGE);
		
		amusementPark.setCapital(50001);
		validateAndAssertViolationsSizeIsOne(amusementPark);
		assertInvalidValueAndPropertyNameAndMessageEquals(amusementPark.getCapital(), CAPITAL, RANGE_500_50000_MESSAGE);
	}
	
	@Test
	public void invalidTotalArea() {
		amusementPark.setTotalArea(null);
		validateAndAssertViolationsSizeIsOne(amusementPark);
		assertInvalidValueAndPropertyNameAndMessageEquals(amusementPark.getTotalArea(), TOTAL_AREA, NOT_NULL_MESSAGE);
		
		amusementPark.setTotalArea(49);
		validateAndAssertViolationsSizeIsOne(amusementPark);
		assertInvalidValueAndPropertyNameAndMessageEquals(amusementPark.getTotalArea(), TOTAL_AREA, RANGE_50_2000_MESSAGE);
		
		amusementPark.setTotalArea(2001);
		validateAndAssertViolationsSizeIsOne(amusementPark);
		assertInvalidValueAndPropertyNameAndMessageEquals(amusementPark.getTotalArea(), TOTAL_AREA, RANGE_50_2000_MESSAGE);
	}
	
	@Test
	public void invalidEntranceFee() {
		amusementPark.setEntranceFee(null);
		validateAndAssertViolationsSizeIsOne(amusementPark);
		assertInvalidValueAndPropertyNameAndMessageEquals(amusementPark.getEntranceFee(), ENTRANCE_FEE, NOT_NULL_MESSAGE);
		
		amusementPark.setEntranceFee(4);
		validateAndAssertViolationsSizeIsOne(amusementPark);
		assertInvalidValueAndPropertyNameAndMessageEquals(amusementPark.getEntranceFee(), ENTRANCE_FEE, RANGE_5_200_MESSAGE);
		
		amusementPark.setEntranceFee(201);
		validateAndAssertViolationsSizeIsOne(amusementPark);
		assertInvalidValueAndPropertyNameAndMessageEquals(amusementPark.getEntranceFee(), ENTRANCE_FEE, RANGE_5_200_MESSAGE);
	}
	
	@Test
	public void invalidAddress() {
		Address address = amusementPark.getAddress();
		amusementPark.setAddress(null);
		validateAndAssertViolationsSizeIsOne(amusementPark);
		assertInvalidValueAndPropertyNameAndMessageEquals(amusementPark.getAddress(), ADDRESS, NOT_NULL_MESSAGE);
		
		address.setCountry(null);
		amusementPark.setAddress(address);
		validateAndAssertViolationsSizeIsOne(amusementPark);
		assertInvalidValueAndPropertyNameAndMessageEquals(address.getCountry(), COUNTRY_IN_ADDRESS, NOT_NULL_MESSAGE);
	}
}