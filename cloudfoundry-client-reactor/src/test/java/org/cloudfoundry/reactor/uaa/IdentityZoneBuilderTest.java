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

package org.cloudfoundry.reactor.uaa;

import io.netty.handler.codec.http.HttpHeaders;
import org.cloudfoundry.uaa.IdentityZoned;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

public final class IdentityZoneBuilderTest {

    private final HttpHeaders outbound = mock(HttpHeaders.class);

    @Test
    public void augment() {
        IdentityZoneBuilder.augment(this.outbound, new StubIdentityZoned());

        verify(this.outbound).set("X-Identity-Zone-Id", "test-identity-zone-id");
        verify(this.outbound).set("X-Identity-Zone-Subdomain", "test-identity-zone-subdomain");
    }

    @Test
    public void augmentNotIdentityZoned() {
        IdentityZoneBuilder.augment(this.outbound, new Object());

        verifyNoInteractions(this.outbound);
    }

    private static final class StubIdentityZoned implements IdentityZoned {

        @Override
        public String getIdentityZoneId() {
            return "test-identity-zone-id";
        }

        @Override
        public String getIdentityZoneSubdomain() {
            return "test-identity-zone-subdomain";
        }
    }

}
