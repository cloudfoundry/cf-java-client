/*
 * Copyright 2013-2017 the original author or authors.
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

package org.cloudfoundry.reactor.uaa;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.Base64Codec;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.uaa.tokens.GetTokenKeyRequest;
import org.cloudfoundry.uaa.tokens.GetTokenKeyResponse;
import org.cloudfoundry.uaa.tokens.Tokens;
import reactor.core.publisher.Mono;

import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Optional;

import static org.cloudfoundry.util.tuple.TupleUtils.function;

final class UsernameProvider {

    private static final Base64Codec BASE64 = new Base64Codec();

    private static final String BEGIN = "-----BEGIN PUBLIC KEY-----";

    private static final String END = "-----END PUBLIC KEY-----";

    private final ConnectionContext connectionContext;

    private final TokenProvider tokenProvider;

    private final Tokens tokens;

    UsernameProvider(ConnectionContext connectionContext, TokenProvider tokenProvider, Tokens tokens) {
        this.connectionContext = connectionContext;
        this.tokenProvider = tokenProvider;
        this.tokens = tokens;
    }

    Mono<String> get() {
        return Mono
            .when(
                getSigningKey(this.tokens),
                this.tokenProvider.getToken(this.connectionContext))
            .map(function(UsernameProvider::getUsername));
    }

    private static PublicKey generateKey(String pem) {
        try {
            return KeyFactory
                .getInstance("RSA")
                .generatePublic(new X509EncodedKeySpec(BASE64.decode(pem)));
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    private static Mono<PublicKey> getSigningKey(Tokens tokens) {
        return requestTokenKey(tokens)
            .map(GetTokenKeyResponse::getValue)
            .map(pem -> pem.replace(BEGIN, "").replace(END, "").trim())
            .map(UsernameProvider::generateKey);
    }

    private static String getUsername(PublicKey publicKey, String token) {
        Jws<Claims> jws = Jwts.parser().setSigningKey(publicKey).parseClaimsJws(token);
        return Optional
            .ofNullable(jws.getBody().get("user_name", String.class))
            .orElseThrow(() -> new IllegalStateException("Unable to retrieve username from token"));
    }

    private static Mono<GetTokenKeyResponse> requestTokenKey(Tokens tokens) {
        return tokens
            .getKey(GetTokenKeyRequest.builder()
                .build());
    }

}
