package hu.beni.amusementpark.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import hu.beni.amusementpark.entity.Visitor;

public interface VisitorService {

	Visitor findByEmail(String email);

	Visitor signUp(Visitor visitor);

	void uploadMoney(Integer ammount, String email);

	Visitor findOne(Long visitorId);

	Visitor leavePark(Long amusementParkId, Long visitorId, String email);

	Visitor enterPark(Long amusementParkId, Long visitorId);

	Visitor getOnMachine(Long amusementParkId, Long machineId, Long visitorId, String email);

	Visitor getOffMachine(Long machineId, Long visitorId, String email);

	Page<Visitor> findAll(Pageable pageable);

	void delete(Long visitorId);

}
