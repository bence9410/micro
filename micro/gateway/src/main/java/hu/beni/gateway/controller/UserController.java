package hu.beni.gateway.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Principal;

import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

	@GetMapping("/user")
	public Principal getUser(Principal principal) {
		return principal;
	}

	/*@GetMapping("/")
	public String main() throws IOException {
		return StreamUtils.copyToString(new ClassPathResource("public/index.html").getInputStream(),
				StandardCharsets.UTF_8);
	}*/

}
