package hu.beni.amusement.park.test.validation;

import static hu.beni.amusement.park.constants.FieldNameConstants.*;
import static hu.beni.amusement.park.constants.StringParamConstants.*;
import static hu.beni.amusement.park.constants.ValidationMessageConstants.*;

import org.junit.Before;
import org.junit.Test;

import hu.beni.amusement.park.entity.AmusementPark;
import hu.beni.amusement.park.entity.GuestBookRegistry;
import hu.beni.amusement.park.entity.Visitor;

public class GuestBookRegistryValidationTests extends AbstractValidation<GuestBookRegistry>{
	
	private GuestBookRegistry guestBookRegistry;

	@Before
	public void setUp() {
		guestBookRegistry = GuestBookRegistry.builder().textOfRegistry(OPINION_ON_THE_PARK)
				.visitor(Visitor.builder().id(1000L).build())
				.amusementPark(AmusementPark.builder().id(1001L).build()).build();
	}
	
	@Test
	public void validAmusementPark() {
		validateAndAssertNoViolations(guestBookRegistry);
	}
	
	@Test
	public void invalidTextOfRegistry() {
		guestBookRegistry.setTextOfRegistry(null);
		validateAndAssertViolationsSizeIsOne(guestBookRegistry);
		assertInvalidValueAndPropertyNameAndMessageEquals(guestBookRegistry.getTextOfRegistry(), TEXT_OF_REGISTRY, NOT_NULL_MESSAGE);
		
		guestBookRegistry.setTextOfRegistry(STRING_WITH_4_LENGTH);
		validateAndAssertViolationsSizeIsOne(guestBookRegistry);
		assertInvalidValueAndPropertyNameAndMessageEquals(guestBookRegistry.getTextOfRegistry(), TEXT_OF_REGISTRY, SIZE_5_100_MESSAGE);
		
		guestBookRegistry.setTextOfRegistry(STRING_WITH_101_LENGTH);
		validateAndAssertViolationsSizeIsOne(guestBookRegistry);
		assertInvalidValueAndPropertyNameAndMessageEquals(guestBookRegistry.getTextOfRegistry(), TEXT_OF_REGISTRY, SIZE_5_100_MESSAGE);
	}
	
	@Test
	public void nullAmusementPark() {
		guestBookRegistry.setAmusementPark(null);
		validateAndAssertViolationsSizeIsOne(guestBookRegistry);
		assertInvalidValueAndPropertyNameAndMessageEquals(guestBookRegistry.getAmusementPark(), AMUSEMENT_PARK, NOT_NULL_MESSAGE);
	}
	
	@Test
	public void nullVisitor() {
		guestBookRegistry.setVisitor(null);
		validateAndAssertViolationsSizeIsOne(guestBookRegistry);
		assertInvalidValueAndPropertyNameAndMessageEquals(guestBookRegistry.getVisitor(), VISITOR, NOT_NULL_MESSAGE);
	}	
}
