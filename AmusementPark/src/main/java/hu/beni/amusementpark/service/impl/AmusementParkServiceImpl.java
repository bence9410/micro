package hu.beni.amusementpark.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hu.beni.amusementpark.archive.ArchiveSender;
import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.repository.AmusementParkRepository;
import hu.beni.amusementpark.repository.VisitorRepository;
import hu.beni.amusementpark.service.AmusementParkService;
import lombok.RequiredArgsConstructor;

import static hu.beni.amusementpark.exception.ExceptionUtil.*;
import static hu.beni.amusementpark.constants.ErrorMessageConstants.VISITORS_IN_PARK;
import static hu.beni.amusementpark.constants.ErrorMessageConstants.NO_AMUSEMENT_PARK_WITH_ID;

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
        return ifNull(amusementParkRepository.findById(amusementParkId), NO_AMUSEMENT_PARK_WITH_ID);
    }
    
    @Override
    public AmusementPark findOne(Specification<AmusementPark> specification) {
    	return ifNull(amusementParkRepository.findOne(specification), NO_AMUSEMENT_PARK_WITH_ID);
    }

    @Override
    public void delete(Long amusementParkId) {
    	ifNotZero(visitorRepository.countByAmusementParkId(amusementParkId), VISITORS_IN_PARK);
    	AmusementPark amusementPark = ifNull(amusementParkRepository.findById(amusementParkId), NO_AMUSEMENT_PARK_WITH_ID);
    	amusementParkRepository.delete(amusementPark);
    	amusementParkArchivator.sendToArchive(amusementPark);
    }
    
    @Override
    public List<AmusementPark> findAll(){
    	return amusementParkRepository.findAll();
    }
    
    @Override
    public Page<AmusementPark> findAllFetchAddress(Pageable pageable){
    	return amusementParkRepository.findAllFetchAddress(pageable);
    }
    
    @Override
    public List<AmusementPark> findAll(Specification<AmusementPark> specification) {
    	return amusementParkRepository.findAll(specification);
    }    
}
