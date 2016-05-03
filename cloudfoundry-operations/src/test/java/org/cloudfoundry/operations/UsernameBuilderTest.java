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

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.Base64Codec;
import org.cloudfoundry.uaa.UaaClient;
import org.cloudfoundry.uaa.accesstokens.GetTokenKeyRequest;
import org.cloudfoundry.uaa.accesstokens.GetTokenKeyResponse;
import org.junit.Test;
import reactor.core.publisher.Mono;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public final class UsernameBuilderTest extends AbstractOperationsTest {

    private static final Base64Codec BASE64 = new Base64Codec();

    private final String publicKey;

    private final String token;

    public UsernameBuilderTest() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(1024);

        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        this.publicKey = getPublicKey(keyPair.getPublic());
        this.token = getToken(keyPair.getPrivate());
    }

    @Test
    public void test() {
        when(this.cloudFoundryClient.getAccessToken()).thenReturn(Mono.just(this.token));
        requestTokenKey(this.uaaClient, this.publicKey);

        String username = new UsernameBuilder()
            .cloudFoundryClient(this.cloudFoundryClient)
            .uaaClient(this.uaaClient)
            .build()
            .get();

        assertEquals("test-username", username);
    }

    private static String getPublicKey(PublicKey publicKey) {
        return String.format("-----BEGIN PUBLIC KEY-----\n%s\n-----END PUBLIC KEY-----", BASE64.encode(publicKey.getEncoded()));
    }

    private static String getToken(PrivateKey privateKey) {
        return Jwts
            .builder()
            .signWith(SignatureAlgorithm.RS256, privateKey)
            .claim("user_name", "test-username")
            .compact();
    }

    private static void requestTokenKey(UaaClient uaaClient, String key) {
        when(uaaClient.accessTokens()
            .getTokenKey(GetTokenKeyRequest.builder()
                .build()))
            .thenReturn(Mono
                .just(GetTokenKeyResponse.builder()
                    .value(key)
                    .build()));
    }

}
