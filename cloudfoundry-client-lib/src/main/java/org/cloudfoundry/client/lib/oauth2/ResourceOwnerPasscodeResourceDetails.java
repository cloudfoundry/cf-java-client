package org.cloudfoundry.client.lib.oauth2;

import org.springframework.security.oauth2.client.resource.BaseOAuth2ProtectedResourceDetails;

/**
 * Resource details for passcode auth.
 *
 * @see ResourceOwnerPasscodeAccessTokenProvider
 *
 * @author Matthias Winzeler <matthias.winzeler@gmail.com>
 */
public class ResourceOwnerPasscodeResourceDetails extends BaseOAuth2ProtectedResourceDetails {
    private String passcode;

    public ResourceOwnerPasscodeResourceDetails() {
        setGrantType("password");
    }

    public String getPasscode() {
        return passcode;
    }

    public void setPasscode(String passcode) {
        this.passcode = passcode;
    }
}
