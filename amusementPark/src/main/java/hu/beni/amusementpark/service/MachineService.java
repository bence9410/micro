package hu.beni.amusementpark.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static hu.beni.amusementpark.constants.ErrorMessageConstants.*;
import static hu.beni.amusementpark.exception.ExceptionUtil.*;
import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.entity.Machine;
import hu.beni.amusementpark.repository.AmusementParkRepository;
import hu.beni.amusementpark.repository.MachineRepository;
import hu.beni.amusementpark.repository.VisitorRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MachineService {

    private final AmusementParkRepository amusementParkRepository;
    private final MachineRepository machineRepository;
    private final VisitorRepository visitorRepository;

    public Machine addMachine(Long amusementParkId, Machine machine) {
        AmusementPark amusementPark = amusementParkRepository.findByIdReadOnlyIdAndCapitalAndTotalArea(amusementParkId);
        exceptionIfNull(amusementPark, NO_AMUSEMENT_PARK_WITH_ID);
        checkForMoneyAndFreeArea(amusementPark, machine);
        return buyMachine(amusementPark, machine);
    }

    private void checkForMoneyAndFreeArea(AmusementPark amusementPark, Machine machine) {
        exceptionIfFirstLessThanSecond(amusementPark.getCapital(), machine.getPrice(), MACHINE_IS_TOO_EXPENSIVE);
        exceptionIfFirstLessThanSecond(amusementPark.getTotalArea(),
                Optional.ofNullable(machineRepository.sumAreaByAmusementParkId(amusementPark.getId())).orElse(0L) + machine.getSize(), MACHINE_IS_TOO_BIG);
    }

    private Machine buyMachine(AmusementPark amusementPark, Machine machine) {
        amusementParkRepository.decreaseCapitalById(machine.getPrice(), amusementPark.getId());
        machine.setAmusementPark(amusementPark);
        return machineRepository.save(machine);
    }

    public Machine findOne(Long amusementParkId) {
        return machineRepository.findOne(amusementParkId);
    }

    public void removeMachine(Long amusementParkId, Long machineId) {
        Machine machine = machineRepository.findByAmusementParkIdAndMachineId(amusementParkId, machineId);
        exceptionIfNull(machine, NO_MACHINE_IN_PARK_WITH_ID);
        exceptionIfNotZero(visitorRepository.countByMachineId(machineId), VISITORS_ON_MACHINE);
        amusementParkRepository.incrementCapitalById(machine.getPrice(), amusementParkId);
        machineRepository.delete(machineId);
    }

}
