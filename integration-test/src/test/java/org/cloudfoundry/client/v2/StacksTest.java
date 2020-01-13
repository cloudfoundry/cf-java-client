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

package org.cloudfoundry.client.v2;

import org.cloudfoundry.AbstractIntegrationTest;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.stacks.CreateStackRequest;
import org.cloudfoundry.client.v2.stacks.CreateStackResponse;
import org.cloudfoundry.client.v2.stacks.DeleteStackRequest;
import org.cloudfoundry.client.v2.stacks.GetStackRequest;
import org.cloudfoundry.client.v2.stacks.GetStackResponse;
import org.cloudfoundry.client.v2.stacks.ListStacksRequest;
import org.cloudfoundry.client.v2.stacks.StackResource;
import org.cloudfoundry.util.JobUtils;
import org.cloudfoundry.util.PaginationUtils;
import org.cloudfoundry.util.ResourceUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;

public final class StacksTest extends AbstractIntegrationTest {

    @Autowired
    private CloudFoundryClient cloudFoundryClient;

    @Autowired
    private String stackName;

    @Test
    public void create() {
        String stackName = this.nameFactory.getStackName();

        this.cloudFoundryClient.stacks()
            .create(CreateStackRequest.builder()
                .description("Test stack description")
                .name(stackName)
                .build())
            .thenMany(requestListStacks(this.cloudFoundryClient, stackName))
            .map(response -> ResourceUtils.getEntity(response).getDescription())
            .as(StepVerifier::create)
            .expectNext("Test stack description")
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void delete() {
        String stackName = this.nameFactory.getStackName();

        createStackId(this.cloudFoundryClient, stackName)
            .delayUntil(stackId -> this.cloudFoundryClient.stacks()
                .delete(DeleteStackRequest.builder()
                    .async(false)
                    .stackId(stackId)
                    .build()))
            .flatMap(stackId -> requestGetStack(this.cloudFoundryClient, stackId))
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(ClientV2Exception.class).hasMessageMatching("CF-StackNotFound\\([0-9]+\\): The stack could not be found: .*"))
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void deleteAsync() {
        String stackName = this.nameFactory.getStackName();

        createStackId(this.cloudFoundryClient, stackName)
            .delayUntil(stackId -> this.cloudFoundryClient.stacks()
                .delete(DeleteStackRequest.builder()
                    .async(true)
                    .stackId(stackId)
                    .build())
                .flatMap(job -> JobUtils.waitForCompletion(this.cloudFoundryClient, Duration.ofMinutes(5), job)))
            .flatMap(stackId -> requestGetStack(this.cloudFoundryClient, stackId))
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(ClientV2Exception.class).hasMessageMatching("CF-StackNotFound\\([0-9]+\\): The stack could not be found: .*"))
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void get() {
        getStackId(this.cloudFoundryClient, this.stackName)
            .flatMap(stackId -> this.cloudFoundryClient.stacks()
                .get(GetStackRequest.builder()
                    .stackId(stackId)
                    .build()))
            .map(resource -> resource.getEntity().getName())
            .as(StepVerifier::create)
            .expectNext(this.stackName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void list() {
        getStackId(this.cloudFoundryClient, this.stackName)
            .flatMapMany(stackId -> PaginationUtils
                .requestClientV2Resources(page -> this.cloudFoundryClient.stacks()
                    .list(ListStacksRequest.builder()
                        .page(page)
                        .build()))
                .filter(resource -> ResourceUtils.getId(resource).equals(stackId)))
            .map(resource -> resource.getEntity().getName())
            .as(StepVerifier::create)
            .expectNext(this.stackName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listFilterByName() {
        PaginationUtils
            .requestClientV2Resources(page -> this.cloudFoundryClient.stacks()
                .list(ListStacksRequest.builder()
                    .name(this.stackName)
                    .page(page)
                    .build()))
            .map(resource -> resource.getEntity().getName())
            .as(StepVerifier::create)
            .expectNext(this.stackName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    private static Mono<String> createStackId(CloudFoundryClient cloudFoundryClient, String stackName) {
        return requestCreateStack(cloudFoundryClient, stackName)
            .map(ResourceUtils::getId);
    }

    private static Mono<String> getStackId(CloudFoundryClient cloudFoundryClient, String stackName) {
        return requestListStacks(cloudFoundryClient, stackName)
            .single()
            .map(ResourceUtils::getId);
    }

    private static Mono<CreateStackResponse> requestCreateStack(CloudFoundryClient cloudFoundryClient, String stackName) {
        return cloudFoundryClient.stacks()
            .create(CreateStackRequest.builder()
                .description("Test stack description")
                .name(stackName)
                .build());
    }

    private static Mono<GetStackResponse> requestGetStack(CloudFoundryClient cloudFoundryClient, String stackId) {
        return cloudFoundryClient.stacks()
            .get(GetStackRequest.builder()
                .stackId(stackId)
                .build());
    }

    private static Flux<StackResource> requestListStacks(CloudFoundryClient cloudFoundryClient, String stackName) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.stacks()
                .list(ListStacksRequest.builder()
                    .name(stackName)
                    .page(page)
                    .build()));
    }

}
