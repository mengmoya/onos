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
 * Immutable representation of a web l3 access.
 */
public final class WebL3Access {
    private final String address;

    /**
     * WebL3Access constructor.
     *
     * @param address ip address
     */
    public WebL3Access(String address) {
        checkNotNull(address, "address cannot be null");
        this.address = address;
    }

    /**
     * Returns the WebL3Access address.
     *
     * @return address
     */
    public String getAddress() {
        return address;
    }
}
