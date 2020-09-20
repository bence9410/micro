package hu.beni.amusementpark.controller;

import static java.util.stream.Collectors.toList;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hu.beni.amusementpark.client.VisitorClient;
import hu.beni.amusementpark.entity.Message;
import hu.beni.amusementpark.repository.MessageRepository;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/amusement-park")
@RequiredArgsConstructor
public class AmusementParkController {

	private final MessageRepository messageRepository;
	private final VisitorClient visitorClient;

	@GetMapping("/hello")
	public String hello() {
		return "Hello from AmusementPark!";
	}

	@GetMapping
	@PreAuthorize("hasRole('ADMIN')")
	public List<String> getMessages() {
		List<String> results = messageRepository.findAll().stream().map(Message::getContent).collect(toList());
		try {
			results.addAll(visitorClient.getMessages());
		} catch (Exception e) {
		}
		return results;
	}

}
