package hu.beni.amusementpark.test;

import static hu.beni.amusementpark.constants.RabbitMQConstants.RABBIT_MQ_PROFILE_NAME;
import static hu.beni.amusementpark.helper.ValidEntityFactory.createAmusementParkWithAddress;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import hu.beni.amusementpark.service.AmusementParkService;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE, properties = {"spring.profiles.active="+ RABBIT_MQ_PROFILE_NAME})
public class RabbitMQArchiveSenderTests {
		
	@Autowired
	private AmusementParkService amusementParkService;
	
	@Test
	public void test() {
		amusementParkService.delete(amusementParkService.save(createAmusementParkWithAddress()).getId());
	}
}
