package org.myexample.activemq;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.caching.impl.CacheImpl;
import org.wso2.carbon.context.PrivilegedCarbonContext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import static org.myexample.activemq.CacheInvalidatorUtils.BROKER_URL;
import static org.myexample.activemq.CacheInvalidatorUtils.CLEAR_ALL_PREFIX;
import static org.myexample.activemq.CacheInvalidatorUtils.TOPIC_NAME;
import static org.myexample.activemq.CacheInvalidatorUtils.getActiveMQBrokerUrl;
import static org.myexample.activemq.CacheInvalidatorUtils.getCacheInvalidationTopic;
import static org.myexample.activemq.CacheInvalidatorUtils.getProducerName;
import static org.myexample.activemq.CacheInvalidatorUtils.isActiveMQCacheInvalidatorEnabled;

public class ConsumerActiveMQCacheInvalidator {

    private static Log log = LogFactory.getLog(ConsumerActiveMQCacheInvalidator.class);

    private ConsumerActiveMQCacheInvalidator() {

    }

    public static void startService() {

        if (!isActiveMQCacheInvalidatorEnabled()) {
//            log.debug("ActiveMQ based cache invalidation is not enabled");
            log.info(".........................ActiveMQ broker consumer is not enabled..............");
            return;
        }

        // Create a connection factory
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(getActiveMQBrokerUrl());

        try {
            // Create a connection
            Connection connection = connectionFactory.createConnection();
            connection.start();

            // Create a session
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Create a topic
            Topic topic = session.createTopic(getCacheInvalidationTopic());

            // Create a message consumer
            MessageConsumer consumer = session.createConsumer(topic);

            // Create a message listener for the subscriber
            consumer.setMessageListener(message -> {
                if (message instanceof TextMessage) {
                    try {
                        System.out.println("Consumer Received message: " + ((TextMessage) message).getText());
                        invalidateCache(((TextMessage) message).getText());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
//                System.out.println(getActiveMQBrokerUrl());
//                System.out.println(isActiveMQCacheInvalidatorEnabled());
//                System.out.println(getCacheInvalidationTopic());
//                System.out.println(getProducerName());
            });

        } catch (Exception e) {
            log.error("Something went wrong with ActiveMQ consumer " + e);
        }
    }

    public static void invalidateCache(String message) {

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

                CacheManager cacheMgr = Caching.getCacheManagerFactory().getCacheManager(cacheManager);
                Cache<Object, Object> cache2 = cacheMgr.getCache(cache);
                if (cache2 instanceof CacheImpl) {
                    if (CLEAR_ALL_PREFIX.equals(cacheKey)) {
                        ((CacheImpl) cache2).removeAllLocal();
                    } else {
                        ((CacheImpl) cache2).removeLocal(cacheKey);
                    }
                }
                System.out.println("Cache invalidated for tenant " + tenantId + " for manager " + cacheManager +
                        " with cacheKey " + cacheKey);

            } finally {
                PrivilegedCarbonContext.endTenantFlow();
            }
        } else {
            System.out.println("Input does not match the pattern.");
            log.debug("Input doesn't match the expected msg pattern.");
        }
    }

}
