package hu.beni.amusementpark.test.unit;

import static hu.beni.amusementpark.constants.ErrorMessageConstants.NO_AMUSEMENT_PARK_WITH_ID;
import static hu.beni.amusementpark.constants.ErrorMessageConstants.NO_VISITOR_IN_PARK_WITH_ID;
import static hu.beni.amusementpark.constants.StringParamConstants.OPINION_ON_THE_PARK;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.entity.GuestBookRegistry;
import hu.beni.amusementpark.entity.Visitor;
import hu.beni.amusementpark.exception.AmusementParkException;
import hu.beni.amusementpark.repository.AmusementParkRepository;
import hu.beni.amusementpark.repository.GuestBookRegistryRepository;
import hu.beni.amusementpark.repository.VisitorRepository;
import hu.beni.amusementpark.service.GuestBookRegistryService;
import hu.beni.amusementpark.service.impl.GuestBookRegistryServiceImpl;

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

		when(guestBookRegistryRepository.findById(guestBookRegistryId)).thenReturn(Optional.of(guestBookRegistry));

		assertEquals(guestBookRegistry, guestBookService.findOne(guestBookRegistryId));

		verify(guestBookRegistryRepository).findById(guestBookRegistryId);
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
		verify(visitorRepository).findById(visitorId);
	}

	@Test
	public void addRegistryPositive() {
		AmusementPark amusementPark = AmusementPark.builder().id(0L).build();
		Long amusementParkId = amusementPark.getId();
		Visitor visitor = Visitor.builder().id(1L).build();
		Long visitorId = visitor.getId();
		String textOfRegistry = OPINION_ON_THE_PARK;

		when(amusementParkRepository.findByIdReadOnlyId(amusementParkId)).thenReturn(amusementPark);
		when(visitorRepository.findById(visitorId)).thenReturn(Optional.of(visitor));
		GuestBookRegistry guestBookRegistry = GuestBookRegistry.builder().amusementPark(amusementPark)
				.textOfRegistry(textOfRegistry).visitor(visitor).build();
		when(guestBookRegistryRepository.save(any(GuestBookRegistry.class))).thenReturn(guestBookRegistry);
		
		assertEquals(guestBookRegistry, guestBookService.addRegistry(amusementParkId, visitorId, textOfRegistry));
		
		verify(amusementParkRepository).findByIdReadOnlyId(amusementParkId);
		verify(visitorRepository).findById(visitorId);
		verify(guestBookRegistryRepository).save(any(GuestBookRegistry.class));
	}

}