package hu.beni.amusementpark.mapper;

import static hu.beni.amusementpark.constants.ErrorMessageConstants.validationError;
import static hu.beni.amusementpark.factory.LinkFactory.createGetOnMachineLink;
import static hu.beni.amusementpark.factory.LinkFactory.createMachineSelfLink;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import hu.beni.amusementpark.controller.MachineController;
import hu.beni.amusementpark.entity.Machine;
import hu.beni.amusementpark.enums.MachineType;
import hu.beni.amusementpark.exception.AmusementParkException;
import hu.beni.clientsupport.resource.MachineResource;

@Component
@ConditionalOnWebApplication
public class MachineMapper extends EntityMapper<Machine, MachineResource> {

	public MachineMapper(PagedResourcesAssembler<Machine> pagedResourcesAssembler) {
		super(MachineController.class, MachineResource.class, pagedResourcesAssembler);
	}

	@Override
	public MachineResource toResource(Machine entity) {
		return MachineResource.builder() //@formatter:off
				.identifier(entity.getId())
				.fantasyName(entity.getFantasyName())
				.size(entity.getSize())
				.price(entity.getPrice())
				.numberOfSeats(entity.getNumberOfSeats())
				.minimumRequiredAge(entity.getMinimumRequiredAge())
				.ticketPrice(entity.getTicketPrice())
				.type(entity.getType().toString())
				.links(createLinks(entity)).build(); //@formatter:on
	}

	@Override
	public Machine toEntity(MachineResource resource) {
		return Machine.builder() //@formatter:off
				.id(resource.getIdentifier())
				.fantasyName(resource.getFantasyName())
				.size(resource.getSize())
				.price(resource.getPrice())
				.numberOfSeats(resource.getNumberOfSeats())
				.minimumRequiredAge(resource.getMinimumRequiredAge())
				.ticketPrice(resource.getTicketPrice())
				.type(MachineType.valueOf(isValidMachineType(resource.getType()))).build(); //@formatter:on
	}

	private Link[] createLinks(Machine machine) {
		Long amusementParkId = machine.getAmusementPark().getId();
		Long machineId = machine.getId();
		return new Link[] { //@formatter:off
				createMachineSelfLink(amusementParkId, machineId),
				createGetOnMachineLink(amusementParkId, machineId) }; //@formatter:off
	}
	
	private String isValidMachineType(String value) {
		Set<String> machineTypes = Stream.of(MachineType.values()).map(MachineType::name)
				.collect(Collectors.toSet());
		return Optional.of(value).filter(machineTypes::contains).orElseThrow(() -> 
			new AmusementParkException(validationError("type", String.format("must be one of %s", machineTypes.toString()))));		
	}
}
