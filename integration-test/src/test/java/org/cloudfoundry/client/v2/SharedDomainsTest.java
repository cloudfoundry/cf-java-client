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

package org.cloudfoundry.client.v2;

import org.cloudfoundry.AbstractIntegrationTest;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.shareddomains.CreateSharedDomainRequest;
import org.cloudfoundry.client.v2.shareddomains.CreateSharedDomainResponse;
import org.cloudfoundry.client.v2.shareddomains.DeleteSharedDomainRequest;
import org.cloudfoundry.client.v2.shareddomains.GetSharedDomainRequest;
import org.cloudfoundry.client.v2.shareddomains.GetSharedDomainResponse;
import org.cloudfoundry.client.v2.shareddomains.ListSharedDomainsRequest;
import org.cloudfoundry.client.v2.shareddomains.SharedDomainEntity;
import org.cloudfoundry.client.v2.shareddomains.SharedDomainResource;
import org.cloudfoundry.util.JobUtils;
import org.cloudfoundry.util.PaginationUtils;
import org.cloudfoundry.util.ResourceUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

public final class SharedDomainsTest extends AbstractIntegrationTest {

    @Autowired
    private CloudFoundryClient cloudFoundryClient;

    @Test
    public void create() throws TimeoutException, InterruptedException {
        String domainName = this.nameFactory.getDomainName();

        this.cloudFoundryClient.sharedDomains()
            .create(CreateSharedDomainRequest.builder()
                .name(domainName)
                .build())
            .map(ResourceUtils::getId)
            .flatMap(sharedDomainId -> getSharedDomainResource(this.cloudFoundryClient, sharedDomainId))
            .map(resource -> ResourceUtils.getEntity(resource).getName())
            .as(StepVerifier::create)
            .expectNext(domainName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void deleteAsyncFalse() throws TimeoutException, InterruptedException {
        String domainName = this.nameFactory.getDomainName();

        getSharedDomainId(this.cloudFoundryClient, domainName)
            .flatMap(sharedDomainId -> this.cloudFoundryClient.sharedDomains()
                .delete(DeleteSharedDomainRequest.builder()
                    .async(false)
                    .sharedDomainId(sharedDomainId)
                    .build()))
            .then(requestListSharedDomains(this.cloudFoundryClient, domainName)
                .singleOrEmpty())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void deleteAsyncTrue() throws TimeoutException, InterruptedException {
        String domainName = this.nameFactory.getDomainName();

        getSharedDomainId(this.cloudFoundryClient, domainName)
            .flatMap(sharedDomainId -> this.cloudFoundryClient.sharedDomains()
                .delete(DeleteSharedDomainRequest.builder()
                    .async(true)
                    .sharedDomainId(sharedDomainId)
                    .build())
                .flatMap(job -> JobUtils.waitForCompletion(this.cloudFoundryClient, Duration.ofMinutes(5), job)))
            .then(requestListSharedDomains(this.cloudFoundryClient, domainName)
                .singleOrEmpty())
            .as(StepVerifier::create)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void get() throws TimeoutException, InterruptedException {
        String domainName = this.nameFactory.getDomainName();

        getSharedDomainId(this.cloudFoundryClient, domainName)
            .flatMap(sharedDomainId -> this.cloudFoundryClient.sharedDomains()
                .get(GetSharedDomainRequest.builder()
                    .sharedDomainId(sharedDomainId)
                    .build()))
            .map(ResourceUtils::getEntity)
            .map(SharedDomainEntity::getName)
            .as(StepVerifier::create)
            .expectNext(domainName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void list() throws TimeoutException, InterruptedException {
        String domainName = this.nameFactory.getDomainName();

        getSharedDomainId(this.cloudFoundryClient, domainName)
            .flatMap(sharedDomainId -> requestListSharedDomains(this.cloudFoundryClient, null)
                .filter(resource -> sharedDomainId.equals(ResourceUtils.getId(resource)))
                .single())
            .map(resource -> ResourceUtils.getEntity(resource).getName())
            .as(StepVerifier::create)
            .expectNext(domainName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listFilterByName() throws TimeoutException, InterruptedException {
        String domainName = this.nameFactory.getDomainName();

        getSharedDomainId(this.cloudFoundryClient, domainName)
            .flatMap(sharedDomainId -> Mono.when(
                Mono.just(sharedDomainId),
                requestListSharedDomains(this.cloudFoundryClient, domainName)
                    .single()
                    .map(ResourceUtils::getId)
            ))
            .as(StepVerifier::create)
            .consumeNextWith(tupleEquality())
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    private static Mono<String> getSharedDomainId(CloudFoundryClient cloudFoundryClient, String domainName) {
        return requestCreateSharedDomain(cloudFoundryClient, domainName)
            .map(ResourceUtils::getId);
    }

    private static Mono<GetSharedDomainResponse> getSharedDomainResource(CloudFoundryClient cloudFoundryClient, String sharedDomainId) {
        return cloudFoundryClient.sharedDomains()
            .get(GetSharedDomainRequest.builder()
                .sharedDomainId(sharedDomainId)
                .build());
    }

    private static Mono<CreateSharedDomainResponse> requestCreateSharedDomain(CloudFoundryClient cloudFoundryClient, String sharedDomainName) {
        return cloudFoundryClient.sharedDomains()
            .create(CreateSharedDomainRequest.builder()
                .name(sharedDomainName)
                .build());
    }

    private static Flux<SharedDomainResource> requestListSharedDomains(CloudFoundryClient cloudFoundryClient, String sharedDomainName) {
        ListSharedDomainsRequest.Builder requestBuilder = ListSharedDomainsRequest.builder();
        Optional.ofNullable(sharedDomainName).ifPresent(requestBuilder::name);

        return PaginationUtils.requestClientV2Resources(page -> cloudFoundryClient.sharedDomains()
            .list(requestBuilder
                .page(page)
                .build()));
    }

}
