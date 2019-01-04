/**
 * Copyright 2015-2016 Eurocommercial Properties NV
 * <p>
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.incode.eurocommercial.relatio.camel.processor.enrich;

import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import org.apache.camel.Message;

import org.apache.isis.schema.common.v1.OidDto;

import org.incode.eurocommercial.relatio.camel.processor.util.MessageUtil;

import lombok.Setter;

public class EnrichmentService {
    private UriBuilder uriBuilder;

    @Setter
    private String base;

    @Setter
    private String username;

    @Setter
    private String password;

    @Setter
    private JaxRsClient jaxRsClient;

    public void init() {
        jaxRsClient = new JaxRsClient.Default();
    }

    public void invokeAction(String service, String action, String queryString) throws Exception {
        uriBuilder = UriBuilder
                .fromUri(base + "services/{service}/actions/{action}/invoke/")
                .replaceQuery(queryString);
        final URI uri = uriBuilder.build(service, action);

        JaxRsResponse jaxRsResponse = jaxRsClient.get(uri, username, password);

        final int status = jaxRsResponse.getStatus();
        if(status != 200) {
            final String responseEntity = jaxRsResponse.readEntity(String.class);

            System.out.println("Failed to invoke " + service + "#" + action + "/?" + queryString);
            System.out.println("URI: " + uri.toString());
            System.out.println("Response: " + responseEntity);

            throw new Exception(responseEntity);
        }
    }

    public <T> T retrieveDto(OidDto oidDto, Class<T> dtoClass) throws Exception {
        final String objectType = oidDto.getType() != null? oidDto.getType() : oidDto.getObjectType();
        final String objectIdentifier = oidDto.getId() != null ? oidDto.getId(): oidDto.getObjectIdentifier();

        uriBuilder = UriBuilder.fromUri(base + "objects/{objectType}/{objectInstance}");
        final URI uri = uriBuilder.build(objectType, objectIdentifier);

        JaxRsResponse jaxRsResponse = jaxRsClient.invoke(uri, dtoClass, username, password);

        final int status = jaxRsResponse.getStatus();
        if(status != 200) {
            final String responseEntity = jaxRsResponse.readEntity(String.class);


            System.out.println("Failed to retrieve " + dtoClass.getName());
            System.out.println("URI: " + uri.toString());
            System.out.println("Response: " + responseEntity);

            throw new Exception(responseEntity);
        }

        return jaxRsResponse.readEntity(dtoClass);
    }

    public <T> void enrichMessageWithDto(Message message, OidDto oidDto, Class<T> dtoClass) throws Exception {
        enrichMessageWithDto(message, oidDto, dtoClass, "default");
    }

    public <T> void enrichMessageWithDto(Message message, OidDto oidDto, Class<T> dtoClass, String role) throws Exception {
        T dto = retrieveDto(oidDto, dtoClass);
        MessageUtil.setHeader(message, dto, role);
    }

    public <T> void enrichMessageWithLocalDto(Message message, T dto) {
        enrichMessageWithLocalDto(message, dto, "default");
    }

    public <T> void enrichMessageWithLocalDto(Message message, T dto, String role) {
        MessageUtil.setHeader(message, dto, role);
    }
}
