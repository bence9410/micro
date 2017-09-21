package hu.beni.amusementpark.service;

import hu.beni.amusementpark.entity.Machine;

public interface MachineService {

    Machine addMachine(Long amusementParkId, Machine machine);

    Machine findOne(Long machineId);

    void removeMachine(Long amusementParkId, Long machineId);

}
