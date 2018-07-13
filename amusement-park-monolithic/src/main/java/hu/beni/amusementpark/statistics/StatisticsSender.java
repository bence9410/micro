package hu.beni.amusementpark.statistics;

import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import hu.beni.clientsupport.dto.VisitorSpentMoneyInParkEvent;

public interface StatisticsSender {

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	void handleVisitorEvent(VisitorSpentMoneyInParkEvent event);

}
