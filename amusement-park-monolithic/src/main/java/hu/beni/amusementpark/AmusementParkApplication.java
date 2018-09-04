package hu.beni.amusementpark;

import java.time.LocalDate;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import hu.beni.amusementpark.entity.Visitor;
import hu.beni.amusementpark.service.VisitorService;

@SpringBootApplication
public class AmusementParkApplication {

	public static void main(String[] args) {
		SpringApplication.run(AmusementParkApplication.class, args);
	}

	@Bean
	public ApplicationRunner applicationRunner(VisitorService visitorService) {
		return args -> visitorService.signUp(Visitor.builder() // @formatter:off
				.username("admin")
				.password(new BCryptPasswordEncoder().encode("password"))
				.authority("ROLE_ADMIN")
				.dateOfBirth(LocalDate.of(1994, 10, 22)).build()); // @formatter:on
	}
}