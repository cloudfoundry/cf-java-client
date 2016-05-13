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

package org.cloudfoundry.operations.serviceadmin;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.servicebrokers.ListServiceBrokersRequest;
import org.cloudfoundry.client.v2.servicebrokers.ServiceBrokerEntity;
import org.cloudfoundry.client.v2.servicebrokers.ServiceBrokerResource;
import org.cloudfoundry.util.PaginationUtils;
import org.cloudfoundry.util.ResourceUtils;
import reactor.core.publisher.Flux;

public final class DefaultServiceAdmin implements ServiceAdmin {

    private final CloudFoundryClient cloudFoundryClient;

    public DefaultServiceAdmin(CloudFoundryClient cloudFoundryClient) {
        this.cloudFoundryClient = cloudFoundryClient;
    }

    @Override
    public Flux<ServiceBroker> listServiceBrokers() {
        return requestServiceBrokers(this.cloudFoundryClient)
            .map(this::toServiceBroker);
    }

    private static Flux<ServiceBrokerResource> requestServiceBrokers(CloudFoundryClient cloudFoundryClient) {
        return PaginationUtils
            .requestResources(page -> cloudFoundryClient.serviceBrokers().list(
                ListServiceBrokersRequest.builder()
                    .page(page)
                    .build()));
    }

    private ServiceBroker toServiceBroker(ServiceBrokerResource serviceBrokerResource) {
        ServiceBrokerEntity entity = ResourceUtils.getEntity(serviceBrokerResource);
        return ServiceBroker.builder()
            .name(entity.getName())
            .url(entity.getBrokerUrl())
            .build();
    }

}
