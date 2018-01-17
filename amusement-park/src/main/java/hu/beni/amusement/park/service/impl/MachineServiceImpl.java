package hu.beni.amusement.park.service.impl;

import static hu.beni.amusement.park.constants.ErrorMessageConstants.MACHINE_IS_TOO_BIG;
import static hu.beni.amusement.park.constants.ErrorMessageConstants.MACHINE_IS_TOO_EXPENSIVE;
import static hu.beni.amusement.park.constants.ErrorMessageConstants.NO_AMUSEMENT_PARK_WITH_ID;
import static hu.beni.amusement.park.constants.ErrorMessageConstants.NO_MACHINE_IN_PARK_WITH_ID;
import static hu.beni.amusement.park.constants.ErrorMessageConstants.VISITORS_ON_MACHINE;
import static hu.beni.amusement.park.exception.ExceptionUtil.exceptionIfFirstLessThanSecond;
import static hu.beni.amusement.park.exception.ExceptionUtil.exceptionIfNotZero;
import static hu.beni.amusement.park.exception.ExceptionUtil.exceptionIfNull;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hu.beni.amusement.park.entity.AmusementPark;
import hu.beni.amusement.park.entity.Machine;
import hu.beni.amusement.park.repository.AmusementParkRepository;
import hu.beni.amusement.park.repository.MachineRepository;
import hu.beni.amusement.park.repository.VisitorRepository;
import hu.beni.amusement.park.service.MachineService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MachineServiceImpl implements MachineService {

    private final AmusementParkRepository amusementParkRepository;
    private final MachineRepository machineRepository;
    private final VisitorRepository visitorRepository;

    @Override
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

    @Override
    public Machine findOne(Long machineId) {
        return machineRepository.findOne(machineId);
    }

    @Override
    public void removeMachine(Long amusementParkId, Long machineId) {
        Machine machine = machineRepository.findByAmusementParkIdAndMachineId(amusementParkId, machineId);
        exceptionIfNull(machine, NO_MACHINE_IN_PARK_WITH_ID);
        exceptionIfNotZero(visitorRepository.countByMachineId(machineId), VISITORS_ON_MACHINE);
        amusementParkRepository.incrementCapitalById(machine.getPrice(), amusementParkId);
        machineRepository.delete(machineId);
    }

}
