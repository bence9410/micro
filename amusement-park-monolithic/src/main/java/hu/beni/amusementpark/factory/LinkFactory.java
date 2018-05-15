package hu.beni.amusementpark.factory;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.springframework.hateoas.Link;

import hu.beni.amusementpark.controller.AmusementParkController;
import hu.beni.amusementpark.controller.GuestBookRegistryController;
import hu.beni.amusementpark.controller.MachineController;
import hu.beni.amusementpark.controller.VisitorController;

import static hu.beni.clientsupport.constants.HATEOASLinkRelConstants.*;

public class LinkFactory {

	private static final Class<AmusementParkController> amusementParkControllerClass = AmusementParkController.class;
	private static final Class<MachineController> machineControllerClass = MachineController.class;
	private static final Class<VisitorController> visitorControllerClass = VisitorController.class;
	private static final Class<GuestBookRegistryController> guestBookRegistryControllerClass = GuestBookRegistryController.class;

	public static Link createAmusementParkLink() {
		return linkTo(methodOn(amusementParkControllerClass).findAllPaged(null)).withRel(AMUSEMENT_PARK);
	}

	public static Link createAmusementParkSelfLink(Long amusementParkId) {
		return linkTo(methodOn(amusementParkControllerClass).findOne(amusementParkId)).withSelfRel();
	}

	public static Link createMachineLink(Long amusementParkId) {
		return linkTo(methodOn(machineControllerClass).addMachine(amusementParkId, null)).withRel(MACHINE);
	}

	public static Link createMachineSelfLink(Long amusementParkId, Long machineId) {
		return linkTo(methodOn(machineControllerClass).findOne(amusementParkId, machineId)).withSelfRel();
	}

	public static Link createVisitorSelfLink(Long visitorId) {
		return linkTo(methodOn(visitorControllerClass).findOne(visitorId)).withSelfRel();
	}

	public static Link createVisitorSignUpLink() {
		return linkTo(methodOn(visitorControllerClass).signUp(null, null)).withRel(VISITOR_SIGN_UP);
	}

	public static Link createVisitorEnterParkLink(Long visitorId) {
		return linkTo(methodOn(visitorControllerClass).enterPark(null, visitorId)).withRel(VISITOR_ENTER_PARK);
	}

	public static Link createGetOnMachineLink(Long amusementParkId, Long machineId, Long visitorId) {
		return linkTo(methodOn(visitorControllerClass).getOnMachine(amusementParkId, machineId, visitorId))
				.withRel(GET_ON_MACHINE);
	}

	public static Link createGetOffMachineLink(Long amusementParkId, Long machineId, Long visitorId) {
		return linkTo(methodOn(visitorControllerClass).getOffMachine(amusementParkId, machineId, visitorId))
				.withRel(GET_OFF_MACHINE);
	}

	public static Link createVisitorLeavePark(Long amusementParkId, Long visitorId) {
		return linkTo(methodOn(visitorControllerClass).leavePark(amusementParkId, visitorId))
				.withRel(VISITOR_LEAVE_PARK);
	}

	public static Link createGuestBookRegistrySelfLink(Long guestBookRegistryId) {
		return linkTo(methodOn(guestBookRegistryControllerClass).findOne(guestBookRegistryId)).withSelfRel();
	}

	public static Link createAddGuestBookRegistryLink(Long amusementParkId, Long visitorId) {
		return linkTo(methodOn(guestBookRegistryControllerClass).addRegistry(amusementParkId, visitorId, null))
				.withRel(ADD_REGISTRY);
	}

	private LinkFactory() {
		super();
	}
}
