package hu.beni.amusementpark.service;

import hu.beni.amusementpark.entity.Visitor;

public interface VisitorService {
    
        Integer findSpendingMoneyByUsername(String username);

	Visitor signUp(Visitor visitor);

	Visitor findOne(Long visitorId);
	
	Visitor leavePark(Long amusementParkId, Long visitorId);

	Visitor enterPark(Long amusementParkId, Long visitorId, Integer spendingMoney);

	Visitor getOnMachine(Long amusementParkId, Long machineId, Long visitorId);

	Visitor getOffMachine(Long machineId, Long visitorId);

}
