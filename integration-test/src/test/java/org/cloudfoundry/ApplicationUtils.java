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

import static org.cloudfoundry.util.DelayUtils.exponentialBackOff;
import static org.cloudfoundry.util.tuple.TupleUtils.function;

import java.nio.file.Path;
import java.time.Duration;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.applications.ApplicationInstanceInfo;
import org.cloudfoundry.client.v2.applications.ApplicationInstancesRequest;
import org.cloudfoundry.client.v2.applications.ApplicationInstancesResponse;
import org.cloudfoundry.client.v2.applications.AssociateApplicationRouteRequest;
import org.cloudfoundry.client.v2.applications.AssociateApplicationRouteResponse;
import org.cloudfoundry.client.v2.applications.CreateApplicationRequest;
import org.cloudfoundry.client.v2.applications.CreateApplicationResponse;
import org.cloudfoundry.client.v2.applications.GetApplicationRequest;
import org.cloudfoundry.client.v2.applications.GetApplicationResponse;
import org.cloudfoundry.client.v2.applications.UpdateApplicationRequest;
import org.cloudfoundry.client.v2.applications.UpdateApplicationResponse;
import org.cloudfoundry.client.v2.applications.UploadApplicationRequest;
import org.cloudfoundry.client.v2.applications.UploadApplicationResponse;
import org.cloudfoundry.client.v2.routes.CreateRouteRequest;
import org.cloudfoundry.client.v2.routes.CreateRouteResponse;
import org.cloudfoundry.client.v2.servicebindings.CreateServiceBindingRequest;
import org.cloudfoundry.client.v2.shareddomains.ListSharedDomainsRequest;
import org.cloudfoundry.client.v2.shareddomains.SharedDomainResource;
import org.cloudfoundry.util.JobUtils;
import org.cloudfoundry.util.PaginationUtils;
import org.cloudfoundry.util.ResourceUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ApplicationUtils {

    static Mono<ApplicationMetadata> pushApplication(
            CloudFoundryClient cloudFoundryClient,
            Path applicationBits,
            String applicationName,
            Collection<String> boundServices,
            Map<String, Object> env,
            String hostName,
            String spaceId) {
        return getSharedDomain(cloudFoundryClient)
                .flatMap(
                        domain ->
                                Mono.zip(
                                                createApplicationId(
                                                        cloudFoundryClient,
                                                        spaceId,
                                                        applicationName),
                                                createRouteId(
                                                        cloudFoundryClient,
                                                        ResourceUtils.getId(domain),
                                                        spaceId,
                                                        hostName))
                                        .flatMap(
                                                function(
                                                        (applicationId, routeId) ->
                                                                Mono.zip(
                                                                                requestAssociateApplicationRoute(
                                                                                        cloudFoundryClient,
                                                                                        applicationId,
                                                                                        routeId),
                                                                                bindServices(
                                                                                        cloudFoundryClient,
                                                                                        applicationId,
                                                                                        boundServices))
                                                                        .thenReturn(applicationId)))
                                        .flatMap(
                                                applicationId ->
                                                        createRunningApplication(
                                                                        cloudFoundryClient,
                                                                        applicationBits,
                                                                        applicationId,
                                                                        env)
                                                                .map(
                                                                        ignore ->
                                                                                new ApplicationUtils
                                                                                        .ApplicationMetadata(
                                                                                        applicationId,
                                                                                        spaceId,
                                                                                        String
                                                                                                .format(
                                                                                                        "https://%s.%s",
                                                                                                        hostName,
                                                                                                        ResourceUtils
                                                                                                                .getEntity(
                                                                                                                        domain)
                                                                                                                .getName())))));
    }

    private static Mono<String> bindServices(
            CloudFoundryClient cloudFoundryClient,
            String applicationId,
            Collection<String> boundServices) {
        return Mono.zip(
                        boundServices.stream()
                                .map(
                                        serviceInstanceId ->
                                                cloudFoundryClient
                                                        .serviceBindingsV2()
                                                        .create(
                                                                CreateServiceBindingRequest
                                                                        .builder()
                                                                        .applicationId(
                                                                                applicationId)
                                                                        .serviceInstanceId(
                                                                                serviceInstanceId)
                                                                        .build()))
                                .collect(Collectors.toList()),
                        a -> Mono.just(applicationId))
                .thenReturn(applicationId);
    }

    private static Mono<String> createApplicationId(
            CloudFoundryClient cloudFoundryClient, String spaceId, String applicationName) {
        return requestCreateApplication(cloudFoundryClient, spaceId, applicationName)
                .map(ResourceUtils::getId);
    }

    private static Mono<String> createRouteId(
            CloudFoundryClient cloudFoundryClient,
            String domainId,
            String spaceId,
            String hostName) {
        return requestCreateRoute(cloudFoundryClient, domainId, spaceId, hostName)
                .map(ResourceUtils::getId);
    }

    private static Mono<String> createRunningApplication(
            CloudFoundryClient cloudFoundryClient,
            Path applicationBits,
            String applicationId,
            Map<String, Object> env) {
        return ApplicationUtils.requestUploadApplication(
                        cloudFoundryClient, applicationId, applicationBits)
                .flatMap(
                        job ->
                                JobUtils.waitForCompletion(
                                        cloudFoundryClient, Duration.ofMinutes(5), job))
                .then(requestUpdateApplication(cloudFoundryClient, applicationId, env, "STARTED"))
                .then(
                        getApplicationPackageState(cloudFoundryClient, applicationId)
                                .filter(state -> "STAGED".equals(state) || "FAILED".equals(state))
                                .repeatWhenEmpty(
                                        exponentialBackOff(
                                                Duration.ofSeconds(1),
                                                Duration.ofSeconds(15),
                                                Duration.ofMinutes(5))))
                .then(
                        getApplicationInstanceState(cloudFoundryClient, applicationId)
                                .filter("RUNNING"::equals)
                                .repeatWhenEmpty(
                                        exponentialBackOff(
                                                Duration.ofSeconds(1),
                                                Duration.ofSeconds(15),
                                                Duration.ofMinutes(5))));
    }

    private static Mono<String> getApplicationPackageState(
            CloudFoundryClient cloudFoundryClient, String applicationId) {
        return requestGetApplication(cloudFoundryClient, applicationId)
                .map(response -> ResourceUtils.getEntity(response).getPackageState());
    }

    private static Mono<String> getApplicationInstanceState(
            CloudFoundryClient cloudFoundryClient, String applicationId) {
        return requestApplicationInstances(cloudFoundryClient, applicationId)
                .flatMapMany(response -> Flux.fromIterable(response.getInstances().values()))
                .single()
                .map(ApplicationInstanceInfo::getState);
    }

    private static Mono<SharedDomainResource> getSharedDomain(
            CloudFoundryClient cloudFoundryClient) {
        return requestListSharedDomains(cloudFoundryClient)
                .filter(
                        resource ->
                                !Optional.ofNullable(
                                                ResourceUtils.getEntity(resource).getInternal())
                                        .orElse(false))
                .next();
    }

    private static Mono<ApplicationInstancesResponse> requestApplicationInstances(
            CloudFoundryClient cloudFoundryClient, String applicationId) {
        return cloudFoundryClient
                .applicationsV2()
                .instances(
                        ApplicationInstancesRequest.builder().applicationId(applicationId).build());
    }

    private static Mono<AssociateApplicationRouteResponse> requestAssociateApplicationRoute(
            CloudFoundryClient cloudFoundryClient, String applicationId, String routeId) {
        return cloudFoundryClient
                .applicationsV2()
                .associateRoute(
                        AssociateApplicationRouteRequest.builder()
                                .applicationId(applicationId)
                                .routeId(routeId)
                                .build());
    }

    private static Mono<CreateApplicationResponse> requestCreateApplication(
            CloudFoundryClient cloudFoundryClient, String spaceId, String applicationName) {
        return cloudFoundryClient
                .applicationsV2()
                .create(
                        CreateApplicationRequest.builder()
                                .buildpack("https://github.com/cloudfoundry/java-buildpack.git")
                                .memory(768)
                                .name(applicationName)
                                .spaceId(spaceId)
                                .build());
    }

    private static Mono<CreateRouteResponse> requestCreateRoute(
            CloudFoundryClient cloudFoundryClient,
            String domainId,
            String spaceId,
            String hostName) {
        return cloudFoundryClient
                .routes()
                .create(
                        CreateRouteRequest.builder()
                                .domainId(domainId)
                                .host(hostName)
                                .spaceId(spaceId)
                                .build());
    }

    private static Mono<GetApplicationResponse> requestGetApplication(
            CloudFoundryClient cloudFoundryClient, String applicationId) {
        return cloudFoundryClient
                .applicationsV2()
                .get(GetApplicationRequest.builder().applicationId(applicationId).build());
    }

    private static Flux<SharedDomainResource> requestListSharedDomains(
            CloudFoundryClient cloudFoundryClient) {
        return PaginationUtils.requestClientV2Resources(
                page ->
                        cloudFoundryClient
                                .sharedDomains()
                                .list(ListSharedDomainsRequest.builder().page(page).build()));
    }

    private static Mono<UpdateApplicationResponse> requestUpdateApplication(
            CloudFoundryClient cloudFoundryClient,
            String applicationId,
            Map<String, Object> env,
            String state) {
        return cloudFoundryClient
                .applicationsV2()
                .update(
                        UpdateApplicationRequest.builder()
                                .applicationId(applicationId)
                                .environmentJsons(env)
                                .state(state)
                                .build());
    }

    private static Mono<UploadApplicationResponse> requestUploadApplication(
            CloudFoundryClient cloudFoundryClient, String applicationId, Path application) {
        return cloudFoundryClient
                .applicationsV2()
                .upload(
                        UploadApplicationRequest.builder()
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
}
