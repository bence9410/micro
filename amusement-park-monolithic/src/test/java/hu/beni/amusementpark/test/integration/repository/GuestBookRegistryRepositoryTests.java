package hu.beni.amusementpark.test.integration.repository;

import static hu.beni.amusementpark.constants.StringParamConstants.OPINION_ON_THE_PARK;
import static hu.beni.amusementpark.helper.ValidEntityFactory.createAmusementPark;
import static hu.beni.amusementpark.helper.ValidEntityFactory.createVisitor;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.entity.GuestBookRegistry;
import hu.beni.amusementpark.entity.Visitor;
import hu.beni.amusementpark.repository.AmusementParkRepository;
import hu.beni.amusementpark.repository.GuestBookRegistryRepository;
import hu.beni.amusementpark.repository.VisitorRepository;
import hu.beni.amusementpark.test.integration.AbstractStatementCounterTests;

public class GuestBookRegistryRepositoryTests extends AbstractStatementCounterTests {

	@Autowired
	private AmusementParkRepository amusementParkRepository;

	@Autowired
	private VisitorRepository visitorRepository;

	@Autowired
	private GuestBookRegistryRepository guestBookRegistryRepository;

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
		save();

		saveAll();

		findById();

		findAll();

		deleteById();

		deleteAll();
	}

	private void save() {
		GuestBookRegistry guestBookRegistryBeforeSave = createGuestBookRegistrySetAmusementParkAndVisitor();
		guestBookRegistry = guestBookRegistryRepository.save(guestBookRegistryBeforeSave);
		assertNotNull(guestBookRegistry.getId());
		assertEquals(guestBookRegistryBeforeSave, guestBookRegistry);
		assertTrue(guestBookRegistry.getDateOfRegistry().isBefore(LocalDateTime.now()));
		select++;
		insert++;
		incrementSelectIfOracleDBProfileActive();
		assertStatements();
	}

	private GuestBookRegistry createGuestBookRegistrySetAmusementParkAndVisitor() {
		return GuestBookRegistry.builder() //@formatter:off
				.textOfRegistry(OPINION_ON_THE_PARK)
				.amusementPark(amusementPark)
				.visitor(visitor).build(); //@formatter:on
	}

	private void saveAll() {
		guestBookRegistryRepository.saveAll(Arrays.asList(createGuestBookRegistrySetAmusementParkAndVisitor(),
				createGuestBookRegistrySetAmusementParkAndVisitor()));
		select++;
		insert += 2;
		incrementSelectIfOracleDBProfileActive(2);
		assertStatements();
	}

	private void findById() {
		assertEquals(guestBookRegistry, guestBookRegistryRepository.findById(guestBookRegistry.getId()).get());
		select++;
		assertStatements();
	}

	private void findAll() {
		assertTrue(guestBookRegistryRepository.findAll().contains(guestBookRegistry));
		select++;
		assertStatements();
	}

	private void deleteById() {
		guestBookRegistryRepository.deleteById(guestBookRegistry.getId());
		select++;
		delete++;
		assertStatements();
	}

	private void deleteAll() {
		guestBookRegistryRepository.deleteAll();
		select++;
		delete += 2;
		assertStatements();
	}

}
