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

package org.cloudfoundry.operations;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.Base64Codec;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.uaa.UaaClient;
import org.cloudfoundry.uaa.accesstokens.GetTokenKeyRequest;
import org.cloudfoundry.uaa.accesstokens.GetTokenKeyResponse;
import reactor.core.publisher.Mono;

import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import static org.cloudfoundry.util.tuple.TupleUtils.function;

@Accessors(chain = true, fluent = true)
@Setter
@ToString
final class UsernameBuilder {

    private static final Base64Codec BASE64 = new Base64Codec();

    private static final String BEGIN = "-----BEGIN PUBLIC KEY-----";

    private static final String END = "-----END PUBLIC KEY-----";

    private CloudFoundryClient cloudFoundryClient;

    private UaaClient uaaClient;

    Mono<String> build() {
        return Mono
            .when(
                getSigningKey(this.uaaClient),
                this.cloudFoundryClient.getAccessToken()
            )
            .map(function(UsernameBuilder::getUsername));
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

    private static Mono<PublicKey> getSigningKey(UaaClient uaaClient) {
        return requestTokenKey(uaaClient)
            .map(GetTokenKeyResponse::getValue)
            .map(pem -> pem.replace(BEGIN, "").replace(END, "").trim())
            .map(UsernameBuilder::generateKey);
    }

    private static String getUsername(PublicKey publicKey, String token) {
        Jws<Claims> jws = Jwts.parser().setSigningKey(publicKey).parseClaimsJws(token);
        return jws.getBody().get("user_name", String.class);
    }

    private static Mono<GetTokenKeyResponse> requestTokenKey(UaaClient uaaClient) {
        return uaaClient.accessTokens()
            .getTokenKey(GetTokenKeyRequest.builder()
                .build());
    }

}
