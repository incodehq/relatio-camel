package org.incode.eurocommercial.relatio.camel.processor.enrich;

import org.apache.camel.BeanInject;
import org.apache.camel.Exchange;
import org.apache.isis.schema.common.v1.ValueDto;
import org.apache.isis.schema.ixn.v1.ActionInvocationDto;
import org.apache.isis.schema.ixn.v1.InteractionDto;
import org.incode.eurocommercial.relatio.camel.processor.ProcessorAbstract;
import org.incode.eurocommercial.relatio.camel.processor.util.ParamUtil;
import org.incode.eurocommercial.relatio.canonical.profile.v1.ProfileDto;

import javax.xml.datatype.XMLGregorianCalendar;

public class FetchProfileFromSync extends ProcessorAbstract {
    @Override
    public void process(final Exchange exchange) {
        final ActionInvocationDto invocation = (ActionInvocationDto) exchange.getIn().getBody(InteractionDto.class).getExecution();

        String uuid = ParamUtil.getParamValue(invocation, "uuid", ValueDto::getString);
        String firstName = ParamUtil.getParamValue(invocation, "firstName", ValueDto::getString);
        String lastName = ParamUtil.getParamValue(invocation, "lastName", ValueDto::getString);
        XMLGregorianCalendar dateOfBirth = ParamUtil.getParamValue(invocation, "dateOfBirth", ValueDto::getLocalDate);
        XMLGregorianCalendar approximateDateOfBirth = ParamUtil.getParamValue(invocation, "approximateDateOfBirth", ValueDto::getLocalDate);
        String gender = ParamUtil.getParamValue(invocation, "gender", ValueDto::getString);
        String cellPhoneNumber = ParamUtil.getParamValue(invocation, "cellPhoneNumber", ValueDto::getString);
        String facebookAccount = ParamUtil.getParamValue(invocation, "facebookAccount", ValueDto::getString);
        Boolean privacyConsent = ParamUtil.getParamValue(invocation, "privacyConsent", ValueDto::isBoolean);
        Boolean marketingConsent = ParamUtil.getParamValue(invocation, "marketingConsent", ValueDto::isBoolean);
        String emailAccount = ParamUtil.getParamValue(invocation, "emailAccount", ValueDto::getString);


        ProfileDto profileDto = new ProfileDto();


        profileDto.setUuid(uuid);
        profileDto.setFirstName(firstName);
        profileDto.setLastName(lastName);
        profileDto.setDateOfBirth(dateOfBirth);
        profileDto.setApproximateDateOfBirth(approximateDateOfBirth);

        //TODO: profileDto.setGender(gender);

        profileDto.setCellPhoneNumber(cellPhoneNumber);
        profileDto.setFacebookAccount(facebookAccount);
        profileDto.setPrivacyConsent(privacyConsent);
        profileDto.setMarketingConsent(marketingConsent);
        profileDto.setEmailAccount(emailAccount);

        enrichmentService.enrichMessageWithLocalDto(exchange.getIn(), profileDto);
    }

    @BeanInject EnrichmentService enrichmentService;
}
