package hu.beni.amusementpark.test;

import static hu.beni.amusementpark.constants.RabbitMQConstants.RABBIT_MQ_PROFILE_NAME;
import static hu.beni.amusementpark.helper.ValidEntityFactory.createAmusementParkWithAddress;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import hu.beni.amusementpark.helper.RabbitMQReceiverConfig.Receiver;
import hu.beni.amusementpark.service.AmusementParkService;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE, properties = {"spring.profiles.active="+ RABBIT_MQ_PROFILE_NAME})
public class RabbitMQArchiveSenderTests {
		
	@Autowired
	private AmusementParkService amusementParkService;
	
	@Autowired
	private Receiver receiver;
	
	@Test
	public void test() throws InterruptedException {
		amusementParkService.delete(amusementParkService.save(createAmusementParkWithAddress()).getId());
		assertTrue(receiver.getCountDownLatch().await(10000, TimeUnit.MILLISECONDS));
	}
}
