package hu.beni.amusementpark.test.integration;

import static hu.beni.amusementpark.helper.ValidEntityFactory.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
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
import org.springframework.test.context.junit4.SpringRunner;

import hu.beni.amusementpark.entity.Address;
import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.exception.AmusementParkException;
import hu.beni.amusementpark.repository.AmusementParkRepository;
import hu.beni.amusementpark.service.AmusementParkService;

import static hu.beni.amusementpark.constants.ErrorMessageConstants.NO_ARCHIVE_SEND_TYPE;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class AmusementParkServiceIntegrationTests {

	@Autowired
	private AmusementParkService amusementParkService;

	@Autowired
	private AmusementParkRepository amusementParkRepository;

	@Test
	public void test() {
		Address address = createAddress();
		AmusementPark amusementPark = createAmusementPark();
		address.setAmusementPark(amusementPark);
		amusementPark.setAddress(address);

		AmusementPark createdAmusementPark = amusementParkService.save(amusementPark);
		assertNotNull(createdAmusementPark);
		Long id = createdAmusementPark.getId();
		amusementPark.setId(id);
		address.setId(createdAmusementPark.getAddress().getId());

		assertNotNull(id);
		assertNotNull(address.getId());

		AmusementPark readAmusementPark = amusementParkService.findOne(id);
		assertEquals(amusementPark, readAmusementPark);

		assertThatThrownBy(() -> amusementParkService.delete(id))
			.isInstanceOf(AmusementParkException.class).hasMessage(NO_ARCHIVE_SEND_TYPE);
		assertNotNull(amusementParkService.findOne(id));
		
		amusementParkRepository.delete(id);
		assertNull(amusementParkService.findOne(id));
	}

	@Test
	public void pageAndSortTest() {
		amusementParkRepository.deleteAll();
		createNineAmusementParkWithAscendantCapital();

		Pageable pageable = new PageRequest(0, 5, new Sort("capital"));

		Page<AmusementPark> firstPage = amusementParkService.findAll(pageable);
		Page<AmusementPark> lastPage = amusementParkService.findAll(firstPage.nextPageable());

		assertTrue(lastPage.isLast());

		int minCapital = firstPage.getContent().get(0).getCapital();
		int maxCapital = lastPage.getContent().get(lastPage.getNumberOfElements() - 1).getCapital();

		Consumer<? super AmusementPark> minMaxCapitalAsserter = minMaxCapitalAsserter(minCapital, maxCapital);

		firstPage.forEach(minMaxCapitalAsserter);
		lastPage.forEach(minMaxCapitalAsserter);

		amusementParkRepository.deleteAll();
	}

	@Test
	public void specificationTest() {
		amusementParkRepository.deleteAll();
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

		amusementPark = amusementParkService.findOne(fieldLikeParam(AmusementPark.class, "name", name+"1%"));
		assertNotNull(amusementPark);
		assertTrue(amusementPark.getName().startsWith(name+"1"));
		
		Specification<AmusementPark> nameLikeAndCapitalGreaterThan = Specifications
				.where(fieldLikeParam(AmusementPark.class, "name", name + "%"))
				.and(fieldLikeParam(AmusementPark.class, "address.city", name + "%"))
				.and(fieldGreaterThanParam(AmusementPark.class, "capital", capital));

		List<AmusementPark> amusementParks = amusementParkService.findAll(nameLikeAndCapitalGreaterThan);
		assertFalse(amusementParks.isEmpty());

		for (AmusementPark a : amusementParks) {
			assertTrue(a.getName().startsWith(name) && a.getCapital() > capital);
		}
		
		amusementParkRepository.deleteAll();
	}

	private void createNineAmusementParkWithAscendantCapital() {
		IntStream.range(1000, 1009).forEach(i -> {
			AmusementPark amusementPark = createAmusementParkWithAddress();
			amusementPark.setCapital(i);
			amusementParkService.save(amusementPark);
		});
	}

	private Consumer<? super AmusementPark> minMaxCapitalAsserter(int min, int max) {
		return a -> assertTrue(min <= a.getCapital() && max >= a.getCapital());
	}

	private <T> Specification<T> fieldLikeParam(Class<T> clazz, String fieldName, String param) {
		return (Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> cb.like(getPathIfFieldNameContainsDot(root, fieldName), param);
	}

	private <T> Specification<T> fieldGreaterThanParam(Class<T> clazz, String fieldName, int param) {
		return (Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> cb.greaterThan(getPathIfFieldNameContainsDot(root, fieldName), param);
	}
	
	private <T,Y> Path<Y> getPathIfFieldNameContainsDot(Root<T> root, String fieldName){
		String[] parts = fieldName.split("\\.");
		Stream.of(parts).forEach(s -> System.out.println(s));
		Path<Y> path = root.get(parts[0]);
		for(int i = 1; i < parts.length; i++) {
			path = path.get(parts[i]);
		}
		return path;
	}
}