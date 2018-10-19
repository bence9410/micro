package hu.beni.amusementpark.service.impl;

import static hu.beni.amusementpark.constants.SpringProfileConstants.RABBIT_MQ;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.entity.Machine;
import hu.beni.amusementpark.entity.Visitor;
import hu.beni.amusementpark.repository.AmusementParkKnowVisitorRepository;
import hu.beni.amusementpark.repository.AmusementParkRepository;
import hu.beni.amusementpark.repository.MachineRepository;
import hu.beni.amusementpark.repository.VisitorRepository;
import hu.beni.clientsupport.dto.VisitorEnterParkEventDTO;
import hu.beni.clientsupport.dto.VisitorGetOnMachineEventDTO;

@Service
@Profile(RABBIT_MQ)
public class StatisticsSenderVisitorServiceImpl extends DefaultVisitorServiceImpl {

	private final ApplicationEventPublisher eventPublisher;

	public StatisticsSenderVisitorServiceImpl(AmusementParkRepository amusementParkRepository,
			MachineRepository machineRepository, VisitorRepository visitorRepository,
			AmusementParkKnowVisitorRepository amusementParkKnowVisitorRepository,
			ApplicationEventPublisher eventPublisher) {
		super(amusementParkRepository, machineRepository, visitorRepository, amusementParkKnowVisitorRepository);
		this.eventPublisher = eventPublisher;
	}

	@Override
	public Visitor enterPark(Long amusementParkId, Long visitorId) {
		AmusementPark amusementPark = findAmusementParkIdAndEntranceFeeByIdExceptionIfNotFound(amusementParkId);
		Visitor visitor = enterPark(amusementPark, visitorId);
		eventPublisher
				.publishEvent(new VisitorEnterParkEventDTO(amusementParkId, visitorId, amusementPark.getEntranceFee()));
		return visitor;
	}

	@Override
	public Visitor getOnMachine(Long amusementParkId, Long machineId, Long visitorId, String email) {
		Machine machine = findMachineByIdAndAmusementParkIdExceptionIfNotFound(amusementParkId, machineId);
		Visitor visitor = getOnMachine(amusementParkId, machine, visitorId, email);
		eventPublisher.publishEvent(
				new VisitorGetOnMachineEventDTO(amusementParkId, visitorId, machine.getTicketPrice(), machineId));
		return visitor;
	}

}
