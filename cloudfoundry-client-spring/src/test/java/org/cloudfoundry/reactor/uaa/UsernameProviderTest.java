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

package org.cloudfoundry.reactor.uaa;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.Base64Codec;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.uaa.tokens.GetTokenKeyRequest;
import org.cloudfoundry.uaa.tokens.GetTokenKeyResponse;
import org.cloudfoundry.uaa.tokens.Tokens;
import org.cloudfoundry.util.test.TestSubscriber;
import org.junit.Test;
import reactor.core.publisher.Mono;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.Duration;

import static org.cloudfoundry.util.test.TestObjects.fill;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class UsernameProviderTest {

    private static final Base64Codec BASE64 = new Base64Codec();

    private final ConnectionContext connectionContext = mock(ConnectionContext.class);

    private final String publicKey;

    private final String token;

    private final TokenProvider tokenProvider = mock(TokenProvider.class);

    private final Tokens tokens = mock(Tokens.class);

    public UsernameProviderTest() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(1024);

        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        this.publicKey = getPublicKey(keyPair.getPublic());
        this.token = getToken(keyPair.getPrivate());
    }

    @Test
    public void test() throws InterruptedException {
        requestTokenKey(this.tokens, this.publicKey);
        when(this.tokenProvider.getToken(this.connectionContext)).thenReturn(Mono.just(this.token));

        TestSubscriber<String> testSubscriber = new TestSubscriber<>();

        new UsernameProvider(this.connectionContext, this.tokenProvider, this.tokens)
            .get()
            .subscribe(testSubscriber
                .assertEquals("test-username"));

        testSubscriber.verify(Duration.ofSeconds(1));
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

    private static void requestTokenKey(Tokens tokens, String key) {
        when(tokens
            .getKey(GetTokenKeyRequest.builder()
                .build()))
            .thenReturn(Mono
                .just(fill(GetTokenKeyResponse.builder())
                    .value(key)
                    .build()));
    }

}
