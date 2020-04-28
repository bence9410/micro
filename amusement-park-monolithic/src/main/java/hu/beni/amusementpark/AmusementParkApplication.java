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
import hu.beni.amusementpark.service.GuestBookRegistryService;
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
			VisitorService visitorService, GuestBookRegistryService guestBookRegistryService) {
		PasswordEncoder encoder = new BCryptPasswordEncoder();
		return args -> {

			Visitor visitor = Visitor
					.builder() // @formatter:off
					.email("bence@gmail.com")
					.password(encoder.encode("password"))
					.authority("ROLE_ADMIN")
					.dateOfBirth(LocalDate.of(1995, 05, 10)).build(); // @formatter:on

			visitorService.signUp(visitor);

			AmusementPark amusementPark = AmusementPark
					.builder() //@formatter:off
                .name("Bence parkja")
                .capital(30000)
                .totalArea(2000)
                .entranceFee(50)
                .build(); //@formatter:on

			amusementParkService.save(amusementPark);

			visitorService.enterPark(amusementPark.getId(), visitor.getEmail());
			guestBookRegistryService.addRegistry(amusementPark.getId(), visitor.getEmail(), "Nagyon élveztem.");
			guestBookRegistryService.addRegistry(amusementPark.getId(), visitor.getEmail(), "Jó volt.");
			visitorService.leavePark(amusementPark.getId(), visitor.getEmail());

			machineService.addMachine(amusementPark.getId(), Machine
					.builder() //@formatter:off
		            .fantasyName("Retro körhinta")
		            .size(100)
		            .price(250)
		            .numberOfSeats(10)
		            .minimumRequiredAge(12)
		            .ticketPrice(10)
		            .type(MachineType.CAROUSEL).build()); //@formatter:on

			machineService.addMachine(amusementPark.getId(), Machine
					.builder() //@formatter:off
                .fantasyName("Mágikus dodgem")
                .size(150)
                .price(250)
                .numberOfSeats(10)
                .minimumRequiredAge(12)
                .ticketPrice(10)
                .type(MachineType.DODGEM).build()); //@formatter:on

			machineService.addMachine(amusementPark.getId(), Machine
					.builder() //@formatter:off
	                .fantasyName("Gokart")
	                .size(150)
	                .price(250)
	                .numberOfSeats(10)
	                .minimumRequiredAge(12)
	                .ticketPrice(10)
	                .type(MachineType.GOKART).build()); //@formatter:on

			machineService.addMachine(amusementPark.getId(), Machine
					.builder() //@formatter:off
	                .fantasyName("Titanic")
	                .size(150)
	                .price(250)
	                .numberOfSeats(10)
	                .minimumRequiredAge(12)
	                .ticketPrice(10)
	                .type(MachineType.SHIP).build()); //@formatter:on

			machineService.addMachine(amusementPark.getId(), Machine
					.builder() //@formatter:off
	                .fantasyName("Hullámvasút")
	                .size(150)
	                .price(250)
	                .numberOfSeats(10)
	                .minimumRequiredAge(12)
	                .ticketPrice(10)
	                .type(MachineType.ROLLER_COASTER).build()); //@formatter:on
		};
	}

	@Bean
	@Profile({ "oracleDB", "postgres" })
	public ApplicationRunner applicationRunnerOracle(AmusementParkService amusementParkService,
			MachineService machineService, VisitorService visitorService,
			GuestBookRegistryService guestBookRegistryService) {
		PasswordEncoder encoder = new BCryptPasswordEncoder(); // @formatter:off
		return args -> IntStream.range(0, 5).forEach(i -> visitorService.signUp(Visitor.builder() 
						.email("admin" + i + "@gmail.com")
						.password(encoder.encode("password"))
						.authority("ROLE_ADMIN")
						.dateOfBirth(LocalDate.of(1994, 10, 22)).build())); // @formatter:on
	}
}