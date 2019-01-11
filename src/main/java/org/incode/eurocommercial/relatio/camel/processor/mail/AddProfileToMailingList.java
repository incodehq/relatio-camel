package org.incode.eurocommercial.relatio.camel.processor.mail;

import org.apache.camel.BeanInject;
import org.apache.camel.Exchange;

import org.incode.eurocommercial.relatio.camel.processor.ProcessorAbstract;
import org.incode.eurocommercial.relatio.camel.processor.util.MessageUtil;
import org.incode.eurocommercial.relatio.canonical.profile.v1.ProfileDto;

public class AddProfileToMailingList extends ProcessorAbstract {
    @Override
    public void process(final Exchange exchange) {
        final ProfileDto profileDto = MessageUtil.getHeader(exchange.getIn(), ProfileDto.class, "default");

        if (profileDto.isPrivacyConsent()) {
            mailService.propagateProfile(profileDto);
        }
    }

    @BeanInject MailService mailService;
}
