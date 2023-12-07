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
import org.wso2.carbon.user.core.service.RealmService;
import org.myexample.activemq.ConsumerActiveMQCacheInvalidator;

@Component(name = "org.myexample.activemq.ConsumerActiveMQCacheInvalidatorServiceComponent",
        immediate = true)
public class ConsumerActiveMQCacheInvalidatorServiceComponent {

    private static Log log = LogFactory.getLog(ConsumerActiveMQCacheInvalidatorServiceComponent.class);
    private ServiceRegistration serviceRegistration = null;

    @Activate
    protected void activate(ComponentContext context) {

        //register the custom listener as an OSGI service.
//        serviceRegistration = context.getBundleContext().registerService(UserOperationEventListener.class.getName(),
//                new ConsumerUserOperationEventListener(),null);
        ConsumerActiveMQCacheInvalidator.startService();
        log.info("ConsumerUserOperationEventListenerServiceComponent bundle activated successfully.");
    }

    protected void deactivate(ComponentContext context) {

        if (log.isDebugEnabled()) {
            log.debug("ConsumerUserOperationEventListenerServiceComponent is deactivated.");
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

        ConsumerActiveMQCacheInvalidatorDataHolder.getInstance().setRealmService(realmService);
    }

    protected void unsetRealmService(RealmService realmService) {

        ConsumerActiveMQCacheInvalidatorDataHolder.getInstance().setRealmService(null);
    }

}
