package example;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import example.shared.Constant.RPCConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

public class RPCServer {
    private final static Logger logger = LoggerFactory.getLogger(RPCServer.class);

    public static void main(String[] args) throws Exception {
        final Connection connection = new RabbitFactory().getConnection();
        final Channel channel = connection.createChannel();

        channel.queueDeclare(RPCConfig.QUEUE_NAME, false, false, false, null);
        channel.queuePurge(RPCConfig.QUEUE_NAME);

        channel.basicQos(1);

        logger.info("[*] Awaiting RPC requests. To exit press CTRL+C");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            BasicProperties replyProps = new BasicProperties.Builder()
                    .correlationId(delivery.getProperties().getCorrelationId())
                    .build();

            StringBuilder responseBuilder = new StringBuilder();
            try {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                int n = Integer.parseInt(message);
                logger.info("[.] fib({})", message);
                responseBuilder.append(fib(n));
            } catch (RuntimeException e) {
                logger.info(" [.] {}", e.toString());
            } finally {
                channel.basicPublish(
                        "",
                        delivery.getProperties().getReplyTo(),
                        replyProps,
                        responseBuilder.toString().getBytes(StandardCharsets.UTF_8)
                );
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            }
        };

        channel.basicConsume(RPCConfig.QUEUE_NAME, false, deliverCallback, consumerTag -> {
        });
    }

    private static int fib(int n) {
        if (n == 0) return 0;
        if (n == 1) return 1;
        return fib(n - 1) + fib(n - 2);
    }
}
