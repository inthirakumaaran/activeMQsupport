package org.myexample.activemq;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.caching.impl.CachingConstants;
import org.wso2.carbon.caching.impl.clustering.ClusterCacheInvalidationRequest;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.utils.multitenancy.MultitenantConstants;

import javax.cache.CacheEntryInfo;
import javax.cache.CacheInvalidationRequestSender;
import javax.cache.event.CacheEntryCreatedListener;
import javax.cache.event.CacheEntryEvent;
import javax.cache.event.CacheEntryListenerException;
import javax.cache.event.CacheEntryRemovedListener;
import javax.cache.event.CacheEntryUpdatedListener;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import static org.myexample.activemq.CacheInvalidatorUtils.BROKER_URL;
import static org.myexample.activemq.CacheInvalidatorUtils.PRODUCER_1;
import static org.myexample.activemq.CacheInvalidatorUtils.TOPIC_NAME;
import static org.myexample.activemq.CacheInvalidatorUtils.getActiveMQBrokerUrl;
import static org.myexample.activemq.CacheInvalidatorUtils.getCacheInvalidationTopic;
import static org.myexample.activemq.CacheInvalidatorUtils.getProducerName;
import static org.myexample.activemq.CacheInvalidatorUtils.isActiveMQCacheInvalidatorEnabled;

public class ProducerActiveMQCacheInvalidator implements CacheEntryRemovedListener, CacheEntryUpdatedListener,
        CacheEntryCreatedListener, CacheInvalidationRequestSender {

    private static Log log = LogFactory.getLog(ProducerActiveMQCacheInvalidator.class);

    @Override
    public void send(CacheEntryInfo cacheEntryInfo) {

        String tenantDomain = cacheEntryInfo.getTenantDomain();
        int tenantId = cacheEntryInfo.getTenantId();

//        if (!isActiveMQCacheInvalidatorEnabled()) {
//            log.debug("ActiveMQ broker is not enabled");
//            return;
//        }
        if (MultitenantConstants.INVALID_TENANT_ID == tenantId) {
            if (log.isDebugEnabled()) {
                String stackTrace = ExceptionUtils.getStackTrace(new Throwable());
                log.debug("Tenant information cannot be found in the request. This originated from: \n" + stackTrace);
            }
            return;
        }

        if (!cacheEntryInfo.getCacheName().startsWith(CachingConstants.LOCAL_CACHE_PREFIX)) {
            return;
        }

        int numberOfRetries = 0;
        if (log.isDebugEnabled()) {
            log.debug("Sending cache invalidation message to other cluster nodes for '" + cacheEntryInfo.getCacheKey() +
                    "' of the cache '" + cacheEntryInfo.getCacheName() + "' of the cache manager '" +
                    cacheEntryInfo.getCacheManagerName() + "'");
        }

        //Send the cluster message
        ClusterCacheInvalidationRequest.CacheInfo cacheInfo =
                new ClusterCacheInvalidationRequest.CacheInfo(cacheEntryInfo.getCacheManagerName(),
                        cacheEntryInfo.getCacheName(),
                        cacheEntryInfo.getCacheKey());

        ClusterCacheInvalidationRequest clusterCacheInvalidationRequest = new ClusterCacheInvalidationRequest(
                cacheInfo, tenantDomain, tenantId);

        while (numberOfRetries < 60) {

            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(getActiveMQBrokerUrl());

//            System.out.println(getActiveMQBrokerUrl());
//            System.out.println(isActiveMQCacheInvalidatorEnabled());
//            System.out.println(getCacheInvalidationTopic());
//            System.out.println(getProducerName());

            try {
                // Create a connection
                Connection connection = connectionFactory.createConnection();
                connection.start();

                // Create a session
                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

                // Create a topic
                Topic topic = session.createTopic(getCacheInvalidationTopic());

                // Create a message producer
                MessageProducer producer = session.createProducer(topic);

                // Create and send a message from the publisher
                TextMessage message = session.createTextMessage(clusterCacheInvalidationRequest.toString());
                message.setStringProperty("sender", getProducerName());
                producer.send(message);

                // Clean up resources
                producer.close();
                session.close();
                connection.close();
                break;
            } catch (JMSException e) {
                log.error("Something went wrong with activeMQ producer." + e);
                numberOfRetries++;
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ignored) {
                    log.debug("Thread sleeping interrupted for ActiveMQ producer.");
                }
            }
        }

    }

    public void entryCreated(CacheEntryEvent cacheEntryEvent) throws CacheEntryListenerException {

    }

    @Override
    public void entryRemoved(CacheEntryEvent cacheEntryEvent) throws CacheEntryListenerException {

        send(createCacheInfo(cacheEntryEvent));
    }

    @Override
    public void entryUpdated(CacheEntryEvent cacheEntryEvent) throws CacheEntryListenerException {

        send(createCacheInfo(cacheEntryEvent));
    }

    public static CacheEntryInfo createCacheInfo(CacheEntryEvent cacheEntryEvent) {

        String tenantDomain = PrivilegedCarbonContext.getThreadLocalCarbonContext().getTenantDomain(true);
        int tenantId = PrivilegedCarbonContext.getThreadLocalCarbonContext().getTenantId(true);
        return new CacheEntryInfo(cacheEntryEvent.getSource().getCacheManager().getName(),
                cacheEntryEvent.getSource().getName(), cacheEntryEvent.getKey(), tenantDomain, tenantId);
    }
}
