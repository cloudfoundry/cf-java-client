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

import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.uaa.BasicAuthorizationBuilder;
import org.cloudfoundry.reactor.util.JsonCodec;
import org.cloudfoundry.reactor.util.NetworkLogging;
import org.cloudfoundry.uaa.BasicAuthorized;
import org.immutables.value.Value;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * The OAuth Password Grant implementation of {@link TokenProvider}
 */
@Value.Immutable
abstract class _PasswordGrantTokenProvider implements BasicAuthorized, TokenProvider {

    /**
     * The client id.  Defaults to {@code cf}.
     */
    @Value.Default
    public String getClientId() {
        return "cf";
    }

    /**
     * The client secret Defaults to {@code ""}.
     */
    @Value.Default
    public String getClientSecret() {
        return "";
    }

    @Override
    @Value.Derived
    public final Mono<String> getToken(ConnectionContext connectionContext) {
        return connectionContext.getRoot("authorization_endpoint")
            .map(this::getTokenUri)
            .then(uri -> connectionContext.getHttpClient()
                .post(uri, outbound -> {
                    BasicAuthorizationBuilder.augment(outbound, this);
                    return outbound
                        .addHeader("Content-Length", "0")
                        .removeTransferEncodingChunked()
                        .sendHeaders();
                })
                .doOnSubscribe(NetworkLogging.get(uri))
                .compose(NetworkLogging.response(uri)))
            .then(i -> i.receive().aggregate().toInputStream())
            .map(JsonCodec.decode(connectionContext.getObjectMapper(), Map.class))
            .map(r -> r.get("access_token"))
            .cast(String.class)
            .cache();
    }

    /**
     * The password
     */
    abstract String getPassword();

    /**
     * The username
     */
    abstract String getUsername();

    private String getTokenUri(String root) {
        return UriComponentsBuilder.fromUriString(root)
            .pathSegment("oauth", "token")
            .queryParam("grant_type", "password")
            .queryParam("username", getUsername())
            .queryParam("password", getPassword())
            .build().toUriString();
    }

}
