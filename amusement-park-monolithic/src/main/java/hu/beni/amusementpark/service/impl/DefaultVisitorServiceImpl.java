package hu.beni.amusementpark.service.impl;

import static hu.beni.amusementpark.constants.ErrorMessageConstants.NOT_ENOUGH_MONEY;
import static hu.beni.amusementpark.constants.ErrorMessageConstants.NO_AMUSEMENT_PARK_WITH_ID;
import static hu.beni.amusementpark.constants.ErrorMessageConstants.NO_FREE_SEAT_ON_MACHINE;
import static hu.beni.amusementpark.constants.ErrorMessageConstants.NO_MACHINE_IN_PARK_WITH_ID;
import static hu.beni.amusementpark.constants.ErrorMessageConstants.NO_VISITOR_IN_PARK_WITH_ID;
import static hu.beni.amusementpark.constants.ErrorMessageConstants.NO_VISITOR_ON_MACHINE_WITH_ID;
import static hu.beni.amusementpark.constants.ErrorMessageConstants.USERNAME_ALREADY_TAKEN;
import static hu.beni.amusementpark.constants.ErrorMessageConstants.VISITOR_IS_IN_A_PARK;
import static hu.beni.amusementpark.constants.ErrorMessageConstants.VISITOR_IS_ON_A_MACHINE;
import static hu.beni.amusementpark.constants.ErrorMessageConstants.VISITOR_IS_TOO_YOUNG;
import static hu.beni.amusementpark.constants.ErrorMessageConstants.VISITOR_NOT_SIGNED_UP;
import static hu.beni.amusementpark.constants.SpringProfileConstants.RABBIT_MQ;
import static hu.beni.amusementpark.exception.ExceptionUtil.ifEquals;
import static hu.beni.amusementpark.exception.ExceptionUtil.ifFirstLessThanSecond;
import static hu.beni.amusementpark.exception.ExceptionUtil.ifNotNull;
import static hu.beni.amusementpark.exception.ExceptionUtil.ifNotZero;
import static hu.beni.amusementpark.exception.ExceptionUtil.ifNull;
import static hu.beni.amusementpark.exception.ExceptionUtil.ifPrimitivesEquals;

import java.time.LocalDate;
import java.time.Period;

import javax.transaction.Transactional;

import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.entity.AmusementParkKnowVisitor;
import hu.beni.amusementpark.entity.Machine;
import hu.beni.amusementpark.entity.Visitor;
import hu.beni.amusementpark.enums.VisitorState;
import hu.beni.amusementpark.repository.AmusementParkKnowVisitorRepository;
import hu.beni.amusementpark.repository.AmusementParkRepository;
import hu.beni.amusementpark.repository.MachineRepository;
import hu.beni.amusementpark.repository.VisitorRepository;
import hu.beni.amusementpark.service.VisitorService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
@Profile("!" + RABBIT_MQ)
public class DefaultVisitorServiceImpl implements VisitorService {

	private final AmusementParkRepository amusementParkRepository;
	private final MachineRepository machineRepository;
	private final VisitorRepository visitorRepository;
	private final AmusementParkKnowVisitorRepository amusementParkKnowVisitorRepository;

	@Override
	public Visitor findByUsernameReadAuthorityAndSpendingMoneyAndPhoto() {
		return visitorRepository.findByUsernameReadAuthorityAndSpendingMoneyAndPhoto();
	}

	@Override
	public Integer findSpendingMoneyByUsername() {
		return visitorRepository.findSpendingMoneyByUsername();
	}

	@Override
	public Visitor signUp(Visitor visitor) {
		ifNotZero(visitorRepository.countByUsername(visitor.getUsername()),
				String.format(USERNAME_ALREADY_TAKEN, visitor.getUsername()));
		visitor.setSpendingMoney(100);
		return visitorRepository.save(visitor);
	}

	@Override
	public Visitor findOne(Long visitorId) {
		return ifNull(visitorRepository.findById(visitorId), VISITOR_NOT_SIGNED_UP);
	}

	@Override
	public Visitor enterPark(Long amusementParkId, Long visitorId) {
		return enterPark(findAmusementParkIdAndEntranceFeeByIdExceptionIfNotFound(amusementParkId), visitorId);
	}

	protected AmusementPark findAmusementParkIdAndEntranceFeeByIdExceptionIfNotFound(Long amusementParkId) {
		return ifNull(amusementParkRepository.findByIdReadOnlyIdAndEntranceFee(amusementParkId),
				NO_AMUSEMENT_PARK_WITH_ID);
	}

	protected Visitor enterPark(AmusementPark amusementPark, Long visitorId) {
		Visitor visitor = ifNull(visitorRepository.findById(visitorId), VISITOR_NOT_SIGNED_UP);
		checkIfVisitorAbleToEnterPark(amusementPark.getEntranceFee(), visitor);
		addToKnownVisitorsIfFirstEnter(amusementPark, visitor);
		incrementCaitalAndDecreaseSpendingMoneyAndSetPark(amusementPark, visitor);
		return visitor;
	}

	private void checkIfVisitorAbleToEnterPark(Integer entranceFee, Visitor visitor) {
		Integer spendingMoney = visitor.getSpendingMoney();
		ifFirstLessThanSecond(spendingMoney, entranceFee, NOT_ENOUGH_MONEY);
		ifNotNull(visitor.getAmusementPark(), VISITOR_IS_IN_A_PARK);
	}

	private void addToKnownVisitorsIfFirstEnter(AmusementPark amusementPark, Visitor visitor) {
		if (amusementParkKnowVisitorRepository.countByAmusementParkIdAndVisitorId(amusementPark.getId(),
				visitor.getId()) == 0) {
			amusementParkKnowVisitorRepository.save(new AmusementParkKnowVisitor(amusementPark, visitor));
		}
	}

	private void incrementCaitalAndDecreaseSpendingMoneyAndSetPark(AmusementPark amusementPark, Visitor visitor) {
		visitor.setSpendingMoney(visitor.getSpendingMoney() - amusementPark.getEntranceFee());
		visitor.setState(VisitorState.REST);
		visitor.setAmusementPark(amusementPark);
		amusementParkRepository.incrementCapitalById(amusementPark.getEntranceFee(), amusementPark.getId());
	}

	@Override
	public Visitor getOnMachine(Long amusementParkId, Long machineId, Long visitorId) {
		return getOnMachine(amusementParkId,
				findMachineByIdAndAmusementParkIdExceptionIfNotFound(amusementParkId, machineId), visitorId);
	}

	protected Machine findMachineByIdAndAmusementParkIdExceptionIfNotFound(Long amusementParkId, Long machineId) {
		return ifNull(machineRepository.findByAmusementParkIdAndMachineId(amusementParkId, machineId),
				NO_MACHINE_IN_PARK_WITH_ID);
	}

	protected Visitor getOnMachine(Long amusementParkId, Machine machine, Long visitorId) {
		Visitor visitor = ifNull(visitorRepository.findByAmusementParkIdAndVisitorId(amusementParkId, visitorId),
				NO_VISITOR_IN_PARK_WITH_ID);
		checkIfVisitorAbleToGetOnMachine(machine, visitor);
		incrementCapitalAndDecreaseSpendingMoneyAndSetMachine(amusementParkId, machine, visitor);
		return visitor;
	}

	private void checkIfVisitorAbleToGetOnMachine(Machine machine, Visitor visitor) {
		ifEquals(VisitorState.ON_MACHINE, visitor.getState(), VISITOR_IS_ON_A_MACHINE);
		ifFirstLessThanSecond(visitor.getSpendingMoney(), machine.getTicketPrice(), NOT_ENOUGH_MONEY);
		ifFirstLessThanSecond(Period.between(visitor.getDateOfBirth(), LocalDate.now()).getYears(),
				machine.getMinimumRequiredAge(), VISITOR_IS_TOO_YOUNG);
		ifPrimitivesEquals(visitorRepository.countByMachineId(machine.getId()), machine.getNumberOfSeats(),
				NO_FREE_SEAT_ON_MACHINE);
	}

	private void incrementCapitalAndDecreaseSpendingMoneyAndSetMachine(Long amusementParkId, Machine machine,
			Visitor visitor) {
		amusementParkRepository.incrementCapitalById(machine.getTicketPrice(), amusementParkId);
		visitor.setSpendingMoney(visitor.getSpendingMoney() - machine.getTicketPrice());
		visitor.setMachine(machine);
		visitor.setState(VisitorState.ON_MACHINE);
	}

	@Override
	public Visitor getOffMachine(Long machineId, Long visitorId) {
		Visitor visitor = ifNull(visitorRepository.findByMachineIdAndVisitorId(machineId, visitorId),
				NO_VISITOR_ON_MACHINE_WITH_ID);
		visitor.setMachine(null);
		visitor.setState(VisitorState.REST);
		return visitor;
	}

	@Override
	public Visitor leavePark(Long amusementParkId, Long visitorId) {
		Visitor visitor = ifNull(visitorRepository.findByAmusementParkIdAndVisitorId(amusementParkId, visitorId),
				NO_VISITOR_IN_PARK_WITH_ID);
		visitor.setAmusementPark(null);
		visitor.setState(null);
		return visitor;
	}

	@Override
	public Page<Visitor> findAll(Pageable pageable) {
		return visitorRepository.findAll(pageable);
	}

	@Override
	public void delete(Long visitorId) {
		visitorRepository.deleteById(visitorId);
	}
}