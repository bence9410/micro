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

		DataProperties data = properties.getData();
		NumberOfProperties numberOf = properties.getNumberOf();

		int visitors = numberOf.getVisitors();
		int machinesPerPark = numberOf.getMachinesPerPark();
		int totalNumberOfParks = numberOf.getAmusementParks();

		calculateAndSetExpectedCapitalBeforeVisitorsSum(totalNumberOfParks, data.getAmusementPark().getCapital(),
				machinesPerPark * data.getMachine().getPrice());

		int moneyOneVisitorSpendInAPark = data.getAmusementPark().getEntranceFee()
				+ machinesPerPark * data.getMachine().getTicketPrice();

		int totalMoneyAllVisitorsSpend = moneyOneVisitorSpendInAPark * totalNumberOfParks * visitors;

		calculateAndSetExpectedCapitalAfterVisitorsSum(totalMoneyAllVisitorsSpend);

		calculateAndSetExpectedSpendingMoneySum(data.getVisitor().getSpendingMoney(), visitors,
				totalMoneyAllVisitorsSpend);

	}

	private void calculateAndSetExpectedCapitalBeforeVisitorsSum(int totalNumberOfParks, int oneParkCapital,
			int machinesPriceForOnePark) {
		expectedCapitalBeforeVisitorsSum = totalNumberOfParks * (oneParkCapital - machinesPriceForOnePark);
	}

	private void calculateAndSetExpectedCapitalAfterVisitorsSum(int moneyVisitorsSpend) {
		expectedCapitalAfterVisitorsSum = expectedCapitalBeforeVisitorsSum + moneyVisitorsSpend;
	}

	private void calculateAndSetExpectedSpendingMoneySum(int oneVisitorSpendingMoney, int numberOfVisitors,
			int totalMoneyAllVisitorsSpend) {
		expectedSpendingMoneySum = oneVisitorSpendingMoney * numberOfVisitors - totalMoneyAllVisitorsSpend;
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
