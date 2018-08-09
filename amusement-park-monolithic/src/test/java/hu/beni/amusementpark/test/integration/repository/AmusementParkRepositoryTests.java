package hu.beni.amusementpark.test.integration.repository;

import static hu.beni.amusementpark.helper.ValidEntityFactory.createAmusementParkWithAddress;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.repository.AmusementParkRepository;
import hu.beni.amusementpark.test.integration.AbstractStatementCounterTests;

public class AmusementParkRepositoryTests extends AbstractStatementCounterTests {

	@Autowired
	private TransactionTemplate transactionTemplate;

	@Autowired
	private AmusementParkRepository amusementParkRepository;

	private AmusementPark amusementPark;
	private Long amusementParkId;
	private Integer ammount = 100;

	private <T> T doInTransaction(TransactionCallback<T> transactionCallback) {
		return transactionTemplate.execute(transactionCallback);
	}

	@Test
	public void test() {
		save();

		saveAll();

		incrementCapitalById();

		decreaseCapitalById();

		findById();

		findByIdReadOnlyId();

		findByIdReadOnlyIdAndEntranceFee();

		findByIdReadOnlyIdAndCapitalAndTotalArea();

		findAll();

		deleteById();

		deleteAll();
	}

	private void save() {
		AmusementPark amusementParkBeforeSave = createAmusementParkWithAddress();
		amusementPark = amusementParkRepository.save(amusementParkBeforeSave);
		amusementParkId = amusementPark.getId();
		assertNotNull(amusementParkId);
		assertEquals(amusementParkBeforeSave, amusementPark);
		assertEquals(amusementParkBeforeSave.getAddress(), amusementPark.getAddress());
		insert++;
		incrementSelectIfOracleDBProfileActive();
		assertStatements();
	}

	private void saveAll() {
		amusementParkRepository
				.saveAll(Arrays.asList(createAmusementParkWithAddress(), createAmusementParkWithAddress()));
		insert += 2;
		incrementSelectIfOracleDBProfileActive(2);
		assertStatements();
	}

	private void incrementCapitalById() {
		doInTransaction(state -> {
			amusementParkRepository.incrementCapitalById(ammount, amusementParkId);
			return null;
		});
		update++;
		assertStatements();
		assertCapitalIsIncremented();
	}

	private void assertCapitalIsIncremented() {
		assertEquals(amusementPark.getCapital() + ammount,
				amusementParkRepository.findById(amusementParkId).get().getCapital().intValue());
		select++;
		assertStatements();
	}

	private void decreaseCapitalById() {
		doInTransaction(state -> {
			amusementParkRepository.decreaseCapitalById(ammount, amusementParkId);
			return null;
		});
		update++;
		assertStatements();
	}

	private void findById() {
		assertEquals(amusementPark, amusementParkRepository.findById(amusementParkId).get());
		select++;
		assertStatements();
	}

	private void findByIdReadOnlyId() {
		amusementParkRepository.findByIdReadOnlyId(amusementParkId);
		select++;
		assertStatements();
	}

	private void findByIdReadOnlyIdAndEntranceFee() {
		amusementParkRepository.findByIdReadOnlyIdAndEntranceFee(amusementParkId);
		select++;
		assertStatements();
	}

	private void findByIdReadOnlyIdAndCapitalAndTotalArea() {
		amusementParkRepository.findByIdReadOnlyIdAndCapitalAndTotalArea(amusementParkId);
		select++;
		assertStatements();
	}

	private void findAll() {
		List<AmusementPark> amusementParks = amusementParkRepository.findAll(PageRequest.of(0, 10)).getContent();
		assertTrue(amusementParks.contains(amusementPark));
		select++;
		assertStatements();
	}

	private void deleteById() {
		amusementParkRepository.deleteById(amusementParkId);
		select += 4;
		delete++;
		assertStatements();
	}

	private void deleteAll() {
		amusementParkRepository.deleteAll();
		select += 7;
		delete += 2;
		assertStatements();
	}

}
