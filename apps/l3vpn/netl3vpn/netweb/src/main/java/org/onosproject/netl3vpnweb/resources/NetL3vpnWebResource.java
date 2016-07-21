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
package org.onosproject.netl3vpnweb.resources;

import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static javax.ws.rs.core.Response.Status.OK;

import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.onosproject.netl3vpn.manager.NetL3vpnService;
import org.onosproject.rest.AbstractWebResource;
import org.onosproject.yang.gen.v1.net.l3vpn.rev20160701.netl3vpn.instances.Instance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * REST resource for interacting with l3vpn network.
 */
@Path("instance")
public class NetL3vpnWebResource extends AbstractWebResource {
    public static final String CREATE_INSTANCE_FAILED = "Create instance failed";
    protected static final Logger log = LoggerFactory
            .getLogger(NetL3vpnWebResource.class);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createInstance(InputStream input) {
        try {
            JsonNode cfg = this.mapper().readTree(input);
            Instance instance = codec(Instance.class).decode((ObjectNode) cfg,
                                                             this);
            Boolean issuccess = get(NetL3vpnService.class)
                    .createL3vpn(instance);
            if (!issuccess) {
                return Response.status(INTERNAL_SERVER_ERROR)
                        .entity(CREATE_INSTANCE_FAILED).build();
            }
            return Response.status(OK).entity(issuccess.toString()).build();
        } catch (Exception e) {
            log.error("Creates instance failed because of exception {}",
                      e.toString());
            return Response.status(INTERNAL_SERVER_ERROR).entity(e.toString())
                    .build();
        }
    }
}
