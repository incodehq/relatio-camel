package org.incode.eurocommercial.relatio.camel.processor.mail;

import org.apache.camel.BeanInject;
import org.apache.camel.Exchange;

import org.incode.eurocommercial.relatio.camel.processor.ProcessorAbstract;
import org.incode.eurocommercial.relatio.camel.processor.util.MessageUtil;
import org.incode.eurocommercial.ecpcrm.canonical.center.v1.CenterDto;
import org.incode.eurocommercial.ecpcrm.canonical.user.v1.UserDto;

public class AddUserToMailingList extends ProcessorAbstract {
    @Override
    public void process(final Exchange exchange) {
        final UserDto userDto = MessageUtil.getHeader(exchange.getIn(), UserDto.class, "default");
        final CenterDto centerDto = MessageUtil.getHeader(exchange.getIn(), CenterDto.class, "default");

        if (userDto.isPromotionalEmails()) {
            mailService.subscribeUser(userDto, centerDto);
        } else {
            mailService.unsubscribeUser(userDto, centerDto);
        }
    }

    @BeanInject MailService mailService;
}
