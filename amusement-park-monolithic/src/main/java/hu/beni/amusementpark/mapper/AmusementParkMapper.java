package hu.beni.amusementpark.mapper;

import static hu.beni.amusementpark.constants.HATEOASLinkNameConstants.MACHINE;
import static hu.beni.amusementpark.constants.HATEOASLinkNameConstants.VISITOR_ENTER_PARK;
import static hu.beni.amusementpark.constants.HATEOASLinkNameConstants.VISITOR_SIGN_UP;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import hu.beni.amusementpark.controller.AmusementParkController;
import hu.beni.amusementpark.controller.MachineController;
import hu.beni.amusementpark.controller.VisitorController;
import hu.beni.amusementpark.entity.Address;
import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.clientsupport.dto.AddressDTO;
import hu.beni.clientsupport.resource.AmusementParkResource;

@Component
@ConditionalOnWebApplication
public class AmusementParkMapper extends EntityMapper<AmusementPark, AmusementParkResource> {

	public AmusementParkMapper(PagedResourcesAssembler<AmusementPark> pagedResourcesAssembler) {
		super(AmusementParkController.class, AmusementParkResource.class, pagedResourcesAssembler);
	}

	@Override
	public AmusementParkResource toResource(AmusementPark entity) {
		return AmusementParkResource.builder() //@formatter:off
				.identifier(entity.getId())
				.name(entity.getName())
				.capital(entity.getCapital())
				.totalArea(entity.getTotalArea())
				.entranceFee(entity.getEntranceFee())
				.address(toDTO(entity.getAddress()))
				.links(createLinks(entity)).build(); //@formatter:on
	}

	@Override
	public AmusementPark toEntity(AmusementParkResource resource) {
		return AmusementPark.builder() //@formatter:off
				.id(resource.getIdentifier())
				.name(resource.getName())
				.capital(resource.getCapital())
				.totalArea(resource.getTotalArea())
				.entranceFee(resource.getEntranceFee())
				.address(toEntity(resource.getAddress())).build(); //@formatter:on
	}

	private AddressDTO toDTO(Address entity) {
		return AddressDTO.builder() //@formatter:off
				.identifier(entity.getId())
				.country(entity.getCountry())
				.zipCode(entity.getZipCode())
				.city(entity.getCity())
				.street(entity.getStreet())			
				.houseNumber(entity.getHouseNumber()).build(); //@formatter:on
	}

	private Address toEntity(AddressDTO dto) {
		return Address.builder() //@formatter:off
				.id(dto.getIdentifier())
				.country(dto.getCountry())
				.zipCode(dto.getZipCode())
				.city(dto.getCity())
				.street(dto.getStreet())			
				.houseNumber(dto.getHouseNumber()).build(); //@formatter:on
	}

	private Link[] createLinks(AmusementPark amusementPark) {
		Long id = amusementPark.getId();
		return new Link[] { linkTo(methodOn(AmusementParkController.class).findOne(id)).withSelfRel(),
				linkTo(methodOn(MachineController.class).addMachine(id, null)).withRel(MACHINE),
				linkTo(methodOn(VisitorController.class).signUp(null, null)).withRel(VISITOR_SIGN_UP),
				linkTo(methodOn(VisitorController.class).enterPark(id, null)).withRel(VISITOR_ENTER_PARK) };
	}
}