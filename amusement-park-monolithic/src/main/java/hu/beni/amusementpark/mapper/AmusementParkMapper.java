package hu.beni.amusementpark.mapper;

import static hu.beni.amusementpark.factory.LinkFactory.createAmusementParkSelfLink;
import static hu.beni.amusementpark.factory.LinkFactory.createMachineLink;
import static hu.beni.amusementpark.factory.LinkFactory.createVisitorEnterParkLink;
import static hu.beni.amusementpark.factory.LinkFactory.createVisitorSignUpLink;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import hu.beni.amusementpark.controller.AmusementParkController;
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
		AmusementPark amusementPark = AmusementPark.builder() //@formatter:off
				.id(resource.getIdentifier())
				.name(resource.getName())
				.capital(resource.getCapital())
				.totalArea(resource.getTotalArea())
				.entranceFee(resource.getEntranceFee())
				.address(toEntity(resource.getAddress())).build(); //@formatter:on
		amusementPark.getAddress().setAmusementPark(amusementPark);
		return amusementPark;

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
		Long amusementParkId = amusementPark.getId();
		return new Link[] { //@formatter:off
				createAmusementParkSelfLink(amusementParkId),
				createMachineLink(amusementParkId),
				createVisitorSignUpLink(),
				createVisitorEnterParkLink(amusementParkId, null)}; //@formatter:on
	}
}