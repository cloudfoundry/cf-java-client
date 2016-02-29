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

import org.junit.Test;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Mono;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.RETURNS_SMART_NULLS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class AuthorizationConfiguratorTest {

    private final OAuth2TokenProvider tokenProvider = mock(OAuth2TokenProvider.class, RETURNS_SMART_NULLS);

    private final AuthorizationConfigurator authorizationConfigurator = new AuthorizationConfigurator(this.tokenProvider);

    @Test
    public void test() {
        when(this.tokenProvider.getToken()).thenReturn(Mono.just("test-token"));

        HttpHeaders headers = new HttpHeaders();

        this.authorizationConfigurator.beforeRequest(headers);

        assertEquals("Bearer test-token", headers.getFirst("Authorization"));
    }

}
