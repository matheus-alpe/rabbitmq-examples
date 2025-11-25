package example;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RabbitFactory {
    private final ConnectionFactory factory;

    public RabbitFactory() {
        this("localhost");
    }

    public RabbitFactory(String host) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        this.factory = factory;
    }

    public Connection getConnection() throws IOException, TimeoutException {
        return factory.newConnection();
    }
}
