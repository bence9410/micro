package hu.beni.amusementpark.service.impl;

import static hu.beni.amusementpark.constants.ErrorMessageConstants.*;
import static hu.beni.amusementpark.exception.ExceptionUtil.*;

import java.time.LocalDate;
import java.time.Period;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    
    @Override
    public Integer findSpendingMoneyByUsername(){
        return ifNull(visitorRepository.findSpendingMoneyByUserName(), VISITOR_NOT_SIGNED_UP);
    }
    
    @Override
    public Visitor signUp(Visitor visitor) {
    	return visitorRepository.save(visitor);
    }

    @Override
    public Visitor findOne(Long visitorId) {
        return ifNull(visitorRepository.findById(visitorId), VISITOR_NOT_SIGNED_UP);
    }

    @Override
    public Visitor enterPark(Long amusementParkId, Long visitorId) {
        AmusementPark amusementPark = ifNull(amusementParkRepository.findByIdReadOnlyIdAndEntranceFee(amusementParkId), NO_AMUSEMENT_PARK_WITH_ID);
        
        Visitor visitor = ifNull(visitorRepository.findById(visitorId), VISITOR_NOT_SIGNED_UP);
        
        Integer entranceFee = amusementPark.getEntranceFee();
        Integer spendingMoney = visitor.getSpendingMoney();
        ifFirstLessThanSecond(spendingMoney, entranceFee, NOT_ENOUGH_MONEY);
        
        ifNotZero(visitorRepository.countByVisitorIdWhereAmusementParkIsNotNull(visitorId), VISITOR_IS_IN_A_PARK);
        
        if (amusementParkRepository.countKnownVisitor(amusementParkId, visitorId) == 0) {
        	amusementParkRepository.addKnownVisitor(amusementParkId, visitorId);
        }
        
        visitor.setSpendingMoney(spendingMoney - entranceFee);
        visitor.setState(VisitorState.REST);
        visitor.setAmusementPark(amusementPark);
        
        amusementParkRepository.incrementCapitalById(entranceFee, amusementParkId);
        return visitorRepository.save(visitor);
    }

    @Override
    public Visitor getOnMachine(Long amusementParkId, Long machineId, Long visitorId) {
        Machine machine = ifNull(machineRepository.findByAmusementParkIdAndMachineId(amusementParkId, machineId), NO_MACHINE_IN_PARK_WITH_ID);
        Visitor visitor = ifNull(visitorRepository.findByAmusementParkIdAndVisitorId(amusementParkId, visitorId), NO_VISITOR_IN_PARK_WITH_ID);
        
        checkIfVisitorAbleToGetOnMachine(machine, visitor);
        
        return incrementCapitalAndDecraiseSpendingMoneyAndSave(amusementParkId, machine, visitor);
    }
    
    private void checkIfVisitorAbleToGetOnMachine(Machine machine, Visitor visitor) {
        ifEquals(VisitorState.ON_MACHINE, visitor.getState(), VISITOR_IS_ON_A_MACHINE);
        ifFirstLessThanSecond(visitor.getSpendingMoney(), machine.getTicketPrice(), NOT_ENOUGH_MONEY);
        ifFirstLessThanSecond(Period.between(visitor.getDateOfBirth(), LocalDate.now()).getYears(), machine.getMinimumRequiredAge(), VISITOR_IS_TOO_YOUNG);
        ifPrimitivesEquals(visitorRepository.countByMachineId(machine.getId()), machine.getNumberOfSeats(), NO_FREE_SEAT_ON_MACHINE);
    }

    private Visitor incrementCapitalAndDecraiseSpendingMoneyAndSave(Long amusementParkId, Machine machine, Visitor visitor) {
        amusementParkRepository.incrementCapitalById(machine.getTicketPrice(), amusementParkId);
        visitor.setSpendingMoney(visitor.getSpendingMoney() - machine.getTicketPrice());
        visitor.setMachine(machine);
        visitor.setState(VisitorState.ON_MACHINE);
        return visitorRepository.save(visitor);
    } 
    
    @Override
    public Visitor getOffMachine(Long machineId, Long visitorId) {
        Visitor visitor = ifNull(visitorRepository.findByMachineIdAndVisitorId(machineId, visitorId), NO_VISITOR_ON_MACHINE_WITH_ID);
        visitor.setMachine(null);
        visitor.setState(VisitorState.REST);
        return visitorRepository.save(visitor);
    }
    
    @Override
    public Visitor leavePark(Long amusementParkId, Long visitorId) {
    	Visitor visitor = ifNull(visitorRepository.findByAmusementParkIdAndVisitorId(amusementParkId, visitorId), NO_VISITOR_IN_PARK_WITH_ID);
    	visitor.setAmusementPark(null);
    	visitor.setState(null);
    	return visitorRepository.save(visitor);
    }

	@Override
	public Page<Visitor> findAll(Pageable pageable) {
		return visitorRepository.findAll(pageable);
	}

	@Override
	public void delete(Long visitorId) {
		visitorRepository.deleteById(visitorId);
	}
}