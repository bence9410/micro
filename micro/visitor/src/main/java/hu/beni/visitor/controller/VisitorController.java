package hu.beni.visitor.controller;

import static java.util.stream.Collectors.toList;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hu.beni.visitor.entity.Message;
import hu.beni.visitor.repository.MessageRepository;

@RestController
@RequestMapping("/visitor")
public class VisitorController {

	private final MessageRepository messageRepository;
	private final String hello;

	public VisitorController(MessageRepository messageRepository, @Value("${server.port}") int serverPort) {
		this.messageRepository = messageRepository;
		hello = String.format("Hello from %d Visitor!", serverPort);
	}

	@GetMapping("/hello")
	public String hello() {
		return hello;
	}

	@GetMapping
	@PreAuthorize("hasRole('ADMIN')")
	public List<String> getMessages() {
		return messageRepository.findAll().stream().map(Message::getContent).collect(toList());
	}

}
