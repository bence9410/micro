package hu.beni.amusementpark.service;

import java.util.List;

import hu.beni.amusementpark.entity.Machine;

public interface MachineService {

    Machine addMachine(Long amusementParkId, Machine machine);

    Machine findOne(Long amusementParkId, Long machineId);
    
    List<Machine> findAllByAmusementParkId(Long amusementParkId);

    void removeMachine(Long amusementParkId, Long machineId);

}
