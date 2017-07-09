package hu.beni.amusementpark.controller;

import hu.beni.amusementpark.entity.Visitor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import hu.beni.amusementpark.service.VisitorService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.Resource;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping(path = "amusementPark/{amusementParkId}")
@RequiredArgsConstructor
public class VisitorController {

    private final VisitorService visitorService;

    @PostMapping("/visitor")
    public Resource<Visitor> enterPark(@PathVariable(name = "amusementParkId") Long amusementParkId, @RequestBody Visitor visitor) {
        return createResource(amusementParkId, visitorService.enterPark(amusementParkId, visitor));
    }

    @GetMapping("/visitor/{visitorId}")
    public Resource<Visitor> read(@PathVariable(name = "amusementParkId") Long amusementParkId, @PathVariable(name = "visitorId") Long visitorId) {
        return createResource(amusementParkId, visitorService.read(visitorId));
    }

    @DeleteMapping("/visitor/{visitorId}")
    public void leavePark(@PathVariable(name = "visitorId") Long visitorId) {
        visitorService.leavePark(visitorId);
    }

    @PutMapping("/machine/{machineId}/visitor/{visitorId}/getOnMachine")
    public Resource<Visitor> getOnMachine(@PathVariable(name = "amusementParkId") Long amusementParkId,
            @PathVariable(name = "machineId") Long machineId, @PathVariable(name = "visitorId") Long visitorId) {
        Resource<Visitor> visitorResource = createResource(amusementParkId, visitorService.getOnMachine(amusementParkId, machineId, visitorId));
        visitorResource.add(linkTo(methodOn(VisitorController.class).getOffMachine(amusementParkId, machineId, visitorId)).withRel("getOffMachine"));
        return visitorResource;
    }

    @PutMapping("/machine/{machineId}/visitor/{visitorId}/getOffMachine")
    public Resource<Visitor> getOffMachine(@PathVariable(name = "amusementParkId") Long amusementParkId,
            @PathVariable(name = "machineId") Long machineId, @PathVariable(name = "visitorId") Long visitorId) {
        return createResource(amusementParkId, visitorService.getOffMachine(machineId, visitorId));
    }

    private Resource<Visitor> createResource(Long amusementParkId, Visitor visitor) {
        return new Resource<>(visitor, linkTo(methodOn(VisitorController.class).read(amusementParkId, visitor.getId())).withSelfRel());
    }

}
