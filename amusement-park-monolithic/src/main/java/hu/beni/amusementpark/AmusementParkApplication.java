package hu.beni.amusementpark;

import java.time.LocalDate;
import java.util.stream.IntStream;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.entity.Machine;
import hu.beni.amusementpark.entity.Visitor;
import hu.beni.amusementpark.enums.MachineType;
import hu.beni.amusementpark.service.AmusementParkService;
import hu.beni.amusementpark.service.MachineService;
import hu.beni.amusementpark.service.VisitorService;

@SpringBootApplication
public class AmusementParkApplication {

	public static void main(String[] args) {
		SpringApplication.run(AmusementParkApplication.class, args);
	}

	@Bean
	@Profile("default")
	public ApplicationRunner applicationRunner(AmusementParkService amusementParkService, MachineService machineService,
			VisitorService visitorService) {
		PasswordEncoder encoder = new BCryptPasswordEncoder();
		return args -> {

			visitorService.signUp(Visitor.builder() // @formatter:off
				.email("jeni@gmail.com")
				.password(encoder.encode("password"))
				.authority("ROLE_USER")
				.dateOfBirth(LocalDate.of(1995, 05, 10)).build()); // @formatter:on

			AmusementPark amusementPark = AmusementPark.builder() //@formatter:off
                .name("Beni parkja")
                .capital(3000)
                .totalArea(1000)
                .entranceFee(50)
                .build(); //@formatter:on

			amusementParkService.save(amusementPark);

			machineService.addMachine(amusementPark.getId(), Machine.builder() //@formatter:off
                .fantasyName("Nagy hajó")
                .size(100)
                .price(250)
                .numberOfSeats(10)
                .minimumRequiredAge(18)
                .ticketPrice(10)
                .type(MachineType.CAROUSEL).build()); //@formatter:on

			IntStream.range(0, 5).forEach(i -> visitorService.signUp(Visitor.builder() // @formatter:off
				.email("admin" + i + "@gmail.com")
				.password(encoder.encode("password"))
				.authority("ROLE_ADMIN")
				.dateOfBirth(LocalDate.of(1994, 10, 22)).build())); // @formatter:on
		};
	}
}