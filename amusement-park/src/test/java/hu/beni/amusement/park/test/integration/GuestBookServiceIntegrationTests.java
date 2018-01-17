package hu.beni.amusement.park.test.integration;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import hu.beni.amusement.park.entity.AmusementPark;
import hu.beni.amusement.park.entity.GuestBookRegistry;
import hu.beni.amusement.park.entity.Visitor;
import hu.beni.amusement.park.service.AmusementParkService;
import hu.beni.amusement.park.service.GuestBookRegistryService;
import hu.beni.amusement.park.service.VisitorService;

import static hu.beni.amusement.park.constants.StringParamConstants.OPINION_ON_THE_PARK;
import static hu.beni.amusement.park.helper.ValidEntityFactory.*;
import static org.junit.Assert.*;

import java.sql.Timestamp;
import java.time.Instant;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
public class GuestBookServiceIntegrationTests {

	@Autowired
	private AmusementParkService amusementParkService;

	@Autowired
	private VisitorService visitorService;

	@Autowired
	private GuestBookRegistryService guestBookService;

	@Test
	public void test() {
		AmusementPark amusementPark = createAmusementParkWithAddress();
		Long amusementParkId = amusementParkService.save(amusementPark).getId();
		
		Visitor visitor = createVisitor();
		Long visitorId = visitorService.signUp(visitor).getId();
		
		visitorService.enterPark(amusementParkId, visitorId, 200);

		String textOfRegistry = OPINION_ON_THE_PARK;

		Long guestBookRegistryId = guestBookService.addRegistry(amusementParkId, visitorId, textOfRegistry).getId();
		GuestBookRegistry guestBookRegistry = guestBookService.findOne(guestBookRegistryId);
		
		assertEquals(textOfRegistry, guestBookRegistry.getTextOfRegistry());
		assertTrue(guestBookRegistry.getDateOfRegistry().before(Timestamp.from(Instant.now())));
		
		visitorService.leavePark(amusementParkId, visitorId);
		
		assertNotNull(guestBookService.findOne(guestBookRegistryId));
	}

}
