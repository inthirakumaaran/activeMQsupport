package org.myexample.activemq.internal;

import org.wso2.carbon.user.core.service.RealmService;

public class ActiveMQCacheInvalidatorDataHolder {

    private static ActiveMQCacheInvalidatorDataHolder instance = new ActiveMQCacheInvalidatorDataHolder();


    public static ActiveMQCacheInvalidatorDataHolder getInstance() {

        return instance;
    }

}
