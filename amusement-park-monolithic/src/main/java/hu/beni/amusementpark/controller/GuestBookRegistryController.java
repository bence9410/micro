package hu.beni.amusementpark.controller;

import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import hu.beni.amusementpark.dto.request.AmusementParkSearchRequestDto;
import hu.beni.amusementpark.dto.request.GuestBookRegistryRequestDto;
import hu.beni.amusementpark.dto.response.GuestBookRegistryResponseDto;
import hu.beni.amusementpark.exception.AmusementParkException;
import hu.beni.amusementpark.mapper.GuestBookRegistryMapper;
import hu.beni.amusementpark.service.GuestBookRegistryService;
import hu.beni.clientsupport.resource.GuestBookRegistryResource;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@ConditionalOnWebApplication
public class GuestBookRegistryController {

	private final ObjectMapper objectMapper;
	private final GuestBookRegistryService guestBookRegistryService;
	private final GuestBookRegistryMapper guestBookRegistryMapper;
	private final PagedResourcesAssembler<GuestBookRegistryResponseDto> pagedResourceAssembler;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(AmusementParkSearchRequestDto.class, new PropertyEditorSupport() {
			@Override
			public void setAsText(String text) throws IllegalArgumentException {
				try {
					setValue(objectMapper.readValue(URLDecoder.decode(text, StandardCharsets.UTF_8.toString()),
							GuestBookRegistryRequestDto.class));
				} catch (IOException e) {
					throw new AmusementParkException("Wrong input!", e);
				}
			}
		});
	}

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
	public PagedResources<Resource<GuestBookRegistryResponseDto>> findAllPaged(@PathVariable Long amusementParkId,
			@RequestParam(required = false) GuestBookRegistryRequestDto input, @PageableDefault Pageable pageable) {
		input.setAmusementParkId(amusementParkId);
		return pagedResourceAssembler.toResource(guestBookRegistryService.findAll(input, pageable));
	}
}
