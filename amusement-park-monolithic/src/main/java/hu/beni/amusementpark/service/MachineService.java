package hu.beni.amusementpark.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import hu.beni.amusementpark.dto.request.MachineSearchRequestDto;
import hu.beni.amusementpark.dto.response.MachineSearchResponseDto;
import hu.beni.amusementpark.entity.Machine;

public interface MachineService {

	Machine addMachine(Long amusementParkId, Machine machine);

	Machine findOne(Long amusementParkId, Long machineId);

	List<Machine> findAllByAmusementParkId(Long amusementParkId);

	void removeMachine(Long amusementParkId, Long machineId);

	Page<MachineSearchResponseDto> findAllPaged(MachineSearchRequestDto dto, Pageable pageable);

}
