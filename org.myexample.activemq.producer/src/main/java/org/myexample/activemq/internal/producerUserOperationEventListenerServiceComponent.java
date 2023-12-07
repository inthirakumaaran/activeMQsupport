package org.myexample.activemq.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.wso2.carbon.user.core.listener.UserOperationEventListener;
import org.wso2.carbon.user.core.service.RealmService;
import org.myexample.activemq.producerUserOperationEventListener;

import javax.cache.CacheInvalidationRequestSender;
import javax.cache.event.CacheEntryListener;
import javax.cache.event.CacheEntryRemovedListener;
import javax.cache.event.CacheEntryUpdatedListener;

@Component(name = "org.myexample.activemq.producerUserOperationEventListenerServiceComponent",
        immediate = true)
public class producerUserOperationEventListenerServiceComponent {

    private static Log log = LogFactory.getLog(producerUserOperationEventListenerServiceComponent.class);
    private ServiceRegistration serviceRegistration = null;

    @Activate
    protected void activate(ComponentContext context) {

        //register the custom listener as an OSGI service.

        producerUserOperationEventListener producer = new producerUserOperationEventListener();
//        serviceRegistration = context.getBundleContext().registerService(UserOperationEventListener.class.getName(),
//                producer,null);

        context.getBundleContext().registerService(CacheEntryListener.class.getName(),producer,null);
        context.getBundleContext().registerService(CacheInvalidationRequestSender.class.getName(),
                producer,null);
        context.getBundleContext().registerService(CacheEntryRemovedListener.class.getName(),
                producer,null);
        context.getBundleContext().registerService(CacheEntryUpdatedListener.class.getName(),
                producer,null);
        log.info("producerUserOperationEventListenerServiceComponent bundle activated successfully.");
    }

    protected void deactivate(ComponentContext context) {

        if (log.isDebugEnabled()) {
            log.debug("producerUserOperationEventListenerServiceComponent is deactivated.");
        }

        // Unregistering the custom listener service.
        if (serviceRegistration != null) {
            serviceRegistration.unregister();
        }
    }

    @Reference(name = "user.realm.service.default",
            service = RealmService.class,
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unsetRealmService")
    protected void setRealmService(RealmService realmService) {

        producerUserOperationEventListenerDataHolder.getInstance().setRealmService(realmService);
    }

    protected void unsetRealmService(RealmService realmService) {

        producerUserOperationEventListenerDataHolder.getInstance().setRealmService(null);
    }

}
