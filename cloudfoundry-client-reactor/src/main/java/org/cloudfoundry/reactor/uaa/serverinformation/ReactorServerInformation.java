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

package org.cloudfoundry.reactor.uaa.serverinformation;

import io.netty.util.AsciiString;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.uaa.AbstractUaaOperations;
import org.cloudfoundry.uaa.identityzones.IdentityZones;
import org.cloudfoundry.uaa.serverinformation.AutoLoginRequest;
import org.cloudfoundry.uaa.serverinformation.GetAutoLoginAuthenticationCodeRequest;
import org.cloudfoundry.uaa.serverinformation.GetAutoLoginAuthenticationCodeResponse;
import org.cloudfoundry.uaa.serverinformation.GetInfoRequest;
import org.cloudfoundry.uaa.serverinformation.GetInfoResponse;
import org.cloudfoundry.uaa.serverinformation.ServerInformation;
import reactor.core.publisher.Mono;

import java.util.Base64;
import java.util.Map;

import static io.netty.handler.codec.http.HttpHeaderNames.AUTHORIZATION;

/**
 * The Reactor-based implementation of {@link IdentityZones}
 */
public final class ReactorServerInformation extends AbstractUaaOperations implements ServerInformation {

    private static final AsciiString BASIC_PREAMBLE = new AsciiString("Basic ");

    /**
     * Creates an instance
     *
     * @param connectionContext the {@link ConnectionContext} to use when communicating with the server
     * @param root              the root URI of the server. Typically something like {@code https://uaa.run.pivotal.io}.
     * @param tokenProvider     the {@link TokenProvider} to use when communicating with the server
     * @param requestTags       map with custom http headers which will be added to web request
     */
    public ReactorServerInformation(ConnectionContext connectionContext, Mono<String> root, TokenProvider tokenProvider, Map<String, String> requestTags) {
        super(connectionContext, root, tokenProvider, requestTags);
    }

    @Override
    public Mono<Void> autoLogin(AutoLoginRequest request) {
        return get(request, Void.class, builder -> builder.pathSegment("autologin"))
            .checkpoint();
    }

    @Override
    public Mono<GetAutoLoginAuthenticationCodeResponse> getAuthenticationCode(GetAutoLoginAuthenticationCodeRequest request) {
        return post(request, GetAutoLoginAuthenticationCodeResponse.class, builder -> builder.pathSegment("autologin"),
            outbound -> {
            },
            outbound -> {
                String encoded = Base64.getEncoder().encodeToString(new AsciiString(request.getClientId()).concat(":").concat(request.getClientSecret()).toByteArray());
                outbound.set(AUTHORIZATION, BASIC_PREAMBLE + encoded);

                return Mono.just(outbound);
            })
            .checkpoint();
    }

    @Override
    public Mono<GetInfoResponse> getInfo(GetInfoRequest request) {
        return get(request, GetInfoResponse.class, builder -> builder.pathSegment("info"))
            .checkpoint();
    }
}
