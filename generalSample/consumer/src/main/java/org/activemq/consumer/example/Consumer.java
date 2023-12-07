package org.activemq.consumer.example;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
/**
 * Hello world!
 *
 */
public class Consumer
{
    // ActiveMQ broker URL
    private static final String BROKER_URL = "tcp://localhost:61616";

    // Topic name
    private static final String TOPIC_NAME = "CacheTopic";

    public static void main(String[] args) {

        System.out.println( "Consumer is started" );
        // Create a connection factory
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(BROKER_URL);

        try {
            // Create a connection
            Connection connection = connectionFactory.createConnection();
            connection.start();

            // Create a session
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Create a topic
            Topic topic = session.createTopic(TOPIC_NAME);

            // Create a message consumer
            MessageConsumer consumer = session.createConsumer(topic);

            // Create a message listener for the subscriber
            consumer.setMessageListener(message -> {
                if (message instanceof TextMessage) {
                    try {
                        System.out.println("Consumer Received message: " + ((TextMessage) message).getText());
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
            });

            // Keep the consumer running to receive messages
            System.out.println("Consumer is waiting for messages. Press Ctrl+C to exit.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
