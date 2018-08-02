package hu.beni.amusementpark.test.rabbitmq;

import static hu.beni.amusementpark.constants.SpringProfileConstants.RABBIT_MQ;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import hu.beni.amusementpark.AmusementParkApplication;
import hu.beni.amusementpark.config.RabbitMQTestConfig;
import hu.beni.amusementpark.service.AmusementParkService;
import hu.beni.amusementpark.service.MachineService;
import hu.beni.amusementpark.service.VisitorService;

@RunWith(SpringRunner.class)
@ActiveProfiles(RABBIT_MQ)
@SpringBootTest(webEnvironment = WebEnvironment.NONE, classes = { AmusementParkApplication.class,
		RabbitMQTestConfig.class })
public abstract class AbstractRabbitMQTests {

	@Autowired
	protected AmusementParkService amusementParkService;

	@Autowired
	protected MachineService machineService;

	@Autowired
	protected VisitorService visitorService;

}
