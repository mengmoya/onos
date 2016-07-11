package org.onosproject.netl3vpn.manager.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;

import org.onosproject.netl3vpn.entity.WebAc;
import org.onosproject.netl3vpn.entity.WebNetL3vpnInstance;
import org.onosproject.netl3vpn.entity.WebNetL3vpnInstance.TopoModeType;
import org.onosproject.netl3vpn.util.ConvertUtil;
import org.onosproject.yang.gen.v1.net.l3vpn.rev20160701.netl3vpn.acgroup.acs.Ac;
import org.onosproject.yang.gen.v1.net.l3vpn.rev20160701.netl3vpn.instances.Instance;
import org.onosproject.yang.gen.v1.net.l3vpn.rev20160701.netl3vpn.instances.instance.nes.Ne;

public class NetL3vpnParse {
    private static final String INSTANCE_NOT_NULL = "Instance can not be null";

    private Instance instance;

    public NetL3vpnParse(Instance instance) {
        this.instance = instance;
    }

    public WebNetL3vpnInstance cfgParse() {
        checkNotNull(instance, INSTANCE_NOT_NULL);
        WebNetL3vpnInstance webNetL3vpnInstance = new WebNetL3vpnInstance();
        webNetL3vpnInstance.setId(instance.id());
        webNetL3vpnInstance.setName(instance.name());
        webNetL3vpnInstance.setMode(TopoModeType.valueOf("FullMesh"));
        List<String> neIdList = new ArrayList<String>();
        for (Ne ne : instance.nes().ne()) {
            neIdList.add(ne.id());
        }
        webNetL3vpnInstance.setNeIdList(neIdList);
        List<WebAc> webAcs = new ArrayList<WebAc>();
        for (Ac ac : instance.acs().ac()) {
            webAcs.add(ConvertUtil.convertToWebAc(ac));
        }
        webNetL3vpnInstance.setAcList(webAcs);
        return webNetL3vpnInstance;
    }
}
