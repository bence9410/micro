package hu.beni.amusementpark.controller;

import static hu.beni.amusementpark.factory.LinkFactory.createAmusementParkLink;
import static hu.beni.amusementpark.factory.LinkFactory.createLoginLink;
import static hu.beni.amusementpark.factory.LinkFactory.createLogoutLink;
import static hu.beni.amusementpark.factory.LinkFactory.createUserLink;
import static hu.beni.amusementpark.factory.LinkFactory.createVisitorSignUpLink;

import org.springframework.hateoas.Link;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BaseLinksController {

	@GetMapping("/links")
	public Link[] getBaseLinks() {
		return new Link[] { createAmusementParkLink(), createVisitorSignUpLink(), createLoginLink(), createLogoutLink(),
				createUserLink() };
	}

}
