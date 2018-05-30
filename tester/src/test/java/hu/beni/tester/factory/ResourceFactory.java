package hu.beni.tester.factory;

import hu.beni.clientsupport.factory.ValidResourceFactory;
import hu.beni.clientsupport.resource.AmusementParkResource;
import hu.beni.clientsupport.resource.MachineResource;
import hu.beni.clientsupport.resource.VisitorResource;

public class ResourceFactory {

	public static final int AMUSEMENT_PARK_CAPITAL = 3000;
	public static final int AMUSEMENT_PARK_ENTRANCE_FEE = 50;
	public static final int MACHINE_PRICE = 250;
	public static final int MACHINE_TICKET_PRICE = 10;
	public static final int VISITOR_SPENDING_MONEY = 1000000;

	public static AmusementParkResource createAmusementParkWithAddress() {
		AmusementParkResource amusementParkResource = ValidResourceFactory.createAmusementParkWithAddress();
		amusementParkResource.setCapital(AMUSEMENT_PARK_CAPITAL);
		amusementParkResource.setEntranceFee(AMUSEMENT_PARK_ENTRANCE_FEE);
		return amusementParkResource;
	}

	public static MachineResource createMachine() {
		MachineResource machineResource = ValidResourceFactory.createMachine();
		machineResource.setPrice(MACHINE_PRICE);
		machineResource.setTicketPrice(MACHINE_TICKET_PRICE);
		return machineResource;
	}

	public static VisitorResource createVisitor() {
		VisitorResource visitorResource = ValidResourceFactory.createVisitor();
		visitorResource.setSpendingMoney(VISITOR_SPENDING_MONEY);
		return visitorResource;
	}
}