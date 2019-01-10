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
package org.incode.eurocommercial.relatio.camel.processor.mail;

import com.ecwid.maleorang.MailchimpClient;
import com.ecwid.maleorang.MailchimpException;
import com.ecwid.maleorang.MailchimpObject;
import com.ecwid.maleorang.method.v3_0.lists.members.EditMemberMethod;
import com.ecwid.maleorang.method.v3_0.lists.members.MemberInfo;
import com.google.common.base.Strings;
import lombok.Setter;
import org.incode.eurocommercial.relatio.canonical.profile.v1.ProfileDto;

import java.io.IOException;

public class MailService {

    @Setter
    private String apiKey;

    private MailchimpClient client;

    public void init() {
        if (!Strings.isNullOrEmpty(apiKey)) {
            client = new MailchimpClient(apiKey);
        }
    }

    public void tearDown() {
        try {
            client.close();
        } catch (IOException ignored) {
        }
    }


    private MemberInfo createOrUpdate(ProfileDto profileDto, String listId, String status) {
        if (client == null) {
            return null;
        }

        EditMemberMethod.CreateOrUpdate method = new EditMemberMethod.CreateOrUpdate(listId, profileDto.getEmailAccount());


        method.status = status;
        method.merge_fields = new MailchimpObject();
        method.merge_fields.mapping.put("FNAME", profileDto.getFirstName());
        method.merge_fields.mapping.put("LNAME", profileDto.getLastName());
        method.merge_fields.mapping.put("dateOfBirth", profileDto.getDateOfBirth().toString());
        method.merge_fields.mapping.put("approximateDateOfBirth", profileDto.getApproximateDateOfBirth().toString());
//        method.merge_fields.mapping.put("gender", profileDto.getGender().value());
        method.merge_fields.mapping.put("cellPhoneNumber", profileDto.getCellPhoneNumber());
        method.merge_fields.mapping.put("faceBookAccount", profileDto.getFacebookAccount());
        method.merge_fields.mapping.put("privacyConsent", booleanToStatus(profileDto.isPrivacyConsent()));
        method.merge_fields.mapping.put("marketingConsent", status);

        try {
            return client.execute(method);
        } catch (IOException | MailchimpException e) {
            e.printStackTrace();
            return null;
        }
    }


    public MemberInfo propogateProfile(ProfileDto profileDto){
        if(profileDto.isMarketingConsent()) {
            return createOrUpdate(profileDto, "af6fb1a850", "subscribed");
        }else {
            return createOrUpdate(profileDto, "af6fb1a850", "unsubscribed");
        }
    }

    private String booleanToStatus(boolean bool){
        return bool ? "true" : "false";
    }
}
