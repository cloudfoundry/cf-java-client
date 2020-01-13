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

package org.cloudfoundry.uaa.authorizations;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import java.util.List;
import java.util.Locale;

/**
 * The response from the Get Open ID Provider Configuration request
 */
@JsonDeserialize
@Value.Immutable
abstract class _GetOpenIdProviderConfigurationResponse {

    /**
     * The authorization endpoint
     */
    @JsonProperty("authorization_endpoint")
    abstract String getAuthorizationEndpoint();

    /**
     * Whether the claims parameter is supported
     */
    @JsonProperty("claims_parameter_supported")
    abstract Boolean getClaimsParameterSupported();

    /**
     * The issuer
     */
    @JsonProperty("issuer")
    abstract String getIssuer();

    /**
     * The Java Web Key Set endpoint
     */
    @JsonProperty("jwks_uri")
    abstract String getJavaWebKeySetEndpoint();

    /**
     * The location of human-readable documentation
     */
    @JsonProperty("service_documentation")
    abstract String getServiceDocumentation();

    /**
     * The claim types supported
     */
    @JsonProperty("claim_types_supported")
    abstract List<String> getSupportedClaimTypes();

    /**
     * The claims supported
     */
    @JsonProperty("claims_supported")
    abstract List<String> getSupportedClaims();

    /**
     * The id token encryption algorithms supported
     */
    @JsonProperty("id_token_encryption_alg_values_supported")
    abstract List<String> getSupportedIdTokenEncryptionAlgorithms();

    /**
     * The id token signing algorithms supported
     */
    @JsonProperty("id_token_signing_alg_values_supported")
    abstract List<String> getSupportedIdTokenSigningAlgorithms();

    /**
     * The response types supported
     */
    @JsonProperty("response_types_supported")
    abstract List<String> getSupportedResponseTypes();

    /**
     * The scopes supported
     */
    @JsonProperty("scopes_supported")
    abstract List<String> getSupportedScopes();

    /**
     * The subject types supported
     */
    @JsonProperty("subject_types_supported")
    abstract List<String> getSupportedSubjectTypes();

    /**
     * The token endpoint authorization methods supported
     */
    @JsonProperty("token_endpoint_auth_methods_supported")
    abstract List<String> getSupportedTokenEndpointAuthorizationMethods();

    /**
     * The token endpoint authorization signing algorithms supported
     */
    @JsonProperty("token_endpoint_auth_signing_alg_values_supported")
    abstract List<String> getSupportedTokenEndpointAuthorizationSigningAlgorithms();

    /**
     * The UI locales supported
     */
    @JsonProperty("ui_locales_supported")
    abstract List<Locale> getSupportedUiLocales();

    /**
     * The token endpoint
     */
    @JsonProperty("token_endpoint")
    abstract String getTokenEndpoint();

    /**
     * The user info endpoint
     */
    @JsonProperty("userinfo_endpoint")
    abstract String getUserInfoEndpoint();

}