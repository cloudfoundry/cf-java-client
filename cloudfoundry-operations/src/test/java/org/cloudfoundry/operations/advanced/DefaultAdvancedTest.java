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

package org.cloudfoundry.operations.advanced;

import org.cloudfoundry.operations.AbstractOperationsApiTest;
import org.cloudfoundry.uaa.UaaClient;
import org.cloudfoundry.uaa.authorizations.AuthorizeByAuthorizationCodeGrantApiRequest;
import org.junit.Before;
import reactor.core.publisher.Mono;
import reactor.test.subscriber.ScriptedSubscriber;

import static org.mockito.Mockito.when;

public final class DefaultAdvancedTest {

    private static void requestAuthorizeByAuthorizationCodeGrantApi(UaaClient uaaClient) {
        when(uaaClient.authorizations()
            .authorizationCodeGrantApi(AuthorizeByAuthorizationCodeGrantApiRequest.builder()
                .clientId("ssh-proxy")
                .build()))
            .thenReturn(Mono.just("test-code"));
    }

    public static final class SshCode extends AbstractOperationsApiTest<String> {

        private final DefaultAdvanced advanced = new DefaultAdvanced(Mono.just(this.uaaClient));

        @Before
        public void setUp() throws Exception {
            requestAuthorizeByAuthorizationCodeGrantApi(this.uaaClient);

        }

        @Override
        protected ScriptedSubscriber<String> expectations() {
            return ScriptedSubscriber.<String>create()
                .expectValue("test-code")
                .expectComplete();
        }

        @Override
        protected Mono<String> invoke() {
            return this.advanced
                .sshCode();
        }

    }

}
