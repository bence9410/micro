package hu.beni.amusementpark.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import hu.beni.amusementpark.entity.AmusementPark;

public interface AmusementParkService {

	AmusementPark save(AmusementPark amusementPark);

	AmusementPark findById(Long amusementParkId);

	void delete(Long amusementParkId);

	Page<AmusementPark> findAll(Pageable pageable);

}
