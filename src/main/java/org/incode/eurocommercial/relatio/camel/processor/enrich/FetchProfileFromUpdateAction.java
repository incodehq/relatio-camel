package org.incode.eurocommercial.relatio.camel.processor.enrich;

import org.apache.camel.Message;
import org.apache.isis.schema.common.v1.OidDto;
import org.apache.isis.schema.ixn.v1.InteractionDto;
import org.incode.eurocommercial.relatio.canonical.profile.v1.ProfileDto;

public class FetchProfileFromUpdateAction extends EnrichViaRestfulObjectsAbstract {

    public FetchProfileFromUpdateAction() {
        super();
    }

    protected OidDto obtainDtoFrom(final Message inMessage) {
        final InteractionDto interactionDto = (InteractionDto) inMessage.getBody();
        return interactionDto.getExecution().getTarget();
    }

    @Override
    protected Class<?> dtoClass(final String objectType) {
        return ProfileDto.class;
    }
}
