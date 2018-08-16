package hu.beni.amusementpark.controller;

import static hu.beni.amusementpark.constants.RequestMappingConstants.A_VISITOR;
import static hu.beni.amusementpark.constants.RequestMappingConstants.IN_A_PARK_A_VISITOR_ENTER_PARK;
import static hu.beni.amusementpark.constants.RequestMappingConstants.IN_A_PARK_A_VISITOR_LEAVE_PARK;
import static hu.beni.amusementpark.constants.RequestMappingConstants.IN_A_PARK_ON_A_MACHINE_A_VISITOR_GET_OFF;
import static hu.beni.amusementpark.constants.RequestMappingConstants.IN_A_PARK_ON_A_MACHINE_A_VISITOR_GET_ON;
import static hu.beni.amusementpark.constants.RequestMappingConstants.SIGN_UP;
import static hu.beni.amusementpark.constants.RequestMappingConstants.ME;
import static hu.beni.amusementpark.constants.RequestMappingConstants.VISITORS;

import java.util.Arrays;
import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import hu.beni.amusementpark.entity.Visitor;
import hu.beni.amusementpark.mapper.VisitorMapper;
import hu.beni.amusementpark.service.VisitorService;
import hu.beni.amusementpark.validator.VisitorResourceValidator;
import hu.beni.clientsupport.resource.VisitorResource;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@ConditionalOnWebApplication
public class VisitorController {

	private final VisitorService visitorService;
	private final VisitorMapper visitorMapper;

	@InitBinder("visitorResource")
	protected void initBinder(WebDataBinder webDataBinder) {
		webDataBinder.addValidators(new VisitorResourceValidator());
	}

	@GetMapping(ME)
	public ResponseEntity<VisitorResource> getUser(Authentication authentication) {
		ResponseEntity<VisitorResource> response;
		if (authentication == null) {
			response = ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		} else {
			Visitor visitor = visitorService.findByUsernameReadAuthorityAndSpendingMoney();
			visitor.setUsername(authentication.getName());
			response = ResponseEntity.ok(visitorMapper.toResource(visitor));
		}
		return response;
	}

	@PostMapping(SIGN_UP)
	public VisitorResource signUp(@RequestBody VisitorResource visitorResource) {
		Visitor visitor = visitorMapper.toEntity(visitorResource);
		visitor.setAuthority("ROLE_ADMIN");
		List<SimpleGrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority(visitor.getAuthority()));
		SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
				new User(visitor.getUsername(), visitorResource.getPassword(), authorities), null, authorities));
		return visitorMapper.toResource(visitorService.signUp(visitor));
	}

	@GetMapping(VISITORS)
	public PagedResources<VisitorResource> findAllPaged(@PageableDefault Pageable pageable) {
		return visitorMapper.toPagedResources(visitorService.findAll(pageable));
	}

	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping(A_VISITOR)
	public void delete(@PathVariable Long visitorId) {
		visitorService.delete(visitorId);
	}

	@GetMapping(A_VISITOR)
	public VisitorResource findOne(@PathVariable Long visitorId) {
		return visitorMapper.toResource(visitorService.findOne(visitorId));
	}

	@PutMapping(IN_A_PARK_A_VISITOR_ENTER_PARK)
	public VisitorResource enterPark(@PathVariable Long amusementParkId, @PathVariable Long visitorId) {
		return visitorMapper.toResource(visitorService.enterPark(amusementParkId, visitorId));
	}

	@PutMapping(IN_A_PARK_A_VISITOR_LEAVE_PARK)
	public VisitorResource leavePark(@PathVariable Long amusementParkId, @PathVariable Long visitorId) {
		return visitorMapper.toResource(visitorService.leavePark(amusementParkId, visitorId));
	}

	@PutMapping(IN_A_PARK_ON_A_MACHINE_A_VISITOR_GET_ON)
	public VisitorResource getOnMachine(@PathVariable Long amusementParkId, @PathVariable Long machineId,
			@PathVariable Long visitorId) {
		return visitorMapper.toResource(visitorService.getOnMachine(amusementParkId, machineId, visitorId));
	}

	@PutMapping(IN_A_PARK_ON_A_MACHINE_A_VISITOR_GET_OFF)
	public VisitorResource getOffMachine(@PathVariable Long amusementParkId, @PathVariable Long machineId,
			@PathVariable Long visitorId) {
		return visitorMapper.toResource(visitorService.getOffMachine(machineId, visitorId));
	}
}
