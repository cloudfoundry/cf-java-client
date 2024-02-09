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

package org.cloudfoundry;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.applications.DeleteApplicationRequest;
import org.cloudfoundry.client.v2.servicebrokers.CreateServiceBrokerRequest;
import org.cloudfoundry.client.v2.servicebrokers.CreateServiceBrokerResponse;
import org.cloudfoundry.client.v2.serviceplans.ListServicePlansRequest;
import org.cloudfoundry.client.v2.serviceplans.ServicePlanResource;
import org.cloudfoundry.client.v2.serviceplans.UpdateServicePlanRequest;
import org.cloudfoundry.client.v2.serviceplans.UpdateServicePlanResponse;
import org.cloudfoundry.client.v2.services.ListServicesRequest;
import org.cloudfoundry.client.v2.services.ServiceResource;
import org.cloudfoundry.util.PaginationUtils;
import org.cloudfoundry.util.ResourceUtils;
import org.springframework.core.io.ClassPathResource;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public final class ServiceBrokerUtils {

    @SuppressWarnings("BlockingMethodInNonBlockingContext")
    public static Mono<ServiceBrokerUtils.ServiceBrokerMetadata> createServiceBroker(
            CloudFoundryClient cloudFoundryClient,
            NameFactory nameFactory,
            String planName,
            String serviceBrokerName,
            String serviceName,
            String spaceId,
            Boolean spaceScoped) {
        Path application;
        try {
            application = new ClassPathResource("test-service-broker.jar").getFile().toPath();
        } catch (IOException e) {
            throw Exceptions.propagate(e);
        }

        return pushServiceBrokerApplication(
                        cloudFoundryClient,
                        application,
                        nameFactory,
                        planName,
                        serviceName,
                        spaceId)
                .flatMap(
                        applicationMetadata ->
                                requestCreateServiceBroker(
                                                cloudFoundryClient,
                                                applicationMetadata,
                                                serviceBrokerName,
                                                spaceScoped)
                                        .delayUntil(
                                                response ->
                                                        Mono.zip(
                                                                makeServicePlanPubliclyVisible(
                                                                        cloudFoundryClient,
                                                                        serviceName,
                                                                        spaceScoped),
                                                                makeServicePlanPubliclyVisible(
                                                                        cloudFoundryClient,
                                                                        serviceName + "-shareable",
                                                                        spaceScoped)))
                                        .map(
                                                response ->
                                                        new ServiceBrokerMetadata(
                                                                applicationMetadata,
                                                                ResourceUtils.getId(response))));
    }

    public static Mono<Void> deleteServiceBroker(
            CloudFoundryClient cloudFoundryClient, String applicationId) {
        return cloudFoundryClient
                .applicationsV2()
                .delete(DeleteApplicationRequest.builder().applicationId(applicationId).build());
    }

    public static Mono<ApplicationUtils.ApplicationMetadata> pushServiceBrokerApplication(
            CloudFoundryClient cloudFoundryClient,
            Path application,
            NameFactory nameFactory,
            String planName,
            String serviceName,
            String spaceId) {
        String applicationName = nameFactory.getApplicationName();
        String hostName = nameFactory.getHostName();

        Map<String, Object> env = new HashMap<>();
        env.put("SERVICE_NAME", serviceName);
        env.put("PLAN_NAME", planName);

        return ApplicationUtils.pushApplication(
                cloudFoundryClient,
                application,
                applicationName,
                Collections.emptyList(),
                env,
                hostName,
                spaceId);
    }

    private static Mono<String> getServiceId(
            CloudFoundryClient cloudFoundryClient, String serviceName) {
        return requestListServices(cloudFoundryClient, serviceName)
                .single()
                .map(ResourceUtils::getId);
    }

    private static Mono<String> getServicePlanId(
            CloudFoundryClient cloudFoundryClient, String serviceId) {
        return requestListServicePlans(cloudFoundryClient, serviceId)
                .single()
                .map(ResourceUtils::getId);
    }

    private static Mono<UpdateServicePlanResponse> makeServicePlanPubliclyVisible(
            CloudFoundryClient cloudFoundryClient, String serviceName, Boolean spaceScoped) {
        if (spaceScoped) {
            return Mono.empty();
        }

        return getServiceId(cloudFoundryClient, serviceName)
                .flatMap(serviceId -> getServicePlanId(cloudFoundryClient, serviceId))
                .flatMap(planId -> requestUpdateServicePlan(cloudFoundryClient, planId, true));
    }

    private static Mono<CreateServiceBrokerResponse> requestCreateServiceBroker(
            CloudFoundryClient cloudFoundryClient,
            ApplicationUtils.ApplicationMetadata applicationMetadata,
            String serviceBrokerName,
            Boolean spaceScoped) {
        return cloudFoundryClient
                .serviceBrokers()
                .create(
                        CreateServiceBrokerRequest.builder()
                                .authenticationPassword("test-authentication-password")
                                .authenticationUsername("test-authentication-username")
                                .brokerUrl(applicationMetadata.uri)
                                .name(serviceBrokerName)
                                .spaceId(spaceScoped ? applicationMetadata.spaceId : null)
                                .build());
    }

    private static Flux<ServicePlanResource> requestListServicePlans(
            CloudFoundryClient cloudFoundryClient, String serviceId) {
        return PaginationUtils.requestClientV2Resources(
                page ->
                        cloudFoundryClient
                                .servicePlans()
                                .list(
                                        ListServicePlansRequest.builder()
                                                .serviceId(serviceId)
                                                .page(page)
                                                .build()));
    }

    private static Flux<ServiceResource> requestListServices(
            CloudFoundryClient cloudFoundryClient, String serviceName) {
        return PaginationUtils.requestClientV2Resources(
                page ->
                        cloudFoundryClient
                                .services()
                                .list(ListServicesRequest.builder().label(serviceName).build()));
    }

    private static Mono<UpdateServicePlanResponse> requestUpdateServicePlan(
            CloudFoundryClient cloudFoundryClient, String planId, Boolean visibility) {
        return cloudFoundryClient
                .servicePlans()
                .update(
                        UpdateServicePlanRequest.builder()
                                .servicePlanId(planId)
                                .publiclyVisible(visibility)
                                .build());
    }

    public static final class ServiceBrokerMetadata {

        public final ApplicationUtils.ApplicationMetadata applicationMetadata;

        public final String serviceBrokerId;

        private ServiceBrokerMetadata(
                ApplicationUtils.ApplicationMetadata applicationMetadata, String serviceBrokerId) {
            this.applicationMetadata = applicationMetadata;
            this.serviceBrokerId = serviceBrokerId;
        }
    }
}
