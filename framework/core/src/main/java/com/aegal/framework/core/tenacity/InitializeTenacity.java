package com.aegal.framework.core.tenacity;

import com.yammer.tenacity.core.TenacityCommand;
import com.yammer.tenacity.core.properties.TenacityPropertyKey;

/**
 * User: A.Egal
 * Date: 9/2/14
 * Time: 10:59 PM
 */
public class InitializeTenacity {

    public static void initialize(TenacityPropertyKey[] keys) {
        for (TenacityPropertyKey key : keys) {
            new TenacityCommand<String>(key){
                @Override
                protected String run() throws Exception {
                    return "";
                }
            }.execute();
        }
    }
}
