package hu.beni.amusementpark.controller;

import java.security.Principal;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.PagedResources;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import hu.beni.amusementpark.mapper.VisitorMapper;
import hu.beni.amusementpark.service.VisitorService;
import hu.beni.clientsupport.resource.VisitorResource;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@ConditionalOnWebApplication
public class VisitorController {

	private final VisitorService visitorService;
	private final VisitorMapper visitorMapper;

	@GetMapping("/visitor/spending-money")
	public Integer findSpendingMoneyByUsername() {
		return visitorService.findSpendingMoneyByUsername();
	}

	@PostMapping("/visitor")
	public VisitorResource signUp(@RequestBody VisitorResource visitorResource, Principal principal) {
		visitorResource.setUsername(principal.getName());
		return visitorMapper.toResource(visitorService.signUp(visitorMapper.toEntity(visitorResource)));
	}

	@GetMapping("/visitor")
	public PagedResources<VisitorResource> findAllPaged(@PageableDefault Pageable pageable) {
		return visitorMapper.toPagedResources(visitorService.findAll(pageable));
	}

	@DeleteMapping("/visitor/{visitorId}")
	public void delete(@PathVariable Long visitorId) {
		visitorService.delete(visitorId);
	}

	@GetMapping("/visitor/{visitorId}")
	public VisitorResource findOne(@PathVariable Long visitorId) {
		return visitorMapper.toResource(visitorService.findOne(visitorId));
	}

	@PutMapping("amusement-park/{amusementParkId}/visitor/{visitorId}/enter-park")
	public VisitorResource enterPark(@PathVariable Long amusementParkId, @PathVariable Long visitorId) {
		return visitorMapper.toResource(visitorService.enterPark(amusementParkId, visitorId));
	}

	@PutMapping("amusement-park/{amusementParkId}/visitor/{visitorId}/leave-park")
	public VisitorResource leavePark(@PathVariable Long amusementParkId, @PathVariable Long visitorId) {
		return visitorMapper.toResource(visitorService.leavePark(amusementParkId, visitorId));
	}

	@PutMapping("amusement-park/{amusementParkId}/machine/{machineId}/visitor/{visitorId}/get-on-machine")
	public VisitorResource getOnMachine(@PathVariable Long amusementParkId, @PathVariable Long machineId,
			@PathVariable Long visitorId) {
		return visitorMapper.toResource(visitorService.getOnMachine(amusementParkId, machineId, visitorId));
	}

	@PutMapping("amusement-park/{amusementParkId}/machine/{machineId}/visitor/{visitorId}/get-off-machine")
	public VisitorResource getOffMachine(@PathVariable Long amusementParkId, @PathVariable Long machineId,
			@PathVariable Long visitorId) {
		return visitorMapper.toResource(visitorService.getOffMachine(machineId, visitorId));
	}
}
