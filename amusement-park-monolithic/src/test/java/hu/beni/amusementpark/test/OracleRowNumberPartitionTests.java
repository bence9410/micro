package hu.beni.amusementpark.test;

import static hu.beni.amusementpark.constants.SpringProfileConstants.ORACLE_DB;
import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.persistence.EntityManager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.entity.GuestBookRegistry;
import hu.beni.amusementpark.entity.Visitor;
import hu.beni.amusementpark.helper.ValidEntityFactory;
import hu.beni.amusementpark.repository.AmusementParkRepository;
import hu.beni.amusementpark.repository.GuestBookRegistryRepository;
import hu.beni.amusementpark.repository.VisitorRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@ActiveProfiles(ORACLE_DB)
public class OracleRowNumberPartitionTests {

	private static final String SELECT_ALL_VISITOR_NAME_AND_LAST_GUEST_BOOK_REGISTRY = "select visitor.name, "
			+ "guestBookRegistry.text_of_registry from Visitor visitor join (select * from (select row_number() "
			+ "over (partition by visitor_id order by date_of_registry desc) rn, gbr.* from guest_book_registry "
			+ "gbr) where rn = 1) guestBookRegistry on visitor.id = guestBookRegistry.visitor_id";

	@Autowired
	private AmusementParkRepository amusementParkRepository;

	@Autowired
	private VisitorRepository visitorRepository;

	@Autowired
	private GuestBookRegistryRepository guestBookRegistryRepository;

	@Autowired
	private EntityManager entityManager;

	private GuestBookRegistry g1;
	private GuestBookRegistry g2;

	@Test
	public void test() {

		createSamlpeData();

		List<Object[]> result = executeQuery();

		Object[] row = result.get(0);

		assertEquals(g1.getVisitor().getName(), row[0]);
		assertEquals(g1.getTextOfRegistry(), row[1]);

		row = result.get(1);

		assertEquals(g2.getVisitor().getName(), row[0]);
		assertEquals(g2.getTextOfRegistry(), row[1]);
	}

	private void createSamlpeData() {
		AmusementPark amusementPark = amusementParkRepository.save(ValidEntityFactory.createAmusementParkWithAddress());
		Visitor v1 = ValidEntityFactory.createVisitor();
		Visitor v2 = ValidEntityFactory.createVisitor();
		v1.setAmusementPark(amusementPark);
		v1.setName("jenike");
		v2.setAmusementPark(amusementPark);

		v1 = visitorRepository.save(v1);
		v2 = visitorRepository.save(v2);

		guestBookRegistryRepository.save(createGuestBook("asddd", amusementPark, v1));
		guestBookRegistryRepository.save(createGuestBook("asddd", amusementPark, v1));
		guestBookRegistryRepository.save(createGuestBook("dsaaa", amusementPark, v2));

		g1 = guestBookRegistryRepository.save(createGuestBook("csodas", amusementPark, v1));
		g2 = guestBookRegistryRepository.save(createGuestBook("pompas", amusementPark, v2));
	}

	private GuestBookRegistry createGuestBook(String textOfRegistry, AmusementPark amusementPark, Visitor visitor) {
		return GuestBookRegistry.builder().textOfRegistry(textOfRegistry).amusementPark(amusementPark).visitor(visitor)
				.build();
	}

	@SuppressWarnings("unchecked")
	private List<Object[]> executeQuery() {
		return entityManager.createNativeQuery(SELECT_ALL_VISITOR_NAME_AND_LAST_GUEST_BOOK_REGISTRY).getResultList();
	}

}
