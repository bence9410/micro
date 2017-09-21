package hu.beni.amusementpark.test.integration.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;
import static org.junit.Assert.*;

import java.sql.Timestamp;
import java.time.Instant;

import hu.beni.amusementpark.entity.Address;
import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.entity.GuestBook;
import hu.beni.amusementpark.entity.Visitor;
import hu.beni.amusementpark.service.AmusementParkService;
import hu.beni.amusementpark.service.GuestBookService;
import hu.beni.amusementpark.service.VisitorService;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
public class GuestBookServiceTests {

	@Autowired
	private AmusementParkService amusementParkService;

	@Autowired
	private VisitorService visitorService;

	@Autowired
	private GuestBookService guestBookService;

	@Test
	public void test() {
		AmusementPark amusementPark = amusementParkService.save(
				AmusementPark.builder().capital(100).entranceFee(10).address(Address.builder().build()).build());
		Long amusementParkId = amusementPark.getId();

		Visitor visitor = visitorService.enterPark(amusementParkId, Visitor.builder().spendingMoney(50).build());
		Long visitorId = visitor.getId();

		String textOfRegistry = "Nagyon jó!";

		Long guestBookId = guestBookService.writeInGuestBook(amusementParkId, visitorId, textOfRegistry).getId();
		GuestBook guestBook = guestBookService.findOne(guestBookId);
		
		assertEquals(textOfRegistry, guestBook.getTextOfRegistry());
		assertTrue(guestBook.getDateOfRegistry().before(Timestamp.from(Instant.now())));
	}

}
