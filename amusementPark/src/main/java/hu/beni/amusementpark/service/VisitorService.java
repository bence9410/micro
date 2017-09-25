package hu.beni.amusementpark.service;

import hu.beni.amusementpark.entity.Visitor;

public interface VisitorService {

    Visitor findOne(Long visitorId);

    void leavePark(Long amusementParkId, Long visitorId);
    
    Visitor enterPark(Long amusementParkId, Visitor visitor);
    
    Visitor getOnMachine(Long amusementParkId, Long machineId, Long visitorId);
    
    Visitor getOffMachine(Long machineId, Long visitorId);

}
