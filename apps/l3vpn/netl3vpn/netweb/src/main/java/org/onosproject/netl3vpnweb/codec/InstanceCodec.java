package org.onosproject.netl3vpnweb.codec;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;

import org.onosproject.codec.CodecContext;
import org.onosproject.codec.JsonCodec;
import org.onosproject.yang.gen.v1.l3vpn.comm.type.rev20141225.nel3vpncommtype.Ipv4Address;
import org.onosproject.yang.gen.v1.net.l3vpn.rev20160701.netl3vpn.acgroup.Acs;
import org.onosproject.yang.gen.v1.net.l3vpn.rev20160701.netl3vpn.acgroup.AcsBuilder;
import org.onosproject.yang.gen.v1.net.l3vpn.rev20160701.netl3vpn.acgroup.acs.Ac;
import org.onosproject.yang.gen.v1.net.l3vpn.rev20160701.netl3vpn.acgroup.acs.AcBuilder;
import org.onosproject.yang.gen.v1.net.l3vpn.rev20160701.netl3vpn.acgroup.acs.ac.L2Access;
import org.onosproject.yang.gen.v1.net.l3vpn.rev20160701.netl3vpn.acgroup.acs.ac.L2AccessBuilder;
import org.onosproject.yang.gen.v1.net.l3vpn.rev20160701.netl3vpn.acgroup.acs.ac.L3Access;
import org.onosproject.yang.gen.v1.net.l3vpn.rev20160701.netl3vpn.acgroup.acs.ac.L3AccessBuilder;
import org.onosproject.yang.gen.v1.net.l3vpn.rev20160701.netl3vpn.instances.Instance;
import org.onosproject.yang.gen.v1.net.l3vpn.rev20160701.netl3vpn.instances.InstanceBuilder;
import org.onosproject.yang.gen.v1.net.l3vpn.rev20160701.netl3vpn.instances.instance.Nes;
import org.onosproject.yang.gen.v1.net.l3vpn.rev20160701.netl3vpn.instances.instance.NesBuilder;
import org.onosproject.yang.gen.v1.net.l3vpn.rev20160701.netl3vpn.instances.instance.nes.Ne;
import org.onosproject.yang.gen.v1.net.l3vpn.rev20160701.netl3vpn.instances.instance.nes.NeBuilder;
import org.onosproject.yang.gen.v1.net.l3vpn.type.rev20160701.netl3vpntype.l2access.Port;
import org.onosproject.yang.gen.v1.net.l3vpn.type.rev20160701.netl3vpntype.l2access.PortBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public final class InstanceCodec extends JsonCodec<Instance> {
    public static final String OBJECTNODE_NOT_NULL = "ObjectNode can not be null";
    public static final String CODECCONTEXT_NOT_NULL = "CodecContext can not be null";
    public static final String JSON_NOT_NULL = "JsonNode can not be null";

    @Override
    public ObjectNode encode(Instance vPort, CodecContext context) {
        // TODO
        return null;
    }

    @Override
    public Instance decode(ObjectNode json, CodecContext context) {
        checkNotNull(json, OBJECTNODE_NOT_NULL);
        checkNotNull(context, CODECCONTEXT_NOT_NULL);
        JsonNode instancesNode = json.get("instances");
        if (instancesNode == null) {
            instancesNode = json.get("instance");
        }
        Instance instance = changeJsonToInstance(instancesNode);
        return instance;
    }

    /**
     * Returns a instance from Instance json node.
     *
     * @param instanceNode the Instance json node
     * @return Instance a Instance
     */
    public Instance changeJsonToInstance(JsonNode instancesNode) {
        // TODO check the fields?
        checkNotNull(instancesNode, JSON_NOT_NULL);
        JsonNode instanceNode = instancesNode.get("instance");
        if (instanceNode == null) {
            instanceNode = instancesNode;
        }
        InstanceBuilder instanceBuiler = new InstanceBuilder();
        String id = instanceNode.get("id").asText();
        String name = instanceNode.get("name").asText();
        String mode = instanceNode.get("mode").asText();
        JsonNode nesNode = instanceNode.get("nes");
        Nes nes = createNesByJsonNode(nesNode);
        JsonNode acsNode = instanceNode.get("acs");
        Acs acs = createAcsByJsonNode(acsNode);
        Instance instance = instanceBuiler.id(id).name(name).mode(mode).nes(nes)
                .acs(acs).build();
        return instance;
    }

    public Nes createNesByJsonNode(JsonNode nesNode) {
        checkNotNull(nesNode, JSON_NOT_NULL);
        List<Ne> neList = new ArrayList<Ne>();
        if (nesNode.isArray()) {
            for (JsonNode neNode : nesNode) {
                Ne ne = changeJsonToNe(neNode);
                neList.add(ne);
            }
        } else {
            Ne ne = changeJsonToNe(nesNode);
            neList.add(ne);
        }
        Nes nes = new NesBuilder().ne(neList).build();
        return nes;
    }

    public Ne changeJsonToNe(JsonNode nesNode) {
        JsonNode neNode = nesNode.get("nes");
        if (neNode == null) {
            neNode = nesNode;
        }
        Ne ne = new NeBuilder().id(neNode.get("id").asText()).build();
        return ne;
    }

    public Acs createAcsByJsonNode(JsonNode acsNode) {
        checkNotNull(acsNode, JSON_NOT_NULL);
        List<Ac> acList = new ArrayList<Ac>();
        if (acsNode.isArray()) {
            for (JsonNode acNode : acsNode) {
                Ac ac = changeJsonToAc(acNode);
                acList.add(ac);
            }
        } else {
            Ac ac = changeJsonToAc(acsNode);
            acList.add(ac);
        }
        Acs acs = new AcsBuilder().ac(acList).build();
        return acs;
    }

    public Ac changeJsonToAc(JsonNode acsNode) {
        JsonNode acNode = acsNode.get("acs");
        if (acNode == null) {
            acNode = acsNode;
        }
        JsonNode l2AccessNode = acNode.get("l2-access");
        String accessType = l2AccessNode.get("access-type").asText();
        String ltpId = l2AccessNode.get("port").get("ltp-id").asText();
        Port port = new PortBuilder().ltpId(ltpId).build();
        L2Access l2Access = new L2AccessBuilder().accessType(accessType)
                .port(port).build();
        String ipv4Address = acNode.get("l3-access").get("address").asText();
        Ipv4Address address = new Ipv4Address(ipv4Address);
        L3Access l3Access = new L3AccessBuilder().address(address).build();
        Ac ac = new AcBuilder().id(acNode.get("id").asText())
                .neId(acNode.get("ne-id").asText()).l2Access(l2Access)
                .l3Access(l3Access).build();
        return ac;
    }
}
