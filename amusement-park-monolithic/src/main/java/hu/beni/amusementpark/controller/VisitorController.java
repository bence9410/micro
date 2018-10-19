package hu.beni.amusementpark.controller;

import static hu.beni.amusementpark.constants.Constants.REMEMBER_ME;
import static hu.beni.amusementpark.constants.Constants.ROLE_VISITOR;
import static hu.beni.amusementpark.constants.Constants.SET_COOKIE;
import static hu.beni.amusementpark.constants.Constants.TRUE_LOWERCASE;
import static hu.beni.amusementpark.constants.FieldNameConstants.EMAIL;
import static hu.beni.amusementpark.constants.FieldNameConstants.PASSWO;
import static hu.beni.amusementpark.constants.RequestMappingConstants.A_VISITOR;
import static hu.beni.amusementpark.constants.RequestMappingConstants.IN_A_PARK_A_VISITOR_ENTER_PARK;
import static hu.beni.amusementpark.constants.RequestMappingConstants.IN_A_PARK_A_VISITOR_LEAVE_PARK;
import static hu.beni.amusementpark.constants.RequestMappingConstants.IN_A_PARK_ON_A_MACHINE_A_VISITOR_GET_OFF;
import static hu.beni.amusementpark.constants.RequestMappingConstants.IN_A_PARK_ON_A_MACHINE_A_VISITOR_GET_ON;
import static hu.beni.amusementpark.constants.RequestMappingConstants.ME;
import static hu.beni.amusementpark.constants.RequestMappingConstants.SIGN_UP;
import static hu.beni.amusementpark.constants.RequestMappingConstants.UPLOAD_MONEY;
import static hu.beni.amusementpark.constants.RequestMappingConstants.VISITORS;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import hu.beni.amusementpark.constants.RequestMappingConstants;
import hu.beni.amusementpark.entity.Visitor;
import hu.beni.amusementpark.mapper.VisitorMapper;
import hu.beni.amusementpark.service.VisitorService;
import hu.beni.clientsupport.constants.HATEOASLinkRelConstants;
import hu.beni.clientsupport.resource.VisitorResource;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@ConditionalOnWebApplication
public class VisitorController {

	private final VisitorService visitorService;
	private final VisitorMapper visitorMapper;
	private final RestTemplate restTemplate;

	@GetMapping(ME)
	public ResponseEntity<VisitorResource> getUser(Principal principal) {
		return Optional.ofNullable(principal).map(Principal::getName).map(visitorService::findByEmail)
				.map(visitorMapper::toResource).map(ResponseEntity::ok)
				.orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
	}

	@PostMapping(SIGN_UP)
	public ResponseEntity<VisitorResource> signUp(HttpServletRequest request,
			@RequestParam(name = REMEMBER_ME, required = false) String rememberMe,
			@Valid @RequestBody VisitorResource visitorResource) {
		signUpAsVisitor(visitorResource);
		return copyCookiesAndBody(login(createLoginUrl(request), rememberMe, visitorResource));
	}

	private Visitor signUpAsVisitor(VisitorResource visitorResource) {
		Visitor visitor = visitorMapper.toEntity(visitorResource);
		visitor.setAuthority(ROLE_VISITOR);
		return visitorService.signUp(visitor);
	}

	private String createLoginUrl(HttpServletRequest request) {
		return "http://" + request.getServerName() + ":" + request.getServerPort() + RequestMappingConstants.SLASH
				+ HATEOASLinkRelConstants.LOGIN;
	}

	private ResponseEntity<VisitorResource> login(String loginUrl, String rememberMe, VisitorResource visitorResource) {
		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add(EMAIL, visitorResource.getEmail());
		map.add(PASSWO, visitorResource.getPassword());
		if (TRUE_LOWERCASE.equals(rememberMe)) {
			map.add(REMEMBER_ME, rememberMe);
		}
		return restTemplate.postForEntity(loginUrl, map, VisitorResource.class);
	}

	private ResponseEntity<VisitorResource> copyCookiesAndBody(ResponseEntity<VisitorResource> loginResponse) {
		List<String> cookies = loginResponse.getHeaders().get(SET_COOKIE);
		return ResponseEntity.ok().header(SET_COOKIE, cookies.toArray(new String[cookies.size()]))
				.body(loginResponse.getBody());
	}

	@PostMapping(UPLOAD_MONEY)
	public Void uploadMoney(@RequestBody Integer ammount, Principal principal) {
		visitorService.uploadMoney(ammount, principal.getName());
		return null;
	}

	@GetMapping(VISITORS)
	public PagedResources<VisitorResource> findAllPaged(@PageableDefault Pageable pageable) {
		return visitorMapper.toPagedResources(visitorService.findAll(pageable));
	}

	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping(A_VISITOR)
	public void delete(@PathVariable String visitorEmail) {
		visitorService.delete(visitorEmail);
	}

	@PutMapping(IN_A_PARK_A_VISITOR_ENTER_PARK)
	public VisitorResource enterPark(@PathVariable Long amusementParkId, Principal principal) {
		return visitorMapper.toResource(visitorService.enterPark(amusementParkId, principal.getName()));
	}

	@PutMapping(IN_A_PARK_A_VISITOR_LEAVE_PARK)
	public VisitorResource leavePark(@PathVariable Long amusementParkId, Principal principal) {
		return visitorMapper.toResource(visitorService.leavePark(amusementParkId, principal.getName()));
	}

	@PutMapping(IN_A_PARK_ON_A_MACHINE_A_VISITOR_GET_ON)
	public VisitorResource getOnMachine(@PathVariable Long amusementParkId, @PathVariable Long machineId,
			Principal principal) {
		return visitorMapper.toResource(visitorService.getOnMachine(amusementParkId, machineId, principal.getName()));
	}

	@PutMapping(IN_A_PARK_ON_A_MACHINE_A_VISITOR_GET_OFF)
	public VisitorResource getOffMachine(@PathVariable Long amusementParkId, @PathVariable Long machineId,
			Principal principal) {
		return visitorMapper.toResource(visitorService.getOffMachine(machineId, principal.getName()));
	}
}
