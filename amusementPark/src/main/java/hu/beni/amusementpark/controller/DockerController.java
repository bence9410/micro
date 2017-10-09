package hu.beni.amusementpark.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/docker")
public class DockerController {

	@GetMapping
	public String getContainerId() {
		String response = System.getenv("HOSTNAME");
		return response == null ? "Not running in a Docker container" : response;
	}

}
