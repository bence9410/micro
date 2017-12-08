package hu.beni.amusementpark;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import hu.beni.amusementpark.entity.GuestBookRegistry;
import hu.beni.amusementpark.entity.Visitor;
import hu.beni.amusementpark.repository.GuestBookRegistryRepository;
import hu.beni.amusementpark.repository.VisitorRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class Valid {
	
	@Autowired
	private VisitorRepository visitorRepository;
	
	@Autowired
	private GuestBookRegistryRepository guestBookRegistryRepository;

	@Test
	public void test() {
		Visitor visitor = visitorRepository.save(Visitor.builder().name("").build());
		visitor.setName(null);
		guestBookRegistryRepository.save(GuestBookRegistry.builder().textOfRegistry("Nagyon jó!")
				.visitor(visitor).build());
		System.out.println(visitorRepository.findOne(visitor.getId()));
	}
		
}
