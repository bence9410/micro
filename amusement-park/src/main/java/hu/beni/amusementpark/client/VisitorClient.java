package hu.beni.amusementpark.client;

import java.util.List;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("visitor")
@RequestMapping("/visitor")
public interface VisitorClient {

	@GetMapping
	List<String> getMessages();

}
