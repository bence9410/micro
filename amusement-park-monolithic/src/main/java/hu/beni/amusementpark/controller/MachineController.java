package hu.beni.amusementpark.controller;

import hu.beni.amusementpark.mapper.MachineMapper;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import hu.beni.amusementpark.service.MachineService;
import hu.beni.clientsupport.resource.MachineResource;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("amusement-park/{amusementParkId}/machine")
@RequiredArgsConstructor
@ConditionalOnWebApplication
public class MachineController {

	private final MachineService machineService;
	private final MachineMapper machineMapper;

	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	public MachineResource addMachine(@PathVariable Long amusementParkId, @RequestBody MachineResource machine) {
		return machineMapper.toResource(machineService.addMachine(amusementParkId, machineMapper.toEntity(machine)));
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
