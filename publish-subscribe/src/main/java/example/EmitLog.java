package example;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

import static example.Constant.EXCHANGE_NAME;

public class EmitLog {
    private final static Logger logger = LoggerFactory.getLogger(EmitLog.class);

    public static void main(String[] args) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        try (Connection connection = factory.newConnection(); Channel channel = connection.createChannel()) {
            channel.exchangeDeclare(EXCHANGE_NAME, "fanout");

            String message = args.length < 1 ? "info: Hello World!" : String.join(" ", args);

            channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes(StandardCharsets.UTF_8));
            logger.info("Sent '{}'", message);
        } catch (Exception e) {
            logger.error("Exception occurred: ", e);
        }
    }
}
