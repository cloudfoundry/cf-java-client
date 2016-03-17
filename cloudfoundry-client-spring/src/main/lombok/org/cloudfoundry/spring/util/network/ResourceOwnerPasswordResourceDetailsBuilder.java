/*
 * Copyright 2013-2016 the original author or authors.
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

package org.cloudfoundry.spring.util.network;

import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;

import java.net.URI;
import java.util.Optional;

@Accessors(chain = true, fluent = true)
@Setter
@ToString
final class ResourceOwnerPasswordResourceDetailsBuilder {

    private URI accessTokenUri;

    private String clientId;

    private String clientSecret;

    private String password;

    private String username;

    public ResourceOwnerPasswordResourceDetails build() {
        ResourceOwnerPasswordResourceDetails details = new ResourceOwnerPasswordResourceDetails();
        details.setAccessTokenUri(this.accessTokenUri.toASCIIString());
        details.setClientId(Optional.ofNullable(this.clientId).orElse("cf"));
        details.setClientSecret(Optional.ofNullable(this.clientSecret).orElse(""));
        details.setUsername(this.username);
        details.setPassword(this.password);

        return details;
    }

}
