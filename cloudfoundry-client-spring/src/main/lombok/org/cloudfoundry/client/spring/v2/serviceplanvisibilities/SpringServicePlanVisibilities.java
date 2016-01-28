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

package org.cloudfoundry.client.spring.v2.serviceplanvisibilities;


import org.cloudfoundry.client.spring.util.AbstractSpringOperations;
import org.cloudfoundry.client.spring.util.QueryBuilder;
import org.cloudfoundry.client.v2.serviceplanvisibilities.CreateServicePlanVisibilityRequest;
import org.cloudfoundry.client.v2.serviceplanvisibilities.CreateServicePlanVisibilityResponse;
import org.cloudfoundry.client.v2.serviceplanvisibilities.DeleteServicePlanVisibilityRequest;
import org.cloudfoundry.client.v2.serviceplanvisibilities.ServicePlanVisibilities;
import org.springframework.web.client.RestOperations;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.core.publisher.ProcessorGroup;
import reactor.fn.Consumer;

public final class SpringServicePlanVisibilities extends AbstractSpringOperations implements ServicePlanVisibilities {

    /**
     * Creates an instance
     *
     * @param restOperations the {@link RestOperations } to use to communicate with the server
     * @param root           the root URI of the server.  Typically something like {@code https://api.run.pivotal.io}.
     * @param processorGroup The group to use when making requests
     */
    public SpringServicePlanVisibilities(RestOperations restOperations, java.net.URI root, ProcessorGroup processorGroup) {
        super(restOperations, root, processorGroup);
    }

    @Override
    public Mono<CreateServicePlanVisibilityResponse> create(final CreateServicePlanVisibilityRequest request) {
        return post(request, CreateServicePlanVisibilityResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "service_plan_visibilities");
            }

        });
    }

    @Override
    public Mono<Void> delete(final DeleteServicePlanVisibilityRequest request) {
        return delete(request, Void.class, new Consumer<UriComponentsBuilder>() {
            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "service_plan_visibilities", request.getServicePlanVisibilityId());
                QueryBuilder.augment(builder, request);
            }
        });
    }

}
