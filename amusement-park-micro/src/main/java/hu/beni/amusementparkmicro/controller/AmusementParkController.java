package hu.beni.amusementparkmicro.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/amusement-park")
public class AmusementParkController {

	@GetMapping("/hello")
	@PreAuthorize("hasRole('ADMIN')")
	public String hello() {
		return "Hello from AmusementPark!";
	}
	
}
