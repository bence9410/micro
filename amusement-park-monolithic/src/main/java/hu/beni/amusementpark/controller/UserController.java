package hu.beni.amusementpark.controller;

import static hu.beni.amusementpark.constants.RequestMappingConstants.USER;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import hu.beni.amusementpark.service.VisitorService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class UserController {

	private final VisitorService visitorService;

	@GetMapping(USER)
	public ResponseEntity<Map<String, Object>> getUser(Authentication authentication) {
		ResponseEntity<Map<String, Object>> response;
		if (authentication == null) {
			response = ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		} else {
			Map<String, Object> map = new HashMap<>();
			map.put("name", authentication.getName());
			map.put("authorities", authentication.getAuthorities());
			map.put("spendingMoney", visitorService.findSpendingMoneyByUsername());
			response = ResponseEntity.ok(map);
		}
		return response;
	}

}
