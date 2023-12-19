package org.myexample.activemq.internal;

import org.wso2.carbon.user.core.service.RealmService;

public class ActiveMQCacheInvalidatorDataHolder {

    private static ActiveMQCacheInvalidatorDataHolder instance =
            new ActiveMQCacheInvalidatorDataHolder();

    private RealmService realmService;

    public static ActiveMQCacheInvalidatorDataHolder getInstance() {

        return instance;
    }

    public RealmService getRealmService() {

        return realmService;
    }

    public void setRealmService(RealmService realmService) {

        this.realmService = realmService;
    }

}
