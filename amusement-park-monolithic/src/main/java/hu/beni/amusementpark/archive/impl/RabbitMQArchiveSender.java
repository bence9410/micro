package hu.beni.amusementpark.archive.impl;

import static hu.beni.amusementpark.constants.RabbitMQConstants.QUEUE_NAME;
import static hu.beni.amusementpark.constants.SpringProfileConstants.ORACLE_DB;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import hu.beni.amusementpark.archive.ArchiveSender;
import hu.beni.amusementpark.entity.Address;
import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.entity.GuestBookRegistry;
import hu.beni.amusementpark.entity.Machine;
import hu.beni.amusementpark.entity.Visitor;
import hu.beni.clientsupport.dto.AddressDTO;
import hu.beni.clientsupport.dto.ArchiveAmusementParkDTO;
import hu.beni.clientsupport.dto.ArchiveGuestBookRegistryDTO;
import hu.beni.clientsupport.dto.ArchiveMachineDTO;
import hu.beni.clientsupport.dto.ArchiveVisitorDTO;
import lombok.RequiredArgsConstructor;

@Component
@Profile(ORACLE_DB)
@RequiredArgsConstructor
public class RabbitMQArchiveSender implements ArchiveSender {

	private final RabbitTemplate rabbitTemplate;

	@Override
	public void sendToArchive(AmusementPark amusementPark) {
		ArchiveAmusementParkDTO archiveAmusementParkDTO = convertToArchiveAmusementPark(amusementPark);
		archiveAmusementParkDTO.setAddress(convertToAddressDTO(amusementPark.getAddress()));
		archiveAmusementParkDTO
				.setMachines(amusementPark.getMachines().stream().map(this::convertToArchiveMachine).collect(toList()));
		archiveAmusementParkDTO.setKnownVisitors(
				amusementPark.getKnownVisitors().stream().map(this::convertToArchiveVisitor).collect(toSet()));
		archiveAmusementParkDTO.setGuestBookRegistries(amusementPark.getGuestBookRegistries().stream()
				.map(this::convertToArchiveGuestBookRegistry).collect(toList()));
		rabbitTemplate.convertAndSend(QUEUE_NAME, archiveAmusementParkDTO);
	}

	private ArchiveAmusementParkDTO convertToArchiveAmusementPark(AmusementPark amusementPark) {
		return ArchiveAmusementParkDTO.builder() //@formatter:off
				.identifier(amusementPark.getId())
				.name(amusementPark.getName())
				.capital(amusementPark.getCapital())
				.totalArea(amusementPark.getTotalArea())
				.entranceFee(amusementPark.getEntranceFee()).build(); //@formatter:on
	}

	private AddressDTO convertToAddressDTO(Address address) {
		return AddressDTO.builder() //@formatter:off
					.identifier(address.getId())
					.country(address.getCountry())
					.zipCode(address.getZipCode())
					.city(address.getCity())
					.street(address.getStreet())			
					.houseNumber(address.getHouseNumber()).build(); //@formatter:on
	}

	private ArchiveMachineDTO convertToArchiveMachine(Machine machine) {
		return ArchiveMachineDTO.builder() //@formatter:off
				.identifier(machine.getId())
				.fantasyName(machine.getFantasyName())
				.size(machine.getSize())
				.price(machine.getPrice())
				.numberOfSeats(machine.getNumberOfSeats())
				.minimumRequiredAge(machine.getMinimumRequiredAge())
				.ticketPrice(machine.getTicketPrice())
				.type(machine.getType().toString()).build(); //@formatter:on
	}

	private ArchiveVisitorDTO convertToArchiveVisitor(Visitor visitor) {
		return ArchiveVisitorDTO.builder() //@formatter:off
				.identifier(visitor.getId())
				.name(visitor.getName())
				.username(visitor.getUsername())
				.dateOfBirth(visitor.getDateOfBirth())
				.dateOfSignUp(visitor.getDateOfSignUp()).build(); //@formatter:on
	}

	private ArchiveGuestBookRegistryDTO convertToArchiveGuestBookRegistry(GuestBookRegistry guestBookRegistry) {
		return ArchiveGuestBookRegistryDTO.builder() //@formatter:off
				.identifier(guestBookRegistry.getId())
				.textOfRegistry(guestBookRegistry.getTextOfRegistry())
				.dateOfRegistry(guestBookRegistry.getDateOfRegistry())
				.visitorId(guestBookRegistry.getVisitor().getId()).build(); //@formatter:on
	}

}
