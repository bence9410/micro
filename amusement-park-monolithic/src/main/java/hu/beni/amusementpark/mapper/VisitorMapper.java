package hu.beni.amusementpark.mapper;

import static hu.beni.amusementpark.factory.LinkFactory.createAddGuestBookRegistryLink;
import static hu.beni.amusementpark.factory.LinkFactory.createAmusementParkLink;
import static hu.beni.amusementpark.factory.LinkFactory.createGetOffMachineLink;
import static hu.beni.amusementpark.factory.LinkFactory.createGetOnMachineLink;
import static hu.beni.amusementpark.factory.LinkFactory.createMachineLink;
import static hu.beni.amusementpark.factory.LinkFactory.createMeLinkWithSelfRel;
import static hu.beni.amusementpark.factory.LinkFactory.createVisitorEnterParkLink;
import static hu.beni.amusementpark.factory.LinkFactory.createVisitorLeavePark;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import hu.beni.amusementpark.controller.VisitorController;
import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.entity.Machine;
import hu.beni.amusementpark.entity.Visitor;
import hu.beni.clientsupport.resource.VisitorResource;

@Component
@ConditionalOnWebApplication
public class VisitorMapper extends EntityMapper<Visitor, VisitorResource> {

	private final PasswordEncoder passwordEncoder;

	public VisitorMapper(PagedResourcesAssembler<Visitor> pagedResourcesAssembler, PasswordEncoder passwordEncoder) {
		super(VisitorController.class, VisitorResource.class, pagedResourcesAssembler);
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public VisitorResource toResource(Visitor entity) {
		return VisitorResource.builder() //@formatter:off
				.email(entity.getEmail())
				.authority(entity.getAuthority())
				.dateOfBirth(entity.getDateOfBirth())
				.spendingMoney(entity.getSpendingMoney())
				.photo(entity.getPhoto())
				.links(createLinks(entity)).build(); //@formatter:on
	}

	@Override
	public Visitor toEntity(VisitorResource resource) {
		return Visitor.builder() //@formatter:off
				.email(resource.getEmail())
				.password(passwordEncoder.encode(resource.getPassword()))
				.dateOfBirth(resource.getDateOfBirth())
				.spendingMoney(resource.getSpendingMoney())
				.photo(resource.getPhoto()).build(); //@formatter:on
	}

	private Link[] createLinks(Visitor visitor) {
		List<Link> links = new ArrayList<>();
		links.add(createMeLinkWithSelfRel());
		AmusementPark amusementPark = visitor.getAmusementPark();
		if (amusementPark == null) {
			links.add(createVisitorEnterParkLink(null));
			links.add(createAmusementParkLink());
		} else {
			Long amusementParkId = amusementPark.getId();
			Machine machine = visitor.getMachine();
			if (machine == null) {
				links.add(createMachineLink(amusementParkId));
				links.add(createVisitorLeavePark(amusementParkId));
				links.add(createGetOnMachineLink(amusementParkId, null));
				links.add(createAddGuestBookRegistryLink(amusementParkId));
			} else {
				links.add(createGetOffMachineLink(amusementParkId, machine.getId()));
			}
		}
		return links.toArray(new Link[links.size()]);
	}

}
