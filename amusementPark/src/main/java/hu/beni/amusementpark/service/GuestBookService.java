package hu.beni.amusementpark.service;

import static hu.beni.amusementpark.constants.ErrorMessageConstants.NO_AMUSEMENT_PARK_WITH_ID;
import static hu.beni.amusementpark.constants.ErrorMessageConstants.NO_VISITOR_IN_PARK_WITH_ID;
import static hu.beni.amusementpark.exception.ExceptionUtil.exceptionIfNull;

import java.sql.Timestamp;
import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.entity.GuestBook;
import hu.beni.amusementpark.entity.Visitor;
import hu.beni.amusementpark.repository.AmusementParkRepository;
import hu.beni.amusementpark.repository.GuestBookRepository;
import hu.beni.amusementpark.repository.VisitorRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class GuestBookService {
	
	private final AmusementParkRepository amusementParkRepository;
	private final VisitorRepository visitorRepository;
	private final GuestBookRepository guestBookRepository;
	
	public GuestBook findOne(Long guestBookId) {
		return guestBookRepository.findOne(guestBookId);
	}
	
	public GuestBook writeInGuestBook(Long amusementParkId, Long visitorId, String textOfRegistry) {
		AmusementPark amusementPark = amusementParkRepository.findByIdReadOnlyId(amusementParkId);
		exceptionIfNull(amusementPark, NO_AMUSEMENT_PARK_WITH_ID);
		Visitor visitor = visitorRepository.findByAmusementParkIdAndVisitorIdReadOnlyId(amusementParkId, visitorId);
		exceptionIfNull(visitor, NO_VISITOR_IN_PARK_WITH_ID);
		return guestBookRepository.save(GuestBook.builder().textOfRegistry(textOfRegistry)
				.dateOfRegistry(Timestamp.from(Instant.now()))
				.amusementPark(amusementPark).visitor(visitor).build());
	}
	
	
}
