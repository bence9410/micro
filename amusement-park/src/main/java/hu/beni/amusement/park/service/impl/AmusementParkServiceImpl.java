package hu.beni.amusement.park.service.impl;

import static hu.beni.amusement.park.constants.ErrorMessageConstants.NO_AMUSEMENT_PARK_WITH_ID;
import static hu.beni.amusement.park.constants.ErrorMessageConstants.VISITORS_IN_PARK;
import static hu.beni.amusement.park.exception.ExceptionUtil.exceptionIfNotZero;
import static hu.beni.amusement.park.exception.ExceptionUtil.exceptionIfNull;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hu.beni.amusement.park.archive.ArchiveSender;
import hu.beni.amusement.park.entity.AmusementPark;
import hu.beni.amusement.park.repository.AmusementParkRepository;
import hu.beni.amusement.park.repository.VisitorRepository;
import hu.beni.amusement.park.service.AmusementParkService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AmusementParkServiceImpl implements AmusementParkService {

    private final AmusementParkRepository amusementParkRepository;
    private final VisitorRepository visitorRepository;
    private final ArchiveSender amusementParkArchivator;
	
    @Override
    public AmusementPark save(AmusementPark amusementPark) {
    	return amusementParkRepository.save(amusementPark);
    }	

    @Override
    public AmusementPark findOne(Long amusementParkId) {
        return amusementParkRepository.findOne(amusementParkId);
    }
    
    @Override
    public AmusementPark findOne(Specification<AmusementPark> specification) {
    	return amusementParkRepository.findOne(specification);
    }

    @Override
    public void delete(Long amusementParkId) {
    	exceptionIfNotZero(visitorRepository.countByAmusementParkId(amusementParkId), VISITORS_IN_PARK);
    	AmusementPark amusementPark = amusementParkRepository.findOne(amusementParkId);
    	exceptionIfNull(amusementPark, NO_AMUSEMENT_PARK_WITH_ID);
    	amusementParkRepository.delete(amusementPark);
    	amusementParkArchivator.sendToArchive(amusementPark);
    }
    
    @Override
    public List<AmusementPark> findAll(){
    	return amusementParkRepository.findAllFetchAddress();
    }
    
    @Override
    public Page<AmusementPark> findAll(Pageable pageable){
    	return amusementParkRepository.findAll(pageable);
    }
    
    @Override
    public List<AmusementPark> findAll(Specification<AmusementPark> specification) {
    	return amusementParkRepository.findAll(specification);
    }    
}
