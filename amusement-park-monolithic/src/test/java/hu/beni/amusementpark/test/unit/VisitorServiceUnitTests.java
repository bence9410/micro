package hu.beni.amusementpark.test.unit;

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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.entity.Machine;
import hu.beni.amusementpark.entity.Visitor;
import hu.beni.amusementpark.enums.VisitorState;
import hu.beni.amusementpark.exception.AmusementParkException;
import hu.beni.amusementpark.repository.AmusementParkRepository;
import hu.beni.amusementpark.repository.MachineRepository;
import hu.beni.amusementpark.repository.VisitorRepository;
import hu.beni.amusementpark.service.VisitorService;
import hu.beni.amusementpark.service.impl.VisitorServiceImpl;

public class VisitorServiceUnitTests {

	private AmusementParkRepository amusementParkRepository;
	private MachineRepository machineRepository;
	private VisitorRepository visitorRepository;

	private VisitorService visitorService;

	@Before
	public void setUp() {
		amusementParkRepository = mock(AmusementParkRepository.class);
		machineRepository = mock(MachineRepository.class);
		visitorRepository = mock(VisitorRepository.class);
		visitorService = new VisitorServiceImpl(amusementParkRepository, machineRepository, visitorRepository);
	}

	@After
	public void verifyNoMoreInteractionsOnMocks() {
		verifyNoMoreInteractions(amusementParkRepository, machineRepository, visitorRepository);
	}

	@Test
	public void findSpendingMoneyByUsernameNegativeNotSignedUp() {
		assertThatThrownBy(() -> visitorService.findSpendingMoneyByUsername())
				.isInstanceOf(AmusementParkException.class).hasMessage(VISITOR_NOT_SIGNED_UP);

		verify(visitorRepository).findSpendingMoneyByUsername();
	}

	@Test
	public void findSpendingMoneyByUsernamePositive() {
		Integer spendingMoney = 1000;

		when(visitorRepository.findSpendingMoneyByUsername()).thenReturn(Optional.of(spendingMoney));

		assertEquals(spendingMoney, visitorService.findSpendingMoneyByUsername());

		verify(visitorRepository).findSpendingMoneyByUsername();
	}

	@Test
	public void signUpPositive() {
		Visitor visitor = Visitor.builder().build();

		when(visitorRepository.save(visitor)).thenReturn(visitor);

		assertEquals(visitor, visitorService.signUp(visitor));

		verify(visitorRepository).save(visitor);
	}

	@Test
	public void findOneNegativeNotSignedUp() {
		Long visitorId = 0L;

		assertThatThrownBy(() -> visitorService.findOne(visitorId)).isInstanceOf(AmusementParkException.class)
				.hasMessage(VISITOR_NOT_SIGNED_UP);

		verify(visitorRepository).findById(visitorId);
	}

	@Test
	public void findOnePositive() {
		Visitor visitor = Visitor.builder().id(0L).build();
		Long visitorId = visitor.getId();

		when(visitorRepository.findById(visitorId)).thenReturn(Optional.of(visitor));

		assertEquals(visitor, visitorService.findOne(visitorId));

		verify(visitorRepository).findById(visitorId);
	}

	@Test
	public void enterParkNegativeNoPark() {
		Long amusementParkId = 0L;
		Long visitorId = 1L;

		assertThatThrownBy(() -> visitorService.enterPark(amusementParkId, visitorId))
				.isInstanceOf(AmusementParkException.class).hasMessage(NO_AMUSEMENT_PARK_WITH_ID);

		verify(amusementParkRepository).findByIdReadOnlyIdAndEntranceFee(amusementParkId);
	}

	@Test
	public void enterParkNegativeNotSignedUp() {
		AmusementPark amusementPark = AmusementPark.builder().id(0L).entranceFee(50).build();
		Long amusementParkId = amusementPark.getId();
		Long visitorId = 1L;

		when(amusementParkRepository.findByIdReadOnlyIdAndEntranceFee(amusementParkId))
				.thenReturn(Optional.of(amusementPark));

		assertThatThrownBy(() -> visitorService.enterPark(amusementParkId, visitorId))
				.isInstanceOf(AmusementParkException.class).hasMessage(VISITOR_NOT_SIGNED_UP);

		verify(amusementParkRepository).findByIdReadOnlyIdAndEntranceFee(amusementParkId);
		verify(visitorRepository).findById(visitorId);
	}

	@Test
	public void enterParkNegativeNotEnoughMoney() {
		AmusementPark amusementPark = AmusementPark.builder().id(0L).entranceFee(50).build();
		Long amusementParkId = amusementPark.getId();
		Visitor visitor = Visitor.builder().id(1L).spendingMoney(20).build();
		Long visitorId = visitor.getId();

		when(amusementParkRepository.findByIdReadOnlyIdAndEntranceFee(amusementParkId))
				.thenReturn(Optional.of(amusementPark));
		when(visitorRepository.findById(visitorId)).thenReturn(Optional.of(visitor));

		assertThatThrownBy(() -> visitorService.enterPark(amusementParkId, visitorId))
				.isInstanceOf(AmusementParkException.class).hasMessage(NOT_ENOUGH_MONEY);

		verify(amusementParkRepository).findByIdReadOnlyIdAndEntranceFee(amusementParkId);
		verify(visitorRepository).findById(visitorId);
	}

	@Test
	public void enterParkNegativeVisitorIsInAPark() {
		AmusementPark amusementPark = AmusementPark.builder().id(0L).entranceFee(50).build();
		Long amusementParkId = amusementPark.getId();
		Visitor visitor = Visitor.builder().id(1L).spendingMoney(100).amusementPark(amusementPark).build();
		Long visitorId = visitor.getId();

		when(amusementParkRepository.findByIdReadOnlyIdAndEntranceFee(amusementParkId))
				.thenReturn(Optional.of(amusementPark));
		when(visitorRepository.findById(visitorId)).thenReturn(Optional.of(visitor));

		assertThatThrownBy(() -> visitorService.enterPark(amusementParkId, visitorId))
				.isInstanceOf(AmusementParkException.class).hasMessage(VISITOR_IS_IN_A_PARK);

		verify(amusementParkRepository).findByIdReadOnlyIdAndEntranceFee(amusementParkId);
		verify(visitorRepository).findById(visitorId);
	}

	@Test
	public void enterParkPositiveAddVisitorToKnown() {
		AmusementPark amusementPark = AmusementPark.builder().id(0L).entranceFee(50).build();
		Long amusementParkId = amusementPark.getId();
		Visitor visitor = Visitor.builder().id(1L).spendingMoney(100).build();
		Long visitorId = visitor.getId();
		Integer spendingMoney = visitor.getSpendingMoney();

		when(amusementParkRepository.findByIdReadOnlyIdAndEntranceFee(amusementParkId))
				.thenReturn(Optional.of(amusementPark));
		when(visitorRepository.findById(visitorId)).thenReturn(Optional.of(visitor));

		assertEquals(visitor, visitorService.enterPark(amusementParkId, visitorId));

		assertEquals(spendingMoney - amusementPark.getEntranceFee(), visitor.getSpendingMoney().longValue());
		assertEquals(amusementPark, visitor.getAmusementPark());
		assertEquals(VisitorState.REST, visitor.getState());

		verify(amusementParkRepository).findByIdReadOnlyIdAndEntranceFee(amusementParkId);
		verify(visitorRepository).findById(visitorId);
		verify(amusementParkRepository).countKnownVisitor(amusementParkId, visitorId);
		verify(amusementParkRepository).addKnownVisitor(amusementParkId, visitorId);
		verify(amusementParkRepository).incrementCapitalById(amusementPark.getEntranceFee(), amusementParkId);
	}

	@Test
	public void enterParkPositiveVisitorAlreadyKnown() {
		AmusementPark amusementPark = AmusementPark.builder().id(0L).entranceFee(50).build();
		Long amusementParkId = amusementPark.getId();
		Visitor visitor = Visitor.builder().id(1L).spendingMoney(100).build();
		Long visitorId = visitor.getId();
		Long numberOfKnowsVisitorsById = 1L;
		Integer spendingMoney = visitor.getSpendingMoney();

		when(amusementParkRepository.findByIdReadOnlyIdAndEntranceFee(amusementParkId))
				.thenReturn(Optional.of(amusementPark));
		when(visitorRepository.findById(visitorId)).thenReturn(Optional.of(visitor));
		when(amusementParkRepository.countKnownVisitor(amusementParkId, visitorId))
				.thenReturn(numberOfKnowsVisitorsById);

		assertEquals(visitor, visitorService.enterPark(amusementParkId, visitorId));

		assertEquals(spendingMoney - amusementPark.getEntranceFee(), visitor.getSpendingMoney().longValue());
		assertEquals(amusementPark, visitor.getAmusementPark());
		assertEquals(VisitorState.REST, visitor.getState());

		verify(amusementParkRepository).findByIdReadOnlyIdAndEntranceFee(amusementParkId);
		verify(visitorRepository).findById(visitorId);
		verify(amusementParkRepository).countKnownVisitor(amusementParkId, visitorId);
		verify(amusementParkRepository).incrementCapitalById(amusementPark.getEntranceFee(), amusementParkId);
	}

	@Test
	public void getOnMachineNegativeNoMachineInPark() {
		Long amusementParkId = 0L;
		Long machineId = 1L;
		Long visitorId = 2L;

		assertThatThrownBy(() -> visitorService.getOnMachine(amusementParkId, machineId, visitorId))
				.isInstanceOf(AmusementParkException.class).hasMessage(NO_MACHINE_IN_PARK_WITH_ID);

		verify(machineRepository).findByAmusementParkIdAndMachineId(amusementParkId, machineId);
	}

	@Test
	public void getOnMachineNegativeNoVisitorInPark() {
		Long amusementParkId = 0L;
		Machine machine = Machine.builder().id(1L).build();
		Long machineId = machine.getId();
		Long visitorId = 2L;

		when(machineRepository.findByAmusementParkIdAndMachineId(amusementParkId, machineId))
				.thenReturn(Optional.of(machine));

		assertThatThrownBy(() -> visitorService.getOnMachine(amusementParkId, machineId, visitorId))
				.isInstanceOf(AmusementParkException.class).hasMessage(NO_VISITOR_IN_PARK_WITH_ID);

		verify(machineRepository).findByAmusementParkIdAndMachineId(amusementParkId, machineId);
		verify(visitorRepository).findByAmusementParkIdAndVisitorId(amusementParkId, visitorId);
	}

	@Test
	public void getOnMachineNegativeOnMachine() {
		Long amusementParkId = 0L;
		Machine machine = Machine.builder().id(1L).build();
		Long machineId = machine.getId();
		Visitor visitor = Visitor.builder().id(2L).state(VisitorState.ON_MACHINE).build();
		Long visitorId = visitor.getId();

		when(machineRepository.findByAmusementParkIdAndMachineId(amusementParkId, machineId))
				.thenReturn(Optional.of(machine));
		when(visitorRepository.findByAmusementParkIdAndVisitorId(amusementParkId, visitorId))
				.thenReturn(Optional.of(visitor));

		assertThatThrownBy(() -> visitorService.getOnMachine(amusementParkId, machineId, visitorId))
				.isInstanceOf(AmusementParkException.class).hasMessage(VISITOR_IS_ON_A_MACHINE);

		verify(machineRepository).findByAmusementParkIdAndMachineId(amusementParkId, machineId);
		verify(visitorRepository).findByAmusementParkIdAndVisitorId(amusementParkId, visitorId);
	}

	@Test
	public void getOnMachineNegativeNotEnoughtMoney() {
		Long amusementParkId = 0L;
		Machine machine = Machine.builder().id(1L).ticketPrice(50).build();
		Long machineId = machine.getId();
		Visitor visitor = Visitor.builder().id(2L).spendingMoney(40).build();
		Long visitorId = visitor.getId();

		when(machineRepository.findByAmusementParkIdAndMachineId(amusementParkId, machineId))
				.thenReturn(Optional.of(machine));
		when(visitorRepository.findByAmusementParkIdAndVisitorId(amusementParkId, visitorId))
				.thenReturn(Optional.of(visitor));

		assertThatThrownBy(() -> visitorService.getOnMachine(amusementParkId, machineId, visitorId))
				.isInstanceOf(AmusementParkException.class).hasMessage(NOT_ENOUGH_MONEY);

		verify(machineRepository).findByAmusementParkIdAndMachineId(amusementParkId, machineId);
		verify(visitorRepository).findByAmusementParkIdAndVisitorId(amusementParkId, visitorId);
	}

	@Test
	public void getOnMachineNegativeTooYoung() {
		Long amusementParkId = 0L;
		Machine machine = Machine.builder().id(1L).ticketPrice(20).minimumRequiredAge(20).build();
		Long machineId = machine.getId();
		Visitor visitor = Visitor.builder().id(2L).spendingMoney(40).dateOfBirth(LocalDate.now()).build();
		Long visitorId = visitor.getId();

		when(machineRepository.findByAmusementParkIdAndMachineId(amusementParkId, machineId))
				.thenReturn(Optional.of(machine));
		when(visitorRepository.findByAmusementParkIdAndVisitorId(amusementParkId, visitorId))
				.thenReturn(Optional.of(visitor));

		assertThatThrownBy(() -> visitorService.getOnMachine(amusementParkId, machineId, visitorId))
				.isInstanceOf(AmusementParkException.class).hasMessage(VISITOR_IS_TOO_YOUNG);

		verify(machineRepository).findByAmusementParkIdAndMachineId(amusementParkId, machineId);
		verify(visitorRepository).findByAmusementParkIdAndVisitorId(amusementParkId, visitorId);
	}

	@Test
	public void getOnMachineNegativeNoFreeSeat() {
		Long amusementParkId = 0L;
		Machine machine = Machine.builder().id(1L).ticketPrice(20).minimumRequiredAge(20).numberOfSeats(10).build();
		Long machineId = machine.getId();
		Visitor visitor = Visitor.builder().id(2L).spendingMoney(40).dateOfBirth(LocalDate.of(1990, 10, 20)).build();
		Long visitorId = visitor.getId();

		when(machineRepository.findByAmusementParkIdAndMachineId(amusementParkId, machineId))
				.thenReturn(Optional.of(machine));
		when(visitorRepository.findByAmusementParkIdAndVisitorId(amusementParkId, visitorId))
				.thenReturn(Optional.of(visitor));
		when(visitorRepository.countByMachineId(machineId)).thenReturn(10L);

		assertThatThrownBy(() -> visitorService.getOnMachine(amusementParkId, machineId, visitorId))
				.isInstanceOf(AmusementParkException.class).hasMessage(NO_FREE_SEAT_ON_MACHINE);

		verify(machineRepository).findByAmusementParkIdAndMachineId(amusementParkId, machineId);
		verify(visitorRepository).findByAmusementParkIdAndVisitorId(amusementParkId, visitorId);
		verify(visitorRepository).countByMachineId(machineId);
	}

	@Test
	public void getOnMachinePositive() {
		Long amusementParkId = 0L;
		Machine machine = Machine.builder().id(1L).ticketPrice(20).minimumRequiredAge(20).numberOfSeats(10).build();
		Long machineId = machine.getId();
		Visitor visitor = Visitor.builder().id(2L).spendingMoney(40).dateOfBirth(LocalDate.of(1990, 10, 20)).build();
		Long visitorId = visitor.getId();
		Integer spendingMoney = visitor.getSpendingMoney();

		when(machineRepository.findByAmusementParkIdAndMachineId(amusementParkId, machineId))
				.thenReturn(Optional.of(machine));
		when(visitorRepository.findByAmusementParkIdAndVisitorId(amusementParkId, visitorId))
				.thenReturn(Optional.of(visitor));
		when(visitorRepository.countByMachineId(machineId)).thenReturn(1L);

		assertEquals(visitor, visitorService.getOnMachine(amusementParkId, machineId, visitorId));

		assertEquals(spendingMoney - machine.getTicketPrice(), visitor.getSpendingMoney().longValue());
		assertEquals(machine, visitor.getMachine());
		assertEquals(VisitorState.ON_MACHINE, visitor.getState());

		verify(machineRepository).findByAmusementParkIdAndMachineId(amusementParkId, machineId);
		verify(visitorRepository).findByAmusementParkIdAndVisitorId(amusementParkId, visitorId);
		verify(visitorRepository).countByMachineId(machineId);
		verify(amusementParkRepository).incrementCapitalById(machine.getTicketPrice(), amusementParkId);
	}

	@Test
	public void getOffMachineNegativeNoVisitor() {
		Long machineId = 0L;
		Long visitorId = 1L;

		assertThatThrownBy(() -> visitorService.getOffMachine(machineId, visitorId))
				.isInstanceOf(AmusementParkException.class).hasMessage(NO_VISITOR_ON_MACHINE_WITH_ID);

		verify(visitorRepository).findByMachineIdAndVisitorId(machineId, visitorId);
	}

	@Test
	public void getOffMachinePositive() {
		Long machineId = 0L;
		Visitor visitor = Visitor.builder().id(1L).state(VisitorState.ON_MACHINE).machine(Machine.builder().build())
				.build();
		Long visitorId = visitor.getId();

		when(visitorRepository.findByMachineIdAndVisitorId(machineId, visitorId)).thenReturn(Optional.of(visitor));

		assertEquals(visitor, visitorService.getOffMachine(machineId, visitorId));

		assertNull(visitor.getMachine());
		assertEquals(VisitorState.REST, visitor.getState());

		verify(visitorRepository).findByMachineIdAndVisitorId(machineId, visitorId);
	}

	@Test
	public void leaveParkNegativeNoVisitorInPark() {
		Long amusementParkId = 0L;
		Long visitorId = 1L;

		assertThatThrownBy(() -> visitorService.leavePark(amusementParkId, visitorId))
				.isInstanceOf(AmusementParkException.class).hasMessage(NO_VISITOR_IN_PARK_WITH_ID);

		verify(visitorRepository).findByAmusementParkIdAndVisitorId(amusementParkId, visitorId);
	}

	@Test
	public void leaveParkPositive() {
		Long amusementParkId = 0L;
		Visitor visitor = Visitor.builder().id(1L).amusementPark(AmusementPark.builder().build())
				.state(VisitorState.REST).spendingMoney(100).build();
		Long visitorId = visitor.getId();

		when(visitorRepository.findByAmusementParkIdAndVisitorId(amusementParkId, visitorId))
				.thenReturn(Optional.of(visitor));

		visitorService.leavePark(amusementParkId, visitorId);

		assertNull(visitor.getAmusementPark());
		assertNull(visitor.getState());
		assertNotNull(visitor.getSpendingMoney());

		verify(visitorRepository).findByAmusementParkIdAndVisitorId(amusementParkId, visitorId);
	}

	@Test
	public void findAllPositive() {
		Page<Visitor> page = new PageImpl<>(
				Arrays.asList(Visitor.builder().id(0L).build(), Visitor.builder().id(1L).build()));
		Pageable pageable = PageRequest.of(0, 10);

		when(visitorRepository.findAll(pageable)).thenReturn(page);

		assertEquals(page, visitorService.findAll(pageable));

		verify(visitorRepository).findAll(pageable);
	}

	@Test
	public void deletePositive() {
		Long visitorId = 0L;

		visitorService.delete(visitorId);

		verify(visitorRepository).deleteById(visitorId);
	}
}