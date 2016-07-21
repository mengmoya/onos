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
package org.onosproject.netl3vpn.manager.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.onosproject.ne.Bgp;
import org.onosproject.ne.BgpImportProtocol;
import org.onosproject.ne.BgpImportProtocol.ProtocolType;
import org.onosproject.ne.NeData;
import org.onosproject.ne.VpnAc;
import org.onosproject.ne.VpnInstance;
import org.onosproject.ne.VrfEntity;
import org.onosproject.net.AnnotationKeys;
import org.onosproject.net.DeviceId;
import org.onosproject.net.Port;
import org.onosproject.net.PortNumber;
import org.onosproject.net.device.DeviceService;
import org.onosproject.netl3vpn.entity.NetL3VpnAllocateRes;
import org.onosproject.netl3vpn.entity.WebAc;
import org.onosproject.netl3vpn.entity.WebNetL3vpnInstance;

/**
 * L3vpn network decompose configuration handler.
 */
public final class NetL3vpnDecompHandler {
    private static NetL3vpnDecompHandler netL3vpnDecompHandler = null;
    private NetL3VpnAllocateRes l3VpnAllocateRes;
    private DeviceService deviceService;

    /**
     * Initializes default values.
     */
    private NetL3vpnDecompHandler() {
    }

    /**
     * Returns single instance of this class.
     *
     * @return this class single instance
     */
    public static NetL3vpnDecompHandler getInstance() {
        if (netL3vpnDecompHandler == null) {
            netL3vpnDecompHandler = new NetL3vpnDecompHandler();
        }
        return netL3vpnDecompHandler;
    }

    /**
     * Initialization the allocate resource entity and device service.
     *
     * @param l3VpnAllocateRes the allocate resource entity
     * @param deviceService device service
     */
    public void initialize(NetL3VpnAllocateRes l3VpnAllocateRes,
                           DeviceService deviceService) {
        this.l3VpnAllocateRes = l3VpnAllocateRes;
        this.deviceService = deviceService;
    }

    /**
     * Decompose the l3vpn network instance to network element data.
     *
     * @param webNetL3vpnInstance the l3vpn network instance
     * @return network element data
     */
    public NeData decompNeData(WebNetL3vpnInstance webNetL3vpnInstance) {
        List<VpnInstance> vpnInstanceList = new ArrayList<VpnInstance>();
        List<VpnAc> vpnAcList = new ArrayList<VpnAc>();

        Map<String, List<String>> acIdsByNeMap = new HashMap<String, List<String>>();
        Map<String, List<WebAc>> acsByNeMap = new HashMap<String, List<WebAc>>();
        for (WebAc webAc : webNetL3vpnInstance.getAcList()) {
            String neId = webAc.getNeId();
            List<String> acIdsByNeList = acIdsByNeMap.get(neId);
            if (acIdsByNeList == null) {
                acIdsByNeList = new ArrayList<String>();
            }
            acIdsByNeList.add(webAc.getId());
            acIdsByNeMap.put(neId, acIdsByNeList);
            List<WebAc> acsByNeList = acsByNeMap.get(neId);
            if (acsByNeList == null) {
                acsByNeList = new ArrayList<WebAc>();
            }
            acsByNeList.add(webAc);
            acsByNeMap.put(neId, acsByNeList);
        }

        vpnInstanceList = decompVpnInstance(acIdsByNeMap, webNetL3vpnInstance);
        vpnAcList = decompVpnAc(acsByNeMap, webNetL3vpnInstance);
        return new NeData(vpnInstanceList, vpnAcList);
    }

    /**
     * Decompose the l3vpn network instance to list of vpn instance.
     *
     * @param acIdsByNeMap a map of ac ids to each ne
     * @param webNetL3vpnInstance the l3vpn network instance
     * @return list of vpn instance
     */
    public List<VpnInstance> decompVpnInstance(Map<String, List<String>> acIdsByNeMap,
                                               WebNetL3vpnInstance webNetL3vpnInstance) {
        List<VpnInstance> vpnInstanceList = new ArrayList<VpnInstance>();
        for (String neId : webNetL3vpnInstance.getNeIdList()) {
            String vrfName = l3VpnAllocateRes.getVrfName();
            String netVpnId = webNetL3vpnInstance.getId();
            String routeDistinguisher = l3VpnAllocateRes
                    .getRouteDistinguisherMap().get(neId);
            List<String> importTargets = l3VpnAllocateRes.getRouteTargets();
            List<String> exportTargets = l3VpnAllocateRes.getRouteTargets();
            List<String> acIdList = acIdsByNeMap.get(neId);

            BgpImportProtocol bgpImportProtocol = new BgpImportProtocol(ProtocolType.DIRECT, "0");
            List<BgpImportProtocol> importProtocols = new ArrayList<BgpImportProtocol>();
            importProtocols.add(bgpImportProtocol);
            Bgp bgp = new Bgp(importProtocols);

            VrfEntity vrfEntity = new VrfEntity(vrfName, netVpnId,
                                                routeDistinguisher,
                                                importTargets, exportTargets,
                                                acIdList, bgp);
            List<VrfEntity> vrfEntities = new ArrayList<VrfEntity>();
            vrfEntities.add(vrfEntity);
            VpnInstance vpnInstance = new VpnInstance(neId, vrfEntities);
            vpnInstanceList.add(vpnInstance);
        }
        return vpnInstanceList;
    }

    /**
     * Decompose the l3vpn network instance to list of vpn ac.
     *
     * @param acsByNeMap a map of acs entity to each ne
     * @param webNetL3vpnInstance the l3vpn network instance
     * @return list of vpn ac
     */
    public List<VpnAc> decompVpnAc(Map<String, List<WebAc>> acsByNeMap,
                                   WebNetL3vpnInstance webNetL3vpnInstance) {
        List<VpnAc> vpnAcList = new ArrayList<VpnAc>();
        String netVpnId = webNetL3vpnInstance.getId();
        for (String neId : webNetL3vpnInstance.getNeIdList()) {
            List<WebAc> webAcList = acsByNeMap.get(neId);
            for (WebAc webAc : webAcList) {
                String acId = webAc.getId();
                Port port = deviceService
                        .getPort(DeviceId.deviceId(neId),
                                 PortNumber.portNumber(webAc.getL2Access()
                                         .getPort().getLtpId()));
                String acName = port.annotations()
                        .value(AnnotationKeys.PORT_NAME);
                String ipAddress = webAc.getL3Access().getAddress();
                int subNetMask = Integer.parseInt(webAc.getL3Access()
                        .getAddress().split("/")[1]);
                VpnAc vpnAc = new VpnAc(netVpnId, acId, acName, ipAddress,
                                        subNetMask);
                vpnAcList.add(vpnAc);
            }
        }
        return vpnAcList;
    }
}
