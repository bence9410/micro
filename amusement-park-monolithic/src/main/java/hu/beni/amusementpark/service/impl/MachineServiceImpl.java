package hu.beni.amusementpark.service.impl;

import static hu.beni.amusementpark.constants.ErrorMessageConstants.MACHINE_IS_TOO_BIG;
import static hu.beni.amusementpark.constants.ErrorMessageConstants.MACHINE_IS_TOO_EXPENSIVE;
import static hu.beni.amusementpark.constants.ErrorMessageConstants.NO_AMUSEMENT_PARK_WITH_ID;
import static hu.beni.amusementpark.constants.ErrorMessageConstants.NO_MACHINE_IN_PARK_WITH_ID;
import static hu.beni.amusementpark.constants.ErrorMessageConstants.VISITORS_ON_MACHINE;
import static hu.beni.amusementpark.exception.ExceptionUtil.ifFirstLessThanSecond;
import static hu.beni.amusementpark.exception.ExceptionUtil.ifNotZero;
import static hu.beni.amusementpark.exception.ExceptionUtil.ifNull;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hu.beni.amusementpark.dto.request.MachineSearchRequestDto;
import hu.beni.amusementpark.dto.response.MachineSearchResponseDto;
import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.entity.Machine;
import hu.beni.amusementpark.repository.AmusementParkRepository;
import hu.beni.amusementpark.repository.MachineRepository;
import hu.beni.amusementpark.repository.VisitorRepository;
import hu.beni.amusementpark.service.MachineService;
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
		AmusementPark amusementPark = ifNull(
				amusementParkRepository.findByIdReadOnlyIdAndCapitalAndTotalArea(amusementParkId),
				NO_AMUSEMENT_PARK_WITH_ID);
		checkForMoneyAndFreeArea(amusementPark, machine);
		return buyMachine(amusementPark, machine);
	}

	private void checkForMoneyAndFreeArea(AmusementPark amusementPark, Machine machine) {
		ifFirstLessThanSecond(amusementPark.getCapital(), machine.getPrice(), MACHINE_IS_TOO_EXPENSIVE);
		ifFirstLessThanSecond(amusementPark.getTotalArea(),
				machineRepository.sumAreaByAmusementParkId(amusementPark.getId()).orElse(0L) + machine.getSize(),
				MACHINE_IS_TOO_BIG);
	}

	private Machine buyMachine(AmusementPark amusementPark, Machine machine) {
		amusementParkRepository.decreaseCapitalById(machine.getPrice(), amusementPark.getId());
		machine.setAmusementPark(amusementPark);
		return machineRepository.save(machine);
	}

	@Override
	public Machine findOne(Long amusementParkId, Long machineId) {
		return ifNull(machineRepository.findByAmusementParkIdAndMachineId(amusementParkId, machineId),
				NO_MACHINE_IN_PARK_WITH_ID);
	}

	@Override
	public List<Machine> findAllByAmusementParkId(Long amusementParkId) {
		return machineRepository.findAllByAmusementParkId(amusementParkId);
	}

	@Override
	public void removeMachine(Long amusementParkId, Long machineId) {
		Machine machine = ifNull(machineRepository.findByAmusementParkIdAndMachineId(amusementParkId, machineId),
				NO_MACHINE_IN_PARK_WITH_ID);
		ifNotZero(visitorRepository.countByMachineId(machineId), VISITORS_ON_MACHINE);
		amusementParkRepository.incrementCapitalById(machine.getPrice(), amusementParkId);
		machineRepository.deleteById(machineId);
	}

	@Override
	public Page<MachineSearchResponseDto> findAllPaged(MachineSearchRequestDto dto, Pageable pageable) {
		return machineRepository.findAll(dto, pageable);
	}

}
