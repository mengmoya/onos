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

/**
 * Immutable representation of a web ac.
 */
public final class WebAc {
    private final String id;
    private final String neId;
    private final WebL2Access l2Access;
    private final WebL3Access l3Access;

    /**
     * WebAc constructor.
     *
     * @param id ac id
     * @param neId ne id
     * @param l2Access l2 access
     * @param l3Access l3 access
     */
    public WebAc(String id, String neId, WebL2Access l2Access,
                 WebL3Access l3Access) {
        checkNotNull(id, "id cannot be null");
        checkNotNull(neId, "neId cannot be null");
        checkNotNull(l2Access, "l2Access cannot be null");
        checkNotNull(l3Access, "l3Access cannot be null");
        this.id = id;
        this.neId = neId;
        this.l2Access = l2Access;
        this.l3Access = l3Access;
    }

    /**
     * Returns the WebAc id.
     *
     * @return ac id
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the WebAc ne id.
     *
     * @return ne id
     */
    public String getNeId() {
        return neId;
    }

    /**
     * Returns the WebAc l2 access.
     *
     * @return l2 access
     */
    public WebL2Access getL2Access() {
        return l2Access;
    }

    /**
     * Returns the WebAc l3 access.
     *
     * @return l3 access
     */
    public WebL3Access getL3Access() {
        return l3Access;
    }
}
