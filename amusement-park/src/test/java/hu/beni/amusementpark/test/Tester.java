package hu.beni.amusementpark.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.helper.ValidEntityFactory;
import hu.beni.amusementpark.repository.AmusementParkRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
public class Tester {
	
	@Autowired
	private AmusementParkRepository amusementParkRepository;
	
	@Test
	public void test() {
		
		AmusementPark amusementPark = ValidEntityFactory.createAmusementParkWithAddress();
		
		amusementParkRepository.save(amusementPark);
		amusementPark.setId(null);
		amusementPark.getAddress().setId(null);
		amusementParkRepository.save(amusementPark);
		
		Page<AmusementPark> amusementParks = amusementParkRepository.findAllFetchAddress(PageRequest.of(0, 2));
		
		assertTrue(amusementParks.stream()
				.map(park -> park.getAddress().getCity()).allMatch(c -> c != null));
		assertEquals(2, amusementParks.getContent().size());
		
		
		
	}
	

}
