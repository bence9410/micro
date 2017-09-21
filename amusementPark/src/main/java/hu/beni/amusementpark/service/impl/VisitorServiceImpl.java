package hu.beni.amusementpark.service.impl;

import static hu.beni.amusementpark.constants.ErrorMessageConstants.NOT_ENOUGH_MONEY;
import static hu.beni.amusementpark.constants.ErrorMessageConstants.NO_AMUSEMENT_PARK_WITH_ID;
import static hu.beni.amusementpark.constants.ErrorMessageConstants.NO_FREE_SEAT_ON_MACHINE;
import static hu.beni.amusementpark.constants.ErrorMessageConstants.NO_MACHINE_IN_PARK_WITH_ID;
import static hu.beni.amusementpark.constants.ErrorMessageConstants.NO_VISITOR_IN_PARK_WITH_ID;
import static hu.beni.amusementpark.constants.ErrorMessageConstants.NO_VISITOR_ON_MACHINE_WITH_ID;
import static hu.beni.amusementpark.constants.ErrorMessageConstants.VISITOR_IS_ON_A_MACHINE;
import static hu.beni.amusementpark.constants.ErrorMessageConstants.VISITOR_IS_TOO_YOUNG;
import static hu.beni.amusementpark.exception.ExceptionUtil.exceptionIfEquals;
import static hu.beni.amusementpark.exception.ExceptionUtil.exceptionIfFirstLessThanSecond;
import static hu.beni.amusementpark.exception.ExceptionUtil.exceptionIfNull;
import static hu.beni.amusementpark.exception.ExceptionUtil.exceptionIfPrimitivesEquals;

import java.sql.Timestamp;
import java.time.Instant;

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

    public Visitor findOne(Long visitorId) {
        return visitorRepository.findOne(visitorId);
    }

    public void leavePark(Long visitorId) {
        visitorRepository.delete(visitorId);
    }

    public Visitor enterPark(Long amusementParkId, Visitor visitor) {
        AmusementPark amusementPark = amusementParkRepository.findByIdReadOnlyIdAndEntranceFee(amusementParkId);
        exceptionIfNull(amusementPark, NO_AMUSEMENT_PARK_WITH_ID);
        exceptionIfFirstLessThanSecond(visitor.getSpendingMoney(), amusementPark.getEntranceFee(), NOT_ENOUGH_MONEY);
        modifyVisitorForEnter(visitor, amusementPark.getEntranceFee());
        amusementParkRepository.incrementCapitalById(amusementPark.getEntranceFee(), amusementParkId);
        visitor.setAmusementPark(amusementPark);
        return visitorRepository.save(visitor);
    }
    
    private void modifyVisitorForEnter(Visitor visitor, Integer entranceFee){
        visitor.setSpendingMoney(visitor.getSpendingMoney() - entranceFee);
        visitor.setDateOfEntry(Timestamp.from(Instant.now()));
        visitor.setState(VisitorState.REST);
    }
    
    public Visitor getOnMachine(Long amusementParkId, Long machineId, Long visitorId) {
        Machine machine = machineRepository.findByAmusementParkIdAndMachineId(amusementParkId, machineId);
        exceptionIfNull(machine, NO_MACHINE_IN_PARK_WITH_ID);
        Visitor visitor = visitorRepository.findByAmusementParkIdAndVisitorId(amusementParkId, visitorId);
        exceptionIfNull(visitor, NO_VISITOR_IN_PARK_WITH_ID);
        checkIfVisitorAbleToGetOnMachine(visitor, machine);
        return incrementCapitalAndDecraiseSpendingMoneyAndSave(amusementParkId, machine, visitor);
    }

    private void checkIfVisitorAbleToGetOnMachine(Visitor visitor, Machine machine) {
        exceptionIfEquals(VisitorState.ON_MACHINE, visitor.getState(), VISITOR_IS_ON_A_MACHINE);
        exceptionIfFirstLessThanSecond(visitor.getSpendingMoney(), machine.getTicketPrice(), NOT_ENOUGH_MONEY);
        exceptionIfFirstLessThanSecond(visitor.getAge(), machine.getMinimumRequiredAge(), VISITOR_IS_TOO_YOUNG);
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

}
