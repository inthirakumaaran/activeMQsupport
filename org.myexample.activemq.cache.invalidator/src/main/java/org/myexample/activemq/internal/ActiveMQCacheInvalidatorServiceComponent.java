package org.myexample.activemq.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.myexample.activemq.ProducerActiveMQCacheInvalidator;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.wso2.carbon.user.core.service.RealmService;
import org.myexample.activemq.ConsumerActiveMQCacheInvalidator;

import javax.cache.CacheInvalidationRequestSender;
import javax.cache.event.CacheEntryListener;
import javax.cache.event.CacheEntryRemovedListener;
import javax.cache.event.CacheEntryUpdatedListener;

@Component(name = "org.myexample.activemq.ActiveMQCacheInvalidatorServiceComponent",
        immediate = true)
public class ActiveMQCacheInvalidatorServiceComponent {

    private static Log log = LogFactory.getLog(ActiveMQCacheInvalidatorServiceComponent.class);
    private ServiceRegistration serviceRegistration1 = null;
    private ServiceRegistration serviceRegistration2 = null;
    private ServiceRegistration serviceRegistration3 = null;
    private ServiceRegistration serviceRegistration4 = null;


    @Activate
    protected void activate(ComponentContext context) {

        //register the custom listener as an OSGI service.

        ProducerActiveMQCacheInvalidator producer = new ProducerActiveMQCacheInvalidator();

        serviceRegistration1 = context.getBundleContext().registerService(CacheEntryListener.class.getName(),
                producer,null);
        serviceRegistration2 = context.getBundleContext().registerService(CacheInvalidationRequestSender.class.getName(),
                producer,null);
        serviceRegistration3 = context.getBundleContext().registerService(CacheEntryRemovedListener.class.getName(),
                producer,null);
        serviceRegistration4 = context.getBundleContext().registerService(CacheEntryUpdatedListener.class.getName(),
                producer,null);
        ConsumerActiveMQCacheInvalidator.startService();
        log.info("..............................................ActiveMQ Cache Invalidator Service bundle activated successfully.............");
    }

    protected void deactivate(ComponentContext context) {

        // Unregistering the listener service.
        if (serviceRegistration1 != null) {
            serviceRegistration1.unregister();
        }
        if (serviceRegistration2 != null) {
            serviceRegistration2.unregister();
        }
        if (serviceRegistration3 != null) {
            serviceRegistration3.unregister();
        }
        if (serviceRegistration4 != null) {
            serviceRegistration4.unregister();
        }
//        if (log.isDebugEnabled()) {
//            log.debug("ActiveMQ Cache Invalidator Service bundle is deactivated.");
//        }

        log.info("..................................ActiveMQ Cache Invalidator Service bundle is deactivated.......................................");
    }

    @Reference(name = "user.realm.service.default",
            service = RealmService.class,
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unsetRealmService")
    protected void setRealmService(RealmService realmService) {

        ActiveMQCacheInvalidatorDataHolder.getInstance().setRealmService(realmService);
    }

    protected void unsetRealmService(RealmService realmService) {

        ActiveMQCacheInvalidatorDataHolder.getInstance().setRealmService(null);
    }

}
