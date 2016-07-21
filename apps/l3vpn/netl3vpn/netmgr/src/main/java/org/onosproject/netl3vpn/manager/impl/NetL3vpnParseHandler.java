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
import java.util.List;

import org.onosproject.netl3vpn.entity.WebAc;
import org.onosproject.netl3vpn.entity.WebNetL3vpnInstance;
import org.onosproject.netl3vpn.entity.WebNetL3vpnInstance.TopoModeType;
import org.onosproject.netl3vpn.util.ConvertUtil;
import org.onosproject.yang.gen.v1.net.l3vpn.rev20160701.netl3vpn.acgroup.acs.Ac;
import org.onosproject.yang.gen.v1.net.l3vpn.rev20160701.netl3vpn.instances.Instance;
import org.onosproject.yang.gen.v1.net.l3vpn.rev20160701.netl3vpn.instances.instance.nes.Ne;

/**
 * L3vpn network parse configuration handler.
 */
public final class NetL3vpnParseHandler {
    private static NetL3vpnParseHandler netL3vpnParseHandler = null;
    private Instance instance;

    /**
     * Initializes default values.
     */
    private NetL3vpnParseHandler() {
    }

    /**
     * Returns single instance of this class.
     *
     * @return this class single instance
     */
    public static NetL3vpnParseHandler getInstance() {
        if (netL3vpnParseHandler == null) {
            netL3vpnParseHandler = new NetL3vpnParseHandler();
        }
        return netL3vpnParseHandler;
    }

    /**
     * Initialization the instance.
     *
     * @param instance the specific instance
     */
    public void initialize(Instance instance) {
        this.instance = instance;
    }

    /**
     * Return a WebNetL3vpnInstance of parsing the configuration for instance .
     *
     * @return WebNetL3vpnInstance a WebNetL3vpnInstance
     */
    public WebNetL3vpnInstance cfgParse() {
        List<String> neIdList = new ArrayList<String>();
        for (Ne ne : instance.nes().ne()) {
            neIdList.add(ne.id());
        }
        List<WebAc> webAcs = new ArrayList<WebAc>();
        for (Ac ac : instance.acs().ac()) {
            webAcs.add(ConvertUtil.convertToWebAc(ac));
        }
        WebNetL3vpnInstance webNetL3vpnInstance = new WebNetL3vpnInstance(instance
                .id(), instance.name(), TopoModeType
                        .valueOf("FullMesh"), neIdList, webAcs);
        return webNetL3vpnInstance;
    }
}
