package jess.service;

/**
 *
 */
public interface NatsService {
    void publish(String subject, String data);
}
