package org.myexample.activemq.internal;

import org.wso2.carbon.user.core.service.RealmService;

public class producerUserOperationEventListenerDataHolder {

    private static producerUserOperationEventListenerDataHolder instance =
            new producerUserOperationEventListenerDataHolder();

    private RealmService realmService;

    public static producerUserOperationEventListenerDataHolder getInstance() {

        return instance;
    }

    public RealmService getRealmService() {

        return realmService;
    }

    public void setRealmService(RealmService realmService) {

        this.realmService = realmService;
    }

}
