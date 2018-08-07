package hu.beni.amusementpark.test.integration.service;

import static hu.beni.amusementpark.helper.ValidEntityFactory.createAmusementParkWithAddress;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.function.Consumer;
import java.util.stream.IntStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.repository.AmusementParkRepository;
import hu.beni.amusementpark.service.AmusementParkService;
import hu.beni.amusementpark.test.integration.AbstractStatementCounterTests;

public class AmusementParkServiceIntegrationTests extends AbstractStatementCounterTests {

	@Autowired
	private AmusementParkService amusementParkService;

	@Autowired
	private AmusementParkRepository amusementParkRepository;

	private AmusementPark amusementPark;
	private Long amusementParkId;

	@Before
	public void setUp() {
		amusementParkRepository.deleteAll();
		createNineAmusementParkWithAscendantCapital();
		reset();
		assertStatements();
	}

	@After
	public void tearDown() {
		amusementParkRepository.deleteAll();
	}

	@Test
	public void test() {
		save();

		findByIdFetchAddress();
	}

	private void save() {
		AmusementPark amusementParkBeforeSave = createAmusementParkWithAddress();
		amusementPark = amusementParkService.save(amusementParkBeforeSave);
		amusementParkId = amusementPark.getId();
		assertNotNull(amusementParkId);
		assertEquals(amusementParkBeforeSave, amusementPark);
		assertEquals(amusementParkBeforeSave.getAddress(), amusementPark.getAddress());
		insert++;
		incrementSelectIfOracleDBProfileActive();
		assertStatements();
	}

	private void findByIdFetchAddress() {
		AmusementPark foundAmusementPark = amusementParkService.findByIdFetchAddress(amusementParkId);
		assertEquals(amusementPark, foundAmusementPark);
		assertEquals(amusementPark.getAddress(), foundAmusementPark.getAddress());
		select++;
		assertStatements();
	}

	@Test
	public void pageAndSortTest() {
		assertStatements();

		Page<AmusementPark> firstPage = findAllFetchAddress(PageRequest.of(0, 5, Sort.by("capital")));
		Page<AmusementPark> lastPage = findAllFetchAddress(firstPage.nextPageable());
		assertTrue(lastPage.isLast());

		int minCapital = firstPage.getContent().get(0).getCapital();
		int maxCapital = lastPage.getContent().get(lastPage.getNumberOfElements() - 1).getCapital();

		Consumer<AmusementPark> minMaxCapitalAsserter = minMaxCapitalAsserter(minCapital, maxCapital);

		firstPage.forEach(minMaxCapitalAsserter);
		lastPage.forEach(minMaxCapitalAsserter);
	}

	private Page<AmusementPark> findAllFetchAddress(Pageable pageable) {
		Page<AmusementPark> page = amusementParkService.findAllFetchAddress(pageable);
		if (page.isLast()) {
			select++;
		} else {
			select += 2;
		}
		assertStatements();
		return page;
	}

	private void createNineAmusementParkWithAscendantCapital() {
		IntStream.range(1000, 1009).forEach(i -> {
			AmusementPark amusementPark = createAmusementParkWithAddress();
			amusementPark.setCapital(i);
			amusementParkService.save(amusementPark);
		});
	}

	private Consumer<AmusementPark> minMaxCapitalAsserter(int min, int max) {
		return a -> assertTrue(min <= a.getCapital() && max >= a.getCapital());
	}

}