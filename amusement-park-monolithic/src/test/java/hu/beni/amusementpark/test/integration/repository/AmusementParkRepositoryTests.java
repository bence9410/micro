package hu.beni.amusementpark.test.integration.repository;

import static hu.beni.amusementpark.constants.SpringProfileConstants.ORACLE_DB;
import static hu.beni.amusementpark.helper.MySQLStatementCountValidator.assertSQLStatements;
import static hu.beni.amusementpark.helper.MySQLStatementCountValidator.reset;
import static hu.beni.amusementpark.helper.ValidEntityFactory.createAmusementParkWithAddress;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.repository.AmusementParkRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class AmusementParkRepositoryTests {

	@Autowired
	private Environment environment;

	@Autowired
	private TransactionTemplate transactionTemplate;

	@Autowired
	private AmusementParkRepository amusementParkRepository;

	private long insert;
	private long select;
	private long update;
	private long delete;

	private Long amusementParkId;

	private <T> T doInTransaction(TransactionCallback<T> transactionCallback) {
		return transactionTemplate.execute(transactionCallback);
	}

	private void assertStatements() {
		assertSQLStatements(select, insert, update, delete);
	}

	@Before
	public void setUp() {
		reset();
	}

	@Test
	public void test() {
		assertStatements();

		if (Arrays.asList(environment.getActiveProfiles()).contains(ORACLE_DB)) {
			select++;
		}

		save();

		incrementCapitalById();

		decreaseCapitalById();

		findById();

		findByIdReadOnlyId();

		findByIdReadOnlyIdAndEntranceFee();

		findByIdReadOnlyIdAndCapitalAndTotalArea();

		findByIdFetchAddress();

		findAllFetchAddress();

	}

	private void save() {
		amusementParkId = amusementParkRepository.save(createAmusementParkWithAddress()).getId();
		insert += 2;
		assertStatements();
	}

	private void incrementCapitalById() {
		doInTransaction(state -> {
			amusementParkRepository.incrementCapitalById(100, amusementParkId);
			return null;
		});
		update++;
		assertStatements();
	}

	private void decreaseCapitalById() {
		doInTransaction(state -> {
			amusementParkRepository.decreaseCapitalById(100, amusementParkId);
			return null;
		});
		update++;
		assertStatements();
	}

	private void findById() {
		amusementParkRepository.findById(amusementParkId);
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
		assertAddressCityNotNull(amusementParkRepository.findByIdFetchAddress(amusementParkId).get());
		select++;
		assertStatements();
	}

	private void findAllFetchAddress() {
		assertAddressCityNotNull(
				amusementParkRepository.findAllFetchAddress(PageRequest.of(0, 10)).getContent().get(0));
		select++;
		assertStatements();
	}

	private void assertAddressCityNotNull(AmusementPark amusementPark) {
		assertNotNull(amusementPark.getAddress().getCity());
	}

}
