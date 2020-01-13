/*
 * Copyright 2013-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cloudfoundry.uaa.identityproviders;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.cloudfoundry.Nullable;
import org.immutables.value.Value;

/**
 * The payload for the ldap identity provider configuration
 */
@JsonDeserialize
@Value.Immutable
abstract class _LdapConfiguration extends AbstractExternalIdentityProviderConfiguration {

    /**
     * Determines whether or not shadow users must be created before login by an administrator.
     */
    @JsonProperty("addShadowUserOnLogin")
    @Nullable
    abstract Boolean getAddShadowUserOnLogin();

    /**
     * The auto add group flag
     */
    @JsonProperty("autoAddGroups")
    @Nullable
    abstract Boolean getAutoAddGroups();

    /**
     * The URL to the ldap server, must start with ldap:// or ldaps://
     */
    @JsonProperty("baseUrl")
    @Nullable
    abstract String getBaseUrl();

    /**
     * If you specified BindUserDN, then specify the corresponding password to be used for binding here.
     */
    @JsonProperty("bindPassword")
    @Nullable
    abstract String getBindPassword();

    /**
     * The distinguished name the gatekeeper uses to bind to the LDAP server.
     */
    @JsonProperty("bindUserDn")
    @Nullable
    abstract String getBindUserDistinguishedName();

    /**
     * The group role attribute
     */
    @JsonProperty("groupRoleAttribute")
    @Nullable
    abstract String getGroupRoleAttribute();

    /**
     * The group search base
     */
    @JsonProperty("groupSearchBase")
    @Nullable
    abstract String getGroupSearchBase();

    /**
     * The maximum group search depth limit
     */
    @JsonProperty("maxGroupSearchDepth")
    @Nullable
    abstract Integer getGroupSearchDepthLimit();

    /**
     * The group search filter
     */
    @JsonProperty("groupSearchFilter")
    @Nullable
    abstract String getGroupSearchFilter();

    /**
     * The group search subtree
     */
    @JsonProperty("groupSearchSubTree")
    @Nullable
    abstract Boolean getGroupSearchSubTree();

    /**
     * The group ignore partial search result flag
     */
    @JsonProperty("groupsIgnorePartialResults")
    @Nullable
    abstract Boolean getGroupsIgnorePartialResults();

    /**
     * The file to be used for group integration.
     */
    @JsonProperty("ldapGroupFile")
    @Nullable
    abstract LdapGroupFile getLdapGroupFile();

    /**
     * The file to be used for configuring the LDAP authentication.
     */
    @JsonProperty("ldapProfileFile")
    @Nullable
    abstract LdapProfileFile getLdapProfileFile();

    /**
     *
     */
    @JsonProperty("localPasswordCompare")
    @Nullable
    abstract Boolean getLocalPasswordCompare();

    /**
     * The name of the LDAP attribute that contains the user’s email address
     */
    @JsonProperty("mailAttributeName")
    @Nullable
    abstract String getMailAttributeName();

    /**
     * Defines an email pattern containing a {0} to generate an email address for an LDAP user during authentication
     */
    @JsonProperty("mailSubstitute")
    @Nullable
    abstract String getMailSubstitute();

    /**
     * Set to true if you wish to override an LDAP user email address with a generated one
     */
    @JsonProperty("mailSubstituteOverridesLdap")
    @Nullable
    abstract Boolean getMailSubstituteOverridesLdap();

    /**
     * The password attribute name
     */
    @JsonProperty("passwordAttributeName")
    @Nullable
    abstract String getPasswordAttributeName();

    /**
     * The password encoder
     */
    @JsonProperty("passwordEncoder")
    @Nullable
    abstract String getPasswordEncoder();

    /**
     * Configures the UAA LDAP referral behavior. The following values are possible: - follow → Referrals are followed - ignore → Referrals are ignored and the partial result is returned - throw → An
     * error is thrown and the authentication is aborted
     */
    @JsonProperty("referral")
    @Nullable
    abstract String getReferral();

    /**
     * Skips validation of the LDAP cert if set to true.
     */
    @JsonProperty("skipSSLVerification")
    @Nullable
    abstract Boolean getSkipSSLVerification();

    /**
     * The StartTLS options
     */
    @JsonProperty("tlsConfiguration")
    @Nullable
    abstract TlsConfiguration getTlsConfiguration();

    /**
     * The user distinguished name pattern
     */
    @JsonProperty("userDNPattern")
    @Nullable
    abstract String getUserDistinguishedNamePattern();

    /**
     * The user distinguished name pattern delimiter
     */
    @JsonProperty("userDNPatternDelimiter")
    @Nullable
    abstract String getUserDistinguishedNamePatternDelimiter();

    /**
     * The user search base
     */
    @JsonProperty("userSearchBase")
    @Nullable
    abstract String getUserSearchBase();

    /**
     * The user search filter
     */
    @JsonProperty("userSearchFilter")
    @Nullable
    abstract String getUserSearchFilter();

}
