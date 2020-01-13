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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.cloudfoundry.Nullable;

import java.util.List;

@JsonIgnoreProperties(value = "additionalConfiguration")
abstract class AbstractIdentityProviderConfiguration implements IdentityProviderConfiguration {

    /**
     * List of email domains associated with the provider for the purpose of associating users to the correct origin upon invitation. If empty list, no invitations are accepted. Wildcards supported.
     */
    @JsonProperty("emailDomain")
    @Nullable
    abstract List<String> getEmailDomains();

    /**
     * Human readable name/description of this provider
     */
    @JsonProperty("providerDescription")
    @Nullable
    abstract String getProviderDescription();

}
