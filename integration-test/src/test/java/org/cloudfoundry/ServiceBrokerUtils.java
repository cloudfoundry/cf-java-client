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

package org.cloudfoundry;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.applications.ApplicationInstanceInfo;
import org.cloudfoundry.client.v2.applications.ApplicationInstancesRequest;
import org.cloudfoundry.client.v2.applications.ApplicationInstancesResponse;
import org.cloudfoundry.client.v2.applications.AssociateApplicationRouteRequest;
import org.cloudfoundry.client.v2.applications.AssociateApplicationRouteResponse;
import org.cloudfoundry.client.v2.applications.CreateApplicationRequest;
import org.cloudfoundry.client.v2.applications.CreateApplicationResponse;
import org.cloudfoundry.client.v2.applications.DeleteApplicationRequest;
import org.cloudfoundry.client.v2.applications.GetApplicationRequest;
import org.cloudfoundry.client.v2.applications.GetApplicationResponse;
import org.cloudfoundry.client.v2.applications.UpdateApplicationRequest;
import org.cloudfoundry.client.v2.applications.UpdateApplicationResponse;
import org.cloudfoundry.client.v2.applications.UploadApplicationRequest;
import org.cloudfoundry.client.v2.applications.UploadApplicationResponse;
import org.cloudfoundry.client.v2.routes.CreateRouteRequest;
import org.cloudfoundry.client.v2.routes.CreateRouteResponse;
import org.cloudfoundry.client.v2.servicebrokers.CreateServiceBrokerRequest;
import org.cloudfoundry.client.v2.servicebrokers.CreateServiceBrokerResponse;
import org.cloudfoundry.client.v2.serviceplans.ListServicePlansRequest;
import org.cloudfoundry.client.v2.serviceplans.ServicePlanResource;
import org.cloudfoundry.client.v2.serviceplans.UpdateServicePlanRequest;
import org.cloudfoundry.client.v2.serviceplans.UpdateServicePlanResponse;
import org.cloudfoundry.client.v2.services.ListServicesRequest;
import org.cloudfoundry.client.v2.services.ServiceResource;
import org.cloudfoundry.client.v2.shareddomains.ListSharedDomainsRequest;
import org.cloudfoundry.client.v2.shareddomains.SharedDomainResource;
import org.cloudfoundry.util.JobUtils;
import org.cloudfoundry.util.PaginationUtils;
import org.cloudfoundry.util.ResourceUtils;
import org.springframework.core.io.ClassPathResource;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;

import static org.cloudfoundry.util.DelayUtils.exponentialBackOff;
import static org.cloudfoundry.util.tuple.TupleUtils.function;

public final class ServiceBrokerUtils {

    public static Mono<ServiceBrokerUtils.ServiceBrokerMetadata> createServiceBroker(CloudFoundryClient cloudFoundryClient, NameFactory nameFactory, String planName, String serviceBrokerName,
                                                                                     String serviceName, String spaceId, Boolean spaceScoped) {
        Path application;
        try {
            application = new ClassPathResource("test-service-broker.jar").getFile().toPath();
        } catch (IOException e) {
            throw Exceptions.propagate(e);
        }

        return getSharedDomain(cloudFoundryClient)
            .flatMap(domain -> pushServiceBrokerApplication(cloudFoundryClient, application, domain, nameFactory, planName, serviceName, spaceId))
            .flatMap(applicationMetadata -> requestCreateServiceBroker(cloudFoundryClient, applicationMetadata, serviceBrokerName, spaceScoped)
                .delayUntil(response -> makeServicePlanPubliclyVisible(cloudFoundryClient, serviceName, spaceScoped))
                .map(response -> new ServiceBrokerMetadata(applicationMetadata, ResourceUtils.getId(response))));
    }

    public static Mono<Void> deleteServiceBroker(CloudFoundryClient cloudFoundryClient, String applicationId) {
        return cloudFoundryClient.applicationsV2()
            .delete(DeleteApplicationRequest.builder()
                .applicationId(applicationId)
                .build());
    }

    public static Mono<ApplicationMetadata> pushServiceBrokerApplication(CloudFoundryClient cloudFoundryClient, Path application, SharedDomainResource domain, NameFactory nameFactory,
                                                                         String planName, String serviceName, String spaceId) {
        String applicationName = nameFactory.getApplicationName();
        String hostName = nameFactory.getHostName();

        return Mono
            .when(
                createApplicationId(cloudFoundryClient, spaceId, applicationName),
                createRouteId(cloudFoundryClient, ResourceUtils.getId(domain), spaceId, hostName)
            )
            .flatMap(function((applicationId, routeId) -> requestAssociateApplicationRoute(cloudFoundryClient, applicationId, routeId)
                .then(Mono.just(applicationId))))
            .flatMap(applicationId -> createRunningServiceBrokerApplication(cloudFoundryClient, application, applicationId, planName, serviceName)
                .map(ignore -> new ApplicationMetadata(applicationId, spaceId, String.format("https://%s.%s", hostName, ResourceUtils.getEntity(domain).getName()))));
    }

    private static Mono<String> createApplicationId(CloudFoundryClient cloudFoundryClient, String spaceId, String applicationName) {
        return requestCreateApplication(cloudFoundryClient, spaceId, applicationName)
            .map(ResourceUtils::getId);
    }

    private static Mono<String> createRouteId(CloudFoundryClient cloudFoundryClient, String domainId, String spaceId, String hostName) {
        return requestCreateRoute(cloudFoundryClient, domainId, spaceId, hostName)
            .map(ResourceUtils::getId);
    }

    private static Mono<String> createRunningServiceBrokerApplication(CloudFoundryClient cloudFoundryClient, Path application, String applicationId, String planName, String serviceName) {
        return requestUploadApplication(cloudFoundryClient, applicationId, application)
            .flatMap(job -> JobUtils.waitForCompletion(cloudFoundryClient, Duration.ofMinutes(5), job))
            .then(requestUpdateApplication(cloudFoundryClient, applicationId, planName, serviceName, "STARTED"))
            .then(getApplicationPackageState(cloudFoundryClient, applicationId)
                .filter(state -> "STAGED".equals(state) || "FAILED".equals(state))
                .repeatWhenEmpty(exponentialBackOff(Duration.ofSeconds(1), Duration.ofSeconds(15), Duration.ofMinutes(5))))
            .then(getApplicationInstanceState(cloudFoundryClient, applicationId)
                .filter("RUNNING"::equals)
                .repeatWhenEmpty(exponentialBackOff(Duration.ofSeconds(1), Duration.ofSeconds(15), Duration.ofMinutes(5))));
    }

    private static Mono<String> getApplicationInstanceState(CloudFoundryClient cloudFoundryClient, String applicationId) {
        return requestApplicationInstances(cloudFoundryClient, applicationId)
            .flatMapMany(response -> Flux.fromIterable(response.getInstances().values()))
            .single()
            .map(ApplicationInstanceInfo::getState);
    }

    private static Mono<String> getApplicationPackageState(CloudFoundryClient cloudFoundryClient, String applicationId) {
        return requestGetApplication(cloudFoundryClient, applicationId)
            .map(response -> ResourceUtils.getEntity(response).getPackageState());
    }

    private static Mono<String> getServiceId(CloudFoundryClient cloudFoundryClient, String serviceName) {
        return requestListServices(cloudFoundryClient, serviceName)
            .single()
            .map(ResourceUtils::getId);

    }

    private static Mono<String> getServicePlanId(CloudFoundryClient cloudFoundryClient, String serviceId) {
        return requestListServicePlans(cloudFoundryClient, serviceId)
            .single()
            .map(ResourceUtils::getId);
    }

    private static Mono<SharedDomainResource> getSharedDomain(CloudFoundryClient cloudFoundryClient) {
        return requestListSharedDomains(cloudFoundryClient)
            .next();
    }

    private static Mono<UpdateServicePlanResponse> makeServicePlanPubliclyVisible(CloudFoundryClient cloudFoundryClient, String serviceName, Boolean spaceScoped) {
        if (spaceScoped) {
            return Mono.empty();
        }

        return getServiceId(cloudFoundryClient, serviceName)
            .flatMap(serviceId -> getServicePlanId(cloudFoundryClient, serviceId))
            .flatMap(planId -> requestUpdateServicePlan(cloudFoundryClient, planId, true));
    }

    private static Mono<ApplicationInstancesResponse> requestApplicationInstances(CloudFoundryClient cloudFoundryClient, String applicationId) {
        return cloudFoundryClient.applicationsV2()
            .instances(ApplicationInstancesRequest.builder()
                .applicationId(applicationId)
                .build());
    }

    private static Mono<AssociateApplicationRouteResponse> requestAssociateApplicationRoute(CloudFoundryClient cloudFoundryClient, String applicationId, String routeId) {
        return cloudFoundryClient.applicationsV2()
            .associateRoute(AssociateApplicationRouteRequest.builder()
                .applicationId(applicationId)
                .routeId(routeId)
                .build());
    }

    private static Mono<CreateApplicationResponse> requestCreateApplication(CloudFoundryClient cloudFoundryClient, String spaceId, String applicationName) {
        return cloudFoundryClient.applicationsV2()
            .create(CreateApplicationRequest.builder()
                .buildpack("https://github.com/cloudfoundry/java-buildpack.git")
                .memory(768)
                .name(applicationName)
                .spaceId(spaceId)
                .build());
    }

    private static Mono<CreateRouteResponse> requestCreateRoute(CloudFoundryClient cloudFoundryClient, String domainId, String spaceId, String hostName) {
        return cloudFoundryClient.routes()
            .create(CreateRouteRequest.builder()
                .domainId(domainId)
                .host(hostName)
                .spaceId(spaceId)
                .build());
    }

    private static Mono<CreateServiceBrokerResponse> requestCreateServiceBroker(CloudFoundryClient cloudFoundryClient, ApplicationMetadata applicationMetadata, String serviceBrokerName,
                                                                                Boolean spaceScoped) {
        return cloudFoundryClient.serviceBrokers()
            .create(CreateServiceBrokerRequest.builder()
                .authenticationPassword("test-authentication-password")
                .authenticationUsername("test-authentication-username")
                .brokerUrl(applicationMetadata.uri)
                .name(serviceBrokerName)
                .spaceId(spaceScoped ? applicationMetadata.spaceId : null)
                .build());
    }

    private static Mono<GetApplicationResponse> requestGetApplication(CloudFoundryClient cloudFoundryClient, String applicationId) {
        return cloudFoundryClient.applicationsV2()
            .get(GetApplicationRequest.builder()
                .applicationId(applicationId)
                .build());
    }

    private static Flux<ServicePlanResource> requestListServicePlans(CloudFoundryClient cloudFoundryClient, String serviceId) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.servicePlans()
                .list(ListServicePlansRequest.builder()
                    .serviceId(serviceId)
                    .page(page)
                    .build()));
    }

    private static Flux<ServiceResource> requestListServices(CloudFoundryClient cloudFoundryClient, String serviceName) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.services()
                .list(ListServicesRequest.builder()
                    .label(serviceName)
                    .build()));
    }

    private static Flux<SharedDomainResource> requestListSharedDomains(CloudFoundryClient cloudFoundryClient) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.sharedDomains()
                .list(ListSharedDomainsRequest.builder()
                    .page(page)
                    .build()));
    }

    private static Mono<UpdateApplicationResponse> requestUpdateApplication(CloudFoundryClient cloudFoundryClient, String applicationId, String planName, String serviceName, String state) {
        return cloudFoundryClient.applicationsV2()
            .update(UpdateApplicationRequest.builder()
                .applicationId(applicationId)
                .environmentJson("SERVICE_NAME", serviceName)
                .environmentJson("PLAN_NAME", planName)
                .state(state)
                .build());
    }

    private static Mono<UpdateServicePlanResponse> requestUpdateServicePlan(CloudFoundryClient cloudFoundryClient, String planId, Boolean visibility) {
        return cloudFoundryClient.servicePlans()
            .update(UpdateServicePlanRequest.builder()
                .servicePlanId(planId)
                .publiclyVisible(visibility)
                .build());
    }

    private static Mono<UploadApplicationResponse> requestUploadApplication(CloudFoundryClient cloudFoundryClient, String applicationId, Path application) {
        return cloudFoundryClient.applicationsV2()
            .upload(UploadApplicationRequest.builder()
                .application(application)
                .applicationId(applicationId)
                .async(true)
                .build());
    }

    public static final class ApplicationMetadata {

        public final String applicationId;

        public final String spaceId;

        public final String uri;

        private ApplicationMetadata(String applicationId, String spaceId, String uri) {
            this.applicationId = applicationId;
            this.spaceId = spaceId;
            this.uri = uri;
        }

    }

    public static final class ServiceBrokerMetadata {

        public final ServiceBrokerUtils.ApplicationMetadata applicationMetadata;

        public final String serviceBrokerId;

        private ServiceBrokerMetadata(ServiceBrokerUtils.ApplicationMetadata applicationMetadata, String serviceBrokerId) {
            this.applicationMetadata = applicationMetadata;
            this.serviceBrokerId = serviceBrokerId;
        }

    }

}
