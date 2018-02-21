package hu.beni.amusementparkmicro.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/amusementpark")
public class AmusementParkController {

	@GetMapping("/hello")
	public String hello() {
		return "Hello from AmusementPark!";
	}
	
}