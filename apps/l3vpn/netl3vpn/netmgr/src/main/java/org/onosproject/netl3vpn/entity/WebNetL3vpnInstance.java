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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

/**
 * Immutable representation of a web l3 vpn network instance.
 */
public final class WebNetL3vpnInstance {
    private final String id;
    private final String name;
    private final TopoModeType mode;
    private final List<String> neIdList;
    private final List<WebAc> acList;

    /**
     * WebNetL3vpnInstance constructor.
     *
     * @param id instance id
     * @param name vpn name
     * @param mode topo mode type
     * @param neIdList list of ne id
     * @param acList list of ac
     */
    public WebNetL3vpnInstance(String id, String name, TopoModeType mode,
                               List<String> neIdList, List<WebAc> acList) {
        checkNotNull(id, "id cannot be null");
        checkNotNull(name, "name cannot be null");
        checkNotNull(mode, "mode cannot be null");
        checkNotNull(neIdList, "neIdList cannot be null");
        checkNotNull(acList, "acList cannot be null");
        this.id = id;
        this.name = name;
        this.mode = mode;
        this.neIdList = neIdList;
        this.acList = acList;
    }

    /**
     * The enumeration of topo mode type.
     */
    public enum TopoModeType {
        None(0), HubSpoke(1), FullMesh(2);

        int value;

        private TopoModeType(int value) {
            this.value = value;
        }

        public int value() {
            return value;
        }
    }

    /**
     * Returns the WebNetL3vpnInstance id.
     *
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the WebNetL3vpnInstance name.
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the WebNetL3vpnInstance topo mode type.
     *
     * @return topo mode type
     */
    public TopoModeType getMode() {
        return mode;
    }

    /**
     * Returns the WebNetL3vpnInstance ne id list.
     *
     * @return ne id list
     */
    public List<String> getNeIdList() {
        return neIdList;
    }

    /**
     * Returns the WebNetL3vpnInstance ac list.
     *
     * @return ac list
     */
    public List<WebAc> getAcList() {
        return acList;
    }
}
