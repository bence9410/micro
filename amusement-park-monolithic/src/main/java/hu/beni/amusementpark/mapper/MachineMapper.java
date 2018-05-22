package hu.beni.amusementpark.mapper;

import static hu.beni.amusementpark.factory.LinkFactory.createGetOnMachineLink;
import static hu.beni.amusementpark.factory.LinkFactory.createMachineSelfLink;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import hu.beni.amusementpark.controller.MachineController;
import hu.beni.amusementpark.entity.Machine;
import hu.beni.amusementpark.enums.MachineType;
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
				.type(MachineType.valueOf(resource.getType())).build(); //@formatter:on
	}

	private Link[] createLinks(Machine machine) {
		Long amusementParkId = machine.getAmusementPark().getId();
		Long machineId = machine.getId();
		return new Link[] { //@formatter:off
				createMachineSelfLink(amusementParkId, machineId),
				createGetOnMachineLink(amusementParkId, machineId, null) }; //@formatter:off
	}
}
