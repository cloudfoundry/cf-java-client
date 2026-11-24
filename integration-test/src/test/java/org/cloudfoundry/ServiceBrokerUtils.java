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
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v3.Relationship;
import org.cloudfoundry.client.v3.ToOneRelationship;
import org.cloudfoundry.client.v3.applications.DeleteApplicationRequest;
import org.cloudfoundry.client.v3.servicebrokers.BasicAuthentication;
import org.cloudfoundry.client.v3.servicebrokers.CreateServiceBrokerRequest;
import org.cloudfoundry.client.v3.servicebrokers.ListServiceBrokersRequest;
import org.cloudfoundry.client.v3.servicebrokers.ServiceBrokerRelationships;
import org.cloudfoundry.client.v3.servicebrokers.ServiceBrokerResource;
import org.cloudfoundry.client.v3.serviceofferings.ListServiceOfferingsRequest;
import org.cloudfoundry.client.v3.serviceofferings.ServiceOfferingResource;
import org.cloudfoundry.client.v3.serviceplans.ListServicePlansRequest;
import org.cloudfoundry.client.v3.serviceplans.ServicePlanResource;
import org.cloudfoundry.client.v3.serviceplans.UpdateServicePlanVisibilityRequest;
import org.cloudfoundry.client.v3.serviceplans.UpdateServicePlanVisibilityResponse;
import org.cloudfoundry.client.v3.serviceplans.Visibility;
import org.cloudfoundry.util.JobUtils;
import org.cloudfoundry.util.PaginationUtils;
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
                                        .flatMap(
                                                jobId ->
                                                        JobUtils.waitForCompletion(
                                                                        cloudFoundryClient,
                                                                        Duration.ofMinutes(5),
                                                                        jobId)
                                                                .then(
                                                                        requestGetServiceBrokerId(
                                                                                cloudFoundryClient,
                                                                                serviceBrokerName)))
                                        .delayUntil(
                                                brokerId ->
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
                                                brokerId ->
                                                        new ServiceBrokerMetadata(
                                                                applicationMetadata, brokerId)));
    }

    @SuppressWarnings("BlockingMethodInNonBlockingContext")
    public static Mono<ServiceBrokerUtils.ServiceBrokerMetadata> createServiceBrokerNonPublic(
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
                                        .flatMap(
                                                jobId ->
                                                        JobUtils.waitForCompletion(
                                                                        cloudFoundryClient,
                                                                        Duration.ofMinutes(5),
                                                                        jobId)
                                                                .then(
                                                                        requestGetServiceBrokerId(
                                                                                cloudFoundryClient,
                                                                                serviceBrokerName)))
                                        .map(
                                                brokerId ->
                                                        new ServiceBrokerMetadata(
                                                                applicationMetadata, brokerId)));
    }

    public static Mono<Void> deleteServiceBroker(
            CloudFoundryClient cloudFoundryClient, String applicationId) {
        return cloudFoundryClient
                .applicationsV3()
                .delete(DeleteApplicationRequest.builder().applicationId(applicationId).build())
                .then();
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

        Map<String, String> env = new HashMap<>();
        env.put("SERVICE_NAME", serviceName);
        env.put("PLAN_NAME", planName);
        env.put(
                "JAVA_OPTS",
                "--add-opens java.base/java.lang=ALL-UNNAMED"
                        + " --add-opens java.base/java.io=ALL-UNNAMED");

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
                .map(ServiceOfferingResource::getId);
    }

    private static Mono<String> getServicePlanId(
            CloudFoundryClient cloudFoundryClient, String serviceId) {
        return requestListServicePlans(cloudFoundryClient, serviceId)
                .single()
                .map(ServicePlanResource::getId);
    }

    private static Mono<UpdateServicePlanVisibilityResponse> makeServicePlanPubliclyVisible(
            CloudFoundryClient cloudFoundryClient, String serviceName, Boolean spaceScoped) {
        if (spaceScoped) {
            return Mono.empty();
        }

        return getServiceId(cloudFoundryClient, serviceName)
                .flatMap(serviceId -> getServicePlanId(cloudFoundryClient, serviceId))
                .flatMap(planId -> requestUpdateServicePlan(cloudFoundryClient, planId, true));
    }

    private static Mono<String> requestCreateServiceBroker(
            CloudFoundryClient cloudFoundryClient,
            ApplicationUtils.ApplicationMetadata applicationMetadata,
            String serviceBrokerName,
            Boolean spaceScoped) {
        return cloudFoundryClient
                .serviceBrokersV3()
                .create(
                        CreateServiceBrokerRequest.builder()
                                .authentication(
                                        BasicAuthentication.builder()
                                                .username("test-authentication-username")
                                                .password("test-authentication-password")
                                                .build())
                                .name(serviceBrokerName)
                                .relationships(
                                        spaceScoped
                                                ? ServiceBrokerRelationships.builder()
                                                        .space(
                                                                ToOneRelationship.builder()
                                                                        .data(
                                                                                Relationship.builder()
                                                                                        .id(
                                                                                                applicationMetadata
                                                                                                        .spaceId)
                                                                                        .build())
                                                                        .build())
                                                        .build()
                                                : null)
                                .url(applicationMetadata.uri)
                                .build());
    }

    private static Mono<String> requestGetServiceBrokerId(
            CloudFoundryClient cloudFoundryClient, String serviceBrokerName) {
        return PaginationUtils.requestClientV3Resources(
                        page ->
                                cloudFoundryClient
                                        .serviceBrokersV3()
                                        .list(
                                                ListServiceBrokersRequest.builder()
                                                        .name(serviceBrokerName)
                                                        .page(page)
                                                        .build()))
                .single()
                .map(ServiceBrokerResource::getId);
    }

    private static Flux<ServicePlanResource> requestListServicePlans(
            CloudFoundryClient cloudFoundryClient, String serviceOfferingId) {
        return PaginationUtils.requestClientV3Resources(
                page ->
                        cloudFoundryClient
                                .servicePlansV3()
                                .list(
                                        ListServicePlansRequest.builder()
                                                .serviceOfferingId(serviceOfferingId)
                                                .page(page)
                                                .build()));
    }

    private static Flux<ServiceOfferingResource> requestListServices(
            CloudFoundryClient cloudFoundryClient, String serviceName) {
        return PaginationUtils.requestClientV3Resources(
                page ->
                        cloudFoundryClient
                                .serviceOfferingsV3()
                                .list(
                                        ListServiceOfferingsRequest.builder()
                                                .name(serviceName)
                                                .page(page)
                                                .build()));
    }

    private static Mono<UpdateServicePlanVisibilityResponse> requestUpdateServicePlan(
            CloudFoundryClient cloudFoundryClient, String planId, Boolean visibility) {
        return cloudFoundryClient
                .servicePlansV3()
                .updateVisibility(
                        UpdateServicePlanVisibilityRequest.builder()
                                .servicePlanId(planId)
                                .type(Visibility.PUBLIC)
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
