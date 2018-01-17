package hu.beni.amusement.park.service;

import hu.beni.amusement.park.entity.Machine;

public interface MachineService {

    Machine addMachine(Long amusementParkId, Machine machine);

    Machine findOne(Long machineId);

    void removeMachine(Long amusementParkId, Long machineId);

}
