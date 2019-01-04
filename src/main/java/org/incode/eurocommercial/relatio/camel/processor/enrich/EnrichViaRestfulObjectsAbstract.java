package org.incode.eurocommercial.relatio.camel.processor.enrich;

import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import org.apache.camel.Exchange;
import org.apache.camel.Message;

import org.apache.isis.schema.common.v1.OidDto;

import org.incode.eurocommercial.relatio.camel.processor.ProcessorAbstract;

import lombok.Setter;

public abstract class EnrichViaRestfulObjectsAbstract extends ProcessorAbstract {

    private UriBuilder uriBuilder;

    @Setter
    private String base;

    @Setter
    private String username;

    @Setter
    private String password;

    @Setter
    private String dtoRole;

    private JaxRsClient jaxRsClient;

    /**
     * Provides a seam for integration testing
     */
    public void setJaxRsClient(final JaxRsClient jaxRsClient) {
        this.jaxRsClient = jaxRsClient;
    }

    protected EnrichViaRestfulObjectsAbstract() {

    }

    public void init() {
        uriBuilder = UriBuilder.fromUri(base + "objects/{objectType}/{objectInstance}");

        // default implementation
        jaxRsClient = new JaxRsClient.Default();
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        Message inMessage = exchange.getIn();

        final OidDto oidDto = obtainDtoFrom(inMessage);
        if(oidDto == null) {
            return;
        }

        // OidDto changed so now populates type and id rather than objectType and objectIdentifier
        final String objectType = oidDto.getType() != null? oidDto.getType() : oidDto.getObjectType();
        final String objectIdentifier = oidDto.getId() != null ? oidDto.getId(): oidDto.getObjectIdentifier();

        final Class<?> dtoClass = dtoClass(objectType);

        final URI uri = uriBuilder.build(objectType, objectIdentifier);
        String username = this.username;
        String password = this.password;

        System.out.println("Sending request to: " + uri.toString() + " " + username + " " + password);

        JaxRsResponse jaxRsResponse = jaxRsClient.invoke(uri, dtoClass, username, password);

        final int status = jaxRsResponse.getStatus();
        if(status != 200) {
            final String responseEntity = jaxRsResponse.readEntity(String.class);

            System.out.println("Failed to retrieve " + dtoClass.getName());
            System.out.println("URI: " + uri.toString());
            System.out.println("Response: " + responseEntity);

            throw new Exception(responseEntity);
        }

        // set the DTO as header on the exchange for subsequent processors to access
        final Object dto = jaxRsResponse.readEntity(dtoClass);
        setHeader(inMessage, dto, elseDefault(dtoRole));

    }

    /**
     * Mandatory hook method.
     */
    protected abstract OidDto obtainDtoFrom(final Message inMessage);

    /**
     * Mandatory hook method to specify the required type; is used in the Accept http header for the request (x-ro-domain-type parameter).
     * @param objectType
     */
    protected abstract Class<?> dtoClass(final String objectType);



}
