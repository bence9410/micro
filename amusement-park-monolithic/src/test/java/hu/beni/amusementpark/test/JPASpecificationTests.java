package hu.beni.amusementpark.test;

import static hu.beni.amusementpark.helper.ValidEntityFactory.createAmusementParkWithAddress;
import static hu.beni.amusementpark.helper.ValidEntityFactory.createMachine;
import static hu.beni.amusementpark.helper.ValidEntityFactory.createVisitor;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.PostConstruct;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringRunner;

import hu.beni.amusementpark.constants.StringParamConstants;
import hu.beni.amusementpark.entity.Address_;
import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.entity.AmusementPark_;
import hu.beni.amusementpark.entity.GuestBookRegistry;
import hu.beni.amusementpark.entity.Machine;
import hu.beni.amusementpark.entity.Machine_;
import hu.beni.amusementpark.entity.Visitor;
import hu.beni.amusementpark.repository.AmusementParkRepository;
import hu.beni.amusementpark.repository.GuestBookRegistryRepository;
import hu.beni.amusementpark.repository.MachineRepository;
import hu.beni.amusementpark.repository.VisitorRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
public class JPASpecificationTests {

	private static final AmusementPark BASE_AMUSEMENT_PARK = createAmusementParkWithAddress();
	private static List<AmusementPark> amusementParksWithMachineForChildren;

	@Autowired
	private AmusementParkRepository amusementParkRepository;

	@Autowired
	private MachineRepository machineRepository;

	@Autowired
	private VisitorRepository visitorRepository;

	@Autowired
	private GuestBookRegistryRepository guestBookRegistryRepository;

	@PostConstruct
	public void init() {
		if (amusementParksWithMachineForChildren == null) {
			createSampleData();
		}
	}

	private void createSampleData() {
		List<AmusementPark> amusementParks = IntStream.range(1, 11).mapToObj(i -> {
			AmusementPark amusementPark = createAmusementParkWithAddress();
			amusementPark.setName(amusementPark.getName() + i * i); // name1, ... name16, ... name49, ... name100
			amusementPark.setCapital(amusementPark.getCapital() * i); // 3000, 6000, ... 30000
			return amusementPark;
		}).map(amusementParkRepository::save).collect(Collectors.toList());

		Visitor v1 = visitorRepository.save(createVisitor());
		Visitor v2 = visitorRepository.save(createVisitor());

		amusementParksWithMachineForChildren = new ArrayList<>();
		for (int i = 0; i < amusementParks.size(); i++) {
			AmusementPark amusementPark = amusementParks.get(i);
			addMachine(amusementPark, false);
			addGuestBookRegistry(amusementPark, v1);

			if (i == 2 || i == 7) {
				addMachine(amusementPark, true);
				addMachine(amusementPark, true);
				addGuestBookRegistry(amusementPark, v2);
				amusementParksWithMachineForChildren.add(amusementPark);
			}
		}
	}

	private void addMachine(AmusementPark amusementPark, boolean machineForChildren) {
		Machine machine = createMachine();
		machine.setAmusementPark(amusementPark);
		if (machineForChildren) {
			machine.setMinimumRequiredAge(10);
		}
		machineRepository.save(machine);
	}

	private void addGuestBookRegistry(AmusementPark amusementPark, Visitor visitor) {
		guestBookRegistryRepository
				.save(GuestBookRegistry.builder().textOfRegistry(StringParamConstants.OPINION_ON_THE_PARK)
						.amusementPark(amusementPark).visitor(visitor).build());
	}

	@Test(expected = IncorrectResultSizeDataAccessException.class)
	public void findOneNotRetunsUniqueResult() {
		amusementParkRepository.findOne((Root<AmusementPark> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> cb
				.like(root.get(AmusementPark_.name), BASE_AMUSEMENT_PARK.getName() + "1%"));
	}

	@Test
	public void findOneCapitalGreaterThan() {
		int capital = 28000;
		AmusementPark result = amusementParkRepository.findOne((Root<AmusementPark> root, CriteriaQuery<?> query,
				CriteriaBuilder cb) -> cb.gt(root.get(AmusementPark_.capital), capital)).get();
		assertTrue(result.getCapital() > capital);
	}

	@Test
	public void findOneNameLikeAndCapitalGreaterThan() {
		String name = BASE_AMUSEMENT_PARK.getName() + "1";
		int capital = 14000;

		AmusementPark amusementPark = amusementParkRepository.findOne((Root<AmusementPark> root, CriteriaQuery<?> query,
				CriteriaBuilder cb) -> cb.and(cb.like(root.get(AmusementPark_.name), name + "%"),
						cb.gt(root.get(AmusementPark_.capital), capital)))
				.get();

		assertTrue(amusementPark.getName().startsWith(name));
		assertTrue(amusementPark.getCapital() > capital);
	}

	@Test
	public void findOneNameLikeAndCapitalGreaterThanAndAddressCityLike() {
		int capital = 14000;

		AmusementPark amusementPark = amusementParkRepository
				.findOne((Root<AmusementPark> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> cb.and(
						cb.like(root.get(AmusementPark_.name), BASE_AMUSEMENT_PARK.getName() + "1%"),
						cb.gt(root.get(AmusementPark_.capital), capital),
						cb.like(root.get(AmusementPark_.address).get(Address_.city), "Buda%")))
				.get();

		assertTrue(amusementPark.getName().startsWith(BASE_AMUSEMENT_PARK.getName() + "1"));
		assertTrue(amusementPark.getCapital() > capital);
		assertTrue(amusementPark.getAddress().getCity().startsWith("Buda"));
	}

	@Test
	public void findAllWithMachineForChildren() {

		Specification<AmusementPark> spec = (Root<AmusementPark> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
			Subquery<Machine> subQuery = query.subquery(Machine.class);
			Root<Machine> subRoot = subQuery.from(Machine.class);
			subQuery.select(subRoot);
			subQuery.where(
					cb.equal(root.get(AmusementPark_.id), subRoot.get(Machine_.amusementPark).get(AmusementPark_.id)),
					cb.lt(subRoot.get(Machine_.minimumRequiredAge), 18));
			query.orderBy(cb.asc(root.get(AmusementPark_.id)));
			return cb.exists(subQuery);
		};

		List<AmusementPark> resultWithSubQuery = amusementParkRepository.findAll(spec);

		spec = (Root<AmusementPark> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
			Join<AmusementPark, Machine> joinMachines = root.join(AmusementPark_.machines);
			joinMachines.on(cb.lt(joinMachines.get(Machine_.minimumRequiredAge), 18));
			query.distinct(true);
			query.orderBy(cb.asc(root.get(AmusementPark_.id)));
			return null;
		};

		List<AmusementPark> resultWithJoin = amusementParkRepository.findAll(spec);

		assertEquals(amusementParksWithMachineForChildren.size(), resultWithJoin.size());
		assertEquals(amusementParksWithMachineForChildren.size(), resultWithSubQuery.size());

		assertEquals(amusementParksWithMachineForChildren, resultWithSubQuery);
		assertEquals(amusementParksWithMachineForChildren, resultWithJoin);

	}
}
