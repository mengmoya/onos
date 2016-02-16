/*
 *
 *  * Copyright 2016 Open Networking Laboratory
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.onosproject.drivers.cisco;

import org.onlab.packet.VlanId;
import org.onosproject.drivers.utilities.XmlConfigParser;
import org.onosproject.net.DeviceId;
import org.onosproject.net.behaviour.InterfaceConfig;
import org.onosproject.net.driver.AbstractHandlerBehaviour;
import org.onosproject.netconf.NetconfController;
import org.onosproject.netconf.NetconfException;
import org.onosproject.netconf.NetconfSession;
import org.slf4j.Logger;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Configures interfaces on Cisco SM-X devices.
 */
public class InterfaceConfigCiscoSmXImpl extends AbstractHandlerBehaviour
        implements InterfaceConfig {

    private final Logger log = getLogger(getClass());

    /**
     * Adds an interface to a VLAN.
     * @param deviceId the device ID
     * @param intf the name of the interface
     * @param vlanId the VLAN ID
     * @return the result of operation
     */
    @Override
    public boolean addInterfaceToVlan(DeviceId deviceId, String intf, VlanId vlanId) {
        NetconfController controller = checkNotNull(handler()
                                       .get(NetconfController.class));

        NetconfSession session = controller.getDevicesMap().get(handler()
                                 .data().deviceId()).getSession();
        String reply;
        try {
            reply = session.requestSync(addInterfaceToVlanBuilder(intf, vlanId)).trim();
        } catch (NetconfException e) {
            log.error("Failed to configure VLAN ID {} on device {} port {}.",
                      vlanId, deviceId, intf, e);
            return false;
        }

        return XmlConfigParser.configSuccess(XmlConfigParser.loadXml(
                new ByteArrayInputStream(reply.getBytes(StandardCharsets.UTF_8))));
    }

    /**
     * Builds a request crafted to add an interface to a VLAN.
     * @param intf the name of the interface
     * @param vlanId the VLAN ID
     * @return the request string.
     */
    private String addInterfaceToVlanBuilder(String intf, VlanId vlanId) {
        StringBuilder rpc = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        rpc.append("<rpc xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" ");
        rpc.append("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">");
        rpc.append("<edit-config>");
        rpc.append("<target>");
        rpc.append("<running/>");
        rpc.append("</target>");
        rpc.append("<config>");
        rpc.append("<xml-config-data>");
        rpc.append("<Device-Configuration><interface><Param>");
        rpc.append(intf);
        rpc.append("</Param>");
        rpc.append("<ConfigIf-Configuration>");
        rpc.append("<switchport><access><vlan><VLANIDVLANPortAccessMode>");
        rpc.append(vlanId);
        rpc.append("</VLANIDVLANPortAccessMode></vlan></access></switchport>");
        rpc.append("<switchport><mode><access/></mode></switchport>");
        rpc.append("</ConfigIf-Configuration>");
        rpc.append("</interface>");
        rpc.append("</Device-Configuration>");
        rpc.append("</xml-config-data>");
        rpc.append("</config>");
        rpc.append("</edit-config>");
        rpc.append("</rpc>");

        return rpc.toString();
    }

    /**
     * Configures an interface as trunk for VLAN.
     * @param deviceId the device ID
     * @param intf the name of the interface
     * @param vlanId the VLAN ID
     * @return the result of operation
     */
    @Override
    public boolean addTrunkInterface(DeviceId deviceId, String intf, VlanId vlanId) {
        NetconfController controller = checkNotNull(handler()
                                       .get(NetconfController.class));

        NetconfSession session = controller.getDevicesMap().get(handler()
                                 .data().deviceId()).getSession();
        String reply;
        try {
            reply = session.requestSync(addTrunkInterface(intf, vlanId)).trim();
        } catch (NetconfException e) {
            log.error("Failed to configure VLAN ID {} on device {} port {}.",
                      vlanId, deviceId, intf, e);
            return false;
        }

        return XmlConfigParser.configSuccess(XmlConfigParser.loadXml(
                new ByteArrayInputStream(reply.getBytes(StandardCharsets.UTF_8))));
    }

    /**
     * Builds a request crafted to configure an interface as trunk for VLAN.
     * @param intf the name of the interface
     * @param vlanId the VLAN ID
     * @return the request string.
     */
    private String addTrunkInterface(String intf, VlanId vlanId) {
        StringBuilder rpc = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        rpc.append("<rpc xmlns=\"urn:ietf:params:xml:ns:netconf:base:1.0\" ");
        rpc.append("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">");
        rpc.append("<edit-config>");
        rpc.append("<target>");
        rpc.append("<running/>");
        rpc.append("</target>");
        rpc.append("<config>");
        rpc.append("<xml-config-data>");
        rpc.append("<Device-Configuration><interface><Param>");
        rpc.append(intf);
        rpc.append("</Param>");
        rpc.append("<ConfigIf-Configuration>");
        rpc.append("<switchport><trunk><encapsulation><dot1q/></encapsulation></trunk></switchport>");
        rpc.append("<switchport><trunk><allowed><vlan><VLANIDsAllowedVLANsPortTrunkingMode>");
        rpc.append(vlanId);
        rpc.append("</VLANIDsAllowedVLANsPortTrunkingMode></vlan></allowed></trunk></switchport>");
        rpc.append("<switchport><mode><trunk/></mode></switchport>");
        rpc.append("</ConfigIf-Configuration>");
        rpc.append("</interface>");
        rpc.append("</Device-Configuration>");
        rpc.append("</xml-config-data>");
        rpc.append("</config>");
        rpc.append("</edit-config>");
        rpc.append("</rpc>");

        return rpc.toString();
    }

}

