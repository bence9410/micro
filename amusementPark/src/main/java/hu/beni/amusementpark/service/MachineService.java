package hu.beni.amusementpark.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.entity.Machine;
import hu.beni.amusementpark.exception.AmusementParkException;
import hu.beni.amusementpark.exception.ExceptionUtil;
import hu.beni.amusementpark.repository.AmusementParkRepository;
import hu.beni.amusementpark.repository.MachineRepository;
import hu.beni.amusementpark.repository.VisitorRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MachineService {

    private final AmusementParkRepository amusementParkRepository;
    private final MachineRepository machineRepository;
    private final VisitorRepository visitorRepository;

    @Transactional(rollbackFor = Exception.class)
    public Machine addMachine(Long amusementParkId, Machine machine) {
        AmusementPark amusementPark = amusementParkRepository.findOne(amusementParkId);
        checkForMoneyAndFreeArea(amusementPark, machine);
        return buyMachine(amusementPark, machine);
    }

    private void checkForMoneyAndFreeArea(AmusementPark amusementPark, Machine machine) {
        ExceptionUtil.exceptionIfFirstLessThanSecondWithMessage(amusementPark.getCapital(), machine.getPrice(), "No money!");
        ExceptionUtil.exceptionIfFirstLessThanSecondWithMessage(amusementPark.getTotalArea(),
                Optional.ofNullable(machineRepository.sumAreaByAmusementParkId(amusementPark.getId())).orElse(0L).intValue() + machine.getSize(), "Too big!");
    }

    private Machine buyMachine(AmusementPark amusementPark, Machine machine) {
        amusementParkRepository.decreaseCapitalById(machine.getPrice(), amusementPark.getId());
        machine.setAmusementPark(amusementPark);
        return machineRepository.save(machine);
    }

    public Machine read(Long id) {
        return machineRepository.findOne(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeMachine(Long amusementParkId, Long machineId) {
        Machine machine = Optional.ofNullable(machineRepository.findByAmusementParkIdAndMachineId(amusementParkId, machineId))
                .orElseThrow(() -> new AmusementParkException("No machine in the park with the given id!"));
        ExceptionUtil.exceptionIfNotZeroWithMessage(visitorRepository.countByMachineId(machineId), "Visitors on machine!");
        amusementParkRepository.incrementCapitalById(machine.getPrice(), amusementParkId);
        machineRepository.delete(machineId);
    }

}
