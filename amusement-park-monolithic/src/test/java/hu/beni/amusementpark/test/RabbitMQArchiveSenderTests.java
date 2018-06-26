package hu.beni.amusementpark.test;

import static hu.beni.amusementpark.constants.StringParamConstants.OPINION_ON_THE_PARK;
import static hu.beni.amusementpark.helper.ValidEntityFactory.createAmusementParkWithAddress;
import static hu.beni.amusementpark.helper.ValidEntityFactory.createMachine;
import static hu.beni.amusementpark.helper.ValidEntityFactory.createVisitor;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.support.TransactionTemplate;

import hu.beni.amusementpark.AmusementParkApplication;
import hu.beni.amusementpark.entity.Address;
import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.entity.GuestBookRegistry;
import hu.beni.amusementpark.entity.Machine;
import hu.beni.amusementpark.entity.Visitor;
import hu.beni.amusementpark.helper.RabbitMQTestConfig;
import hu.beni.amusementpark.helper.RabbitMQTestConfig.Receiver;
import hu.beni.amusementpark.service.AmusementParkService;
import hu.beni.amusementpark.service.GuestBookRegistryService;
import hu.beni.amusementpark.service.MachineService;
import hu.beni.amusementpark.service.VisitorService;
import hu.beni.clientsupport.dto.AddressDTO;
import hu.beni.clientsupport.dto.ArchiveAmusementParkDTO;
import hu.beni.clientsupport.dto.ArchiveGuestBookRegistryDTO;
import hu.beni.clientsupport.dto.ArchiveMachineDTO;
import hu.beni.clientsupport.dto.ArchiveVisitorDTO;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE, classes = { AmusementParkApplication.class,
		RabbitMQTestConfig.class })
public class RabbitMQArchiveSenderTests {

	@Autowired
	private AmusementParkService amusementParkService;

	@Autowired
	private MachineService machineService;

	@Autowired
	private VisitorService visitorService;

	@Autowired
	private GuestBookRegistryService guestBookRegistryService;

	@Autowired
	private TransactionTemplate transactionTemplate;

	@Autowired
	private Receiver receiver;

	private Long amusementParkId;
	private AmusementPark amusementPark;
	private ArchiveAmusementParkDTO receivedArchiveAmusementPark;

	@Test
	public void test() throws InterruptedException {
		createSampleAmusementPark();

		amusementPark = findAmusementParkWithoutActiveVisitors();

		amusementParkService.delete(amusementParkId);
		assertTrue(receiver.getCountDownLatch().await(10000, TimeUnit.MILLISECONDS));
		receivedArchiveAmusementPark = receiver.getReceivedArchiveAmusementParkDTO();

		assertCreatedAndReceivedAmusementParksEquals();
	}

	private void createSampleAmusementPark() {
		amusementParkId = amusementParkService.save(createAmusementParkWithAddress()).getId();
		machineService.addMachine(amusementParkId, createMachine());
		Long visitorId = visitorService.signUp(createVisitor()).getId();
		visitorService.enterPark(amusementParkId, visitorId);
		guestBookRegistryService.addRegistry(amusementParkId, visitorId, OPINION_ON_THE_PARK);
		visitorService.leavePark(amusementParkId, visitorId);
	}

	private AmusementPark findAmusementParkWithoutActiveVisitors() {
		return transactionTemplate.execute(status -> {
			AmusementPark amusementPark = amusementParkService.findByIdFetchAddress(amusementParkId);
			amusementPark.getMachines().size();
			amusementPark.getGuestBookRegistries().size();
			amusementPark.getKnownVisitors().size();
			return amusementPark;
		});
	}

	private void assertCreatedAndReceivedAmusementParksEquals() {
		assertAmusementParksEquals(amusementPark, receivedArchiveAmusementPark);
		assertAddressesEquals(amusementPark.getAddress(), receivedArchiveAmusementPark.getAddress());
		assertMachinesEquals(amusementPark.getMachines(), receivedArchiveAmusementPark.getMachines());
		assertGuestBookRegistriesEquals(amusementPark.getGuestBookRegistries(),
				receivedArchiveAmusementPark.getGuestBookRegistries());
		assertKnownVisitors(amusementPark.getKnownVisitors(), receivedArchiveAmusementPark.getKnownVisitors());
	}

	private void assertAmusementParksEquals(AmusementPark expected, ArchiveAmusementParkDTO actual) {
		assertEquals(expected.getId(), actual.getIdentifier());
		assertEquals(expected.getName(), actual.getName());
		assertEquals(expected.getCapital(), actual.getCapital());
		assertEquals(expected.getTotalArea(), actual.getTotalArea());
		assertEquals(expected.getEntranceFee(), actual.getEntranceFee());
	}

	private void assertAddressesEquals(Address expected, AddressDTO actual) {
		assertEquals(expected.getId(), actual.getIdentifier());
		assertEquals(expected.getCountry(), actual.getCountry());
		assertEquals(expected.getZipCode(), actual.getZipCode());
		assertEquals(expected.getCity(), actual.getCity());
		assertEquals(expected.getStreet(), actual.getStreet());
		assertEquals(expected.getHouseNumber(), actual.getHouseNumber());
	}

	private void assertMachinesEquals(List<Machine> expectedMachines, List<ArchiveMachineDTO> actualArchiveMachines) {
		assertEquals(expectedMachines.size(), actualArchiveMachines.size());
		Iterator<ArchiveMachineDTO> actualArchiveMachinesIterator = actualArchiveMachines.iterator();
		expectedMachines.forEach(expected -> {
			ArchiveMachineDTO actual = actualArchiveMachinesIterator.next();
			assertEquals(expected.getId(), actual.getIdentifier());
			assertEquals(expected.getFantasyName(), actual.getFantasyName());
			assertEquals(expected.getSize(), actual.getSize());
			assertEquals(expected.getPrice(), actual.getPrice());
			assertEquals(expected.getNumberOfSeats(), actual.getNumberOfSeats());
			assertEquals(expected.getMinimumRequiredAge(), actual.getMinimumRequiredAge());
			assertEquals(expected.getTicketPrice(), actual.getTicketPrice());
			assertEquals(expected.getType().toString(), actual.getType());
		});
	}

	private void assertGuestBookRegistriesEquals(List<GuestBookRegistry> expectedGuestBookRegistries,
			List<ArchiveGuestBookRegistryDTO> actualArchiveGuestBookRegistries) {
		assertEquals(expectedGuestBookRegistries.size(), actualArchiveGuestBookRegistries.size());
		Iterator<ArchiveGuestBookRegistryDTO> actualArchiveGuestBookRegistriesIterator = actualArchiveGuestBookRegistries
				.iterator();
		expectedGuestBookRegistries.forEach(expected -> {
			ArchiveGuestBookRegistryDTO actual = actualArchiveGuestBookRegistriesIterator.next();
			assertEquals(expected.getId(), actual.getIdentifier());
			assertEquals(expected.getTextOfRegistry(), actual.getTextOfRegistry());
			assertEquals(expected.getDateOfRegistry(), actual.getDateOfRegistry());
			assertEquals(expected.getVisitor().getId(), actual.getVisitorId());
		});
	}

	private void assertKnownVisitors(Set<Visitor> expectedKnownVisitors, Set<ArchiveVisitorDTO> actualKnownVisitors) {
		assertEquals(expectedKnownVisitors.size(), actualKnownVisitors.size());
		Iterator<ArchiveVisitorDTO> actualKnownVisitorsIterator = actualKnownVisitors.iterator();
		expectedKnownVisitors.forEach(expected -> {
			ArchiveVisitorDTO actual = actualKnownVisitorsIterator.next();
			assertEquals(expected.getId(), actual.getIdentifier());
			assertEquals(expected.getName(), actual.getName());
			assertEquals(expected.getUsername(), actual.getUsername());
			assertEquals(expected.getDateOfBirth(), actual.getDateOfBirth());
			assertEquals(expected.getDateOfSignUp(), actual.getDateOfSignUp());
		});
	}
}
