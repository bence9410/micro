package hu.beni.amusementpark.service;

import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.entity.Machine;
import org.springframework.stereotype.Service;
import static hu.beni.amusementpark.constants.ErrorMessageConstants.*;
import static hu.beni.amusementpark.exception.ExceptionUtil.*;
import hu.beni.amusementpark.entity.Visitor;
import hu.beni.amusementpark.enums.VisitorState;
import hu.beni.amusementpark.repository.AmusementParkRepository;
import hu.beni.amusementpark.repository.MachineRepository;
import hu.beni.amusementpark.repository.VisitorRepository;
import java.sql.Timestamp;
import java.util.Calendar;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class VisitorService {

    private final AmusementParkRepository amusementParkRepository;
    private final MachineRepository machineRepository;
    private final VisitorRepository visitorRepository;

    public Visitor read(Long id) {
        return visitorRepository.findOne(id);
    }

    public void leavePark(Long id) {
        visitorRepository.delete(id);
    }

    public Visitor enterPark(Long amusementParkId, Visitor visitor) {
        AmusementPark amusementPark = amusementParkRepository.findAmusementParkByIdReadOnlyIdAndEntranceFee(amusementParkId);
        exceptionIfNull(amusementPark, NO_AMUSEMENT_PARK_WITH_ID);
        exceptionIfFirstLessThanSecond(visitor.getSpendingMoney(), amusementPark.getEntranceFee(), NOT_ENOUGH_MONEY);
        visitor.setSpendingMoney(visitor.getSpendingMoney() - amusementPark.getEntranceFee());
        visitor.setDateOfEntry(Timestamp.from(Calendar.getInstance().toInstant()));
        visitor.setState(VisitorState.REST);
        amusementParkRepository.incrementCapitalById(amusementPark.getEntranceFee(), amusementParkId);
        visitor.setAmusementPark(amusementPark);
        return visitorRepository.save(visitor);
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
