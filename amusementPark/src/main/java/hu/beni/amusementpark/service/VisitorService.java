package hu.beni.amusementpark.service;

import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.entity.Machine;
import org.springframework.stereotype.Service;

import hu.beni.amusementpark.entity.Visitor;
import hu.beni.amusementpark.enums.VisitorState;
import hu.beni.amusementpark.exception.AmusementParkException;
import hu.beni.amusementpark.exception.ExceptionUtil;
import hu.beni.amusementpark.repository.AmusementParkRepository;
import hu.beni.amusementpark.repository.MachineRepository;
import hu.beni.amusementpark.repository.VisitorRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
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

    @Transactional(rollbackFor = Exception.class)
    public Visitor enterPark(Long amusementParkId, Visitor visitor) {
        Integer entranceFee = amusementParkRepository.findEntranceFeeById(amusementParkId);
        ExceptionUtil.exceptionIfFirstLessThanSecondWithMessage(visitor.getSpendingMoney(), entranceFee, "Not enogh money!");
        visitor.setSpendingMoney(visitor.getSpendingMoney() - entranceFee);
        amusementParkRepository.incrementCapitalById(entranceFee, amusementParkId);
        visitor.setAmusementPark(AmusementPark.builder().id(amusementParkId).build());
        return visitorRepository.save(visitor);
    }

    @Transactional(rollbackFor = Exception.class)
    public Visitor getOnMachine(Long amusementParkId, Long machineId, Long visitorId) {
        Machine machine = Optional.ofNullable(machineRepository.findByAmusementParkIdAndMachineId(amusementParkId, machineId))
                .orElseThrow(() -> new AmusementParkException("No machine in the park with the given id!"));
        Visitor visitor = Optional.ofNullable(visitorRepository.findByAmusementParkIdAndVisitorId(amusementParkId, visitorId))
                .orElseThrow(() -> new AmusementParkException("No visitor in the park with the given id!"));
        if (VisitorState.ON_MACHINE.equals(visitor.getState())) {
            throw new AmusementParkException("Visitor is on machine");
        }
        ExceptionUtil.exceptionIfFirstLessThanSecondWithMessage(visitor.getSpendingMoney(), machine.getTicketPrice(), "Not enough money!");
        ExceptionUtil.exceptionIfFirstLessThanSecondWithMessage(visitor.getAge(), machine.getMinimumRequiredAge(), "Visitor is too young!");
        if (visitorRepository.countByMachineId(machineId) >= machine.getNumberOfSeats()) {
            throw new AmusementParkException("No free seat on machine");
        }
        amusementParkRepository.incrementCapitalById(machine.getTicketPrice(), amusementParkId);
        visitor.setSpendingMoney(machine.getTicketPrice());
        visitor.setMachine(machine);
        visitor.setState(VisitorState.ON_MACHINE);
        return visitorRepository.save(visitor);
    }

    public Visitor getOffMachine(Long machineId, Long visitorId) {
        Visitor visitor = Optional.ofNullable(visitorRepository.findByMachineIdAndVisitorId(machineId, visitorId))
                .orElseThrow(() -> new AmusementParkException("No visitor on machine with the given id!"));
        visitor.setState(VisitorState.REST);
        return visitorRepository.save(visitor);
    }

}
