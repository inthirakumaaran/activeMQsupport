package org.myexample.activemq;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.wso2.carbon.caching.impl.CacheImpl;
import org.wso2.carbon.context.PrivilegedCarbonContext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.jms.*;


public class ConsumerActiveMQCacheInvalidator {

    //Sample Code
    private static Log log = LogFactory.getLog(ConsumerActiveMQCacheInvalidator.class);
    private static String AUDIT_MESSAGE = "Initiator: %s performed the Action: %s on Target: %s ";

    private static final String BROKER_URL = "tcp://localhost:61616";

    // Cache name prefix of local cache
    public static final String LOCAL_CACHE_PREFIX = "$__local__$.";

    // Cache name prefix of clear all
    public static final String CLEAR_ALL_PREFIX = "$__clear__all__$.";

    // Topic name
    private static final String TOPIC_NAME = "CacheTopic";

    public static void startService() {
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
                        invalidateCache(((TextMessage) message).getText());
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void invalidateCache(String message) {


//            if (log.isDebugEnabled()) {
//                log.debug("Received [" + this + "] ");
//            }

//            String regexPattern =
//                    "ClusterCacheInvalidationRequest\\{tenantId=(-?\\d+), tenantDomain='[\\w.]+', messageId=[\\w-]+, " +
//                            "cacheManager=[\\w.]+, cache=.*?, cacheKey=.*?\\}";

        String regexPattern = "ClusterCacheInvalidationRequest\\{tenantId=(?<tenantId>-?\\d+), " +
                "tenantDomain='(?<tenantDomain>[\\w.]+)', messageId=(?<messageId>[\\w-]+), " +
                "cacheManager=(?<cacheManager>[\\w.]+), cache=(?<cache>.*?), cacheKey=(?<cacheKey>.*?)\\}";



        Pattern pattern = Pattern.compile(regexPattern);
        Matcher matcher = pattern.matcher(message);

        if (matcher.find()) {
            String tenantId = matcher.group("tenantId");
            String tenantDomain = matcher.group("tenantDomain");
            String messageId = matcher.group("messageId");
            String cacheManager = matcher.group("cacheManager");
            String cache = matcher.group("cache");
            String cacheKey = matcher.group("cacheKey");

            try {
                    PrivilegedCarbonContext.startTenantFlow();
                    PrivilegedCarbonContext carbonContext = PrivilegedCarbonContext.getThreadLocalCarbonContext();
                    carbonContext.setTenantId(Integer.valueOf(tenantId));
                    carbonContext.setTenantDomain(tenantDomain);

                    CacheManager cacheManager2 = Caching.getCacheManagerFactory().getCacheManager(cacheManager);
                    Cache<Object, Object> cache2 = cacheManager2.getCache(cache);
                    if (cache2 instanceof CacheImpl) {
                        if (CLEAR_ALL_PREFIX.equals(cacheKey)) {
                            ((CacheImpl) cache2).removeAllLocal();
                        } else {
                            ((CacheImpl) cache2).removeLocal(cacheKey);
                        }
                    }
                System.out.println("Cache invalidated for tenant " + tenantId + " for manager " + cacheManager +
                        " with cacheKey " + cacheKey );

                } finally {
                    PrivilegedCarbonContext.endTenantFlow();
                }
        } else {
            System.out.println("Input does not match the pattern.");
        }
    }

}
