package hu.beni.tester.factory;

import java.time.LocalDate;

import hu.beni.dto.AddressDTO;
import hu.beni.dto.AmusementParkDTO;
import hu.beni.dto.MachineDTO;
import hu.beni.dto.VisitorDTO;

public class ValidDTOFactory {

	public static final int AMUSEMENT_PARK_CAPITAL = 3000;
	public static final int AMUSEMENT_PARK_ENTRANCE_FEE = 50;
	public static final int MACHINE_PRICE = 250;
	public static final int MACHINE_TICKET_PRICE = 10;
	public static final int VISITOR_SPENDING_MONEY = 1000000;

	public static MachineDTO createMachine() {
		return MachineDTO.builder().fantasyName("Nagy hajó").size(100).price(MACHINE_PRICE).numberOfSeats(30)
				.minimumRequiredAge(18).ticketPrice(MACHINE_TICKET_PRICE).type("CAROUSEL").build();
	}

	public static AmusementParkDTO createAmusementParkWithAddress() {
		AddressDTO addressDTO = createAddress();
		AmusementParkDTO amusementParkDTO = createAmusementPark();
		amusementParkDTO.setAddress(addressDTO);
		return amusementParkDTO;
	}

	public static VisitorDTO createVisitor() {
		return VisitorDTO.builder().name("Németh Bence").dateOfBirth(LocalDate.of(1994, 10, 22))
				.spendingMoney(VISITOR_SPENDING_MONEY).build();
	}

	private static AddressDTO createAddress() {
		return AddressDTO.builder().zipCode("1148").city("Budapest").country("Magyarország").street("Fogarasi út")
				.houseNumber("80/C").build();
	}

	private static AmusementParkDTO createAmusementPark() {
		return AmusementParkDTO.builder().name("Beni parkja").capital(AMUSEMENT_PARK_CAPITAL).totalArea(1000)
				.entranceFee(AMUSEMENT_PARK_ENTRANCE_FEE).build();
	}
}