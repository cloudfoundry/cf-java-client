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
import java.util.stream.Collectors;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v3.Relationship;
import org.cloudfoundry.client.v3.ToOneRelationship;
import org.cloudfoundry.client.v3.applications.ApplicationRelationships;
import org.cloudfoundry.client.v3.applications.CreateApplicationRequest;
import org.cloudfoundry.client.v3.applications.CreateApplicationResponse;
import org.cloudfoundry.client.v3.applications.GetApplicationProcessStatisticsRequest;
import org.cloudfoundry.client.v3.applications.GetApplicationProcessStatisticsResponse;
import org.cloudfoundry.client.v3.applications.SetApplicationCurrentDropletRequest;
import org.cloudfoundry.client.v3.applications.StartApplicationRequest;
import org.cloudfoundry.client.v3.applications.UpdateApplicationEnvironmentVariablesRequest;
import org.cloudfoundry.client.v3.builds.BuildState;
import org.cloudfoundry.client.v3.builds.CreateBuildRequest;
import org.cloudfoundry.client.v3.builds.GetBuildRequest;
import org.cloudfoundry.client.v3.domains.DomainResource;
import org.cloudfoundry.client.v3.domains.ListDomainsRequest;
import org.cloudfoundry.client.v3.packages.CreatePackageRequest;
import org.cloudfoundry.client.v3.packages.GetPackageRequest;
import org.cloudfoundry.client.v3.packages.PackageRelationships;
import org.cloudfoundry.client.v3.packages.PackageState;
import org.cloudfoundry.client.v3.packages.PackageType;
import org.cloudfoundry.client.v3.packages.UploadPackageRequest;
import org.cloudfoundry.client.v3.packages.UploadPackageResponse;
import org.cloudfoundry.client.v3.routes.Application;
import org.cloudfoundry.client.v3.routes.CreateRouteRequest;
import org.cloudfoundry.client.v3.routes.CreateRouteResponse;
import org.cloudfoundry.client.v3.routes.Destination;
import org.cloudfoundry.client.v3.routes.InsertRouteDestinationsRequest;
import org.cloudfoundry.client.v3.routes.RouteRelationships;
import org.cloudfoundry.client.v3.servicebindings.CreateServiceBindingRequest;
import org.cloudfoundry.client.v3.servicebindings.ServiceBindingRelationships;
import org.cloudfoundry.client.v3.servicebindings.ServiceBindingType;
import org.cloudfoundry.util.PaginationUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ApplicationUtils {

    static Mono<ApplicationMetadata> pushApplication(
            CloudFoundryClient cloudFoundryClient,
            Path applicationBits,
            String applicationName,
            Collection<String> boundServices,
            Map<String, String> env,
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
                                                        domain.getId(),
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
                                                                                                        domain
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
                                                        .serviceBindingsV3()
                                                        .create(
                                                                CreateServiceBindingRequest
                                                                        .builder()
                                                                        .type(
                                                                                ServiceBindingType
                                                                                        .APPLICATION)
                                                                        .relationships(
                                                                                ServiceBindingRelationships
                                                                                        .builder()
                                                                                        .application(
                                                                                                ToOneRelationship
                                                                                                        .builder()
                                                                                                        .data(
                                                                                                                Relationship
                                                                                                                        .builder()
                                                                                                                        .id(
                                                                                                                                applicationId)
                                                                                                                        .build())
                                                                                                        .build())
                                                                                        .serviceInstance(
                                                                                                ToOneRelationship
                                                                                                        .builder()
                                                                                                        .data(
                                                                                                                Relationship
                                                                                                                        .builder()
                                                                                                                        .id(
                                                                                                                                serviceInstanceId)
                                                                                                                        .build())
                                                                                                        .build())
                                                                                        .build())
                                                                        .build()))
                                .collect(Collectors.toList()),
                        a -> Mono.just(applicationId))
                .thenReturn(applicationId);
    }

    private static Mono<String> createApplicationId(
            CloudFoundryClient cloudFoundryClient, String spaceId, String applicationName) {
        return requestCreateApplication(cloudFoundryClient, spaceId, applicationName)
                .map(response -> response.getId());
    }

    private static Mono<String> createRouteId(
            CloudFoundryClient cloudFoundryClient,
            String domainId,
            String spaceId,
            String hostName) {
        return requestCreateRoute(cloudFoundryClient, domainId, spaceId, hostName)
                .map(response -> response.getId());
    }

    private static Mono<String> createRunningApplication(
            CloudFoundryClient cloudFoundryClient,
            Path applicationBits,
            String applicationId,
            Map<String, String> env) {
        return requestUploadApplication(cloudFoundryClient, applicationId, applicationBits)
                .flatMap(
                        pkg ->
                                getApplicationPackageState(cloudFoundryClient, pkg.getId())
                                        .filter(
                                                state ->
                                                        PackageState.READY.equals(state)
                                                                || PackageState.FAILED.equals(
                                                                        state))
                                        .repeatWhenEmpty(
                                                exponentialBackOff(
                                                        Duration.ofSeconds(1),
                                                        Duration.ofSeconds(15),
                                                        Duration.ofMinutes(5)))
                                        .thenReturn(pkg.getId()))
                .flatMap(
                        packageId ->
                                cloudFoundryClient
                                        .builds()
                                        .create(
                                                CreateBuildRequest.builder()
                                                        .getPackage(
                                                                Relationship.builder()
                                                                        .id(packageId)
                                                                        .build())
                                                        .build()))
                .flatMap(
                        build ->
                                getBuildState(cloudFoundryClient, build.getId())
                                        .filter(
                                                state ->
                                                        BuildState.STAGED.equals(state)
                                                                || BuildState.FAILED.equals(state))
                                        .repeatWhenEmpty(
                                                exponentialBackOff(
                                                        Duration.ofSeconds(1),
                                                        Duration.ofSeconds(15),
                                                        Duration.ofMinutes(5)))
                                        .then(
                                                cloudFoundryClient
                                                        .builds()
                                                        .get(
                                                                GetBuildRequest.builder()
                                                                        .buildId(build.getId())
                                                                        .build()))
                                        .map(b -> b.getDroplet().getId()))
                .flatMap(
                        dropletId ->
                                cloudFoundryClient
                                        .applicationsV3()
                                        .setCurrentDroplet(
                                                SetApplicationCurrentDropletRequest.builder()
                                                        .applicationId(applicationId)
                                                        .data(
                                                                Relationship.builder()
                                                                        .id(dropletId)
                                                                        .build())
                                                        .build()))
                .then(requestUpdateApplication(cloudFoundryClient, applicationId, env))
                .then(
                        getApplicationInstanceState(cloudFoundryClient, applicationId)
                                .filter("RUNNING"::equals)
                                .repeatWhenEmpty(
                                        exponentialBackOff(
                                                Duration.ofSeconds(1),
                                                Duration.ofSeconds(15),
                                                Duration.ofMinutes(5))));
    }

    private static Mono<PackageState> getApplicationPackageState(
            CloudFoundryClient cloudFoundryClient, String packageId) {
        return cloudFoundryClient
                .packages()
                .get(GetPackageRequest.builder().packageId(packageId).build())
                .map(pkg -> pkg.getState());
    }

    private static Mono<BuildState> getBuildState(
            CloudFoundryClient cloudFoundryClient, String buildId) {
        return cloudFoundryClient
                .builds()
                .get(GetBuildRequest.builder().buildId(buildId).build())
                .map(build -> build.getState());
    }

    private static Mono<String> getApplicationInstanceState(
            CloudFoundryClient cloudFoundryClient, String applicationId) {
        return requestApplicationInstances(cloudFoundryClient, applicationId)
                .flatMapIterable(response -> response.getResources())
                .single()
                .map(resource -> resource.getState().getValue());
    }

    private static Mono<DomainResource> getSharedDomain(CloudFoundryClient cloudFoundryClient) {
        return requestListDomains(cloudFoundryClient).filter(domain -> !domain.isInternal()).next();
    }

    private static Mono<GetApplicationProcessStatisticsResponse> requestApplicationInstances(
            CloudFoundryClient cloudFoundryClient, String applicationId) {
        return cloudFoundryClient
                .applicationsV3()
                .getProcessStatistics(
                        GetApplicationProcessStatisticsRequest.builder()
                                .applicationId(applicationId)
                                .type("web") // hardcoded "web" is save for our tests.
                                .build());
    }

    private static Mono<Void> requestAssociateApplicationRoute(
            CloudFoundryClient cloudFoundryClient, String applicationId, String routeId) {
        return cloudFoundryClient
                .routesV3()
                .insertDestinations(
                        InsertRouteDestinationsRequest.builder()
                                .routeId(routeId)
                                .destination(
                                        Destination.builder()
                                                .application(
                                                        Application.builder()
                                                                .applicationId(applicationId)
                                                                .build())
                                                .build())
                                .build())
                .then();
    }

    private static Mono<CreateApplicationResponse> requestCreateApplication(
            CloudFoundryClient cloudFoundryClient, String spaceId, String applicationName) {
        return cloudFoundryClient
                .applicationsV3()
                .create(
                        CreateApplicationRequest.builder()
                                .name(applicationName)
                                .relationships(
                                        ApplicationRelationships.builder()
                                                .space(
                                                        ToOneRelationship.builder()
                                                                .data(
                                                                        Relationship.builder()
                                                                                .id(spaceId)
                                                                                .build())
                                                                .build())
                                                .build())
                                .build());
    }

    private static Mono<CreateRouteResponse> requestCreateRoute(
            CloudFoundryClient cloudFoundryClient,
            String domainId,
            String spaceId,
            String hostName) {
        return cloudFoundryClient
                .routesV3()
                .create(
                        CreateRouteRequest.builder()
                                .host(hostName)
                                .relationships(
                                        RouteRelationships.builder()
                                                .domain(
                                                        ToOneRelationship.builder()
                                                                .data(
                                                                        Relationship.builder()
                                                                                .id(domainId)
                                                                                .build())
                                                                .build())
                                                .space(
                                                        ToOneRelationship.builder()
                                                                .data(
                                                                        Relationship.builder()
                                                                                .id(spaceId)
                                                                                .build())
                                                                .build())
                                                .build())
                                .build());
    }

    private static Flux<DomainResource> requestListDomains(CloudFoundryClient cloudFoundryClient) {
        return PaginationUtils.requestClientV3Resources(
                page ->
                        cloudFoundryClient
                                .domainsV3()
                                .list(ListDomainsRequest.builder().page(page).build()));
    }

    private static Mono<Void> requestUpdateApplication(
            CloudFoundryClient cloudFoundryClient, String applicationId, Map<String, String> env) {
        return cloudFoundryClient
                .applicationsV3()
                .updateEnvironmentVariables(
                        UpdateApplicationEnvironmentVariablesRequest.builder()
                                .applicationId(applicationId)
                                .vars(env)
                                .build())
                .then(
                        cloudFoundryClient
                                .applicationsV3()
                                .start(
                                        StartApplicationRequest.builder()
                                                .applicationId(applicationId)
                                                .build()))
                .then();
    }

    private static Mono<UploadPackageResponse> requestUploadApplication(
            CloudFoundryClient cloudFoundryClient, String applicationId, Path application) {
        // here only uploading the application bits, the record is created earlier.
        return cloudFoundryClient
                .packages()
                .create(
                        CreatePackageRequest.builder()
                                .type(PackageType.BITS)
                                .relationships(
                                        PackageRelationships.builder()
                                                .application(
                                                        ToOneRelationship.builder()
                                                                .data(
                                                                        Relationship.builder()
                                                                                .id(applicationId)
                                                                                .build())
                                                                .build())
                                                .build())
                                .build())
                .flatMap(
                        pkg ->
                                cloudFoundryClient
                                        .packages()
                                        .upload(
                                                UploadPackageRequest.builder()
                                                        .packageId(pkg.getId())
                                                        .bits(application)
                                                        .build()));
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
