package hu.beni.amusementpark.test.integration.service;

import static hu.beni.amusementpark.constants.ErrorMessageConstants.NO_AMUSEMENT_PARK_WITH_ID;
import static hu.beni.amusementpark.constants.ErrorMessageConstants.NO_ARCHIVE_SEND_TYPE;
import static hu.beni.amusementpark.helper.ValidEntityFactory.createAmusementParkWithAddress;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import org.hibernate.LazyInitializationException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.support.TransactionTemplate;

import hu.beni.amusementpark.entity.Address;
import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.exception.AmusementParkException;
import hu.beni.amusementpark.repository.AmusementParkRepository;
import hu.beni.amusementpark.service.AmusementParkService;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
public class AmusementParkServiceIntegrationTests {

	@Autowired
	private Environment environment;

	@Autowired
	private TransactionTemplate transactionTemplate;

	@Autowired
	private AmusementParkService amusementParkService;

	@Autowired
	private AmusementParkRepository amusementParkRepository;

	@Before
	public void setUp() {
		amusementParkRepository.deleteAll();
	}

	@Test
	public void test() {
		AmusementPark amusementPark = amusementParkService.save(createAmusementParkWithAddress());
		Long amusementParkId = amusementPark.getId();

		AmusementPark foundAmusementPark = amusementParkService.findByIdFetchAddress(amusementParkId);

		assertEquals(amusementPark, foundAmusementPark);
		assertEquals(amusementPark.getAddress(), foundAmusementPark.getAddress());

		if (environment.getActiveProfiles().length == 0) {
			assertThatThrownBy(() -> amusementParkService.delete(amusementParkId))
					.isInstanceOf(AmusementParkException.class).hasMessage(NO_ARCHIVE_SEND_TYPE);
			amusementParkService.findByIdFetchAddress(amusementParkId);
		}

		amusementParkRepository.deleteById(amusementParkId);
		assertThatThrownBy(() -> amusementParkService.findByIdFetchAddress(amusementParkId))
				.isInstanceOf(AmusementParkException.class).hasMessage(NO_AMUSEMENT_PARK_WITH_ID);
	}

	@Test
	public void pageAndSortTest() {
		createNineAmusementParkWithAscendantCapital();

		Pageable pageable = PageRequest.of(0, 5, Sort.by("capital"));

		Page<AmusementPark> firstPage = amusementParkService.findAllFetchAddress(pageable);
		Page<AmusementPark> lastPage = amusementParkService.findAllFetchAddress(firstPage.nextPageable());

		assertTrue(lastPage.isLast());

		int minCapital = firstPage.getContent().get(0).getCapital();
		int maxCapital = lastPage.getContent().get(lastPage.getNumberOfElements() - 1).getCapital();

		Consumer<AmusementPark> minMaxCapitalAsserter = minMaxCapitalAsserter(minCapital, maxCapital);

		firstPage.forEach(minMaxCapitalAsserter);
		lastPage.forEach(minMaxCapitalAsserter);
	}

	@Test
	public void specificationTest() {
		createNineAmusementParkWithAscendantCapital();

		String name = "asd";
		int capital = 2000;

		AmusementPark amusementPark = createAmusementParkWithAddress();
		amusementPark.setName(name + "123");
		amusementPark.getAddress().setCity(name + "45");
		amusementParkService.save(amusementPark);

		amusementPark = createAmusementParkWithAddress();
		amusementPark.setName(name + "67");
		amusementPark.getAddress().setCity(name + "89");
		amusementParkService.save(amusementPark);

		amusementPark = amusementParkService.findOne(fieldLikeParam(AmusementPark.class, "name", name + "1%"));
		assertNotNull(amusementPark);
		assertTrue(amusementPark.getName().startsWith(name + "1"));

		Specification<AmusementPark> nameLikeAndAddressCityLikeAndCapitalGreaterThan = Specification
				.where(fieldLikeParam(AmusementPark.class, "name", name + "%"))
				.and(fieldLikeParam(AmusementPark.class, "address.city", name + "%"))
				.and(fieldGreaterThanParam(AmusementPark.class, "capital", capital));

		List<AmusementPark> lazyAmusementParks = amusementParkService
				.findAll(nameLikeAndAddressCityLikeAndCapitalGreaterThan);
		assertFalse(lazyAmusementParks.isEmpty());

		AmusementPark lazyAmusementPark = lazyAmusementParks.get(0);

		Address lazyAddress = lazyAmusementPark.getAddress();

		assertNotNull(lazyAddress.getId());
		assertEquals(lazyAmusementPark.getId(), lazyAddress.getId());

		assertThatThrownBy(() -> lazyAddress.getCity()).isInstanceOf(LazyInitializationException.class);

		transactionTemplate.execute(status -> {
			List<AmusementPark> amusementParks = amusementParkService
					.findAll(nameLikeAndAddressCityLikeAndCapitalGreaterThan);
			assertFalse(amusementParks.isEmpty());

			for (AmusementPark a : amusementParks) {
				assertTrue(a.getName().startsWith(name) && a.getAddress().getCity().startsWith(name)
						&& a.getCapital() > capital);
			}
			return null;
		});
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

	private <T> Specification<T> fieldLikeParam(Class<T> clazz, String fieldName, String param) {
		return (Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> cb.like(createPath(root, fieldName),
				param);
	}

	private <T> Specification<T> fieldGreaterThanParam(Class<T> clazz, String fieldName, int param) {
		return (Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> cb.greaterThan(createPath(root, fieldName),
				param);
	}

	private <T, Y> Path<Y> createPath(Root<T> root, String fieldName) {
		Path<Y> path = null;
		if (fieldName.indexOf('.') == -1) {
			path = root.get(fieldName);
		} else {
			String[] parts = fieldName.split("\\.");
			path = root.get(parts[0]);
			for (int i = 1; i < parts.length; i++) {
				path = path.get(parts[i]);
			}
		}
		return path;
	}
}