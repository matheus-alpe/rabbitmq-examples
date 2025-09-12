package example;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

import static example.shared.Constant.*;

public class EmitLogDirect {
    private final static Logger logger = LoggerFactory.getLogger(EmitLogDirect.class);

    public static void main(String[] args) {
        if (args.length < 2) {
            logger.error("Usage: EmitLogDirect <severity> <message>");
            System.exit(1);
        }

        try (Connection connection = new RabbitFactory().getConnection(); Channel channel = connection.createChannel()) {
            channel.exchangeDeclare(ExchangeConfig.NAME, ExchangeConfig.TYPE);

            Severity severity = Severity.getSeveritiesFromString(args[0]);
            String message = args[1];

            channel.basicPublish(
                    ExchangeConfig.NAME,
                    severity.getLevel(),
                    null,
                    message.getBytes(StandardCharsets.UTF_8)
            );
            logger.info("Sent '{}'", message);
        } catch (Exception e) {
            logger.error("Exception occurred: ", e);
        }
    }
}
