package hu.beni.amusementpark.controller;

import javax.validation.Valid;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.PagedResources;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
		VisitorResource.class.cast(webDataBinder.getTarget())
				.setUsername(SecurityContextHolder.getContext().getAuthentication().getName());
		webDataBinder.addValidators(new VisitorResourceValidator());
	}

	@PostMapping("/visitors")
	public VisitorResource signUp(@Valid @RequestBody VisitorResource visitorResource) {
		return visitorMapper.toResource(visitorService.signUp(visitorMapper.toEntity(visitorResource)));
	}

	@GetMapping("/visitors")
	public PagedResources<VisitorResource> findAllPaged(@PageableDefault Pageable pageable) {
		return visitorMapper.toPagedResources(visitorService.findAll(pageable));
	}

	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/visitors/{visitorId}")
	public void delete(@PathVariable Long visitorId) {
		visitorService.delete(visitorId);
	}

	@GetMapping("/visitors/{visitorId}")
	public VisitorResource findOne(@PathVariable Long visitorId) {
		return visitorMapper.toResource(visitorService.findOne(visitorId));
	}

	@PutMapping("amusement-parks/{amusementParkId}/visitors/{visitorId}/enter-park")
	public VisitorResource enterPark(@PathVariable Long amusementParkId, @PathVariable Long visitorId) {
		return visitorMapper.toResource(visitorService.enterPark(amusementParkId, visitorId));
	}

	@PutMapping("amusement-parks/{amusementParkId}/visitors/{visitorId}/leave-park")
	public VisitorResource leavePark(@PathVariable Long amusementParkId, @PathVariable Long visitorId) {
		return visitorMapper.toResource(visitorService.leavePark(amusementParkId, visitorId));
	}

	@PutMapping("amusement-parks/{amusementParkId}/machines/{machineId}/visitors/{visitorId}/get-on-machine")
	public VisitorResource getOnMachine(@PathVariable Long amusementParkId, @PathVariable Long machineId,
			@PathVariable Long visitorId) {
		return visitorMapper.toResource(visitorService.getOnMachine(amusementParkId, machineId, visitorId));
	}

	@PutMapping("amusement-parks/{amusementParkId}/machines/{machineId}/visitors/{visitorId}/get-off-machine")
	public VisitorResource getOffMachine(@PathVariable Long amusementParkId, @PathVariable Long machineId,
			@PathVariable Long visitorId) {
		return visitorMapper.toResource(visitorService.getOffMachine(machineId, visitorId));
	}
}
