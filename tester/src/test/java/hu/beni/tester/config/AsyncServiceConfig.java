package hu.beni.tester.config;

import static hu.beni.tester.TesterApplicationTests.NUMBER_OF_ADMINS;
import static hu.beni.tester.TesterApplicationTests.NUMBER_OF_VISITORS;
import static hu.beni.tester.constant.Constants.ADMIN;
import static hu.beni.tester.constant.Constants.VISITOR;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import hu.beni.clientsupport.Client;
import hu.beni.tester.service.AsyncService;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class AsyncServiceConfig {

	private final ApplicationContext ctx;

	@Bean
	public List<AsyncService> admins() {
		return createAsyncServices(NUMBER_OF_ADMINS, this::createAdminUsername);
	}

	@Bean
	public List<AsyncService> visitors() {
		return createAsyncServices(NUMBER_OF_VISITORS, this::createVisitorUsername);
	}

	private String createAdminUsername(int usernameIndex) {
		return ADMIN + usernameIndex;
	}

	private String createVisitorUsername(int usernameIndex) {
		return VISITOR + usernameIndex;
	}

	private List<AsyncService> createAsyncServices(int numberOfInstance, IntFunction<String> usernameProducer) {
		return createUsernameStream(numberOfInstance, usernameProducer).map(this::createAsyncService).collect(toList());

	}

	private Stream<String> createUsernameStream(int endExclusive, IntFunction<String> function) {
		return IntStream.range(0, endExclusive).mapToObj(function);
	}

	private AsyncService createAsyncService(String username) {
		return ctx.getBean(AsyncService.class, ctx.getBean(Client.class), username);
	}

}
