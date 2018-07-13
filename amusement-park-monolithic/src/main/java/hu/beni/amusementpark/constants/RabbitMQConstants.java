package hu.beni.amusementpark.constants;

public class RabbitMQConstants {

	private static final String EXCHANGE = "Exchange";

	public static final String ARCHIVE_QUEUE_NAME = "archiveAmusementPark";
	public static final String ARCHIVE_EXCHANGE_NAME = ARCHIVE_QUEUE_NAME + EXCHANGE;

	public static final String STATISTICS_QUEUE_NAME = "visitorStatistics";
	public static final String STATISTICS_EXCHANGE_NAME = STATISTICS_QUEUE_NAME + EXCHANGE;

	private RabbitMQConstants() {
		super();
	}

}
