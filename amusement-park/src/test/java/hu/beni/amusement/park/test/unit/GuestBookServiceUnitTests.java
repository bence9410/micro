package hu.beni.amusement.park.test.unit;

import static hu.beni.amusement.park.constants.ErrorMessageConstants.NO_AMUSEMENT_PARK_WITH_ID;
import static hu.beni.amusement.park.constants.ErrorMessageConstants.NO_VISITOR_IN_PARK_WITH_ID;
import static hu.beni.amusement.park.constants.StringParamConstants.OPINION_ON_THE_PARK;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import hu.beni.amusement.park.entity.AmusementPark;
import hu.beni.amusement.park.entity.GuestBookRegistry;
import hu.beni.amusement.park.entity.Visitor;
import hu.beni.amusement.park.exception.AmusementParkException;
import hu.beni.amusement.park.repository.AmusementParkRepository;
import hu.beni.amusement.park.repository.GuestBookRegistryRepository;
import hu.beni.amusement.park.repository.VisitorRepository;
import hu.beni.amusement.park.service.GuestBookRegistryService;
import hu.beni.amusement.park.service.impl.GuestBookRegistryServiceImpl;

public class GuestBookServiceUnitTests {

	private AmusementParkRepository amusementParkRepository;
	private VisitorRepository visitorRepository;
	private GuestBookRegistryRepository guestBookRegistryRepository;
	
	private GuestBookRegistryService guestBookService;

	@Before
	public void setUp() {
		amusementParkRepository = mock(AmusementParkRepository.class);
		visitorRepository = mock(VisitorRepository.class);
		guestBookRegistryRepository = mock(GuestBookRegistryRepository.class);
		guestBookService = new GuestBookRegistryServiceImpl(amusementParkRepository, visitorRepository, guestBookRegistryRepository);
	}

	@After
	public void verifyNoMoreInteractionsOnMocks() {
		verifyNoMoreInteractions(amusementParkRepository, visitorRepository, guestBookRegistryRepository);
	}

	@Test
	public void findOnePositive() {
		GuestBookRegistry guestBookRegistry = GuestBookRegistry.builder().id(0L).build();
		Long guestBookRegistryId = guestBookRegistry.getId();

		when(guestBookRegistryRepository.findOne(guestBookRegistryId)).thenReturn(guestBookRegistry);

		assertEquals(guestBookRegistry, guestBookService.findOne(guestBookRegistryId));

		verify(guestBookRegistryRepository).findOne(guestBookRegistryId);
	}

	@Test
	public void addRegistryNegativeNoAmusementPark() {
		Long amusementParkId = 0L;
		Long visitorId = 1L;
		String textOfRegistry = OPINION_ON_THE_PARK;

		assertThatThrownBy(() -> guestBookService.addRegistry(amusementParkId, visitorId, textOfRegistry))
				.isInstanceOf(AmusementParkException.class).hasMessage(NO_AMUSEMENT_PARK_WITH_ID);

		verify(amusementParkRepository).findByIdReadOnlyId(amusementParkId);
	}

	@Test
	public void addRegistryNegativeNoVisitorInPark() {
		AmusementPark amusementPark = AmusementPark.builder().id(0L).build();
		Long amusementParkId = amusementPark.getId();
		Long visitorId = 1L;
		String textOfRegistry = OPINION_ON_THE_PARK;

		when(amusementParkRepository.findByIdReadOnlyId(amusementParkId)).thenReturn(amusementPark);

		assertThatThrownBy(() -> guestBookService.addRegistry(amusementParkId, visitorId, textOfRegistry))
				.isInstanceOf(AmusementParkException.class).hasMessage(NO_VISITOR_IN_PARK_WITH_ID);

		verify(amusementParkRepository).findByIdReadOnlyId(amusementParkId);
		verify(visitorRepository).findOne(visitorId);
	}

	@Test
	public void addRegistryPositive() {
		AmusementPark amusementPark = AmusementPark.builder().id(0L).build();
		Long amusementParkId = amusementPark.getId();
		Visitor visitor = Visitor.builder().id(1L).build();
		Long visitorId = visitor.getId();
		String textOfRegistry = OPINION_ON_THE_PARK;

		when(amusementParkRepository.findByIdReadOnlyId(amusementParkId)).thenReturn(amusementPark);
		when(visitorRepository.findOne(visitorId)).thenReturn(visitor);
		GuestBookRegistry guestBookRegistry = GuestBookRegistry.builder().amusementPark(amusementPark)
				.textOfRegistry(textOfRegistry).visitor(visitor).build();
		when(guestBookRegistryRepository.save(any(GuestBookRegistry.class))).thenReturn(guestBookRegistry);
		
		assertEquals(guestBookRegistry, guestBookService.addRegistry(amusementParkId, visitorId, textOfRegistry));
		
		verify(amusementParkRepository).findByIdReadOnlyId(amusementParkId);
		verify(visitorRepository).findOne(visitorId);
		verify(guestBookRegistryRepository).save(any(GuestBookRegistry.class));
	}

}