package hu.beni.amusementpark.service.impl;

import static hu.beni.amusementpark.constants.ErrorMessageConstants.COULD_NOT_FIND_USER;
import static hu.beni.amusementpark.constants.ErrorMessageConstants.EMAIL_ALREADY_TAKEN;
import static hu.beni.amusementpark.constants.ErrorMessageConstants.NOT_ENOUGH_MONEY;
import static hu.beni.amusementpark.constants.ErrorMessageConstants.NO_AMUSEMENT_PARK_WITH_ID;
import static hu.beni.amusementpark.constants.ErrorMessageConstants.NO_FREE_SEAT_ON_MACHINE;
import static hu.beni.amusementpark.constants.ErrorMessageConstants.NO_MACHINE_IN_PARK_WITH_ID;
import static hu.beni.amusementpark.constants.ErrorMessageConstants.NO_VISITOR_IN_PARK_WITH_ID;
import static hu.beni.amusementpark.constants.ErrorMessageConstants.NO_VISITOR_ON_MACHINE_WITH_ID;
import static hu.beni.amusementpark.constants.ErrorMessageConstants.VISITOR_IS_IN_A_PARK;
import static hu.beni.amusementpark.constants.ErrorMessageConstants.VISITOR_IS_ON_A_MACHINE;
import static hu.beni.amusementpark.constants.ErrorMessageConstants.VISITOR_IS_TOO_YOUNG;
import static hu.beni.amusementpark.constants.ErrorMessageConstants.VISITOR_NOT_SIGNED_UP;
import static hu.beni.amusementpark.exception.ExceptionUtil.ifFirstLessThanSecond;
import static hu.beni.amusementpark.exception.ExceptionUtil.ifNotNull;
import static hu.beni.amusementpark.exception.ExceptionUtil.ifNotZero;
import static hu.beni.amusementpark.exception.ExceptionUtil.ifNull;
import static hu.beni.amusementpark.exception.ExceptionUtil.ifPrimitivesEquals;

import java.time.LocalDate;
import java.time.Period;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.entity.AmusementParkKnowVisitor;
import hu.beni.amusementpark.entity.Machine;
import hu.beni.amusementpark.entity.Visitor;
import hu.beni.amusementpark.repository.AmusementParkKnowVisitorRepository;
import hu.beni.amusementpark.repository.AmusementParkRepository;
import hu.beni.amusementpark.repository.MachineRepository;
import hu.beni.amusementpark.repository.VisitorRepository;
import hu.beni.amusementpark.service.VisitorService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class VisitorServiceImpl implements VisitorService {

	private final AmusementParkRepository amusementParkRepository;
	private final MachineRepository machineRepository;
	private final VisitorRepository visitorRepository;
	private final AmusementParkKnowVisitorRepository amusementParkKnowVisitorRepository;

	@Override
	public Visitor findByEmail(String visitorEmail) {
		return ifNull(visitorRepository.findByEmail(visitorEmail), String.format(COULD_NOT_FIND_USER, visitorEmail));
	}

	@Override
	public Visitor signUp(Visitor visitor) {
		ifNotZero(visitorRepository.countByEmail(visitor.getEmail()),
				String.format(EMAIL_ALREADY_TAKEN, visitor.getEmail()));
		visitor.setSpendingMoney(250);
		return visitorRepository.save(visitor);
	}

	public void uploadMoney(Integer ammount, String visitorEmail) {
		visitorRepository.incrementSpendingMoneyByEmail(ammount, visitorEmail);
	}

	@Override
	public Visitor findOne(String visitorEmail) {
		return ifNull(visitorRepository.findById(visitorEmail), VISITOR_NOT_SIGNED_UP);
	}

	@Override
	public Visitor enterPark(Long amusementParkId, String visitorEmail) {
		return enterPark(findAmusementParkIdAndEntranceFeeByIdExceptionIfNotFound(amusementParkId), visitorEmail);
	}

	private AmusementPark findAmusementParkIdAndEntranceFeeByIdExceptionIfNotFound(Long amusementParkId) {
		return ifNull(amusementParkRepository.findByIdReadOnlyIdAndEntranceFee(amusementParkId),
				NO_AMUSEMENT_PARK_WITH_ID);
	}

	private Visitor enterPark(AmusementPark amusementPark, String visitorEmail) {
		Visitor visitor = ifNull(visitorRepository.findById(visitorEmail), VISITOR_NOT_SIGNED_UP);
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
		if (amusementParkKnowVisitorRepository.countByAmusementParkIdAndVisitorEmail(amusementPark.getId(),
				visitor.getEmail()) == 0) {
			amusementParkKnowVisitorRepository.save(new AmusementParkKnowVisitor(amusementPark, visitor));
		}
	}

	private void incrementCaitalAndDecreaseSpendingMoneyAndSetPark(AmusementPark amusementPark, Visitor visitor) {
		visitor.setSpendingMoney(visitor.getSpendingMoney() - amusementPark.getEntranceFee());
		visitor.setAmusementPark(amusementPark);
		amusementParkRepository.incrementCapitalById(amusementPark.getEntranceFee(), amusementPark.getId());
	}

	@Override
	public Visitor getOnMachine(Long amusementParkId, Long machineId, String visitorEmail) {
		return getOnMachine(amusementParkId,
				findMachineByIdAndAmusementParkIdExceptionIfNotFound(amusementParkId, machineId), visitorEmail);
	}

	private Machine findMachineByIdAndAmusementParkIdExceptionIfNotFound(Long amusementParkId, Long machineId) {
		return ifNull(machineRepository.findByAmusementParkIdAndMachineId(amusementParkId, machineId),
				NO_MACHINE_IN_PARK_WITH_ID);
	}

	private Visitor getOnMachine(Long amusementParkId, Machine machine, String visitorEmail) {
		Visitor visitor = ifNull(visitorRepository.findByAmusementParkIdAndVisitorEmail(amusementParkId, visitorEmail),
				NO_VISITOR_IN_PARK_WITH_ID);
		checkIfVisitorAbleToGetOnMachine(machine, visitor);
		incrementCapitalAndDecreaseSpendingMoneyAndSetMachine(amusementParkId, machine, visitor);
		return visitor;
	}

	private void checkIfVisitorAbleToGetOnMachine(Machine machine, Visitor visitor) {
		ifNotNull(visitor.getMachine(), VISITOR_IS_ON_A_MACHINE);
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
	}

	@Override
	public Visitor getOffMachine(Long machineId, String visitorEmail) {
		Visitor visitor = ifNull(visitorRepository.findByMachineIdAndVisitorEmail(machineId, visitorEmail),
				NO_VISITOR_ON_MACHINE_WITH_ID);
		visitor.setMachine(null);
		return visitor;
	}

	@Override
	public Visitor leavePark(Long amusementParkId, String visitorEmail) {
		Visitor visitor = ifNull(visitorRepository.findByAmusementParkIdAndVisitorEmail(amusementParkId, visitorEmail),
				NO_VISITOR_IN_PARK_WITH_ID);
		visitor.setAmusementPark(null);
		return visitor;
	}

	@Override
	public Page<Visitor> findAll(Pageable pageable) {
		return visitorRepository.findAll(pageable);
	}

	@Override
	public void delete(String visitorEmail) {
		visitorRepository.deleteById(visitorEmail);
	}
}