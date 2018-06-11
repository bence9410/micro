package hu.beni.tester.properties;

import javax.annotation.PostConstruct;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties("tester")
public class ApplicationProperties {

	private DataProperties data;

	private NumberOfProperties numberOf;

	@PostConstruct
	public void init() {
		numberOf.setAmusementParks(numberOf.getAdmins() * numberOf.getAmusementParksPerAdmin());
	}

}
