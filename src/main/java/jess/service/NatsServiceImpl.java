package jess.service;

import com.evanlennick.retry4j.CallExecutor;
import com.evanlennick.retry4j.CallResults;
import com.evanlennick.retry4j.RetryConfig;
import com.evanlennick.retry4j.RetryConfigBuilder;
import com.evanlennick.retry4j.exception.RetriesExhaustedException;
import com.evanlennick.retry4j.exception.UnexpectedException;
import io.nats.client.*;

import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class NatsServiceImpl implements NatsService {

    private RetryableNatsConnection connection;

    public NatsServiceImpl() throws IOException, TimeoutException {
        connection = new RetryableNatsConnection();
    }

    @Override
    public void publish(String subject, String data) {
        if(! connection.isConnected())
            return;

        try {
            connection.publish(subject, data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class RetryableNatsConnection {
        private static final Logger LOGGER = Logger.getLogger(RetryableNatsConnection.class.getName());
        private Connection connection;
        private ConnectionFactory cf;
        private RetryConfig config;

        RetryableNatsConnection() throws IOException, TimeoutException {
            LOGGER.log(Level.SEVERE, "Constructing connection.");
            cf = new ConnectionFactory();
            config = new RetryConfigBuilder()
                    .withMaxNumberOfTries(10)
                    .withDelayBetweenTries(5)
                    .withExponentialBackoff()
                    .build();
            setDisconnectHandler();
            connection = cf.createConnection();
        }

        public boolean isConnected() {
            return connection.isConnected();
        }

        public void publish(String subject, String msg) throws IOException {
            connection.publish(subject, msg.getBytes());
        }

        private void setDisconnectHandler() {
            Callable<Void> callable = (this::reconnect);
            cf.setDisconnectedCallback(event -> {
                try {
                    new CallExecutor(config).execute(callable);
                } catch(RetriesExhaustedException | UnexpectedException ree) {
                    LOGGER.log(Level.SEVERE, "Error in back off reconnection!");
                }
            });
        }

        public Void reconnect() throws IOException, TimeoutException {
            LOGGER.log(Level.SEVERE, "Attempting reconnection...");
            connection = cf.createConnection();
            return null;
        }
    }
}
