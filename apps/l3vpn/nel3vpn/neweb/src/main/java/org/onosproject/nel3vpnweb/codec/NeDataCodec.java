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
package org.onosproject.nel3vpnweb.codec;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;

import org.onosproject.codec.CodecContext;
import org.onosproject.codec.JsonCodec;
import org.onosproject.ne.Bgp;
import org.onosproject.ne.BgpImportProtocol;
import org.onosproject.ne.BgpImportProtocol.ProtocolType;
import org.onosproject.ne.NeData;
import org.onosproject.ne.VpnAc;
import org.onosproject.ne.VpnInstance;
import org.onosproject.ne.VrfEntity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * NeData JSON codec.
 */
public final class NeDataCodec extends JsonCodec<NeData> {

    public static final String OBJECTNODE_NOT_NULL = "ObjectNode can not be null";
    public static final String CODECCONTEXT_NOT_NULL = "CodecContext can not be null";

    private static final String DIRECT = "direct";
    private static final String BGP = "bgp";
    private static final String ISIS = "isis";
    private static final String OSPF = "ospf";

    @Override
    public ObjectNode encode(NeData vPort, CodecContext context) {
        // TODO
        return null;
    }

    @Override
    public NeData decode(ObjectNode json, CodecContext context) {
        checkNotNull(json, OBJECTNODE_NOT_NULL);
        checkNotNull(context, CODECCONTEXT_NOT_NULL);
        JsonNode networkelement = json.get("networkelement");
        JsonNode instances = networkelement.get("instances");
        JsonNode acs = networkelement.get("acs");
        List<VpnInstance> vpnInstanceList = new ArrayList<VpnInstance>();
        List<VpnAc> vpnAcList = new ArrayList<VpnAc>();
        for (JsonNode instance : instances) {
            String neid = instance.get("neid").asText();
            JsonNode vrfs = instance.get("vrfs");
            List<VrfEntity> vrfList = new ArrayList<VrfEntity>();
            for (JsonNode vrf : vrfs) {
                String name = vrf.get("name").asText();
                String rd = vrf.get("rd").asText();
                List<String> its = new ArrayList<String>();
                for (JsonNode it : vrf.get("its")) {
                    its.add(it.asText());
                }
                List<String> ets = new ArrayList<String>();
                for (JsonNode et : vrf.get("ets")) {
                    ets.add(et.asText());
                }
                List<String> acids = new ArrayList<String>();
                for (JsonNode acid : vrf.get("acids")) {
                    acids.add(acid.asText());
                }
                JsonNode importprotocols = instance.get("bgp")
                        .get("importprotocols");
                List<BgpImportProtocol> importProtocols = new ArrayList<BgpImportProtocol>();
                for (JsonNode importprotocol : importprotocols) {
                    switch (importprotocol.asText().toUpperCase()) {
                    case DIRECT:
                        importProtocols
                                .add(new BgpImportProtocol(ProtocolType.DIRECT));
                        break;
                    case BGP:
                        importProtocols
                                .add(new BgpImportProtocol(ProtocolType.BGP));
                        break;
                    case ISIS:
                        importProtocols
                                .add(new BgpImportProtocol(ProtocolType.ISIS));
                        break;
                    case OSPF:
                        importProtocols
                                .add(new BgpImportProtocol(ProtocolType.OSPF));
                        break;
                    default:
                        break;
                    }
                }
                Bgp bgp = new Bgp(importProtocols);
                vrfList.add(new VrfEntity(name, rd, its, ets, acids, bgp));
            }
            vpnInstanceList.add(new VpnInstance(neid, vrfList));
        }
        for (JsonNode ac : acs) {
            String id = ac.get("id").asText();
            String name = ac.get("name").asText();
            String ip = ac.get("ip").asText();
            Integer mask = ac.get("mask").asInt();
            vpnAcList.add(new VpnAc(id, name, ip, mask));
        }
        return new NeData(vpnInstanceList, vpnAcList);
    }
}
