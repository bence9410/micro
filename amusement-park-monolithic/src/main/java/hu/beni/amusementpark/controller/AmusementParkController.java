package hu.beni.amusementpark.controller;

import javax.validation.Valid;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.PagedResources;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.mapper.AmusementParkMapper;
import hu.beni.amusementpark.service.AmusementParkService;
import hu.beni.clientsupport.resource.AmusementParkResource;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/amusement-park")
@RequiredArgsConstructor
@ConditionalOnWebApplication
public class AmusementParkController {
	
	private final AmusementParkService amusementParkService;
	private final AmusementParkMapper amusementParkMapper;
	
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public AmusementParkResource save(@Valid @RequestBody AmusementPark amusementPark) {    	
    	amusementPark.getAddress().setAmusementPark(amusementPark);
        return amusementParkMapper.toResource(amusementParkService.save(amusementPark));
    }
    
    @GetMapping
    public PagedResources<AmusementParkResource> findAllPaged(@PageableDefault Pageable pageable){
    	return amusementParkMapper.toPagedResources(amusementParkService.findAllFetchAddress(pageable));
    }

    @GetMapping("/{amusementParkId}")
    public AmusementParkResource findOne(@PathVariable Long amusementParkId) {
        return amusementParkMapper.toResource(amusementParkService.findByIdFetchAddress(amusementParkId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{amusementParkId}")
    public void delete(@PathVariable Long amusementParkId) {
        amusementParkService.delete(amusementParkId);
    }
}
