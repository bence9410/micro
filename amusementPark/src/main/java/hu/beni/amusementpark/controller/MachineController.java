package hu.beni.amusementpark.controller;

import hu.beni.amusementpark.entity.Machine;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import static hu.beni.amusementpark.constants.MappingConstants.*;
import hu.beni.amusementpark.service.MachineService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.Resource;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping(path = "amusementPark/{amusementParkId}/machine")
@RequiredArgsConstructor
public class MachineController {

    private final MachineService machineService;

    @PostMapping
    public Resource<Machine> addMachine(@PathVariable(name = AMUSEMENT_PARK_ID) Long amusementParkId, @RequestBody Machine machine) {
        return createResource(amusementParkId, machineService.addMachine(amusementParkId, machine));
    }

    @GetMapping("/{machineId}")
    public Resource<Machine> read(@PathVariable(name = AMUSEMENT_PARK_ID) Long amusementParkId, @PathVariable(name = MACHINE_ID) Long machineId) {
        return createResource(amusementParkId, machineService.read(machineId));
    }

    @DeleteMapping("/{machineId}")
    public void delete(@PathVariable(name = AMUSEMENT_PARK_ID) Long amusementParkId, @PathVariable(name = MACHINE_ID) Long machineId) {
        machineService.removeMachine(amusementParkId, machineId);
    }

    private Resource<Machine> createResource(Long amusementParkId, Machine machine) {
        return new Resource<>(machine, linkTo(methodOn(MachineController.class).read(amusementParkId, machine.getId())).withSelfRel(),
                linkTo(methodOn(VisitorController.class).getOnMachine(amusementParkId, machine.getId(), null)).withRel("getOnMachine"));
    }

}
