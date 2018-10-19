package hu.beni.amusementpark.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import hu.beni.amusementpark.entity.Visitor;

public interface VisitorService {

	Visitor findByEmail(String visitorEmail);

	Visitor signUp(Visitor visitor);

	void uploadMoney(Integer ammount, String visitorEmail);

	Visitor findOne(String visitorEmail);

	Visitor leavePark(Long amusementParkId, String visitorEmail);

	Visitor enterPark(Long amusementParkId, String visitorEmail);

	Visitor getOnMachine(Long amusementParkId, Long machineId, String visitorEmail);

	Visitor getOffMachine(Long machineId, String visitorEmail);

	Page<Visitor> findAll(Pageable pageable);

	void delete(String visitorEmail);

}
