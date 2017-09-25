package hu.beni.amusementpark.test.unit;

import static hu.beni.amusementpark.constants.ErrorMessageConstants.NO_AMUSEMENT_PARK_WITH_ID;
import static hu.beni.amusementpark.constants.ErrorMessageConstants.NO_VISITOR_IN_PARK_WITH_ID;
import static hu.beni.amusementpark.test.TestConstants.OPINION_ON_THE_PARK;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.entity.GuestBook;
import hu.beni.amusementpark.entity.Visitor;
import hu.beni.amusementpark.exception.AmusementParkException;
import hu.beni.amusementpark.repository.AmusementParkRepository;
import hu.beni.amusementpark.repository.GuestBookRepository;
import hu.beni.amusementpark.repository.VisitorRepository;
import hu.beni.amusementpark.service.GuestBookService;
import hu.beni.amusementpark.service.impl.GuestBookServiceImpl;

public class GuestBookServiceTests {

	private AmusementParkRepository amusementParkRepository;
	private VisitorRepository visitorRepository;
	private GuestBookRepository guestBookRepository;

	private GuestBookService guestBookService;

	@Before
	public void setUp() {
		amusementParkRepository = mock(AmusementParkRepository.class);
		visitorRepository = mock(VisitorRepository.class);
		guestBookRepository = mock(GuestBookRepository.class);
		guestBookService = new GuestBookServiceImpl(amusementParkRepository, visitorRepository, guestBookRepository);
	}

	@After
	public void verifyNoMoreInteractionsOnMocks() {
		verifyNoMoreInteractions(amusementParkRepository);
	}

	@Test
	public void findOnePositive() {
		GuestBook guestBook = GuestBook.builder().id(0L).build();
		Long guestBookId = guestBook.getId();

		when(guestBookRepository.findOne(guestBookId)).thenReturn(guestBook);

		assertEquals(guestBook, guestBookService.findOne(guestBookId));

		verify(guestBookRepository).findOne(guestBookId);
	}

	@Test
	public void writeInGuestBookNegativeNoAmusementPark() {
		Long amusementParkId = 0L;
		Long visitorId = 1L;
		String textOfRegistry = OPINION_ON_THE_PARK;

		assertThatThrownBy(() -> guestBookService.writeInGuestBook(amusementParkId, visitorId, textOfRegistry))
				.isInstanceOf(AmusementParkException.class).hasMessage(NO_AMUSEMENT_PARK_WITH_ID);

		verify(amusementParkRepository).findByIdReadOnlyId(amusementParkId);
	}

	@Test
	public void writeInGuestBookNegativeNoVisitorInPark() {
		AmusementPark amusementPark = AmusementPark.builder().id(0L).build();
		Long amusementParkId = amusementPark.getId();
		Long visitorId = 1L;
		String textOfRegistry = OPINION_ON_THE_PARK;

		when(amusementParkRepository.findByIdReadOnlyId(amusementParkId)).thenReturn(amusementPark);

		assertThatThrownBy(() -> guestBookService.writeInGuestBook(amusementParkId, visitorId, textOfRegistry))
				.isInstanceOf(AmusementParkException.class).hasMessage(NO_VISITOR_IN_PARK_WITH_ID);

		verify(amusementParkRepository).findByIdReadOnlyId(amusementParkId);
		verify(visitorRepository).findByAmusementParkIdAndVisitorIdReadOnlyId(amusementParkId, visitorId);
	}

	@Test
	public void writeInGuestBookPositive() {
		AmusementPark amusementPark = AmusementPark.builder().id(0L).build();
		Long amusementParkId = amusementPark.getId();
		Visitor visitor = Visitor.builder().id(1L).build();
		Long visitorId = visitor.getId();
		String textOfRegistry = OPINION_ON_THE_PARK;

		when(amusementParkRepository.findByIdReadOnlyId(amusementParkId)).thenReturn(amusementPark);
		when(visitorRepository.findByAmusementParkIdAndVisitorIdReadOnlyId(amusementParkId, visitorId)).thenReturn(visitor);
		
		guestBookService.writeInGuestBook(amusementParkId, visitorId, textOfRegistry);
		
		verify(amusementParkRepository).findByIdReadOnlyId(amusementParkId);
		verify(visitorRepository).findByAmusementParkIdAndVisitorIdReadOnlyId(amusementParkId, visitorId);
		verify(guestBookRepository).save(any(GuestBook.class));
	}

}
