package hu.beni.zoo.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/zoo")
public class ZooController {
	
	@GetMapping("/hello")
	@PreAuthorize("hasRole('ADMIN')")
	public String hello() {
		return "Hello from Zoo!";
	}

}
