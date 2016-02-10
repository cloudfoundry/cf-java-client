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

package org.cloudfoundry.client.spring.util.network;

import org.junit.Test;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.RETURNS_SMART_NULLS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class OAuth2RestOperationsOAuth2TokenProviderTest {

    private final OAuth2RestOperations restOperations = mock(OAuth2RestOperations.class, RETURNS_SMART_NULLS);

    private final OAuth2RestOperationsOAuth2TokenProvider tokenProvider = new OAuth2RestOperationsOAuth2TokenProvider(this.restOperations);

    @Test
    public void test() {
        when(this.restOperations.getAccessToken()).thenReturn(new DefaultOAuth2AccessToken("test-token"));

        assertEquals("test-token", this.tokenProvider.getToken());
    }
}
