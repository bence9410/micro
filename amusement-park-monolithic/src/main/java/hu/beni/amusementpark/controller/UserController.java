package hu.beni.amusementpark.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import hu.beni.amusementpark.service.VisitorService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserController {

	private final VisitorService visitorService;

	@GetMapping("/user")
	public Map<String, Object> getUser(Principal principal) {
		UsernamePasswordAuthenticationToken user = UsernamePasswordAuthenticationToken.class.cast(principal);
		Map<String, Object> map = new HashMap<>();
		map.put("name", user.getName());
		map.put("authorities", user.getAuthorities());
		map.put("spendingMoney", visitorService.findSpendingMoneyByUsername());
		return map;
	}

}
