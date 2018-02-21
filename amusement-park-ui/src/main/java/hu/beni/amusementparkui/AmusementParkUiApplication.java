package hu.beni.amusementparkui;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class AmusementParkUiApplication {

	public static void main(String[] args) {
		SpringApplication.run(AmusementParkUiApplication.class, args);
	}
	
	@GetMapping("/hello")
	public String hello() {
		return "hello";
	}
}
