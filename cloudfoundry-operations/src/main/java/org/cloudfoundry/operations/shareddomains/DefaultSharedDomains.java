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
package org.cloudfoundry.operations.shareddomains;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.shareddomains.CreateSharedDomainRequest;
import org.cloudfoundry.client.v2.shareddomains.CreateSharedDomainResponse;
import org.cloudfoundry.util.ValidationUtils;
import reactor.core.publisher.Mono;

import static org.cloudfoundry.util.tuple.TupleUtils.function;

public class DefaultSharedDomains implements SharedDomains {

    private final CloudFoundryClient cloudFoundryClient;

    public DefaultSharedDomains(CloudFoundryClient cloudFoundryClient) {
        this.cloudFoundryClient = cloudFoundryClient;
    }

    @Override
    public Mono<Void> create(CreateSharedDomainRequest request) {
        return ValidationUtils.validate(request)
                .then(request1 -> requestCreateSharedDomain(this.cloudFoundryClient,
                        request1.getName(), request1.getRouterGroupId()))
                .after();
    }

    private static Mono<CreateSharedDomainResponse> requestCreateSharedDomain(
            CloudFoundryClient cloudFoundryClient, String sharedDomain, String routerGroupId) {
        return cloudFoundryClient.sharedDomains()
                .create(CreateSharedDomainRequest.builder()
                        .name(sharedDomain)
                        .routerGroupId(routerGroupId)
                        .build());
    }
}
