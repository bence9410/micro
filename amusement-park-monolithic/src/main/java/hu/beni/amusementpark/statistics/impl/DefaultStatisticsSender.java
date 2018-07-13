package hu.beni.amusementpark.statistics.impl;

import static hu.beni.amusementpark.constants.SpringProfileConstants.RABBIT_MQ;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import hu.beni.amusementpark.statistics.StatisticsSender;
import hu.beni.clientsupport.dto.VisitorSpentMoneyInParkEvent;

@Component
@Profile("!" + RABBIT_MQ)
public class DefaultStatisticsSender implements StatisticsSender {

	@Override
	public void handleVisitorEvent(VisitorSpentMoneyInParkEvent event) {
		// do nothing
	}

}
