package hu.beni.amusementpark.test.validation;

import static hu.beni.amusementpark.test.ValidEntityFactory.createAddress;

import org.junit.Before;
import org.junit.Test;

import hu.beni.amusementpark.entity.Address;

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
		String country = null;
		address.setCountry(country);
		
		validateAndAssertViolationsSizeIsOne(address);
		assertInvalidValueAndPropertyNameAndMessageEquals(country, "country", "may not be null");
		
		country = "as";
		address.setCountry(country);
		validateAndAssertViolationsSizeIsOne(address);
		assertInvalidValueAndPropertyNameAndMessageEquals(country, "country", "size must be between 3 and 15");
		
		country = "asdfghjklxcvbnmq";
		address.setCountry(country);
		validateAndAssertViolationsSizeIsOne(address);
		assertInvalidValueAndPropertyNameAndMessageEquals(country, "country", "size must be between 3 and 15");
	}
	
	@Test
	public void invalidZipCode() {
		String zipCode = null;
		address.setZipCode(zipCode);
		
		validateAndAssertViolationsSizeIsOne(address);
		assertInvalidValueAndPropertyNameAndMessageEquals(zipCode, "zipCode", "may not be null");
		
		zipCode = "as";
		address.setZipCode(zipCode);
		validateAndAssertViolationsSizeIsOne(address);
		assertInvalidValueAndPropertyNameAndMessageEquals(zipCode, "zipCode", "size must be between 3 and 10");
		
		zipCode = "asdfghjklxc";
		address.setZipCode(zipCode);
		validateAndAssertViolationsSizeIsOne(address);
		assertInvalidValueAndPropertyNameAndMessageEquals(zipCode, "zipCode", "size must be between 3 and 10");
	}
	
	@Test
	public void invalidCity() {
		String city = null;
		address.setCity(city);
		
		validateAndAssertViolationsSizeIsOne(address);
		assertInvalidValueAndPropertyNameAndMessageEquals(city, "city", "may not be null");
		
		city = "as";
		address.setCity(city);
		validateAndAssertViolationsSizeIsOne(address);
		assertInvalidValueAndPropertyNameAndMessageEquals(city, "city", "size must be between 3 and 15");
		
		city = "asdfghjklxcvbnmq";
		address.setCity(city);
		validateAndAssertViolationsSizeIsOne(address);
		assertInvalidValueAndPropertyNameAndMessageEquals(city, "city", "size must be between 3 and 15");
	}
	
	@Test
	public void invalidStreet() {
		String street = null;
		address.setStreet(street);
		
		validateAndAssertViolationsSizeIsOne(address);
		assertInvalidValueAndPropertyNameAndMessageEquals(street, "street", "may not be null");
		
		street = "as";
		address.setStreet(street);
		validateAndAssertViolationsSizeIsOne(address);
		assertInvalidValueAndPropertyNameAndMessageEquals(street, "street", "size must be between 3 and 25");
		
		street = "asdfghjklxcvbnmqwertzuiopa";
		address.setStreet(street);
		validateAndAssertViolationsSizeIsOne(address);
		assertInvalidValueAndPropertyNameAndMessageEquals(street, "street", "size must be between 3 and 25");
	}
	

	@Test
	public void invalidHouseNumber() {
		String houseNumber = null;
		address.setHouseNumber(houseNumber);
		
		validateAndAssertViolationsSizeIsOne(address);
		assertInvalidValueAndPropertyNameAndMessageEquals(houseNumber, "houseNumber", "may not be empty");
		
		houseNumber = "asdfgh";
		address.setHouseNumber(houseNumber);
		validateAndAssertViolationsSizeIsOne(address);
		assertInvalidValueAndPropertyNameAndMessageEquals(houseNumber, "houseNumber", "size must be between 0 and 5");
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
