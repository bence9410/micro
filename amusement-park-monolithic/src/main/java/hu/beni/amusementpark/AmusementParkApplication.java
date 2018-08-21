package hu.beni.amusementpark;

import java.time.LocalDate;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import hu.beni.amusementpark.constants.SpringProfileConstants;
import hu.beni.amusementpark.entity.Visitor;
import hu.beni.amusementpark.service.VisitorService;

@SpringBootApplication
public class AmusementParkApplication {

	public static void main(String[] args) {
		SpringApplication.run(AmusementParkApplication.class, args);
	}

	@Bean
	@Profile(SpringProfileConstants.DEFAULT)
	public ApplicationRunner applicationRunner(VisitorService visitorService, PasswordEncoder passwordEncoder) {
		return args -> visitorService.signUp(Visitor.builder() // @formatter:off
				.name("Németh Bence")
				.username("admin")
				.password(passwordEncoder.encode("password"))
				.authority("ROLE_ADMIN")
				.dateOfBirth(LocalDate.of(1994, 10, 22)).build()); // @formatter:on
	}
}