package org.activemq.active.producer.example;

import org.apache.activemq.ActiveMQConnectionFactory;

import java.util.Scanner;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

/**
 * Hello world!
 *
 */
public class ActiveProducer
{
    // ActiveMQ broker URL
    private static final String BROKER_URL = "tcp://localhost:61616";

    // Topic name
    private static final String TOPIC_NAME = "CacheTopic";

    public static void main( String[] args )
    {
        System.out.println( "Active Producer is starting" );
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

            // Create a scanner to take user input
            Scanner scanner = new Scanner(System.in);

            // Continuously take user input and send as messages
            while (true) {
                System.out.print("Enter a message (or 'exit' to quit): ");
                String userInput = scanner.nextLine();

                if ("exit".equalsIgnoreCase(userInput)) {
                    break; // Exit the loop if the user enters 'exit'
                }

                // Create a text message
                TextMessage message = session.createTextMessage(userInput);

                // Send the message
                producer.send(message);

                System.out.println("Message sent: " + userInput);
            }

            // Clean up resources
            producer.close();
            session.close();
            connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
