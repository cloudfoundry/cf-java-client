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

package org.cloudfoundry.operations.stacks;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.stacks.ListStacksRequest;
import org.cloudfoundry.client.v2.stacks.ListStacksResponse;
import org.cloudfoundry.client.v2.stacks.StackResource;
import org.cloudfoundry.utils.ExceptionUtils;
import org.cloudfoundry.utils.PaginationUtils;
import org.cloudfoundry.utils.ResourceUtils;
import org.cloudfoundry.utils.ValidationUtils;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import reactor.fn.Function;
import reactor.rx.Stream;

public final class DefaultStacks implements Stacks {

    private final CloudFoundryClient cloudFoundryClient;

    public DefaultStacks(CloudFoundryClient cloudFoundryClient) {
        this.cloudFoundryClient = cloudFoundryClient;
    }

    @Override
    public Mono<Stack> get(GetStackRequest request) {
        return ValidationUtils
            .validate(request)
            .then(new Function<GetStackRequest, Mono<StackResource>>() {

                @Override
                public Mono<StackResource> apply(GetStackRequest getStackRequest) {
                    return requestStackByName(DefaultStacks.this.cloudFoundryClient, getStackRequest.getName());
                }

            })
            .map(new Function<StackResource, Stack>() {

                @Override
                public Stack apply(StackResource resource) {
                    return toStack(resource);
                }

            });
    }

    @Override
    public Publisher<Stack> list() {
        return requestStacks(this.cloudFoundryClient)
            .map(new Function<StackResource, Stack>() {

                @Override
                public Stack apply(StackResource resource) {
                    return toStack(resource);
                }

            });
    }

    private static Mono<StackResource> requestStackByName(final CloudFoundryClient cloudFoundryClient, final String stackName) {
        return PaginationUtils
            .requestResources(new Function<Integer, Mono<ListStacksResponse>>() {

                @Override
                public Mono<ListStacksResponse> apply(Integer page) {
                    return cloudFoundryClient.stacks().list(
                        ListStacksRequest.builder()
                            .name(stackName)
                            .page(page)
                            .build());
                }

            })
            .single()
            .otherwise(ExceptionUtils.<StackResource>convert("Stack %s does not exist", stackName));
    }

    private static Stream<StackResource> requestStacks(final CloudFoundryClient cloudFoundryClient) {
        return PaginationUtils
            .requestResources(new Function<Integer, Mono<ListStacksResponse>>() {

                @Override
                public Mono<ListStacksResponse> apply(Integer page) {
                    return cloudFoundryClient.stacks().list(
                        ListStacksRequest.builder()
                            .page(page)
                            .build());
                }

            });
    }

    private Stack toStack(StackResource stackResource) {
        return Stack.builder()
            .description(ResourceUtils.getEntity(stackResource).getDescription())
            .name(ResourceUtils.getEntity(stackResource).getName())
            .build();
    }

}
