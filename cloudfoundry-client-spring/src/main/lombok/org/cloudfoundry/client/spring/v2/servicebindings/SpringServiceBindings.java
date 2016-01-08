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

package org.cloudfoundry.client.spring.v2.servicebindings;

import lombok.ToString;
import org.cloudfoundry.client.spring.util.AbstractSpringOperations;
import org.cloudfoundry.client.v2.servicebindings.CreateServiceBindingRequest;
import org.cloudfoundry.client.v2.servicebindings.CreateServiceBindingResponse;
import org.cloudfoundry.client.v2.servicebindings.DeleteServiceBindingRequest;
import org.cloudfoundry.client.v2.servicebindings.GetServiceBindingRequest;
import org.cloudfoundry.client.v2.servicebindings.GetServiceBindingResponse;
import org.cloudfoundry.client.v2.servicebindings.ServiceBindings;
import org.springframework.web.client.RestOperations;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.Mono;
import reactor.core.processor.ProcessorGroup;
import reactor.fn.Consumer;

import java.net.URI;

/**
 * The Spring-based implementation of {@link ServiceBindings}
 */
@ToString(callSuper = true)
public final class SpringServiceBindings extends AbstractSpringOperations implements ServiceBindings {

    /**
     * Creates an instance
     *
     * @param restOperations the {@link RestOperations} to use to communicate with the server
     * @param root           the root URI of the server.  Typically something like {@code https://api.run.pivotal.io}.
     * @param processorGroup The group to use when making requests
     */
    public SpringServiceBindings(RestOperations restOperations, URI root, ProcessorGroup<?> processorGroup) {
        super(restOperations, root, processorGroup);
    }

    @Override
    public Mono<CreateServiceBindingResponse> create(final CreateServiceBindingRequest request) {
        return post(request, CreateServiceBindingResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "service_bindings");
            }

        });
    }

    @Override
    public Mono<Void> delete(final DeleteServiceBindingRequest request) {
        return delete(request, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "service_bindings", request.getId());
            }

        });
    }

    @Override
    public Mono<GetServiceBindingResponse> get(final GetServiceBindingRequest request) {
        return get(request, GetServiceBindingResponse.class, new Consumer<UriComponentsBuilder>() {
            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "service_bindings", request.getId());
            }
        });
    }

}
