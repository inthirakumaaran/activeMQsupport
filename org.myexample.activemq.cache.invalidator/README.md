1. Build the maven project and copy the jar file to the `<IS_HOME>/repository/components/dropins` directory.
2. Add the following identity.xml.j2 template configurations
```yaml
 {% if cache_invalidator.active_mq.broker_url is defined %}
   <CacheInvalidator>
   <ActiveMQ>
   <BrokerURL>{{cache_invalidator.active_mq.broker_url}}</BrokerURL>
   <Enabled>{{cache_invalidator.active_mq.enabled}}</Enabled>
   <TopicName>{{cache_invalidator.active_mq.topic_name}}</TopicName>
   <ProducerName>{{cache_invalidator.active_mq.producer_name}}</ProducerName>
   </ActiveMQ>
   </CacheInvalidator>
   {% endif %}
```
3. Then add the following config 
```yaml
[cache_invalidator.active_mq]
broker_url="tcp://localhost:61616"
enabled="true"
topic_name="CacheTopic"
producer_name="producer1"
```
4. Restart the server.