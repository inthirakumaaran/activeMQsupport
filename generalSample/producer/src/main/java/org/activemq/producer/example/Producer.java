package org.activemq.producer.example;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * Hello world!
 *
 */
public class Producer
{

    // ActiveMQ broker URL
    private static final String BROKER_URL = "tcp://localhost:61616";

    // Topic name
    private static final String TOPIC_NAME = "CacheTopic";
    public static final String PRODUCER_1 = "producer1";

    public static void main( String[] args )
    {
        System.out.println( "Producer is starting" );
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

            // Create a message producer
            MessageProducer producer = session.createProducer(topic);

            // Create a message consumer
            MessageConsumer consumer = session.createConsumer(topic);

            // Create a message listener for the subscriber
            consumer.setMessageListener(message -> {
                if (message instanceof TextMessage) {
                    try {
                        String sender = message.getStringProperty("sender");
                        if (!PRODUCER_1.equals(sender)) {
                            System.out.println("Received message: " + ((TextMessage) message).getText());
                        }
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
            });

            // Create and send a message from the publisher
            TextMessage message = session.createTextMessage("Hello, this is a message from the publisher! 23");
            message.setStringProperty("sender", PRODUCER_1);
            producer.send(message);

            // Sleep for a while to allow the subscriber to receive the message
            Thread.sleep(2000);

            // Clean up resources
            producer.close();
            consumer.close();
            session.close();
            connection.close();
        } catch (JMSException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
