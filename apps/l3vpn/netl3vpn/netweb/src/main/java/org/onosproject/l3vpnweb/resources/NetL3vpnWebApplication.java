package org.onosproject.l3vpnweb.resources;

import java.util.Set;

import org.onlab.rest.AbstractWebApplication;

public class NetL3vpnWebApplication extends AbstractWebApplication {
    @Override
    public Set<Class<?>> getClasses() {
        return getClasses(NetL3vpnWebResource.class);
    }

}
