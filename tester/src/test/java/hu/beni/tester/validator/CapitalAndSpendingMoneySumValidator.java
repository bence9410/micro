package hu.beni.tester.validator;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import hu.beni.tester.dto.SumAndTime;
import hu.beni.tester.properties.ApplicationProperties;
import hu.beni.tester.properties.DataProperties;
import hu.beni.tester.properties.NumberOfProperties;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CapitalAndSpendingMoneySumValidator {

	private final ApplicationProperties properties;

	private int expectedCapitalBeforeVisitorsSum;
	private int expectedCapitalAfterVisitorsSum;
	private int expectedSpendingMoneySum;

	@PostConstruct
	public void init() {

		NumberOfProperties numberOf = properties.getNumberOf();

		DataProperties data = properties.getData();

		int numberOfAdmins = numberOf.getAdmins();
		int numberOfVisitors = numberOf.getVisitors();
		int numberOfParksPerAdmin = numberOf.getAmusementParksPerAdmin();
		int numberOfMachinesPerPark = numberOf.getMachinesPerPark();

		expectedCapitalBeforeVisitorsSum = numberOf.getAdmins() * numberOfParksPerAdmin
				* (data.getAmusementPark().getCapital() - numberOf.getMachinesPerPark() * data.getMachine().getPrice());
		int moneyOneVisitorSpendInAPark = data.getAmusementPark().getEntranceFee()
				+ numberOfMachinesPerPark * data.getMachine().getTicketPrice();
		expectedCapitalAfterVisitorsSum = expectedCapitalBeforeVisitorsSum
				+ numberOfAdmins * moneyOneVisitorSpendInAPark * numberOfAdmins * numberOfParksPerAdmin;
		expectedSpendingMoneySum = (data.getVisitor().getSpendingMoney()
				- (moneyOneVisitorSpendInAPark * numberOfAdmins * numberOfParksPerAdmin)) * numberOfVisitors;
	}

	public Long checkCapitalSumBeforeVisitorsGetTime(SumAndTime sumAndTime) {
		return checkSumAndReturnTime(sumAndTime, expectedCapitalBeforeVisitorsSum,
				"Problem with capital sum before visitors!");
	}

	public Long checkCapitalSumAfterVisitorsGetTime(SumAndTime sumAndTime) {
		return checkSumAndReturnTime(sumAndTime, expectedCapitalAfterVisitorsSum,
				"Problem with capital sum after visitors!");
	}

	public Long checkSpendingMoneySunGetTime(SumAndTime sumAndTime) {
		return checkSumAndReturnTime(sumAndTime, expectedSpendingMoneySum, "Problem with spending money sum!");
	}

	private Long checkSumAndReturnTime(SumAndTime sumAndTime, long expectedSum, String errorMessage) {
		if (sumAndTime.getSum() != expectedSum) {
			throw new RuntimeException(errorMessage);
		}
		return sumAndTime.getTime();
	}
}
