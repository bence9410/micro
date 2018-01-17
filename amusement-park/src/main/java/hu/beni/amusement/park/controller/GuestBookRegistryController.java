package hu.beni.amusement.park.controller;

import static hu.beni.amusement.park.constants.HATEOASLinkNameConstants.*;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import org.springframework.hateoas.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import hu.beni.amusement.park.entity.GuestBookRegistry;
import hu.beni.amusement.park.service.GuestBookRegistryService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class GuestBookRegistryController {
	
	private final GuestBookRegistryService guestBookRegistryService;
	
	@PostMapping("/amusementPark/{amusementParkId}/visitor/{visitorId}/guestBookRegistry")
	public Resource<GuestBookRegistry> addRegistry(@PathVariable Long amusementParkId, 
			@PathVariable Long visitorId, @RequestBody String textOfRegistry) {
		return createResource(amusementParkId, visitorId, guestBookRegistryService.addRegistry(amusementParkId, visitorId, textOfRegistry));
	}
	
	@GetMapping("guestBookRegistry/{guestBookRegistryId}")
	public Resource<GuestBookRegistry> findOne(@PathVariable Long guestBookRegistryId) {
		return createResource(null, null, guestBookRegistryService.findOne(guestBookRegistryId));
	}

	private Resource<GuestBookRegistry> createResource(Long amusementParkId, Long visitorId, GuestBookRegistry guestBookRegistry){
		return new Resource<>(guestBookRegistry, linkTo(methodOn(getClass()).findOne(guestBookRegistry.getId())).withSelfRel(),
				linkTo(methodOn(getClass()).addRegistry(amusementParkId, visitorId, null)).withRel(ADD_REGISTRY));
	}
	
}
