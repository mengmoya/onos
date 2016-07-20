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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.Service;
import org.onlab.util.KryoNamespace;
import org.onosproject.core.ApplicationId;
import org.onosproject.core.CoreService;
import org.onosproject.incubator.net.resource.label.LabelResourceAdminService;
import org.onosproject.incubator.net.resource.label.LabelResourceService;
import org.onosproject.mastership.MastershipService;
import org.onosproject.ne.NeData;
import org.onosproject.ne.VpnAc;
import org.onosproject.ne.VpnInstance;
import org.onosproject.ne.manager.L3vpnNeService;
import org.onosproject.net.DeviceId;
import org.onosproject.net.device.DeviceService;
import org.onosproject.netl3vpn.entity.NetL3VpnAllocateRes;
import org.onosproject.netl3vpn.entity.WebNetL3vpnInstance;
import org.onosproject.netl3vpn.manager.NetL3vpnService;
import org.onosproject.store.serializers.KryoNamespaces;
import org.onosproject.store.service.EventuallyConsistentMap;
import org.onosproject.store.service.LogicalClockService;
import org.onosproject.store.service.StorageService;
import org.onosproject.yang.gen.v1.net.l3vpn.rev20160701.netl3vpn.instances.Instance;
import org.onosproject.yang.gen.v1.net.l3vpn.rev20160701.netl3vpn.instances.instance.nes.Ne;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides implementation of NetL3vpnService.
 */
@Component(immediate = true)
@Service
public class NetL3vpnManager implements NetL3vpnService {
    private static final String INSTANCE_NOT_NULL = "Instance can not be null";
    private static final String APP_ID = "org.onosproject.app.l3vpn.net";
    private static final String RT = "rt";
    private static final String RD = "rd";
    private static final String VRF = "vrf";
    private static final String NETL3INSTANCESTORE = "netl3vpn-instance";
    private static final String NEL3INSTANCESTORE = "nel3vpn-instance";
    private static final String NEL3ACSTORE = "nel3vpn-ac";
    private static final long GLOBAL_LABEL_SPACE_MIN = 2049;
    private static final long GLOBAL_LABEL_SPACE_MAX = 3073;
    protected static final Logger log = LoggerFactory
            .getLogger(NetL3vpnManager.class);

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected CoreService coreService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected DeviceService deviceService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected MastershipService mastershipService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected StorageService storageService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected LogicalClockService clockService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected LabelResourceAdminService labelRsrcAdminService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected LabelResourceService labelRsrcService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    private L3vpnNeService l3vpnNeService;

    private ApplicationId appId;
    private WebNetL3vpnInstance webNetL3vpnInstance;
    private NetL3VpnAllocateRes l3VpnAllocateRes;
    private NetL3vpnParseHandler netL3vpnParseHandler;
    private NetL3vpnLabelHandler netL3vpnLabelHandler;
    private NetL3vpnDecompHandler netL3vpnDecompHandler;
    private EventuallyConsistentMap<String, WebNetL3vpnInstance> webNetL3vpnStore;
    private EventuallyConsistentMap<String, VpnInstance> vpnInstanceStore;
    private EventuallyConsistentMap<String, VpnAc> vpnAcStore;

    @Activate
    public void activate() {
        appId = coreService.registerApplication(APP_ID);
        netL3vpnParseHandler = NetL3vpnParseHandler.getInstance();

        netL3vpnLabelHandler = NetL3vpnLabelHandler.getInstance();
        netL3vpnLabelHandler.initialize(labelRsrcAdminService,
                                        labelRsrcService);

        netL3vpnDecompHandler = NetL3vpnDecompHandler.getInstance();

        KryoNamespace.Builder serializer = KryoNamespace.newBuilder()
                .register(KryoNamespaces.API).register(VpnInstance.class)
                .register(WebNetL3vpnInstance.class).register(VpnAc.class);
        webNetL3vpnStore = storageService
                .<String, WebNetL3vpnInstance>eventuallyConsistentMapBuilder()
                .withName(NETL3INSTANCESTORE).withSerializer(serializer)
                .withTimestampProvider((k, v) -> clockService.getTimestamp())
                .build();
        vpnInstanceStore = storageService
                .<String, VpnInstance>eventuallyConsistentMapBuilder()
                .withName(NEL3INSTANCESTORE).withSerializer(serializer)
                .withTimestampProvider((k, v) -> clockService.getTimestamp())
                .build();
        vpnAcStore = storageService
                .<String, VpnAc>eventuallyConsistentMapBuilder()
                .withName(NEL3ACSTORE).withSerializer(serializer)
                .withTimestampProvider((k, v) -> clockService.getTimestamp())
                .build();

        if (!netL3vpnLabelHandler.reserveGlobalPool(GLOBAL_LABEL_SPACE_MIN,
                                                    GLOBAL_LABEL_SPACE_MAX)) {
            log.debug("Global node pool was already reserved.");
        }
        log.info("Started");
    }

    @Deactivate
    public void deactivate() {
        log.info("Stopped");
    }

    @Override
    public boolean createL3vpn(Instance instance) {
        checkNotNull(instance, INSTANCE_NOT_NULL);
        if (!checkDeviceStatus(instance)) {
            return false;
        }
        netL3vpnParseHandler.initialize(instance);
        webNetL3vpnInstance = netL3vpnParseHandler.cfgParse();
        if (webNetL3vpnInstance == null) {
            log.debug("Parsing the web vpn instance failed, whose identifier is {} ",
                      instance.id());
            return false;
        }
        if (!checkOccupiedResource()) {
            log.debug("The resource of l3vpn instance is occupied.");
            return false;
        }
        webNetL3vpnStore.put(webNetL3vpnInstance.getId(), webNetL3vpnInstance);
        l3VpnAllocateRes = applyResource();
        if (l3VpnAllocateRes == null) {
            log.debug("Apply resources of l3vpn instance failed.");
            return false;
        }
        netL3vpnDecompHandler.initialize(l3VpnAllocateRes, deviceService);
        NeData neData = netL3vpnDecompHandler.decompNeData(webNetL3vpnInstance);
        for (VpnInstance vpnInstance : neData.vpnInstanceList()) {
            vpnInstanceStore.put(vpnInstance.neId(), vpnInstance);
        }
        for (VpnAc vpnAc : neData.vpnAcList()) {
            vpnAcStore.put(vpnAc.acId(), vpnAc);
        }
        return l3vpnNeService.createL3vpn(neData);
    }

    /**
     * Check the status of devices for the instance.
     *
     * @param instance the specific instance
     * @return success or failure
     */
    public boolean checkDeviceStatus(Instance instance) {
        for (Ne ne : instance.nes().ne()) {
            DeviceId deviceId = DeviceId.deviceId(ne.id());
            if (deviceService.getDevice(deviceId) == null) {
                log.debug("Cannot get the device, whose ne id is {}.", ne.id());
                return false;
            }
            if (!deviceService.isAvailable(deviceId)) {
                log.debug("The device whose ne id is {} cannot be available.",
                          ne.id());
                return false;
            }
            if (!mastershipService.isLocalMaster(deviceId)) {
                log.debug("The device whose ne id is {} is not master role.",
                          ne.id());
                return false;
            }
        }
        return true;
    }

    /**
     * Check the resource is valid or not.
     *
     * @return valid or not
     */
    public boolean checkOccupiedResource() {
        for (Entry<String, WebNetL3vpnInstance> entry : webNetL3vpnStore
                .entrySet()) {
            if (entry.getValue().getName() == webNetL3vpnInstance.getName()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Apply the node labels from global node label pool.
     *
     * @return the allocate resource entity
     */
    public NetL3VpnAllocateRes applyResource() {
        String routeTarget = netL3vpnLabelHandler
                .allocateResource(RT, webNetL3vpnInstance);
        Map<String, String> routeDistinguisherMap = new HashMap<String, String>();
        for (String neId : webNetL3vpnInstance.getNeIdList()) {
            String routeDistinguisher = netL3vpnLabelHandler
                    .allocateResource(RD, webNetL3vpnInstance);
            routeDistinguisherMap.put(neId, routeDistinguisher);
        }

        String vrfName = netL3vpnLabelHandler
                .allocateResource(VRF, webNetL3vpnInstance);
        if (routeTarget == null || routeDistinguisherMap == null
                || vrfName == null) {
            return null;
        }

        List<String> routeTargets = new ArrayList<String>();
        routeTargets.add(routeTarget);
        return new NetL3VpnAllocateRes(routeTargets, routeDistinguisherMap,
                                       vrfName);
    }
}
