package hu.beni.amusementpark.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import org.springframework.hateoas.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hu.beni.amusementpark.entity.GuestBook;
import hu.beni.amusementpark.service.GuestBookService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(path = "amusementPark/{amusementParkId}/visitor/{visitorId}/guestBook")
@RequiredArgsConstructor
public class GuestBookController {
	
	private final GuestBookService guestBookService;
	
	@GetMapping("/{guestBookId}")
	public Resource<GuestBook> findOne(@PathVariable Long amusementParkId, @PathVariable Long visitorId, @PathVariable Long guestBookId){
		return createResource(amusementParkId, visitorId, guestBookService.findOne(guestBookId));
	}
	
	@PostMapping
	public Resource<GuestBook> writeInGuestBook(@PathVariable Long amusementParkId, @PathVariable Long visitorId, @RequestBody String textOfRegistry){
		return createResource(amusementParkId, visitorId, guestBookService.writeInGuestBook(amusementParkId, visitorId, textOfRegistry));
	}
	
	private Resource<GuestBook> createResource(Long amusementParkId, Long visitorId, GuestBook guestBook){
		return new Resource<>(guestBook, linkTo(methodOn(getClass()).findOne(amusementParkId, visitorId, guestBook.getId())).withSelfRel());
	}

}
