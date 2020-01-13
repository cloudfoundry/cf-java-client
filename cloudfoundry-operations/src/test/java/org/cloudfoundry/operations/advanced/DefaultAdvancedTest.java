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

package org.cloudfoundry.operations.advanced;

import org.cloudfoundry.operations.AbstractOperationsTest;
import org.cloudfoundry.uaa.UaaClient;
import org.cloudfoundry.uaa.authorizations.AuthorizeByAuthorizationCodeGrantApiRequest;
import org.junit.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.mockito.Mockito.when;

public final class DefaultAdvancedTest extends AbstractOperationsTest {

    private final DefaultAdvanced advanced = new DefaultAdvanced(Mono.just(this.uaaClient));

    @Test
    public void sshCode() {
        requestAuthorizeByAuthorizationCodeGrantApi(this.uaaClient);

        this.advanced
            .sshCode()
            .as(StepVerifier::create)
            .expectNext("test-code")
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    private static void requestAuthorizeByAuthorizationCodeGrantApi(UaaClient uaaClient) {
        when(uaaClient.authorizations()
            .authorizationCodeGrantApi(AuthorizeByAuthorizationCodeGrantApiRequest.builder()
                .clientId("ssh-proxy")
                .build()))
            .thenReturn(Mono.just("test-code"));
    }

}
