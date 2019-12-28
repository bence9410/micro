package hu.beni.amusementpark.test.integration.service;

import static hu.beni.amusementpark.constants.StringParamConstants.OPINION_ON_THE_PARK;
import static hu.beni.amusementpark.helper.ValidEntityFactory.createAmusementPark;
import static hu.beni.amusementpark.helper.ValidEntityFactory.createVisitor;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.entity.GuestBookRegistry;
import hu.beni.amusementpark.entity.Visitor;
import hu.beni.amusementpark.repository.AmusementParkRepository;
import hu.beni.amusementpark.repository.VisitorRepository;
import hu.beni.amusementpark.service.GuestBookRegistryService;
import hu.beni.amusementpark.test.integration.AbstractStatementCounterTests;

public class GuestBookServiceIntegrationTests extends AbstractStatementCounterTests {

	@Autowired
	private GuestBookRegistryService guestBookService;

	@Autowired
	private AmusementParkRepository amusementParkRepository;

	@Autowired
	private VisitorRepository visitorRepository;

	private AmusementPark amusementPark;
	private Visitor visitor;
	private GuestBookRegistry guestBookRegistry;

	@Before
	public void setUp() {
		amusementPark = amusementParkRepository.save(createAmusementPark());
		visitor = createVisitor();
		visitor.setAmusementPark(amusementPark);
		visitor = visitorRepository.save(visitor);
		reset();
	}

	@Test
	public void test() {
		addRegistry();

		findOne();
	}

	private void addRegistry() {
		guestBookRegistry = guestBookService.addRegistry(amusementPark.getId(), visitor.getEmail(),
				OPINION_ON_THE_PARK);
		select += 2;
		insert++;
		incrementSelectIfOracleDBProfileActive();
		assertStatements();
	}

	private void findOne() {
		assertEquals(guestBookRegistry, guestBookService.findOne(guestBookRegistry.getId()));
		select++;
		assertStatements();
	}

}
