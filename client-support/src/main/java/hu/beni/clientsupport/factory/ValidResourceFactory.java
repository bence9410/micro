package hu.beni.clientsupport.factory;

import java.time.LocalDate;

import hu.beni.clientsupport.resource.AmusementParkResource;
import hu.beni.clientsupport.resource.MachineResource;
import hu.beni.clientsupport.resource.VisitorResource;

public class ValidResourceFactory {

	public static MachineResource createMachine() {
		return MachineResource.builder() //@formatter:off
				.fantasyName("Nagy hajó")
				.size(100)
				.price(250)
				.numberOfSeats(30)
				.minimumRequiredAge(18)
				.ticketPrice(10)
				.type("CAROUSEL").build(); //@formatter:on
	}

	public static VisitorResource createVisitor() {
		return VisitorResource.builder() //@formatter:off
				.email("nembence1994@gmail.com")
    			.password("password")
    			.confirmPassword("password")
    			.dateOfBirth(LocalDate.of(1994, 10, 22)).build(); //@formatter:on
	}

	public static AmusementParkResource createAmusementPark() {
		return AmusementParkResource.builder() //@formatter:off
				.name("Beni parkja")
				.capital(3000)
				.totalArea(1000)
				.entranceFee(50).build(); //@formatter:on
	}

	private ValidResourceFactory() {
		super();
	}
}