package hu.beni.amusementpark;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.entity.Visitor;
import hu.beni.amusementpark.repository.ActiveVisitorRepository;
import hu.beni.amusementpark.repository.AmusementParkRepository;
import hu.beni.amusementpark.repository.VisitorRepository;
import hu.beni.amusementpark.service.VisitorService;

import static hu.beni.amusementpark.test.ValidEntityFactory.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
public class ActiveVisitorTests {
	
	@Autowired
	private AmusementParkRepository amusementParkRepository;
	
	@Autowired
	private VisitorRepository visitorRepository;
	
	@Autowired
	private VisitorService visitorService;
	
	@Autowired
	private ActiveVisitorRepository activeVisitorRepository;
	
	@Test
	public void test() {
		AmusementPark amusementPark = createAmusementPark();
		amusementPark.setAddress(createAddress());
		Long amusementParkId = amusementParkRepository.save(amusementPark).getId();
		
		Visitor visitor = createVisitor();
		Long visitorId = visitorService.enterPark(amusementParkId, visitor).getId();
		
		
		
		
		
		
	}

}
