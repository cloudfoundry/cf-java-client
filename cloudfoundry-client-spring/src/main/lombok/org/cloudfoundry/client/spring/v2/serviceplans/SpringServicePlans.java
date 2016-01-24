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

package org.cloudfoundry.client.spring.v2.serviceplans;


import lombok.ToString;
import org.cloudfoundry.client.spring.util.AbstractSpringOperations;
import org.cloudfoundry.client.spring.util.QueryBuilder;
import org.cloudfoundry.client.spring.v2.FilterBuilder;
import org.cloudfoundry.client.v2.serviceplans.ListServicePlanServiceInstancesRequest;
import org.cloudfoundry.client.v2.serviceplans.ListServicePlanServiceInstancesResponse;
import org.cloudfoundry.client.v2.serviceplans.ListServicePlansRequest;
import org.cloudfoundry.client.v2.serviceplans.ListServicePlansResponse;
import org.cloudfoundry.client.v2.serviceplans.ServicePlans;
import org.springframework.web.client.RestOperations;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.core.publisher.ProcessorGroup;
import reactor.fn.Consumer;

/**
 * The Spring-based implementation of {@link ServicePlans}
 */
@ToString(callSuper = true)
public final class SpringServicePlans extends AbstractSpringOperations implements ServicePlans {

    /**
     * Creates an instance
     *
     * @param restOperations the {@link RestOperations } to use to communicate with the server
     * @param root           the root URI of the server.  Typically something like {@code https://api.run.pivotal.io}.
     * @param processorGroup The group to use when making requests
     */
    public SpringServicePlans(RestOperations restOperations, java.net.URI root, ProcessorGroup<?> processorGroup) {
        super(restOperations, root, processorGroup);
    }

    @Override
    public Mono<ListServicePlansResponse> list(final ListServicePlansRequest request) {
        return get(request, ListServicePlansResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "service_plans");
                FilterBuilder.augment(builder, request);
                QueryBuilder.augment(builder, request);
            }

        });
    }

    @Override
    public Mono<ListServicePlanServiceInstancesResponse> listServiceInstances(final ListServicePlanServiceInstancesRequest request) {
        return get(request, ListServicePlanServiceInstancesResponse.class, new Consumer<UriComponentsBuilder>() {

            @Override
            public void accept(UriComponentsBuilder builder) {
                builder.pathSegment("v2", "service_plans", request.getServicePlanId(), "service_instances");
                FilterBuilder.augment(builder, request);
                QueryBuilder.augment(builder, request);
            }

        });
    }

}
