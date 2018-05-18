package hu.beni.amusementpark.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hu.beni.amusementpark.entity.Message;
import hu.beni.amusementpark.repository.MessageRepository;
import lombok.RequiredArgsConstructor;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/amusement-park")
@RequiredArgsConstructor
public class AmusementParkController {
	
	private final MessageRepository messageRepository;

	@GetMapping("/hello")
	public String hello() {
		return "Hello from AmusementPark!";
	}
	
	@GetMapping
	@PreAuthorize("hasRole('ADMIN')")
	public List<String> getMessages(){
		return messageRepository.findAll().stream().map(Message::getContent).collect(toList());
	}
	
}
