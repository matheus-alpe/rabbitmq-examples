package example;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import example.shared.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class RPCClient implements AutoCloseable {
    private final static Logger logger = LoggerFactory.getLogger(RPCClient.class);

    private Connection connection;
    private Channel channel;

    public RPCClient() throws IOException, TimeoutException {
        connection = new RabbitFactory().getConnection();
        channel = connection.createChannel();
    }

    public String call(String message) throws IOException, ExecutionException, InterruptedException {
        String correlationId = UUID.randomUUID().toString();
        String replyQueueName = channel.queueDeclare().getQueue();
        BasicProperties props = new BasicProperties.Builder()
                .correlationId(correlationId)
                .replyTo(replyQueueName)
                .build();

        channel.basicPublish("", Constant.RPCConfig.QUEUE_NAME, props, message.getBytes(StandardCharsets.UTF_8));

        final CompletableFuture<String> response = new CompletableFuture<>();
        String ctag = channel.basicConsume(replyQueueName, true, (consumerTag, delivery) -> {
            if (delivery.getProperties().getCorrelationId().equals(correlationId)) {
                response.complete(new String(delivery.getBody(), StandardCharsets.UTF_8));
            }
        }, consumerTag -> {
        });

        String result = response.get();
        channel.basicCancel(ctag);
        return result;
    }

    @Override
    public void close() throws Exception {
        connection.close();
    }

    public static void main(String[] args) {
        try (RPCClient fibonacciRpc = new RPCClient()) {
            for (int i = 0; i < 32; i++) {
                String i_str = Integer.toString(i);
                logger.info(" [x] Requesting fib({})", i_str);
                String response = fibonacciRpc.call(i_str);
                logger.info(" [.] Got '{}'", response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
