package example;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

import static example.shared.Constant.ExchangeConfig;

public class EmitLogTopic {
    private final static Logger logger = LoggerFactory.getLogger(EmitLogTopic.class);

    public static void main(String[] args) {
        if (args.length < 2) {
            logger.error("Usage: EmitLogDirect <routingKey> <message>");
            System.exit(1);
        }

        try (Connection connection = new RabbitFactory().getConnection(); Channel channel = connection.createChannel()) {
            channel.exchangeDeclare(ExchangeConfig.NAME, ExchangeConfig.TYPE);

            String routingKey = getRouting(args);
            String message = getMessage(args);

            channel.basicPublish(
                    ExchangeConfig.NAME,
                    routingKey,
                    null,
                    message.getBytes(StandardCharsets.UTF_8)
            );
            logger.info("Sent '{}':'{}'", routingKey, message);
        } catch (Exception e) {
            logger.error("Exception occurred: ", e);
        }
    }

    private static String getRouting(String[] strings) {
        if (strings.length < 1)
            return "anonymous.info";
        return strings[0];
    }

    private static String getMessage(String[] strings) {
        if (strings.length < 2)
            return "Hello World!";
        return joinStrings(strings, " ", 1);
    }

    private static String joinStrings(String[] strings, String delimiter, int startIndex) {
        int length = strings.length;
        if (length == 0 || length < startIndex) return "";
        StringBuilder words = new StringBuilder(strings[startIndex]);
        for (int i = startIndex + 1; i < length; i++) {
            words.append(delimiter).append(strings[i]);
        }
        return words.toString();
    }
}
