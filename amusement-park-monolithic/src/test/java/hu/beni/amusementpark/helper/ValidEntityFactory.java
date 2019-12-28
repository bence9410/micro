package hu.beni.amusementpark.helper;

import java.time.LocalDate;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.entity.Machine;
import hu.beni.amusementpark.entity.Visitor;
import hu.beni.amusementpark.enums.MachineType;

public class ValidEntityFactory {

	private static final BCryptPasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

	public static AmusementPark createAmusementPark() {
		return AmusementPark.builder() //@formatter:off
                .name("Beni parkja")
                .capital(3000)
                .totalArea(1000)
                .entranceFee(50).build(); //@formatter:on
	}

	public static Machine createMachine() {
		return Machine.builder() //@formatter:off
                .fantasyName("Nagy hajó")
                .size(100)
                .price(250)
                .numberOfSeats(10)
                .minimumRequiredAge(18)
                .ticketPrice(10)
                .type(MachineType.CAROUSEL).build(); //@formatter:on
	}

	public static Visitor createVisitor() {
		return Visitor.builder() //@formatter:off
    			.email("nembence1994@gmail.com")
    			.password(PASSWORD_ENCODER.encode("password"))
    			.authority("ROLE_ADMIN")
        		.dateOfBirth(LocalDate.of(1994, 10, 22))
        		.spendingMoney(1000).build(); //@formatter:on
	}

}
