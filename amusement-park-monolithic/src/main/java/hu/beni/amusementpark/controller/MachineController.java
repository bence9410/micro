package hu.beni.amusementpark.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hu.beni.amusementpark.mapper.MachineMapper;
import hu.beni.amusementpark.service.MachineService;
import hu.beni.amusementpark.validator.MachineResourceValidator;
import hu.beni.clientsupport.resource.MachineResource;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("amusement-park/{amusementParkId}/machine")
@RequiredArgsConstructor
@ConditionalOnWebApplication
public class MachineController {

	private final MachineService machineService;
	private final MachineMapper machineMapper;

	@InitBinder("machineResource")
	protected void initBinder(WebDataBinder webDataBinder) {
		webDataBinder.addValidators(new MachineResourceValidator());
	}

	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	public MachineResource addMachine(@PathVariable Long amusementParkId,
			@Valid @RequestBody MachineResource machineResource) {
		return machineMapper
				.toResource(machineService.addMachine(amusementParkId, machineMapper.toEntity(machineResource)));
	}

	@GetMapping("/{machineId}")
	public MachineResource findOne(@PathVariable Long amusementParkId, @PathVariable Long machineId) {
		return machineMapper.toResource(machineService.findOne(amusementParkId, machineId));
	}

	@GetMapping
	public List<MachineResource> findAllByAmusementParkId(@PathVariable Long amusementParkId) {
		return machineService.findAllByAmusementParkId(amusementParkId).stream().map(machineMapper::toResource)
				.collect(Collectors.toList());
	}

	@DeleteMapping("/{machineId}")
	@PreAuthorize("hasRole('ADMIN')")
	public void delete(@PathVariable Long amusementParkId, @PathVariable Long machineId) {
		machineService.removeMachine(amusementParkId, machineId);
	}
}
