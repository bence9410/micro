package hu.beni.amusementpark.controller;

import static hu.beni.amusementpark.constants.HATEOASLinkNameConstants.MACHINE;
import static hu.beni.amusementpark.constants.HATEOASLinkNameConstants.VISITOR_ENTER_PARK;
import static hu.beni.amusementpark.constants.HATEOASLinkNameConstants.VISITOR_SIGN_UP;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.service.AmusementParkService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(path = "/amusementPark")
@RequiredArgsConstructor
public class AmusementParkController {
	
    private final AmusementParkService amusementParkService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Resource<AmusementPark> save(@Valid @RequestBody AmusementPark amusementPark) {
    	amusementPark.getAddress().setAmusementPark(amusementPark);
        return createResource(amusementParkService.save(amusementPark));
    }
    
    @GetMapping
    public List<Resource<AmusementPark>> findAll(){
    	return amusementParkService.findAllFetchAddress().stream().map(this::createResource).collect(Collectors.toList());
    }
    
    @GetMapping("/paged")
    public Page<Resource<AmusementPark>> findAllPaged(@RequestParam(name = "page") int page, 
    		@RequestParam(name = "size") int size, @RequestParam(name = "sort", defaultValue = "id") String sort){	
    	return amusementParkService.findAllFetchAddress(PageRequest.of(page, size, Sort.by(sort))).map(this::createResource);
    }

    @GetMapping("/{amusementParkId}")
    public Resource<AmusementPark> findOne(@PathVariable Long amusementParkId) {
        return createResource(amusementParkService.findByIdFetchAddress(amusementParkId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{amusementParkId}")
    public void delete(@PathVariable Long amusementParkId) {
        amusementParkService.delete(amusementParkId);
    }

    private Resource<AmusementPark> createResource(AmusementPark amusementPark) {
        return new Resource<>(amusementPark, linkTo(methodOn(getClass()).findOne(amusementPark.getId())).withSelfRel(),
                linkTo(methodOn(MachineController.class).addMachine(amusementPark.getId(), null)).withRel(MACHINE),
                linkTo(methodOn(VisitorController.class).signUp(null, null)).withRel(VISITOR_SIGN_UP),
                linkTo(methodOn(VisitorController.class).enterPark(amusementPark.getId(), null)).withRel(VISITOR_ENTER_PARK));
    }
}
