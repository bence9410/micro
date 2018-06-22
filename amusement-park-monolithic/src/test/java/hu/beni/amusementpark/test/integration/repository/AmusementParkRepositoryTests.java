package hu.beni.amusementpark.test.integration.repository;

import static hu.beni.amusementpark.helper.MySQLStatementCountValidator.reset;
import static hu.beni.amusementpark.helper.ValidEntityFactory.createAmusementParkWithAddress;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.repository.AmusementParkRepository;

public class AmusementParkRepositoryTests extends AbstractRepositoryTests {

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

	@Before
	public void setUp() {
		amusementParkRepository.deleteAll();
		reset();
	}

	@Test
	public void test() {
		assertStatements();

		save();

		incrementCapitalById();

		decreaseCapitalById();

		findById();

		findByIdReadOnlyId();

		findByIdReadOnlyIdAndEntranceFee();

		findByIdReadOnlyIdAndCapitalAndTotalArea();

		findByIdFetchAddress();

		findAllFetchAddress();

		deleteById();
	}

	private void save() {
		amusementPark = amusementParkRepository.save(createAmusementParkWithAddress());
		amusementParkId = amusementPark.getId();
		insert += 2;
		incrementSelectIfOracleDBProfileActive();
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

	private void findByIdFetchAddress() {
		AmusementPark foundAmusementPark = amusementParkRepository.findByIdFetchAddress(amusementParkId).get();
		assertEquals(amusementPark, foundAmusementPark);
		assertAddressCityNotNull(foundAmusementPark);
		select++;
		assertStatements();
	}

	private void findAllFetchAddress() {
		AmusementPark foundAmusementPark = amusementParkRepository.findAllFetchAddress(PageRequest.of(0, 10))
				.getContent().get(0);
		assertEquals(amusementPark, foundAmusementPark);
		assertAddressCityNotNull(foundAmusementPark);
		select++;
		assertStatements();
	}

	private void assertAddressCityNotNull(AmusementPark amusementPark) {
		assertNotNull(amusementPark.getAddress().getCity());
	}

	private void deleteById() {
		amusementParkRepository.deleteById(amusementParkId);
		select += 4;
		delete += 3;
		assertStatements();
	}

}
