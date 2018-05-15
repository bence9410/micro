package hu.beni.amusementpark.controller;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import hu.beni.amusementpark.mapper.GuestBookRegistryMapper;
import hu.beni.amusementpark.service.GuestBookRegistryService;
import hu.beni.clientsupport.resource.GuestBookRegistryResource;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@ConditionalOnWebApplication
public class GuestBookRegistryController {

	private final GuestBookRegistryService guestBookRegistryService;
	private final GuestBookRegistryMapper guestBookRegistryMapper;

	@PostMapping("/amusement-park/{amusementParkId}/visitor/{visitorId}/guest-book-registry")
	public GuestBookRegistryResource addRegistry(@PathVariable Long amusementParkId, @PathVariable Long visitorId,
			@RequestBody String textOfRegistry) {
		return guestBookRegistryMapper
				.toResource(guestBookRegistryService.addRegistry(amusementParkId, visitorId, textOfRegistry));
	}

	@GetMapping("guest-book-registry/{guestBookRegistryId}")
	public GuestBookRegistryResource findOne(@PathVariable Long guestBookRegistryId) {
		return guestBookRegistryMapper.toResource(guestBookRegistryService.findOne(guestBookRegistryId));
	}
}
