package hu.beni.amusementpark.statistics.impl;

import static hu.beni.amusementpark.constants.SpringProfileConstants.RABBIT_MQ;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import hu.beni.amusementpark.statistics.StatisticsSender;
import hu.beni.clientsupport.dto.VisitorSpentMoneyInParkEvent;
import lombok.RequiredArgsConstructor;

@Component
@Profile(RABBIT_MQ)
@RequiredArgsConstructor
public class RabbitMQStatisticsSender implements StatisticsSender {

	private final RabbitTemplate rabbitTemplate;
	private final Queue statisticsQueue;

	@Override
	@Async
	public void handleVisitorEvent(VisitorSpentMoneyInParkEvent event) {
		rabbitTemplate.convertAndSend(statisticsQueue.getName(), event);
	}
}
