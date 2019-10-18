package hu.beni.amusementpark.service.impl;

import static hu.beni.amusementpark.constants.ErrorMessageConstants.NO_AMUSEMENT_PARK_WITH_ID;
import static hu.beni.amusementpark.constants.ErrorMessageConstants.VISITORS_IN_PARK;
import static hu.beni.amusementpark.exception.ExceptionUtil.ifNotZero;
import static hu.beni.amusementpark.exception.ExceptionUtil.ifNull;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.repository.AmusementParkRepository;
import hu.beni.amusementpark.repository.VisitorRepository;
import hu.beni.amusementpark.service.AmusementParkService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AmusementParkServiceImpl implements AmusementParkService {

	private final AmusementParkRepository amusementParkRepository;
	private final VisitorRepository visitorRepository;

	@Override
	public AmusementPark save(AmusementPark amusementPark) {
		return amusementParkRepository.save(amusementPark);
	}

	@Override
	public AmusementPark findById(Long amusementParkId) {
		return ifNull(amusementParkRepository.findById(amusementParkId), NO_AMUSEMENT_PARK_WITH_ID);
	}

	@Override
	public void delete(Long amusementParkId) {
		ifNotZero(visitorRepository.countByAmusementParkId(amusementParkId), VISITORS_IN_PARK);
		amusementParkRepository
				.delete(ifNull(amusementParkRepository.findById(amusementParkId), NO_AMUSEMENT_PARK_WITH_ID));
	}

	@Override
	public Page<AmusementPark> findAll(Pageable pageable) {
		return amusementParkRepository.findAll(pageable);
	}
}
