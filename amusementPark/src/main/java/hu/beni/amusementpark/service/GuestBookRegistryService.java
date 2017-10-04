package hu.beni.amusementpark.service;

import hu.beni.amusementpark.entity.GuestBook;

public interface GuestBookService {
	
	GuestBook findOne(Long guestBookId);
	
	GuestBook writeInGuestBook(Long amusementParkId, Long visitorId, String textOfRegistry);
	
}
