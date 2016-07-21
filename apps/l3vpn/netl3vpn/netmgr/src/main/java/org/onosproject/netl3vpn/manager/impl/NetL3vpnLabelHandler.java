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

import java.util.Collection;
import java.util.Iterator;

import org.onosproject.incubator.net.resource.label.DefaultLabelResource;
import org.onosproject.incubator.net.resource.label.LabelResource;
import org.onosproject.incubator.net.resource.label.LabelResourceAdminService;
import org.onosproject.incubator.net.resource.label.LabelResourceId;
import org.onosproject.incubator.net.resource.label.LabelResourceService;
import org.onosproject.netl3vpn.entity.WebNetL3vpnInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * L3vpn network label resource handler.
 */
public final class NetL3vpnLabelHandler {
    private static final Logger log = LoggerFactory
            .getLogger(NetL3vpnLabelHandler.class);
    private static final String RD_PREFIX = "100:";
    private static final String RT_PREFIX = "100:";
    private static final String VRF_PREFIX = "VRF_";
    private static final String LABEL_RESOURCE_ADMIN_SERVICE_NULL = "Label Resource Admin Service cannot be null";
    private static NetL3vpnLabelHandler netL3vpnLabelHandler = null;
    private LabelResourceAdminService labelRsrcAdminService;
    private LabelResourceService labelRsrcService;

    /**
     * Initializes default values.
     */
    private NetL3vpnLabelHandler() {
    }

    /**
     * Returns single instance of this class.
     *
     * @return this class single instance
     */
    public static NetL3vpnLabelHandler getInstance() {
        if (netL3vpnLabelHandler == null) {
            netL3vpnLabelHandler = new NetL3vpnLabelHandler();
        }
        return netL3vpnLabelHandler;
    }

    /**
     * Initialization of label manager interfaces.
     *
     * @param labelRsrcAdminService label resource admin service
     * @param labelRsrcService label resource service
     */
    public void initialize(LabelResourceAdminService labelRsrcAdminService,
                           LabelResourceService labelRsrcService) {
        this.labelRsrcAdminService = labelRsrcAdminService;
        this.labelRsrcService = labelRsrcService;
    }

    /**
     * Reserves the global label pool.
     *
     * @param beginLabel minimum value of global label space
     * @param endLabel maximum value of global label space
     * @return success or failure
     */
    public boolean reserveGlobalPool(long beginLabel, long endLabel) {
        checkNotNull(labelRsrcAdminService, LABEL_RESOURCE_ADMIN_SERVICE_NULL);
        return labelRsrcAdminService
                .createGlobalPool(LabelResourceId.labelResourceId(beginLabel),
                                  LabelResourceId.labelResourceId(endLabel));
    }

    /**
     * Allocates node label from global node label pool to specific type.
     *
     * @param allocateType the specific label type
     * @param webNetL3vpnInstance the l3vpn network instance
     * @return allocated label value
     */
    public String allocateResource(String allocateType,
                                   WebNetL3vpnInstance webNetL3vpnInstance) {
        long applyNum = 1; // For each vpn only one rd label
        LabelResourceId specificLabelId = null;
        Collection<LabelResource> result = labelRsrcService
                .applyFromGlobalPool(applyNum);
        if (result.size() > 0) {
            // Only one element to retrieve
            Iterator<LabelResource> iterator = result.iterator();
            DefaultLabelResource defaultLabelResource = (DefaultLabelResource) iterator
                    .next();
            specificLabelId = defaultLabelResource.labelResourceId();
            if (specificLabelId == null) {
                log.error("Unable to retrieve {} label for a vpn id {}.",
                          allocateType, webNetL3vpnInstance.getId());
                return null;
            }
        } else {
            log.error("Unable to allocate {} label for a vpn id {}.",
                      allocateType, webNetL3vpnInstance.getId());
            return null;
        }
        switch (allocateType) {
        case "rd":
            return RD_PREFIX + specificLabelId.id();
        case "rt":
            return RT_PREFIX + specificLabelId.id();
        case "vrf":
            return VRF_PREFIX + specificLabelId.id();
        default:
            log.error("Unable to allocate {} label for a vpn id {}.",
                      allocateType, webNetL3vpnInstance.getId());
            return null;
        }
    }
}
