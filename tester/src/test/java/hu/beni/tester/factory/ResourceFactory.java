package hu.beni.tester.factory;

import org.springframework.stereotype.Component;

import hu.beni.clientsupport.factory.ValidResourceFactory;
import hu.beni.clientsupport.resource.AmusementParkResource;
import hu.beni.clientsupport.resource.MachineResource;
import hu.beni.clientsupport.resource.VisitorResource;
import hu.beni.tester.properties.AmusementParkDataProperties;
import hu.beni.tester.properties.ApplicationProperties;
import hu.beni.tester.properties.DataProperties;
import hu.beni.tester.properties.MachineDataProperties;
import hu.beni.tester.properties.VisitorDataProperties;

@Component
public class ResourceFactory {

	private final AmusementParkDataProperties amusementPark;
	private final MachineDataProperties machine;
	private final VisitorDataProperties visitor;

	public ResourceFactory(ApplicationProperties applicationProperties) {
		DataProperties data = applicationProperties.getData();
		amusementPark = data.getAmusementPark();
		machine = data.getMachine();
		visitor = data.getVisitor();
	}

	public AmusementParkResource createAmusementPark() {
		AmusementParkResource amusementParkResource = ValidResourceFactory.createAmusementPark();
		amusementParkResource.setCapital(amusementPark.getCapital());
		amusementParkResource.setEntranceFee(amusementPark.getEntranceFee());
		return amusementParkResource;
	}

	public MachineResource createMachine() {
		MachineResource machineResource = ValidResourceFactory.createMachine();
		machineResource.setPrice(machine.getPrice());
		machineResource.setTicketPrice(machine.getTicketPrice());
		return machineResource;
	}

	public VisitorResource createVisitor() {
		VisitorResource visitorResource = ValidResourceFactory.createVisitor();
		visitorResource.setSpendingMoney(visitor.getSpendingMoney());
		return visitorResource;
	}
}