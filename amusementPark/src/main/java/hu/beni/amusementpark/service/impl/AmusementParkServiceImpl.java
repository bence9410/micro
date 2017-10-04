package hu.beni.amusementpark.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.repository.AmusementParkRepository;
import hu.beni.amusementpark.service.AmusementParkService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AmusementParkServiceImpl implements AmusementParkService {

    private final AmusementParkRepository amusementParkRepository;
	
    public AmusementPark save(AmusementPark amusementPark) {
    	return amusementParkRepository.save(amusementPark);
    }	

    public AmusementPark findOne(Long amusementParkId) {
        return amusementParkRepository.findOne(amusementParkId);
    }
    
    public AmusementPark findOne(Specification<AmusementPark> specification) {
    	return amusementParkRepository.findOne(specification);
    }

    public void delete(Long amusementParkId) {
        amusementParkRepository.delete(amusementParkId);
    }
    
    public Page<AmusementPark> findAll(Pageable pageable){
    	return amusementParkRepository.findAll(pageable);
    }
    
    public List<AmusementPark> findAll(Specification<AmusementPark> specification) {
    	return amusementParkRepository.findAll(specification);
    }    
}
