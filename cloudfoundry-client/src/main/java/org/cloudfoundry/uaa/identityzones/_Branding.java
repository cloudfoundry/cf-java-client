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

package org.cloudfoundry.uaa.identityzones;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.cloudfoundry.AllowNulls;
import org.cloudfoundry.Nullable;
import org.immutables.value.Value;

import java.util.Map;

/**
 * The payload for the identity branding configuration
 */
@JsonDeserialize
@Value.Immutable
abstract class _Branding {

    /**
     * The banner configuration
     */
    @JsonProperty("banner")
    @Nullable
    abstract Banner getBanner();

    /**
     * This name is used on the UAA Pages and in account management related communication in UAA
     */
    @JsonProperty("companyName")
    @Nullable
    abstract String getCompanyName();

    /**
     * The consent configuration
     */
    @JsonProperty("consent")
    @Nullable
    abstract Consent getConsent();

    /**
     * This text appears on the footer of all UAA pages
     */
    @JsonProperty("footerLegalText")
    @Nullable
    abstract String getFooterLegalText();

    /**
     * These links appear on the footer of all UAA pages. You may choose to add multiple urls for things like Support, Terms of Service etc.
     */
    @AllowNulls
    @JsonProperty("footerLinks")
    @Nullable
    abstract Map<String, String> getFooterLinks();

    /**
     * This is a base64Url encoded PNG image which will be used as the logo on all UAA pages like Login, Sign Up etc.
     */
    @JsonProperty("productLogo")
    @Nullable
    abstract String getProductLogo();

    /**
     * This is a base64 encoded PNG image which will be used as the favicon for the UAA pages
     */
    @JsonProperty("squareLogo")
    @Nullable
    abstract String getSquareLogo();

}
