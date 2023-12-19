package org.myexample.activemq;

/**
 * Constants for the ActiveMQ Cache Invalidator Service.
 */
public class Constants {

    // Broker connection URL.
    public static final String BROKER_URL = "tcp://localhost:61616";

    // Cache name prefix of local cache.
    public static final String LOCAL_CACHE_PREFIX = "$__local__$.";

    // Cache name prefix of clear all.
    public static final String CLEAR_ALL_PREFIX = "$__clear__all__$.";

    // Topic name.
    public static final String TOPIC_NAME = "CacheTopic";

    // Sender name.
    public static final String PRODUCER_1 = "producer1";

    private Constants() {

    }
}
