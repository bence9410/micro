package hu.beni.amusementpark.mapper;

import static hu.beni.amusementpark.constants.HATEOASLinkNameConstants.ADD_REGISTRY;
import static hu.beni.amusementpark.constants.HATEOASLinkNameConstants.GET_OFF_MACHINE;
import static hu.beni.amusementpark.constants.HATEOASLinkNameConstants.GET_ON_MACHINE;
import static hu.beni.amusementpark.constants.HATEOASLinkNameConstants.VISITOR_ENTER_PARK;
import static hu.beni.amusementpark.constants.HATEOASLinkNameConstants.VISITOR_LEAVE_PARK;
import static hu.beni.amusementpark.constants.HATEOASLinkNameConstants.AMUSEMENT_PARK;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import hu.beni.amusementpark.controller.AmusementParkController;
import hu.beni.amusementpark.controller.GuestBookRegistryController;
import hu.beni.amusementpark.controller.VisitorController;
import hu.beni.amusementpark.entity.Visitor;
import hu.beni.amusementpark.enums.VisitorState;
import hu.beni.clientsupport.resource.VisitorResource;

@Component
@ConditionalOnWebApplication
public class VisitorMapper extends EntityMapper<Visitor, VisitorResource> {

	public VisitorMapper(PagedResourcesAssembler<Visitor> pagedResourcesAssembler) {
		super(VisitorController.class, VisitorResource.class, pagedResourcesAssembler);
	}

	@Override
	public VisitorResource toResource(Visitor entity) {
		return VisitorResource.builder() //@formatter:off
				.identifier(entity.getId())
				.name(entity.getName())
				.username(entity.getUsername())
				.dateOfBirth(entity.getDateOfBirth())
				.spendingMoney(entity.getSpendingMoney())
				.state(visitorStateToString(entity.getState()))
				.links(createLinks(entity)).build(); //@formatter:on
	}

	@Override
	public Visitor toEntity(VisitorResource resource) {
		return Visitor.builder() //@formatter:off
				.id(resource.getIdentifier())
				.name(resource.getName())
				.username(resource.getUsername())
				.dateOfBirth(resource.getDateOfBirth())
				.spendingMoney(resource.getSpendingMoney())
				.state(stringToVisitorState(resource.getState())).build(); //@formatter:on
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
		links.add(linkTo(methodOn(VisitorController.class).findOne(visitorId)).withSelfRel());
		if (state == null) {
			links.add(linkTo(methodOn(VisitorController.class).enterPark(null, visitorId)).withRel(VISITOR_ENTER_PARK));
			links.add(linkTo(methodOn(AmusementParkController.class).findAllPaged(null)).withRel(AMUSEMENT_PARK));
		} else {
			Long amusementParkId = visitor.getAmusementPark().getId();
			if (VisitorState.REST.equals(state)) {
				links.add(linkTo(methodOn(VisitorController.class).leavePark(amusementParkId, visitorId))
						.withRel(VISITOR_LEAVE_PARK));
				links.add(linkTo(methodOn(VisitorController.class).getOnMachine(amusementParkId, null, visitorId))
						.withRel(GET_ON_MACHINE));
				links.add(linkTo(
						methodOn(GuestBookRegistryController.class).addRegistry(amusementParkId, visitorId, null))
								.withRel(ADD_REGISTRY));
			} else if (VisitorState.ON_MACHINE.equals(state)) {
				links.add(linkTo(methodOn(VisitorController.class).getOffMachine(amusementParkId,
						visitor.getMachine().getId(), visitorId)).withRel(GET_OFF_MACHINE));
			}
		}
		return links.toArray(new Link[links.size()]);
	}
}
