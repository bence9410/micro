package hu.beni.amusementpark.service.impl;

import static hu.beni.amusementpark.constants.ErrorMessageConstants.NO_AMUSEMENT_PARK_WITH_ID;
import static hu.beni.amusementpark.constants.ErrorMessageConstants.NO_GUEST_BOOK_REGISTRY_WITH_ID;
import static hu.beni.amusementpark.constants.ErrorMessageConstants.NO_VISITOR_IN_PARK_WITH_ID;
import static hu.beni.amusementpark.exception.ExceptionUtil.ifNull;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hu.beni.amusementpark.dto.request.GuestBookRegistrySearchRequestDto;
import hu.beni.amusementpark.dto.response.GuestBookRegistrySearchResponseDto;
import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.entity.GuestBookRegistry;
import hu.beni.amusementpark.entity.Visitor;
import hu.beni.amusementpark.repository.AmusementParkRepository;
import hu.beni.amusementpark.repository.GuestBookRegistryRepository;
import hu.beni.amusementpark.repository.VisitorRepository;
import hu.beni.amusementpark.service.GuestBookRegistryService;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class GuestBookRegistryServiceImpl implements GuestBookRegistryService {

	private final AmusementParkRepository amusementParkRepository;
	private final VisitorRepository visitorRepository;
	private final GuestBookRegistryRepository guestBookRegistryRepository;

	@Override
	public GuestBookRegistry findOne(Long guestBookRegistryId) {
		return ifNull(guestBookRegistryRepository.findById(guestBookRegistryId), NO_GUEST_BOOK_REGISTRY_WITH_ID);
	}

	@Override
	public GuestBookRegistry addRegistry(Long amusementParkId, String visitorEmail, String textOfRegistry) {
		AmusementPark amusementPark = ifNull(amusementParkRepository.findByIdReadOnlyId(amusementParkId),
				NO_AMUSEMENT_PARK_WITH_ID);
		Visitor visitor = ifNull(visitorRepository.findById(visitorEmail), NO_VISITOR_IN_PARK_WITH_ID);
		return guestBookRegistryRepository.save(GuestBookRegistry.builder().textOfRegistry(textOfRegistry)
				.visitor(visitor).amusementPark(amusementPark).build());
	}

	@Override
	public Page<GuestBookRegistrySearchResponseDto> findAll(GuestBookRegistrySearchRequestDto dto, Pageable pageable) {
		return guestBookRegistryRepository.findAll(dto, pageable);
	}

}
