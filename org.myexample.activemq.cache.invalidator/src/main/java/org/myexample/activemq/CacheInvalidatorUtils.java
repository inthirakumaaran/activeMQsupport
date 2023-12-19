package org.myexample.activemq;

import org.apache.commons.lang.StringUtils;
import org.wso2.carbon.identity.core.util.IdentityUtil;

/**
 * Util class for the ActiveMQ Cache Invalidator Service.
 */
public class CacheInvalidatorUtils {

    // Broker connection URL.
    public static final String BROKER_URL = "tcp://localhost:61616";

    public static final String BROKER_URL_PROPERTY = "CacheInvalidator.ActiveMQ.BrokerURL";
    public static final String ACTIVEMQ_INVALIDATOR_ENABLED_PROPERTY = "CacheInvalidator.ActiveMQ.Enabled";
    public static final String ACTIVEMQ_CACHE_TOPIC_PROPERTY = "CacheInvalidator.ActiveMQ.TopicName";
    public static final String ACTIVEMQ_PRODUCER_NAME_PROPERTY = "CacheInvalidator.ActiveMQ.ProducerName";

    // Cache name prefix of local cache.
    public static final String LOCAL_CACHE_PREFIX = "$__local__$.";

    // Cache name prefix of clear all.
    public static final String CLEAR_ALL_PREFIX = "$__clear__all__$.";

    // Topic name.
    public static final String TOPIC_NAME = "CacheTopic";

    // Sender name.
    public static final String PRODUCER_1 = "producer1";

    private CacheInvalidatorUtils() {

    }

    public static String getActiveMQBrokerUrl() {

        if (StringUtils.isNotBlank(IdentityUtil.getProperty(BROKER_URL_PROPERTY))) {
            return IdentityUtil.getProperty(BROKER_URL_PROPERTY).trim();
        }
        return null;
    }

    public static String getProducerName() {

        if (StringUtils.isNotBlank(IdentityUtil.getProperty(ACTIVEMQ_PRODUCER_NAME_PROPERTY))) {
            return IdentityUtil.getProperty(ACTIVEMQ_PRODUCER_NAME_PROPERTY).trim();
        }
        return null;
    }

    public static String getCacheInvalidationTopic() {

        if (StringUtils.isNotBlank(IdentityUtil.getProperty(ACTIVEMQ_CACHE_TOPIC_PROPERTY))) {
            return IdentityUtil.getProperty(ACTIVEMQ_CACHE_TOPIC_PROPERTY).trim();
        }
        return null;
    }

    public static Boolean isActiveMQCacheInvalidatorEnabled() {

        if (StringUtils.isNotBlank(IdentityUtil.getProperty(ACTIVEMQ_INVALIDATOR_ENABLED_PROPERTY))) {
            return Boolean.parseBoolean(IdentityUtil.getProperty(ACTIVEMQ_INVALIDATOR_ENABLED_PROPERTY));
        }
        return null;
    }
}
