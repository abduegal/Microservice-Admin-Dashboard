package com.aegal.frontend;

import com.yammer.tenacity.core.properties.TenacityPropertyKey;
import com.yammer.tenacity.core.properties.TenacityPropertyKeyFactory;

/**
 * User: A.Egal Date: 8/28/14 Time: 9:05 PM
 */
public enum DependencyKeys implements TenacityPropertyKey {
    DASHBOARD_FIND_CONNECTIONS, DASHBOARD_HEALTCHECKS, DASHBOARD_METRICS, DASHBOARD_PING, DASHBOARD_READ_LOGFILE, DASHBOARD_BUILD_GRAPH;

    public static TenacityPropertyKeyFactory getTenacityPropertyKeyFactory() {
        return new TenacityPropertyKeyFactory() {
            @Override
            public TenacityPropertyKey from(String value) {
                return DependencyKeys.valueOf(value.toUpperCase());
            }
        };
    }
}
