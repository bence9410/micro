package hu.beni.amusementpark.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import hu.beni.amusementpark.dto.response.GuestBookRegistryResponseDto;
import hu.beni.amusementpark.mapper.GuestBookRegistryMapper;
import hu.beni.amusementpark.service.GuestBookRegistryService;
import hu.beni.clientsupport.resource.GuestBookRegistryResource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
	
	@GetMapping("/amusement-parks/{amusementParkId}/visitors/guest-book-registries")
	public List<GuestBookRegistryResponseDto> findAllPaged(@PathVariable Long amusementParkId,
			@RequestParam(required = false) String visitorEmail, @RequestParam(required = false) String textOfRegistry, 
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateOfRegistryMin,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateOfRegistryMax,
			@PageableDefault Pageable pageable) {
		log.info("Email: "+ visitorEmail);
		log.info("Text Off Registry: "+ textOfRegistry);
		log.info("Date of Registry min.: "+ dateOfRegistryMin);
		log.info("Date of Registry max.: "+ dateOfRegistryMax);
		
		return guestBookRegistryService.findAll(pageable).getContent().stream().map(g -> GuestBookRegistryResponseDto.builder()
				.visitorEmail(g.getVisitor().getEmail()).textOfRegistry(g.getTextOfRegistry()).dateOfRegistry(g.getDateOfRegistry())
				.build()).collect(Collectors.toList());
		
		
		
	}
}
