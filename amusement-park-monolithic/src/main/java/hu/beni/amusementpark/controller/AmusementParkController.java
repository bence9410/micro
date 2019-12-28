package hu.beni.amusementpark.controller;

import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import javax.validation.Valid;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import hu.beni.amusementpark.dto.request.AmusementParkSearchRequestDto;
import hu.beni.amusementpark.dto.response.AmusementParkPageResponseDto;
import hu.beni.amusementpark.exception.AmusementParkException;
import hu.beni.amusementpark.factory.LinkFactory;
import hu.beni.amusementpark.mapper.AmusementParkMapper;
import hu.beni.amusementpark.service.AmusementParkService;
import hu.beni.clientsupport.resource.AmusementParkResource;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/amusement-parks")
@RequiredArgsConstructor
@ConditionalOnWebApplication
public class AmusementParkController {

	private final ObjectMapper objectMapper;
	private final AmusementParkService amusementParkService;
	private final AmusementParkMapper amusementParkMapper;
	private final PagedResourcesAssembler<AmusementParkPageResponseDto> pagedResourcesAssembler;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(AmusementParkSearchRequestDto.class, new PropertyEditorSupport() {
			@Override
			public void setAsText(String text) throws IllegalArgumentException {
				try {
					setValue(objectMapper.readValue(URLDecoder.decode(text, StandardCharsets.UTF_8.toString()),
							AmusementParkSearchRequestDto.class));
				} catch (IOException e) {
					throw new AmusementParkException("Wrong input!", e);
				}
			}
		});
	}

	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	public AmusementParkResource save(@Valid @RequestBody AmusementParkResource amusementParkResource) {
		return amusementParkMapper
				.toResource(amusementParkService.save(amusementParkMapper.toEntity(amusementParkResource)));
	}

	@GetMapping
	public PagedResources<Resource<AmusementParkPageResponseDto>> findAllPaged(
			@RequestParam(required = false) AmusementParkSearchRequestDto input, @PageableDefault Pageable pageable) {
		PagedResources<Resource<AmusementParkPageResponseDto>> result = pagedResourcesAssembler
				.toResource(amusementParkService.findAll(input, pageable));

		result.getContent()
				.forEach(r -> r.add(new Link[] { LinkFactory.createAmusementParkSelfLink(r.getContent().getId()),
						LinkFactory.createMachineLink(r.getContent().getId()), LinkFactory.createVisitorSignUpLink(),
						LinkFactory.createVisitorEnterParkLink(r.getContent().getId()) }));

		return result;
	}

	@GetMapping("/{amusementParkId}")
	public AmusementParkResource findOne(@PathVariable Long amusementParkId) {
		return amusementParkMapper.toResource(amusementParkService.findById(amusementParkId));
	}

	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/{amusementParkId}")
	public void delete(@PathVariable Long amusementParkId) {
		amusementParkService.delete(amusementParkId);
	}
}
