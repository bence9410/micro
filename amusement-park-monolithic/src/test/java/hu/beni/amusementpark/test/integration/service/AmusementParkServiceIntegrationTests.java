package hu.beni.amusementpark.test.integration.service;

import static hu.beni.amusementpark.helper.ValidEntityFactory.createAmusementPark;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.stream.IntStream;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.service.AmusementParkService;
import hu.beni.amusementpark.test.integration.AbstractStatementCounterTests;

public class AmusementParkServiceIntegrationTests extends AbstractStatementCounterTests {

	@Autowired
	private AmusementParkService amusementParkService;

	private AmusementPark amusementPark;
	private Long amusementParkId;

	@Before
	public void setUp() {
		createNineAmusementParkWithAscendantCapital();
		reset();
	}

	@Test
	public void test() {
		save();

		findByIdFetchAddress();
	}

	private void save() {
		AmusementPark amusementParkBeforeSave = createAmusementPark();
		amusementPark = amusementParkService.save(amusementParkBeforeSave);
		amusementParkId = amusementPark.getId();
		assertNotNull(amusementParkId);
		assertEquals(amusementParkBeforeSave, amusementPark);
		insert++;
		incrementSelectIfOracleDBProfileActive();
		assertStatements();
	}

	private void findByIdFetchAddress() {
		AmusementPark foundAmusementPark = amusementParkService.findById(amusementParkId);
		assertEquals(amusementPark, foundAmusementPark);
		select++;
		assertStatements();
	}

	private void createNineAmusementParkWithAscendantCapital() {
		IntStream.range(1000, 1009).forEach(i -> {
			AmusementPark amusementPark = createAmusementPark();
			amusementPark.setCapital(i);
			amusementParkService.save(amusementPark);
		});
	}

}