package example;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static example.shared.Constant.*;

public class ReceiveLogTopic {
    private final static Logger logger = LoggerFactory.getLogger(ReceiveLogTopic.class);

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            logger.error("Usage: ReceiveLogTopic  [binding_key...");
            System.exit(1);
        }

        final Connection connection = new RabbitFactory().getConnection();
        final Channel channel = connection.createChannel();

        channel.exchangeDeclare(ExchangeConfig.NAME, ExchangeConfig.TYPE);
        String queueName = channel.queueDeclare().getQueue();
        logger.info("Queue name: {}", queueName);

        for (String bindingKey : args) {
            channel.queueBind(queueName, ExchangeConfig.NAME, bindingKey);
        }

        logger.info("[*] Waiting for messages. To exit press CTRL+C");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            String routingKey = delivery.getEnvelope().getRoutingKey();
            logger.info("[{}] Received ({}): '{}'", Thread.currentThread().getName(), routingKey, message);
        };

        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
        });
    }

}
