package hu.beni.zoo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/zoo")
public class ZooController {
	
	@GetMapping("/hello")
	public String hello() {
		return "Hello from Zoo!";
	}

}
