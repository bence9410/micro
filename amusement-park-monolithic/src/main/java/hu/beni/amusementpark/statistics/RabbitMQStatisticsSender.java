package hu.beni.amusementpark.statistics;

import static hu.beni.amusementpark.constants.SpringProfileConstants.RABBIT_MQ;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import hu.beni.clientsupport.dto.VisitorSpentMoneyInParkEvent;
import lombok.RequiredArgsConstructor;

@Component
@Profile(RABBIT_MQ)
@RequiredArgsConstructor
public class RabbitMQStatisticsSender {

	private final RabbitTemplate rabbitTemplate;
	private final Queue statisticsQueue;

	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleVisitorEvent(VisitorSpentMoneyInParkEvent event) {
		rabbitTemplate.convertAndSend(statisticsQueue.getName(), event);
	}
}
