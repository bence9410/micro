package hu.beni.amusementpark.controller;

import java.security.Principal;

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

	@PostMapping("/amusement-parks/{amusementParkId}/visitors/guest-book-registries")
	public GuestBookRegistryResource addRegistry(@PathVariable Long amusementParkId, @RequestBody String textOfRegistry,
			Principal principal) {
		return guestBookRegistryMapper
				.toResource(guestBookRegistryService.addRegistry(amusementParkId, principal.getName(), textOfRegistry));
	}

	@GetMapping("guest-book-registries/{guestBookRegistryId}")
	public GuestBookRegistryResource findOne(@PathVariable Long guestBookRegistryId) {
		return guestBookRegistryMapper.toResource(guestBookRegistryService.findOne(guestBookRegistryId));
	}
}
