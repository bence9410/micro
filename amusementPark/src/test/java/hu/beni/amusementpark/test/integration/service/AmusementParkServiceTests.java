package hu.beni.amusementpark.test.integration.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.function.Consumer;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import hu.beni.amusementpark.entity.Address;
import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.service.AmusementParkService;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class AmusementParkServiceTests {

	@Autowired
	private AmusementParkService amusementParkService;

	@Test
	public void test() {
		Address address = Address.builder().build();
		AmusementPark amusementPark = AmusementPark.builder().address(address).build();

		AmusementPark createdAmusementPark = amusementParkService.save(amusementPark);
		assertNotNull(createdAmusementPark);
		Long id = createdAmusementPark.getId();
		amusementPark.setId(id);
		address.setId(createdAmusementPark.getAddress().getId());

		assertNotNull(id);
		assertNotNull(address.getId());

		AmusementPark readAmusementPark = amusementParkService.findOne(id);
		assertEquals(amusementPark, readAmusementPark);

		amusementParkService.delete(id);
		assertNull(amusementParkService.findOne(id));
	}

	@Test
	@Sql({"classpath:dropCreateAmusementParkTable.sql", "classpath:amusementParkIntegrationTestsData.sql"})
	public void pageAndSortTest() {
		Pageable pageable = new PageRequest(0, 5, new Sort("capital"));

		Page<AmusementPark> firstPage = amusementParkService.findAll(pageable);
		Page<AmusementPark> lastPage = amusementParkService.findAll(firstPage.nextPageable());

		assertTrue(lastPage.isLast());

		int minCapital = firstPage.getContent().get(0).getCapital();
		int maxCapital = lastPage.getContent().get(lastPage.getNumberOfElements() - 1).getCapital();

		Consumer<? super AmusementPark> minMaxCapitalAsserter = minMaxCapitalAsserter(minCapital, maxCapital);

		firstPage.forEach(minMaxCapitalAsserter);
		lastPage.forEach(minMaxCapitalAsserter);
	}

	@Test
	@Sql({"classpath:dropCreateAmusementParkTable.sql", "classpath:amusementParkIntegrationTestsData.sql"})
	public void specificationTest() {
		String name = "asd";
		int capital = 3;

		Specification<AmusementPark> nameLikeAndCapitalGreaterThan = Specifications
				.where(fieldLikeParam(AmusementPark.class, "name", name + "%"))
				.and(fieldGreaterThanParam(AmusementPark.class, "capital", capital));

		List<AmusementPark> amusementParks = amusementParkService.findAll(nameLikeAndCapitalGreaterThan);
		assertFalse(amusementParks.isEmpty());

		for (AmusementPark a : amusementParks) {
			assertTrue(a.getName().startsWith(name) && a.getCapital() > capital);
		}
	}

	private Consumer<? super AmusementPark> minMaxCapitalAsserter(int min, int max) {
		return a -> assertTrue(min <= a.getCapital() && max >= a.getCapital());
	}

	private <T> Specification<T> fieldLikeParam(Class<T> clazz, String fieldName, String param) {
		return (Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> cb.like(root.get(fieldName), param);
	}

	private <T> Specification<T> fieldGreaterThanParam(Class<T> clazz, String fieldName, int param) {
		return (Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> cb.greaterThan(root.get(fieldName), param);
	}
}