/*
 * Copyright 2016-present Open Networking Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.onosproject.netl3vpn.entity;

import java.util.List;
import java.util.Map;

/**
 * Resource allocate class.
 */
public final class NetL3VpnAllocateRes {
    private final List<String> routeTargets;
    private final Map<String, String> routeDistinguisherMap;
    private final String vrfName;

    /**
     * NetL3VpnAllocateRes constructor.
     *
     * @param routeTargets
     * @param routeDistinguisher
     * @param vrfName
     */
    public NetL3VpnAllocateRes(List<String> routeTargets,
                               Map<String, String> routeDistinguisherMap, String vrfName) {
        this.routeTargets = routeTargets;
        this.routeDistinguisherMap = routeDistinguisherMap;
        this.vrfName = vrfName;
    }

    /**
     * Returns the route targets.
     *
     * @return list of route target
     */
    public List<String> getRouteTargets() {
        return routeTargets;
    }

    /**
     * Returns the map of route distinguisher.
     *
     * @return map of route distinguisher
     */
    public Map<String, String> getRouteDistinguisherMap() {
        return routeDistinguisherMap;
    }

    /**
     * Returns the vrf name.
     *
     * @return vrf name
     */
    public String getVrfName() {
        return vrfName;
    }

}
