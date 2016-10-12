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

package org.cloudfoundry.reactor.tokenprovider;

import org.cloudfoundry.reactor.TokenProvider;
import org.immutables.value.Value;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * The One-time Passcode Password Grant implementation of {@link TokenProvider}
 */
@Value.Immutable
abstract class _OneTimePasscodeTokenProvider extends AbstractUaaTokenProvider {

    @Override
    protected UriComponentsBuilder getAccessTokenUri(UriComponentsBuilder builder) {
        return builder
            .queryParam("client_id", getClientId())
            .queryParam("client_secret", getClientSecret())
            .queryParam("grant_type", "password")
            .queryParam("passcode", getPasscode());
    }

    /**
     * The passcode
     */
    abstract String getPasscode();

}
