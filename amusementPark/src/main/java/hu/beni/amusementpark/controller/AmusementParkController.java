package hu.beni.amusementpark.controller;

import hu.beni.amusementpark.entity.AmusementPark;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import static hu.beni.amusementpark.constants.MappingConstants.*;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import hu.beni.amusementpark.service.AmusementParkService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.Resource;
import org.springframework.web.bind.annotation.DeleteMapping;

@RestController
@RequestMapping(path = "/amusementPark")
@RequiredArgsConstructor
public class AmusementParkController {

    private final AmusementParkService amusementParkService;

    @PostMapping
    public Resource<AmusementPark> create(@Valid @RequestBody AmusementPark amusementPark) {
        return createResource(amusementParkService.create(amusementPark));
    }

    @GetMapping("/{amusementParkId}")
    public Resource<AmusementPark> read(@PathVariable(name = AMUSEMENT_PARK_ID) Long amusementParkId) {
        return createResource(amusementParkService.read(amusementParkId));
    }

    @DeleteMapping("/{amusementParkId}")
    public void delete(@PathVariable(name = AMUSEMENT_PARK_ID) Long amusementParkId) {
        amusementParkService.delete(amusementParkId);
    }

    private Resource<AmusementPark> createResource(AmusementPark amusementPark) {
        return new Resource<>(amusementPark, linkTo(methodOn(AmusementParkController.class).read(amusementPark.getId())).withSelfRel(),
                linkTo(methodOn(MachineController.class).addMachine(amusementPark.getId(), null)).withRel(MACHINE),
                linkTo(methodOn(VisitorController.class).enterPark(amusementPark.getId(), null)).withRel(VISITOR));
    }
}
