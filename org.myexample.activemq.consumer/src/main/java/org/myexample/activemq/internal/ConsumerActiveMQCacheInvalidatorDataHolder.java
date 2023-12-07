package org.myexample.activemq.internal;

import org.wso2.carbon.user.core.service.RealmService;

public class ConsumerActiveMQCacheInvalidatorDataHolder {

    private static ConsumerActiveMQCacheInvalidatorDataHolder instance =
            new ConsumerActiveMQCacheInvalidatorDataHolder();

    private RealmService realmService;

    public static ConsumerActiveMQCacheInvalidatorDataHolder getInstance() {

        return instance;
    }

    public RealmService getRealmService() {

        return realmService;
    }

    public void setRealmService(RealmService realmService) {

        this.realmService = realmService;
    }

}
