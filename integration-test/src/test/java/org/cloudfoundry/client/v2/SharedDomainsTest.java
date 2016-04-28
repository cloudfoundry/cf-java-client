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

package org.cloudfoundry.client.v2;

import org.cloudfoundry.AbstractIntegrationTest;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.shareddomains.CreateSharedDomainRequest;
import org.cloudfoundry.client.v2.shareddomains.CreateSharedDomainResponse;
import org.cloudfoundry.client.v2.shareddomains.ListSharedDomainsRequest;
import org.cloudfoundry.client.v2.shareddomains.SharedDomainResource;
import org.cloudfoundry.util.PaginationUtils;
import org.cloudfoundry.util.ResourceUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.tuple.Tuple2;

import java.util.Optional;

import static org.cloudfoundry.util.tuple.TupleUtils.consumer;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public final class SharedDomainsTest extends AbstractIntegrationTest {

    @Autowired
    private CloudFoundryClient cloudFoundryClient;

    @Test
    public void create() {
        String domainName = getDomainName();

        this.cloudFoundryClient.sharedDomains()
            .create(CreateSharedDomainRequest.builder()
                .name(domainName)
                .build())
            .map(ResourceUtils::getId)
            .then(sharedDomainId -> getSharedDomainResource(this.cloudFoundryClient, sharedDomainId))
            .subscribe(this.<SharedDomainResource>testSubscriber()
                .assertThat(sharedDomainResource -> assertEquals(domainName, ResourceUtils.getEntity(sharedDomainResource).getName())));
    }

    @Ignore("TODO: awaiting story https://www.pivotaltracker.com/story/show/101527356")
    @Test
    public void delete() {
        fail("TODO: finish story https://www.pivotaltracker.com/story/show/101527356");
    }

    @Ignore("TODO: awaiting story https://www.pivotaltracker.com/story/show/101527362")
    @Test
    public void get() {
        fail("TODO: finish story https://www.pivotaltracker.com/story/show/101527362");
    }

    @Test
    public void list() {
        String domainName = getDomainName();

        requestCreateSharedDomain(this.cloudFoundryClient, domainName)
            .map(ResourceUtils::getId)
            .then(sharedDomainId -> requestListSharedDomains(this.cloudFoundryClient, null)
                .filter(resource -> sharedDomainId.equals(ResourceUtils.getId(resource)))
                .single())
            .subscribe(this.<SharedDomainResource>testSubscriber()
                .assertThat(sharedDomainResource -> assertEquals(domainName, ResourceUtils.getEntity(sharedDomainResource).getName())));
    }

    @Test
    public void listFilterByName() {
        String domainName = getDomainName();

        requestCreateSharedDomain(this.cloudFoundryClient, domainName)
            .map(ResourceUtils::getId)
            .then(sharedDomainId -> Mono
                .when(
                    Mono.just(sharedDomainId),
                    requestListSharedDomains(this.cloudFoundryClient, domainName)
                        .single()
                ))
            .subscribe(this.<Tuple2<String, SharedDomainResource>>testSubscriber()
                .assertThat(consumer((id, resource) -> assertEquals(id, ResourceUtils.getId(resource)))));
    }

    // TODO: awaiting story https://www.pivotaltracker.com/story/show/101527362 to re-implement with get() 
    private static Mono<SharedDomainResource> getSharedDomainResource(CloudFoundryClient cloudFoundryClient, String sharedDomainId) {
        return PaginationUtils
            .requestResources(page -> cloudFoundryClient.sharedDomains()
                .list((ListSharedDomainsRequest.builder()
                    .page(page)
                    .build())))
            .filter(resource -> sharedDomainId.equals(ResourceUtils.getId(resource)))
            .single();
    }

    private static Mono<CreateSharedDomainResponse> requestCreateSharedDomain(CloudFoundryClient cloudFoundryClient, String sharedDomainName) {
        return cloudFoundryClient.sharedDomains()
            .create(CreateSharedDomainRequest.builder()
                .name(sharedDomainName)
                .build());
    }

    private static Flux<SharedDomainResource> requestListSharedDomains(CloudFoundryClient cloudFoundryClient, String sharedDomainName) {
        ListSharedDomainsRequest.ListSharedDomainsRequestBuilder requestBuilder = ListSharedDomainsRequest.builder();
        Optional.ofNullable(sharedDomainName).ifPresent(requestBuilder::name);

        return PaginationUtils.requestResources(page -> cloudFoundryClient.sharedDomains()
            .list(requestBuilder
                .page(page)
                .build()));
    }

}
