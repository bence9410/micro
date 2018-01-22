package hu.beni.amusementpark.controller;

import hu.beni.amusementpark.entity.Visitor;
import hu.beni.amusementpark.enums.VisitorState;

import org.springframework.web.bind.annotation.RestController;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.security.Principal;

import static hu.beni.amusementpark.constants.HATEOASLinkNameConstants.*;
import hu.beni.amusementpark.service.VisitorService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequiredArgsConstructor
public class VisitorController {

    private final VisitorService visitorService;
    
    @GetMapping("/visitor/spendingMoney")
    public Integer findSpendingMoneyByUsername(){
        return visitorService.findSpendingMoneyByUsername();
    }
    
    @PostMapping("/visitor")
    public Resource<Visitor> signUp(@RequestBody Visitor visitor, Principal principal){
    	visitor.setUsername(principal.getName());
    	return createResourceForNotInParkVisitor(visitorService.signUp(visitor));
    }

    @GetMapping("/visitor/{visitorId}")
    public Resource<Visitor> findOne(@PathVariable Long visitorId) {
    	Visitor visitor = visitorService.findOne(visitorId);
    	VisitorState state = visitor.getState();
    	Resource<Visitor> visitorResource = null;
    	if(state == null) {
    		visitorResource = createResourceForNotInParkVisitor(visitor);
    	} else if (state.equals(VisitorState.REST)) {
    		visitorResource = createResourceForRestVisitor(null, visitor);
    	} else if(state.equals(VisitorState.ON_MACHINE)) {
    		visitorResource = createResourceForOnMachineVisitor(null, null, visitor);
    	}
        return visitorResource;
    }
    
    @PutMapping("amusementPark/{amusementParkId}/visitor/{visitorId}/enterPark")
    public Resource<Visitor> enterPark(@PathVariable Long amusementParkId, @PathVariable Long visitorId) {
        return createResourceForRestVisitor(amusementParkId, visitorService.enterPark(amusementParkId, visitorId));
    }

    @PutMapping("amusementPark/{amusementParkId}/visitor/{visitorId}/leavePark")
    public ResponseEntity<Void> leavePark(@PathVariable Long amusementParkId, @PathVariable Long visitorId) {
        visitorService.leavePark(amusementParkId, visitorId);
        return null;
    }

    @PutMapping("amusementPark/{amusementParkId}/machine/{machineId}/visitor/{visitorId}/getOnMachine")
    public Resource<Visitor> getOnMachine(@PathVariable Long amusementParkId,
            @PathVariable Long machineId, @PathVariable Long visitorId) {
        return createResourceForOnMachineVisitor(amusementParkId, machineId, visitorService.getOnMachine(amusementParkId, machineId, visitorId));    
    }

    @PutMapping("amusementPark/{amusementParkId}/machine/{machineId}/visitor/{visitorId}/getOffMachine")
    public Resource<Visitor> getOffMachine(@PathVariable Long amusementParkId,
            @PathVariable Long machineId, @PathVariable Long visitorId) {
        return createResourceForRestVisitor(amusementParkId, visitorService.getOffMachine(machineId, visitorId));
    }
    
    private Resource<Visitor> createResourceForNotInParkVisitor(Visitor visitor){
    	Resource<Visitor> visitorResource = createResource(visitor);
    	visitorResource.add(linkTo(methodOn(getClass()).enterPark(null, visitor.getId())).withRel(VISITOR_ENTER_PARK));
    	return visitorResource;
    }

    private Resource<Visitor> createResourceForRestVisitor(Long amusementParkId, Visitor visitor){
    	Resource<Visitor> visitorResource = createResource(visitor);
    	visitorResource.add(linkTo(methodOn(getClass()).leavePark(amusementParkId, visitor.getId())).withRel(VISITOR_LEAVE_PARK),
    			linkTo(methodOn(getClass()).getOnMachine(amusementParkId, null, visitor.getId())).withRel(GET_ON_MACHINE),
    			linkTo(methodOn(GuestBookRegistryController.class).addRegistry(amusementParkId, visitor.getId(), null)).withRel(ADD_REGISTRY));
    	return visitorResource;
    }
    
    private Resource<Visitor> createResourceForOnMachineVisitor(Long amusementParkId, Long machineId, Visitor visitor){
    	Resource<Visitor> visitorResource = createResource(visitor);
    	visitorResource.add(linkTo(methodOn(getClass()).getOffMachine(amusementParkId, machineId, visitor.getId())).withRel(GET_OFF_MACHINE));
    	return visitorResource;
    }
    
    private Resource<Visitor> createResource(Visitor visitor) {
        return new Resource<>(visitor, linkTo(methodOn(getClass()).findOne(visitor.getId())).withSelfRel());
    }
    
}
