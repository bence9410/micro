package hu.beni.amusementpark.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import hu.beni.amusementpark.entity.AmusementPark;

public interface AmusementParkService {

	AmusementPark save(AmusementPark amusementPark);

	AmusementPark findByIdFetchAddress(Long amusementParkId);

	AmusementPark findOne(Specification<AmusementPark> specification);

	void delete(Long amusementParkId);

	List<AmusementPark> findAllFetchAddress();

	Page<AmusementPark> findAllFetchAddress(Pageable pageable);

	List<AmusementPark> findAll(Specification<AmusementPark> specification);

}
