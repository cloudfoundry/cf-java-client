/*
 * Copyright 2013-2021 the original author or authors.
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

package org.cloudfoundry.client.v3;

import org.cloudfoundry.AbstractIntegrationTest;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v3.stacks.CreateStackRequest;
import org.cloudfoundry.client.v3.stacks.CreateStackResponse;
import org.cloudfoundry.client.v3.stacks.DeleteStackRequest;
import org.cloudfoundry.client.v3.stacks.GetStackRequest;
import org.cloudfoundry.client.v3.stacks.GetStackResponse;
import org.cloudfoundry.client.v3.stacks.ListStacksRequest;
import org.cloudfoundry.client.v3.stacks.StackResource;
import org.cloudfoundry.client.v3.stacks.Stack;
import org.cloudfoundry.util.PaginationUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

public final class StacksTest extends AbstractIntegrationTest {

    @Autowired
    private CloudFoundryClient cloudFoundryClient;

    @Autowired
    private String stackName;

    @Test
    public void create() {
        String stackName = this.nameFactory.getStackName();

        this.cloudFoundryClient.stacksV3()
            .create(CreateStackRequest.builder()
                .description("Test stack description")
                .name(stackName)
                .build())
            .thenMany(requestListStacks(this.cloudFoundryClient, stackName))
            .map(Stack::getDescription)
            .as(StepVerifier::create)
            .expectNext("Test stack description")
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void delete() {
        String stackName = this.nameFactory.getStackName();

        createStackId(this.cloudFoundryClient, stackName)
            .delayUntil(stackId -> this.cloudFoundryClient.stacksV3()
                .delete(DeleteStackRequest.builder()
                    .stackId(stackId)
                    .build()))
            .flatMap(stackId -> requestGetStack(this.cloudFoundryClient, stackId))
            .as(StepVerifier::create)
            .consumeErrorWith(t -> assertThat(t).isInstanceOf(ClientV3Exception.class).hasMessageMatching("CF-ResourceNotFound\\([0-9]+\\): Stack not found.*"))
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void get() {
        getStackId(this.cloudFoundryClient, this.stackName)
            .flatMap(stackId -> this.cloudFoundryClient.stacksV3()
                .get(GetStackRequest.builder()
                    .stackId(stackId)
                    .build()))
            .map(Stack::getName)
            .as(StepVerifier::create)
            .expectNext(this.stackName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void list() {
        getStackId(this.cloudFoundryClient, this.stackName)
            .flatMapMany(stackId -> PaginationUtils
                .requestClientV3Resources(page -> this.cloudFoundryClient.stacksV3()
                    .list(ListStacksRequest.builder()
                        .page(page)
                        .build()))
                .filter(stack -> stack.getId().equals(stackId)))
            .map(Stack::getName)
            .as(StepVerifier::create)
            .expectNext(this.stackName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    @Test
    public void listFilterByName() {
        PaginationUtils
            .requestClientV3Resources(page -> this.cloudFoundryClient.stacksV3()
                .list(ListStacksRequest.builder()
                    .name(this.stackName)
                    .page(page)
                    .build()))
            .map(Stack::getName)
            .as(StepVerifier::create)
            .expectNext(this.stackName)
            .expectComplete()
            .verify(Duration.ofMinutes(5));
    }

    private static Mono<String> createStackId(CloudFoundryClient cloudFoundryClient, String stackName) {
        return requestCreateStack(cloudFoundryClient, stackName)
            .map(Stack::getId);
    }

    private static Mono<String> getStackId(CloudFoundryClient cloudFoundryClient, String stackName) {
        return requestListStacks(cloudFoundryClient, stackName)
            .single()
            .map(Stack::getId);
    }

    private static Mono<CreateStackResponse> requestCreateStack(CloudFoundryClient cloudFoundryClient, String stackName) {
        return cloudFoundryClient.stacksV3()
            .create(CreateStackRequest.builder()
                .description("Test stack description")
                .name(stackName)
                .build());
    }

    private static Mono<GetStackResponse> requestGetStack(CloudFoundryClient cloudFoundryClient, String stackId) {
        return cloudFoundryClient.stacksV3()
            .get(GetStackRequest.builder()
                .stackId(stackId)
                .build());
    }

    private static Flux<StackResource> requestListStacks(CloudFoundryClient cloudFoundryClient, String stackName) {
        return PaginationUtils
            .requestClientV3Resources(page -> cloudFoundryClient.stacksV3()
                .list(ListStacksRequest.builder()
                    .name(stackName)
                    .page(page)
                    .build()));
    }

}
