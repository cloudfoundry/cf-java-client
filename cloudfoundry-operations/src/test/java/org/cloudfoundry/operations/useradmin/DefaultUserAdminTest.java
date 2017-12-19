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

package org.cloudfoundry.operations.useradmin;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.operations.AbstractOperationsTest;
import org.cloudfoundry.uaa.UaaClient;
import org.cloudfoundry.uaa.users.Email;
import org.cloudfoundry.uaa.users.Meta;
import org.cloudfoundry.uaa.users.Name;
import org.junit.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.mockito.Mockito.when;

public final class DefaultUserAdminTest extends AbstractOperationsTest {

    private final DefaultUserAdmin userAdmin = new DefaultUserAdmin(Mono.just(this.cloudFoundryClient), Mono.just(this.uaaClient));

    @Test
    public void createUserWithPassword() {
        requestCreateUaaUser(this.uaaClient);
        requestCreateUser(this.cloudFoundryClient);

        this.userAdmin
            .create(CreateUserRequest.builder()
                .username("test-username")
                .password("test-password")
                .build())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    private static void requestCreateUaaUser(UaaClient uaaClient) {
        when(uaaClient.users()
            .create(org.cloudfoundry.uaa.users.CreateUserRequest.builder()
                .email(Email.builder()
                    .primary(true)
                    .value("test-username")
                    .build())
                .name(Name.builder()
                    .familyName("test-username")
                    .givenName("test-username")
                    .build())
                .password("test-password")
                .userName("test-username")
                .build()))
            .thenReturn(Mono
                .just(org.cloudfoundry.uaa.users.CreateUserResponse.builder()
                    .id("test-uaa-id")
                    .active(true)
                    .meta(Meta.builder()
                        .created("")
                        .lastModified("")
                        .version(1)
                        .build())
                    .name(Name.builder().build())
                    .origin("")
                    .passwordLastModified("")
                    .userName("test-username")
                    .verified(false)
                    .zoneId("")
                    .build()));
    }

    private static void requestCreateUser(CloudFoundryClient cloudFoundryClient) {
        when(cloudFoundryClient.users()
            .create(org.cloudfoundry.client.v2.users.CreateUserRequest.builder()
                .uaaId("test-uaa-id")
                .build()))
            .thenReturn(Mono
                .just(org.cloudfoundry.client.v2.users.CreateUserResponse.builder()
                    .build()));
    }

}
