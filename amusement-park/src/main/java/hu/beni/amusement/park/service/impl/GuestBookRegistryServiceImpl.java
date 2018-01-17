package hu.beni.amusement.park.service.impl;

import static hu.beni.amusement.park.constants.ErrorMessageConstants.*;
import static hu.beni.amusement.park.exception.ExceptionUtil.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hu.beni.amusement.park.entity.AmusementPark;
import hu.beni.amusement.park.entity.GuestBookRegistry;
import hu.beni.amusement.park.entity.Visitor;
import hu.beni.amusement.park.repository.AmusementParkRepository;
import hu.beni.amusement.park.repository.GuestBookRegistryRepository;
import hu.beni.amusement.park.repository.VisitorRepository;
import hu.beni.amusement.park.service.GuestBookRegistryService;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class GuestBookRegistryServiceImpl implements GuestBookRegistryService{

	private final AmusementParkRepository amusementParkRepository;
	private final VisitorRepository visitorRepository;
	private final GuestBookRegistryRepository guestBookRegistryRepository;
	
        @Override
	public GuestBookRegistry findOne(Long guestBookRegistryId) {
		return guestBookRegistryRepository.findOne(guestBookRegistryId);
	}
	
        @Override
	public GuestBookRegistry addRegistry(Long amusementParkId, Long visitorId, String textOfRegistry) {
		AmusementPark amusementPark = amusementParkRepository.findByIdReadOnlyId(amusementParkId);
		exceptionIfNull(amusementPark, NO_AMUSEMENT_PARK_WITH_ID);
		Visitor visitor = visitorRepository.findOne(visitorId);
		exceptionIfNull(visitor, NO_VISITOR_IN_PARK_WITH_ID);
		return guestBookRegistryRepository.save(GuestBookRegistry.builder().textOfRegistry(textOfRegistry)
				.visitor(visitor).amusementPark(amusementPark).build());
	}
	
}
