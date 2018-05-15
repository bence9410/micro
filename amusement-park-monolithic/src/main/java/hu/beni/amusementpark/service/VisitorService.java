package hu.beni.amusementpark.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import hu.beni.amusementpark.entity.Visitor;

public interface VisitorService {

	Integer findSpendingMoneyByUsername();

	Visitor signUp(Visitor visitor);

	Visitor findOne(Long visitorId);

	Visitor leavePark(Long amusementParkId, Long visitorId);

	Visitor enterPark(Long amusementParkId, Long visitorId);

	Visitor getOnMachine(Long amusementParkId, Long machineId, Long visitorId);

	Visitor getOffMachine(Long machineId, Long visitorId);

	Page<Visitor> findAll(Pageable pageable);

	void delete(Long visitorId);

}
