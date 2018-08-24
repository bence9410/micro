package hu.beni.amusementpark.mapper;

import static hu.beni.amusementpark.factory.LinkFactory.createAddGuestBookRegistryLink;
import static hu.beni.amusementpark.factory.LinkFactory.createAmusementParkLink;
import static hu.beni.amusementpark.factory.LinkFactory.createGetOffMachineLink;
import static hu.beni.amusementpark.factory.LinkFactory.createGetOnMachineLink;
import static hu.beni.amusementpark.factory.LinkFactory.createMachineLink;
import static hu.beni.amusementpark.factory.LinkFactory.createVisitorEnterParkLink;
import static hu.beni.amusementpark.factory.LinkFactory.createVisitorLeavePark;
import static hu.beni.amusementpark.factory.LinkFactory.createVisitorSelfLink;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import hu.beni.amusementpark.controller.VisitorController;
import hu.beni.amusementpark.entity.Visitor;
import hu.beni.amusementpark.enums.VisitorState;
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
				.identifier(entity.getId())
				.name(entity.getName())
				.username(entity.getUsername())
				.authority(entity.getAuthority())
				.dateOfBirth(entity.getDateOfBirth())
				.spendingMoney(entity.getSpendingMoney())
				.state(visitorStateToString(entity.getState()))
				.photo(entity.getPhoto())
				.links(createLinks(entity)).build(); //@formatter:on
	}

	@Override
	public Visitor toEntity(VisitorResource resource) {
		return Visitor.builder() //@formatter:off
				.id(resource.getIdentifier())
				.name(resource.getName())
				.username(resource.getUsername())
				.password(passwordEncoder.encode(resource.getPassword()))
				.dateOfBirth(resource.getDateOfBirth())
				.spendingMoney(resource.getSpendingMoney())
				.state(stringToVisitorState(resource.getState()))
				.photo(resource.getPhoto()).build(); //@formatter:on
	}

	private String visitorStateToString(VisitorState state) {
		return state == null ? null : state.toString();
	}

	private VisitorState stringToVisitorState(String state) {
		return state == null ? null : VisitorState.valueOf(state);
	}

	private Link[] createLinks(Visitor visitor) {
		Long visitorId = visitor.getId();
		VisitorState state = visitor.getState();
		List<Link> links = new ArrayList<>();
		links.add(createVisitorSelfLink(visitorId));
		if (state == null) {
			links.add(createVisitorEnterParkLink(null, visitorId));
			links.add(createAmusementParkLink());
		} else {
			Long amusementParkId = visitor.getAmusementPark().getId();
			if (VisitorState.REST.equals(state)) {
				links.add(createMachineLink(amusementParkId));
				links.add(createVisitorLeavePark(amusementParkId, visitorId));
				links.add(createGetOnMachineLink(amusementParkId, null, visitorId));
				links.add(createAddGuestBookRegistryLink(amusementParkId, visitorId));
			} else if (VisitorState.ON_MACHINE.equals(state)) {
				links.add(createGetOffMachineLink(amusementParkId, visitor.getMachine().getId(), visitorId));
			}
		}
		return links.toArray(new Link[links.size()]);
	}

}
