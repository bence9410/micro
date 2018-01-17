package hu.beni.amusement.park.helper;

import java.time.LocalDate;

import hu.beni.amusement.park.entity.Address;
import hu.beni.amusement.park.entity.AmusementPark;
import hu.beni.amusement.park.entity.Machine;
import hu.beni.amusement.park.entity.Visitor;
import hu.beni.amusement.park.enums.MachineType;

public class ValidEntityFactory {

	public static Address createAddress() {
        return Address.builder()
                .zipCode("1148")
                .city("Budapest")
                .country("Magyarország")
                .street("Fogarasi út")
                .houseNumber("80/C").build();
    }
	
	public static AmusementPark createAmusementPark() {
        return AmusementPark.builder()
                .name("Beni parkja")
                .capital(3000)
                .totalArea(1000)
                .entranceFee(50).build();
    }

    public static Machine createMachine() {
        return Machine.builder()
                .fantasyName("Nagy hajó")
                .size(100)
                .price(250)
                .numberOfSeats(10)
                .minimumRequiredAge(18)
                .ticketPrice(10)
                .type(MachineType.CAROUSEL).build();
    }

    public static Visitor createVisitor() {
    	return Visitor.builder().name("Németh Bence")
        		.dateOfBirth(LocalDate.of(1994, 10, 22)).build();
    }
    
    public static AmusementPark createAmusementParkWithAddress(){
    	Address address = createAddress();
    	AmusementPark amusementPark = createAmusementPark();
    	address.setAmusementPark(amusementPark);
    	amusementPark.setAddress(address);
    	return amusementPark;
    }
    
}
