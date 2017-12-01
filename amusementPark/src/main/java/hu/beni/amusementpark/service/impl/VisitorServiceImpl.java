package hu.beni.amusementpark.service.impl;

import static hu.beni.amusementpark.constants.ErrorMessageConstants.*;
import static hu.beni.amusementpark.exception.ExceptionUtil.*;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.entity.Machine;
import hu.beni.amusementpark.entity.Visitor;
import hu.beni.amusementpark.enums.VisitorState;
import hu.beni.amusementpark.repository.AmusementParkRepository;
import hu.beni.amusementpark.repository.MachineRepository;
import hu.beni.amusementpark.repository.VisitorRepository;
import hu.beni.amusementpark.service.VisitorService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class VisitorServiceImpl implements VisitorService{

    private final AmusementParkRepository amusementParkRepository;
    private final MachineRepository machineRepository;
    private final VisitorRepository visitorRepository;
    
    public Visitor signUp(Visitor visitor) {
    	return visitorRepository.save(visitor);
    }

    public Visitor findOne(Long visitorId) {
        return visitorRepository.findOne(visitorId);
    }	

    public Visitor enterPark(Long amusementParkId, Long visitorId, Integer spendingMoney) {
        AmusementPark amusementPark = amusementParkRepository.findByIdReadOnlyIdAndEntranceFee(amusementParkId);
        exceptionIfNull(amusementPark, NO_AMUSEMENT_PARK_WITH_ID);
        
        Integer entranceFee = amusementPark.getEntranceFee();
        exceptionIfFirstLessThanSecond(spendingMoney, entranceFee, NOT_ENOUGH_MONEY);
        
        exceptionIfNotZero(visitorRepository.countByVisitorIdWhereAmusementParkIsNotNull(visitorId), VISITOR_IS_IN_A_PARK);
        
        Visitor visitor = visitorRepository.findOne(visitorId);
        exceptionIfNull(visitor, VISITOR_NOT_SIGNED_UP);
        
        Optional.of(amusementParkRepository.countKnownVisitor(amusementParkId, visitorId)).filter(count -> count == 0)
        	.ifPresent(count -> amusementParkRepository.addKnownVisitor(amusementParkId, visitorId));
        
        visitor.setSpendingMoney(spendingMoney - entranceFee);
        visitor.setState(VisitorState.REST);
        visitor.setAmusementPark(amusementPark);
        
        amusementParkRepository.incrementCapitalById(entranceFee, amusementParkId);
        return visitorRepository.save(visitor);
    }    

    public Visitor getOnMachine(Long amusementParkId, Long machineId, Long visitorId) {
        Machine machine = machineRepository.findByAmusementParkIdAndMachineId(amusementParkId, machineId);
        exceptionIfNull(machine, NO_MACHINE_IN_PARK_WITH_ID);
        Visitor visitor = visitorRepository.findByAmusementParkIdAndVisitorId(amusementParkId, visitorId);
        exceptionIfNull(visitor, NO_VISITOR_IN_PARK_WITH_ID);
        
        checkIfVisitorAbleToGetOnMachine(machine, visitor);
        
        return incrementCapitalAndDecraiseSpendingMoneyAndSave(amusementParkId, machine, visitor);
    }
    
    private void checkIfVisitorAbleToGetOnMachine(Machine machine, Visitor visitor) {
        exceptionIfEquals(VisitorState.ON_MACHINE, visitor.getState(), VISITOR_IS_ON_A_MACHINE);
        exceptionIfFirstLessThanSecond(visitor.getSpendingMoney(), machine.getTicketPrice(), NOT_ENOUGH_MONEY);
        exceptionIfFirstLessThanSecond(calculateAge(visitor.getDateOfBirth()), machine.getMinimumRequiredAge(), VISITOR_IS_TOO_YOUNG);
        exceptionIfPrimitivesEquals(visitorRepository.countByMachineId(machine.getId()), machine.getNumberOfSeats(), NO_FREE_SEAT_ON_MACHINE);
    }

    private Visitor incrementCapitalAndDecraiseSpendingMoneyAndSave(Long amusementParkId, Machine machine, Visitor visitor) {
        amusementParkRepository.incrementCapitalById(machine.getTicketPrice(), amusementParkId);
        visitor.setSpendingMoney(visitor.getSpendingMoney() - machine.getTicketPrice());
        visitor.setMachine(machine);
        visitor.setState(VisitorState.ON_MACHINE);
        return visitorRepository.save(visitor);
    } 
    
    public Visitor getOffMachine(Long machineId, Long visitorId) {
        Visitor visitor = visitorRepository.findByMachineIdAndVisitorId(machineId, visitorId);
        exceptionIfNull(visitor, NO_VISITOR_ON_MACHINE_WITH_ID);
        visitor.setMachine(null);
        visitor.setState(VisitorState.REST);
        return visitorRepository.save(visitor);
    }
    
    public Visitor leavePark(Long amusementParkId, Long visitorId) {
    	Visitor visitor = visitorRepository.findByAmusementParkIdAndVisitorId(amusementParkId, visitorId);
    	exceptionIfNull(visitor, NO_VISITOR_IN_PARK_WITH_ID);
    	visitor.setAmusementPark(null);
    	visitor.setSpendingMoney(null);
    	visitor.setState(null);
    	return visitorRepository.save(visitor);
    }
    
    private int calculateAge(Timestamp dateOfBirth) { 
    	Calendar calendarOfBirth = Calendar.getInstance();
    	calendarOfBirth.setTime(dateOfBirth);
    	
    	Calendar now = Calendar.getInstance();
    	
    	int age = now.get(Calendar.YEAR) - calendarOfBirth.get(Calendar.YEAR);
    	
    	int nowMonth = now.get(Calendar.MONTH);
    	int birthMonth = now.get(Calendar.MONTH);
    	
    	if(nowMonth < birthMonth || nowMonth == birthMonth && now.get(Calendar.DAY_OF_MONTH) < calendarOfBirth.get(Calendar.DAY_OF_MONTH)) {
    		age--;
    	}
    	
    	return age;
    }
	
}