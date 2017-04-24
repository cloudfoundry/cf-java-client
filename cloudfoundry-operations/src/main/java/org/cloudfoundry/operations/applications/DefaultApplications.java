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

package org.cloudfoundry.operations.applications;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.OrderDirection;
import org.cloudfoundry.client.v2.applications.AbstractApplicationResource;
import org.cloudfoundry.client.v2.applications.ApplicationEnvironmentRequest;
import org.cloudfoundry.client.v2.applications.ApplicationEnvironmentResponse;
import org.cloudfoundry.client.v2.applications.ApplicationInstanceInfo;
import org.cloudfoundry.client.v2.applications.ApplicationInstancesRequest;
import org.cloudfoundry.client.v2.applications.ApplicationInstancesResponse;
import org.cloudfoundry.client.v2.applications.ApplicationStatisticsRequest;
import org.cloudfoundry.client.v2.applications.ApplicationStatisticsResponse;
import org.cloudfoundry.client.v2.applications.AssociateApplicationRouteRequest;
import org.cloudfoundry.client.v2.applications.AssociateApplicationRouteResponse;
import org.cloudfoundry.client.v2.applications.CopyApplicationRequest;
import org.cloudfoundry.client.v2.applications.CopyApplicationResponse;
import org.cloudfoundry.client.v2.applications.CreateApplicationRequest;
import org.cloudfoundry.client.v2.applications.CreateApplicationResponse;
import org.cloudfoundry.client.v2.applications.InstanceStatistics;
import org.cloudfoundry.client.v2.applications.ListApplicationRoutesRequest;
import org.cloudfoundry.client.v2.applications.ListApplicationServiceBindingsRequest;
import org.cloudfoundry.client.v2.applications.RemoveApplicationRouteRequest;
import org.cloudfoundry.client.v2.applications.RemoveApplicationServiceBindingRequest;
import org.cloudfoundry.client.v2.applications.RestageApplicationResponse;
import org.cloudfoundry.client.v2.applications.Statistics;
import org.cloudfoundry.client.v2.applications.SummaryApplicationRequest;
import org.cloudfoundry.client.v2.applications.SummaryApplicationResponse;
import org.cloudfoundry.client.v2.applications.TerminateApplicationInstanceRequest;
import org.cloudfoundry.client.v2.applications.UpdateApplicationRequest;
import org.cloudfoundry.client.v2.applications.UploadApplicationRequest;
import org.cloudfoundry.client.v2.applications.UploadApplicationResponse;
import org.cloudfoundry.client.v2.applications.Usage;
import org.cloudfoundry.client.v2.events.EventEntity;
import org.cloudfoundry.client.v2.events.EventResource;
import org.cloudfoundry.client.v2.events.ListEventsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationPrivateDomainsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationSpacesRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsRequest;
import org.cloudfoundry.client.v2.organizations.OrganizationResource;
import org.cloudfoundry.client.v2.privatedomains.PrivateDomainEntity;
import org.cloudfoundry.client.v2.privatedomains.PrivateDomainResource;
import org.cloudfoundry.client.v2.routes.CreateRouteResponse;
import org.cloudfoundry.client.v2.routes.DeleteRouteRequest;
import org.cloudfoundry.client.v2.routes.DeleteRouteResponse;
import org.cloudfoundry.client.v2.routes.ListRoutesRequest;
import org.cloudfoundry.client.v2.routes.RouteResource;
import org.cloudfoundry.client.v2.servicebindings.CreateServiceBindingRequest;
import org.cloudfoundry.client.v2.servicebindings.CreateServiceBindingResponse;
import org.cloudfoundry.client.v2.servicebindings.ServiceBindingResource;
import org.cloudfoundry.client.v2.serviceinstances.ServiceInstance;
import org.cloudfoundry.client.v2.serviceinstances.UnionServiceInstanceResource;
import org.cloudfoundry.client.v2.shareddomains.ListSharedDomainsRequest;
import org.cloudfoundry.client.v2.shareddomains.SharedDomainEntity;
import org.cloudfoundry.client.v2.shareddomains.SharedDomainResource;
import org.cloudfoundry.client.v2.spaces.GetSpaceRequest;
import org.cloudfoundry.client.v2.spaces.GetSpaceResponse;
import org.cloudfoundry.client.v2.spaces.GetSpaceSummaryRequest;
import org.cloudfoundry.client.v2.spaces.GetSpaceSummaryResponse;
import org.cloudfoundry.client.v2.spaces.ListSpaceApplicationsRequest;
import org.cloudfoundry.client.v2.spaces.ListSpaceServiceInstancesRequest;
import org.cloudfoundry.client.v2.spaces.SpaceApplicationSummary;
import org.cloudfoundry.client.v2.spaces.SpaceResource;
import org.cloudfoundry.client.v2.stacks.GetStackRequest;
import org.cloudfoundry.client.v2.stacks.GetStackResponse;
import org.cloudfoundry.client.v2.stacks.ListStacksRequest;
import org.cloudfoundry.client.v2.stacks.StackResource;
import org.cloudfoundry.doppler.DopplerClient;
import org.cloudfoundry.doppler.Envelope;
import org.cloudfoundry.doppler.EventType;
import org.cloudfoundry.doppler.LogMessage;
import org.cloudfoundry.doppler.RecentLogsRequest;
import org.cloudfoundry.doppler.StreamRequest;
import org.cloudfoundry.operations.util.OperationsLogging;
import org.cloudfoundry.util.DateUtils;
import org.cloudfoundry.util.DelayTimeoutException;
import org.cloudfoundry.util.ExceptionUtils;
import org.cloudfoundry.util.FileUtils;
import org.cloudfoundry.util.FluentMap;
import org.cloudfoundry.util.JobUtils;
import org.cloudfoundry.util.PaginationUtils;
import org.cloudfoundry.util.ResourceMatchingUtils;
import org.cloudfoundry.util.ResourceUtils;
import org.cloudfoundry.util.SortingUtils;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuple4;
import reactor.util.function.Tuples;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import static org.cloudfoundry.util.DelayUtils.exponentialBackOff;
import static org.cloudfoundry.util.tuple.TupleUtils.function;
import static org.cloudfoundry.util.tuple.TupleUtils.predicate;

public final class DefaultApplications implements Applications {

    private static final int CF_APP_STOPPED_STATS_ERROR = 200003;

    private static final int CF_BUILDPACK_COMPILED_FAILED = 170004;

    private static final int CF_INSTANCES_ERROR = 220001;

    private static final int CF_SERVICE_ALREADY_BOUND = 90003;

    private static final int CF_STAGING_NOT_FINISHED = 170002;

    private static final int CF_STAGING_TIME_EXPIRED = 170007;

    private static final Comparator<LogMessage> LOG_MESSAGE_COMPARATOR = Comparator.comparing(LogMessage::getTimestamp);

    private static final Duration LOG_MESSAGE_TIMESPAN = Duration.ofMillis(500);

    private static final int MAX_NUMBER_OF_RECENT_EVENTS = 50;

    private static final String STARTED_STATE = "STARTED";

    private static final String STOPPED_STATE = "STOPPED";

    private final Mono<CloudFoundryClient> cloudFoundryClient;

    private final Mono<DopplerClient> dopplerClient;

    private final RandomWords randomWords;

    private final Mono<String> spaceId;

    public DefaultApplications(Mono<CloudFoundryClient> cloudFoundryClient, Mono<DopplerClient> dopplerClient, Mono<String> spaceId) {
        this(cloudFoundryClient, dopplerClient, new WordListRandomWords(), spaceId);
    }

    DefaultApplications(Mono<CloudFoundryClient> cloudFoundryClient, Mono<DopplerClient> dopplerClient, RandomWords randomWords, Mono<String> spaceId) {
        this.cloudFoundryClient = cloudFoundryClient;
        this.dopplerClient = dopplerClient;
        this.randomWords = randomWords;
        this.spaceId = spaceId;
    }

    @Override
    public Mono<Void> copySource(CopySourceApplicationRequest request) {
        return Mono
            .when(this.cloudFoundryClient, this.spaceId)
            .flatMap(function((cloudFoundryClient, spaceId) -> Mono.when(
                Mono.just(cloudFoundryClient),
                getApplicationId(cloudFoundryClient, request.getName(), spaceId),
                getApplicationIdFromOrgSpace(cloudFoundryClient, request.getTargetName(), spaceId, request.getTargetOrganization(), request.getTargetSpace())
            )))
            .flatMap(function((cloudFoundryClient, sourceApplicationId, targetApplicationId) -> copyBits(cloudFoundryClient, request.getStagingTimeout(), sourceApplicationId, targetApplicationId)
                .then(Mono.just(Tuples.of(cloudFoundryClient, targetApplicationId)))))
            .filter(predicate((cloudFoundryClient, targetApplicationId) -> Optional.ofNullable(request.getRestart()).orElse(false)))
            .flatMap(function((cloudFoundryClient, targetApplicationId) -> restartApplication(cloudFoundryClient, request.getTargetName(), targetApplicationId, request.getStagingTimeout(),
                request.getStartupTimeout())))
            .transform(OperationsLogging.log("Copy Application Source"))
            .checkpoint();
    }

    @Override
    public Mono<Void> delete(DeleteApplicationRequest request) {
        return Mono
            .when(this.cloudFoundryClient, this.spaceId)
            .flatMap(function((cloudFoundryClient, spaceId) -> getRoutesAndApplicationId(cloudFoundryClient, request, spaceId, Optional.ofNullable(request.getDeleteRoutes()).orElse(false))
                .map(function((routes, applicationId) -> Tuples.of(cloudFoundryClient, routes, applicationId)))))
            .flatMap(function((cloudFoundryClient, routes, applicationId) -> deleteRoutes(cloudFoundryClient, request.getCompletionTimeout(), routes)
                .then(Mono.just(Tuples.of(cloudFoundryClient, applicationId)))))
            .flatMap(function((cloudFoundryClient, applicationId) -> removeServiceBindings(cloudFoundryClient, applicationId)
                .then(Mono.just(Tuples.of(cloudFoundryClient, applicationId)))))
            .flatMap(function(DefaultApplications::requestDeleteApplication))
            .transform(OperationsLogging.log("Delete Application"))
            .checkpoint();
    }

    @Override
    public Mono<Void> disableSsh(DisableApplicationSshRequest request) {
        return Mono
            .when(this.cloudFoundryClient, this.spaceId)
            .flatMap(function((cloudFoundryClient, spaceId) -> Mono.when(
                Mono.just(cloudFoundryClient),
                getApplicationIdWhere(cloudFoundryClient, request.getName(), spaceId, sshEnabled(true))
            )))
            .flatMap(function((cloudFoundryClient, applicationId) -> requestUpdateApplicationSsh(cloudFoundryClient, applicationId, false)))
            .then()
            .transform(OperationsLogging.log("Disable Application SSH"))
            .checkpoint();
    }

    @Override
    public Mono<Void> enableSsh(EnableApplicationSshRequest request) {
        return Mono
            .when(this.cloudFoundryClient, this.spaceId)
            .flatMap(function((cloudFoundryClient, spaceId) -> Mono.when(
                Mono.just(cloudFoundryClient),
                getApplicationIdWhere(cloudFoundryClient, request.getName(), spaceId, sshEnabled(false))
            )))
            .flatMap(function((cloudFoundryClient, applicationId) -> requestUpdateApplicationSsh(cloudFoundryClient, applicationId, true)))
            .then()
            .transform(OperationsLogging.log("Enable Application SSH"))
            .checkpoint();
    }

    @Override
    public Mono<ApplicationDetail> get(GetApplicationRequest request) {
        return Mono
            .when(this.cloudFoundryClient, this.spaceId)
            .flatMap(function((cloudFoundryClient, spaceId) -> Mono.when(
                Mono.just(cloudFoundryClient),
                getApplication(cloudFoundryClient, request.getName(), spaceId)
            )))
            .flatMap(function(DefaultApplications::getAuxiliaryContent))
            .map(function(DefaultApplications::toApplicationDetail))
            .transform(OperationsLogging.log("Get Application"))
            .checkpoint();
    }

    @Override
    public Mono<ApplicationManifest> getApplicationManifest(GetApplicationManifestRequest request) {
        return Mono
            .when(this.cloudFoundryClient, this.spaceId)
            .flatMap(function((cloudFoundryClient, spaceId) -> Mono.when(
                Mono.just(cloudFoundryClient),
                getApplicationId(cloudFoundryClient, request.getName(), spaceId)
            )))
            .flatMap(function((cloudFoundryClient, applicationId) -> Mono.when(
                Mono.just(cloudFoundryClient),
                requestApplicationSummary(cloudFoundryClient, applicationId)
            )))
            .flatMap(function((cloudFoundryClient, response) -> Mono.when(
                Mono.just(response),
                getStackName(cloudFoundryClient, response.getStackId())
            )))
            .flatMap(function(DefaultApplications::toApplicationManifest))
            .transform(OperationsLogging.log("Get Application Manifest"))
            .checkpoint();
    }

    @Override
    public Mono<ApplicationEnvironments> getEnvironments(GetApplicationEnvironmentsRequest request) {
        return Mono
            .when(this.cloudFoundryClient, this.spaceId)
            .flatMap(function((cloudFoundryClient, spaceId) -> Mono.when(
                Mono.just(cloudFoundryClient),
                getApplicationId(cloudFoundryClient, request.getName(), spaceId)
            )))
            .flatMap(function(DefaultApplications::requestApplicationEnvironment))
            .map(DefaultApplications::toApplicationEnvironments)
            .transform(OperationsLogging.log("Get Application Environments"))
            .checkpoint();
    }

    @Override
    public Flux<ApplicationEvent> getEvents(GetApplicationEventsRequest request) {
        return Mono
            .when(this.cloudFoundryClient, this.spaceId)
            .flatMap(function((cloudFoundryClient, spaceId) -> Mono.when(
                Mono.just(cloudFoundryClient),
                getApplicationId(cloudFoundryClient, request.getName(), spaceId)
            )))
            .flatMapMany(function((cloudFoundryClient, applicationId) -> requestEvents(applicationId, cloudFoundryClient)
                .take(Optional.ofNullable(request.getMaxNumberOfEvents()).orElse(MAX_NUMBER_OF_RECENT_EVENTS))))
            .map(DefaultApplications::convertToApplicationEvent)
            .transform(OperationsLogging.log("Get Application Events"))
            .checkpoint();
    }

    @Override
    public Mono<ApplicationHealthCheck> getHealthCheck(GetApplicationHealthCheckRequest request) {
        return Mono
            .when(this.cloudFoundryClient, this.spaceId)
            .flatMap(function((cloudFoundryClient, spaceId) -> getApplication(cloudFoundryClient, request.getName(), spaceId)))
            .map(DefaultApplications::toHealthCheck)
            .transform(OperationsLogging.log("Get Application Health Check"))
            .checkpoint();
    }

    @Override
    public Flux<ApplicationSummary> list() {
        return Mono
            .when(this.cloudFoundryClient, this.spaceId)
            .flatMap(function(DefaultApplications::requestSpaceSummary))
            .flatMapMany(DefaultApplications::extractApplications)
            .map(DefaultApplications::toApplicationSummary)
            .transform(OperationsLogging.log("List Applications"))
            .checkpoint();
    }

    @Override
    public Flux<LogMessage> logs(LogsRequest request) {
        return Mono
            .when(this.cloudFoundryClient, this.spaceId)
            .flatMap(function((cloudFoundryClient, spaceId) -> getApplicationId(cloudFoundryClient, request.getName(), spaceId)))
            .flatMapMany(applicationId -> getLogs(this.dopplerClient, applicationId, request.getRecent()))
            .transform(OperationsLogging.log("Get Application Logs"))
            .checkpoint();
    }

    @Override
    @SuppressWarnings("deprecation")
    public Mono<Void> push(PushApplicationRequest request) {
        ApplicationManifest.Builder builder = ApplicationManifest.builder()
            .buildpack(request.getBuildpack())
            .command(request.getCommand())
            .disk(request.getDiskQuota())
            .dockerImage(request.getDockerImage())
            .healthCheckType(request.getHealthCheckType())
            .instances(request.getInstances())
            .memory(request.getMemory())
            .name(request.getName())
            .noHostname(request.getNoHostname())
            .noRoute(request.getNoRoute())
            .path(Optional.ofNullable(request.getPath()).orElse(request.getApplication()))
            .randomRoute(request.getRandomRoute())
            .routePath(request.getRoutePath())
            .stack(request.getStack())
            .timeout(request.getTimeout());

        if (request.getDomain() != null) {
            builder.domain(request.getDomain());
        }

        if (request.getHost() != null) {
            builder.host(request.getHost());
        }

        return pushManifest(PushApplicationManifestRequest.builder()
            .manifest(builder.build())
            .noStart(request.getNoStart())
            .stagingTimeout(request.getStagingTimeout())
            .startupTimeout(request.getStartupTimeout())
            .build())
            .transform(OperationsLogging.log("Push"))
            .checkpoint();
    }

    @Override
    public Mono<Void> pushManifest(PushApplicationManifestRequest request) {
        return Mono
            .when(this.cloudFoundryClient, this.spaceId)
            .flatMap(function((cloudFoundryClient, spaceId) -> Mono.when(
                Mono.just(cloudFoundryClient),
                getSpaceOrganizationId(cloudFoundryClient, spaceId),
                Mono.just(spaceId)
            )))
            .flatMap(function((cloudFoundryClient, organizationId, spaceId) -> Mono.when(
                Mono.just(cloudFoundryClient),
                listAvailableDomains(cloudFoundryClient, organizationId),
                Mono.just(spaceId))))
            .flatMapMany(function((cloudFoundryClient, availableDomains, spaceId) -> Flux.fromIterable(request.getManifests())
                .flatMap(manifest -> {
                    if (manifest.getPath() != null) {
                        return pushApplication(cloudFoundryClient, availableDomains, manifest, this.randomWords, request, spaceId);
                    } else if (!manifest.getDockerImage().isEmpty()) {
                        return pushDocker(cloudFoundryClient, availableDomains, manifest, this.randomWords, request, spaceId);
                    } else {
                        throw new IllegalStateException("One of application or dockerImage must be supplied");
                    }
                })))
            .then()
            .transform(OperationsLogging.log("Push Manifest"))
            .checkpoint();
    }

    @Override
    public Mono<Void> rename(RenameApplicationRequest request) {
        return Mono
            .when(this.cloudFoundryClient, this.spaceId)
            .flatMap(function((cloudFoundryClient, spaceId) -> Mono.when(
                Mono.just(cloudFoundryClient),
                getApplicationId(cloudFoundryClient, request.getName(), spaceId)
            )))
            .flatMap(function((cloudFoundryClient, applicationId) -> requestUpdateApplicationName(cloudFoundryClient, applicationId, request.getNewName())))
            .then()
            .transform(OperationsLogging.log("Rename Application"))
            .checkpoint();
    }

    @Override
    public Mono<Void> restage(RestageApplicationRequest request) {
        return Mono
            .when(this.cloudFoundryClient, this.spaceId)
            .flatMap(function((cloudFoundryClient, spaceId) -> Mono.when(
                Mono.just(cloudFoundryClient),
                getApplicationId(cloudFoundryClient, request.getName(), spaceId)
            )))
            .flatMap(function((cloudFoundryClient, applicationId) -> restageApplication(cloudFoundryClient, request.getName(), applicationId, request.getStagingTimeout(), request.getStartupTimeout())))
            .transform(OperationsLogging.log("Restage Application"))
            .checkpoint();
    }

    @Override
    public Mono<Void> restart(RestartApplicationRequest request) {
        return Mono
            .when(this.cloudFoundryClient, this.spaceId)
            .flatMap(function((cloudFoundryClient, spaceId) -> Mono.when(
                Mono.just(cloudFoundryClient),
                getApplication(cloudFoundryClient, request.getName(), spaceId)
            )))
            .flatMap(function((cloudFoundryClient, resource) -> Mono.when(
                Mono.just(cloudFoundryClient),
                stopApplicationIfNotStopped(cloudFoundryClient, resource)
            )))
            .flatMap(function((cloudFoundryClient, stoppedApplication) -> startApplicationAndWait(cloudFoundryClient, request.getName(), ResourceUtils.getId(stoppedApplication),
                request.getStagingTimeout(), request.getStartupTimeout())))
            .transform(OperationsLogging.log("Restart Application"))
            .checkpoint();
    }

    @Override
    public Mono<Void> restartInstance(RestartApplicationInstanceRequest request) {
        return Mono
            .when(this.cloudFoundryClient, this.spaceId)
            .flatMap(function((cloudFoundryClient, spaceId) -> Mono.when(
                Mono.just(cloudFoundryClient),
                getApplicationId(cloudFoundryClient, request.getName(), spaceId)
            )))
            .flatMap(function((cloudFoundryClient, applicationId) -> requestTerminateApplicationInstance(cloudFoundryClient, applicationId, String.valueOf(request.getInstanceIndex()))))
            .transform(OperationsLogging.log("Restart Application Instance"))
            .checkpoint();
    }

    @Override
    public Mono<Void> scale(ScaleApplicationRequest request) {
        return Mono
            .when(this.cloudFoundryClient, this.spaceId)
            .filter(predicate((cloudFoundryClient, spaceId) -> areModifiersPresent(request)))
            .flatMap(function((cloudFoundryClient, spaceId) -> Mono.when(
                Mono.just(cloudFoundryClient),
                getApplicationId(cloudFoundryClient, request.getName(), spaceId)
            )))
            .flatMap(function((cloudFoundryClient, applicationId) -> Mono.when(
                Mono.just(cloudFoundryClient),
                requestUpdateApplicationScale(cloudFoundryClient, applicationId, request.getDiskLimit(), request.getInstances(), request.getMemoryLimit())
            )))
            .filter(predicate((cloudFoundryClient, resource) -> isRestartRequired(request, resource)))
            .flatMap(function((cloudFoundryClient, resource) -> restartApplication(cloudFoundryClient, request.getName(), ResourceUtils.getId(resource), request.getStagingTimeout(),
                request.getStartupTimeout())))
            .transform(OperationsLogging.log("Scale Application"))
            .checkpoint();
    }

    @Override
    public Mono<Void> setEnvironmentVariable(SetEnvironmentVariableApplicationRequest request) {
        return Mono
            .when(this.cloudFoundryClient, this.spaceId)
            .flatMap(function((cloudFoundryClient, spaceId) -> Mono.when(
                Mono.just(cloudFoundryClient),
                getApplication(cloudFoundryClient, request.getName(), spaceId)
            )))
            .flatMap(function((cloudFoundryClient, resource) -> requestUpdateApplicationEnvironment(cloudFoundryClient, ResourceUtils.getId(resource), addToEnvironment(getEnvironment(resource),
                request.getVariableName(), request.getVariableValue()))))
            .then()
            .transform(OperationsLogging.log("Set Application Environment Variable"))
            .checkpoint();
    }

    @Override
    public Mono<Void> setHealthCheck(SetApplicationHealthCheckRequest request) {
        return Mono
            .when(this.cloudFoundryClient, this.spaceId)
            .flatMap(function((cloudFoundryClient, spaceId) -> Mono.when(
                Mono.just(cloudFoundryClient),
                getApplicationId(cloudFoundryClient, request.getName(), spaceId)
            )))
            .flatMap(function((cloudFoundryClient, applicationId) -> requestUpdateApplicationHealthCheckType(cloudFoundryClient, applicationId, request.getType())))
            .then()
            .transform(OperationsLogging.log("Set Application Health Check"))
            .checkpoint();
    }

    @Override
    public Mono<Boolean> sshEnabled(ApplicationSshEnabledRequest request) {
        return Mono
            .when(this.cloudFoundryClient, this.spaceId)
            .flatMap(function((cloudFoundryClient, spaceId) -> getApplication(cloudFoundryClient, request.getName(), spaceId)))
            .map(applicationResource -> ResourceUtils.getEntity(applicationResource).getEnableSsh())
            .transform(OperationsLogging.log("Is Application SSH Enabled"))
            .checkpoint();
    }

    @Override
    public Mono<Void> start(StartApplicationRequest request) {
        return Mono
            .when(this.cloudFoundryClient, this.spaceId)
            .flatMap(function((cloudFoundryClient, spaceId) -> Mono.when(
                Mono.just(cloudFoundryClient),
                getApplicationIdWhere(cloudFoundryClient, request.getName(), spaceId, isNotIn(STARTED_STATE))
            )))
            .flatMap(function((cloudFoundryClient, applicationId) -> startApplicationAndWait(cloudFoundryClient, request.getName(), applicationId, request.getStagingTimeout(),
                request.getStartupTimeout())))
            .transform(OperationsLogging.log("Start Application"))
            .checkpoint();
    }

    @Override
    public Mono<Void> stop(StopApplicationRequest request) {
        return Mono
            .when(this.cloudFoundryClient, this.spaceId)
            .flatMap(function((cloudFoundryClient, spaceId) -> Mono.when(
                Mono.just(cloudFoundryClient),
                getApplicationIdWhere(cloudFoundryClient, request.getName(), spaceId, isNotIn(STOPPED_STATE))
            )))
            .flatMap(function(DefaultApplications::stopApplication))
            .then()
            .transform(OperationsLogging.log("Stop Application"))
            .checkpoint();
    }

    @Override
    public Mono<Void> unsetEnvironmentVariable(UnsetEnvironmentVariableApplicationRequest request) {
        return Mono
            .when(this.cloudFoundryClient, this.spaceId)
            .flatMap(function((cloudFoundryClient, spaceId) -> Mono.when(
                Mono.just(cloudFoundryClient),
                getApplication(cloudFoundryClient, request.getName(), spaceId)
            )))
            .flatMap(function((cloudFoundryClient, resource) -> requestUpdateApplicationEnvironment(cloudFoundryClient, ResourceUtils.getId(resource), removeFromEnvironment(getEnvironment(resource),
                request.getVariableName()))))
            .then()
            .transform(OperationsLogging.log("Unset Application Environment Variable"))
            .checkpoint();
    }

    private static Map<String, Object> addToEnvironment(Map<String, Object> environment, String variableName, Object variableValue) {
        return FluentMap.<String, Object>builder()
            .entries(environment)
            .entry(variableName, variableValue)
            .build();
    }

    private static boolean areModifiersPresent(ScaleApplicationRequest request) {
        return request.getMemoryLimit() != null || request.getDiskLimit() != null || request.getInstances() != null;
    }

    private static Flux<String> associateDefaultDomain(CloudFoundryClient cloudFoundryClient, String applicationId, List<DomainSummary> availableDomains, ApplicationManifest manifest,
                                                       RandomWords randomWords, String spaceId) {
        return getDefaultDomainId(cloudFoundryClient)
            .flatMapMany(domainId -> getPushRouteIdFromDomain(cloudFoundryClient, availableDomains, domainId, manifest, randomWords, spaceId))
            .flatMap(routeId -> requestAssociateRoute(cloudFoundryClient, applicationId, routeId))
            .map(ResourceUtils::getId);
    }

    private static Mono<Void> bindServices(CloudFoundryClient cloudFoundryClient, String applicationId, ApplicationManifest manifest, String spaceId) {
        if (manifest.getServices() == null || manifest.getServices().size() == 0) {
            return Mono.empty();
        }

        return Flux.fromIterable(manifest.getServices())
            .flatMap(serviceInstanceName -> getServiceId(cloudFoundryClient, serviceInstanceName, spaceId))
            .flatMap(serviceInstanceId -> requestCreateServiceBinding(cloudFoundryClient, applicationId, serviceInstanceId)
                .onErrorResume(ExceptionUtils.statusCode(CF_SERVICE_ALREADY_BOUND), t -> Mono.empty()))
            .then();
    }

    private static BiFunction<String, String, String> collectStates() {
        return (totalState, instanceState) -> {
            if ("RUNNING".equals(instanceState) || "RUNNING".equals(totalState)) {
                return "RUNNING";
            }

            if ("FLAPPING".equals(instanceState) || "CRASHED".equals(instanceState)) {
                return "FAILED";
            }

            return totalState;
        };
    }

    private static ApplicationEvent convertToApplicationEvent(EventResource resource) {
        EventEntity entity = resource.getEntity();
        Date timestamp = null;
        try {
            timestamp = DateUtils.parseFromIso8601(entity.getTimestamp());
        } catch (IllegalArgumentException iae) {
            // do not set time
        }
        return ApplicationEvent.builder()
            .actor(entity.getActorName())
            .description(eventDescription(getMetadataRequest(entity), "instances", "memory", "state", "environment_json"))
            .id(ResourceUtils.getId(resource))
            .event(entity.getType())
            .time(timestamp)
            .build();
    }

    private static Mono<Void> copyBits(CloudFoundryClient cloudFoundryClient, Duration completionTimeout, String sourceApplicationId, String targetApplicationId) {
        return requestCopyBits(cloudFoundryClient, sourceApplicationId, targetApplicationId)
            .flatMap(job -> JobUtils.waitForCompletion(cloudFoundryClient, completionTimeout, job));
    }

    private static Mono<Void> deleteRoute(CloudFoundryClient cloudFoundryClient, String routeId, Duration completionTimeout) {
        return requestDeleteRoute(cloudFoundryClient, routeId)
            .flatMap(job -> JobUtils.waitForCompletion(cloudFoundryClient, completionTimeout, job));
    }

    private static Mono<Void> deleteRoutes(CloudFoundryClient cloudFoundryClient, Duration completionTimeout, Optional<List<org.cloudfoundry.client.v2.routes.Route>> routes) {
        return routes
            .map(Flux::fromIterable)
            .orElse(Flux.empty())
            .map(org.cloudfoundry.client.v2.routes.Route::getId)
            .flatMap(routeId -> deleteRoute(cloudFoundryClient, routeId, completionTimeout))
            .then();
    }

    private static String deriveHostname(String host, ApplicationManifest manifest, RandomWords randomWords) {
        if (Optional.ofNullable(manifest.getNoHostname()).orElse(false)) {
            return "";
        } else if (host != null) {
            return host;
        } else if (Optional.ofNullable(manifest.getRandomRoute()).orElse(false)) {
            return String.join("-", manifest.getName(), randomWords.getAdjective(), randomWords.getNoun());
        } else {
            return manifest.getName();
        }
    }

    private static Statistics emptyApplicationStatistics() {
        return Statistics.builder()
            .usage(emptyApplicationUsage())
            .build();
    }

    private static Usage emptyApplicationUsage() {
        return Usage.builder()
            .build();
    }

    private static InstanceStatistics emptyInstanceStats() {
        return InstanceStatistics.builder()
            .statistics(emptyApplicationStatistics())
            .build();
    }

    private static String eventDescription(Map<String, Object> request, String... entryNames) {
        if (request == null) {
            return "";
        }
        boolean first = true;
        StringBuilder sb = new StringBuilder();
        for (String entryName : entryNames) {
            Object value = request.get(entryName);
            if (value == null) {
                continue;
            }
            if (!first) {
                sb.append(", ");
            }
            first = false;
            sb.append(entryName).append(": ").append(String.valueOf(value));
        }
        return sb.toString();
    }

    private static Flux<SpaceApplicationSummary> extractApplications(GetSpaceSummaryResponse getSpaceSummaryResponse) {
        return Flux.fromIterable(getSpaceSummaryResponse.getApplications());
    }

    private static Mono<AbstractApplicationResource> getApplication(CloudFoundryClient cloudFoundryClient, String application, String spaceId) {
        return requestApplications(cloudFoundryClient, application, spaceId)
            .single()
            .onErrorResume(NoSuchElementException.class, t -> ExceptionUtils.illegalArgument("Application %s does not exist", application));
    }

    private static Mono<String> getApplicationId(CloudFoundryClient cloudFoundryClient, String application, String spaceId) {
        return getApplication(cloudFoundryClient, application, spaceId)
            .map(ResourceUtils::getId);
    }

    private static Mono<String> getApplicationId(CloudFoundryClient cloudFoundryClient, ApplicationManifest manifest, String spaceId, String stackId) {
        return requestApplications(cloudFoundryClient, manifest.getName(), spaceId)
            .singleOrEmpty()
            .flatMap(application -> {
                Map<String, Object> environmentJsons = new HashMap<>(ResourceUtils.getEntity(application).getEnvironmentJsons());
                Optional.ofNullable(manifest.getEnvironmentVariables()).ifPresent(environmentJsons::putAll);

                return requestUpdateApplication(cloudFoundryClient, ResourceUtils.getId(application), environmentJsons, manifest, stackId)
                    .map(ResourceUtils::getId);
            })
            .switchIfEmpty(requestCreateApplication(cloudFoundryClient, manifest, spaceId, stackId)
                .map(ResourceUtils::getId));
    }

    private static Mono<String> getApplicationIdFromOrgSpace(CloudFoundryClient cloudFoundryClient, String application, String spaceId, String organization, String space) {
        return
            getSpaceOrganizationId(cloudFoundryClient, spaceId)
                .flatMap(organizationId -> organization != null ? getOrganizationId(cloudFoundryClient, organization) : Mono.just(organizationId))
                .flatMap(organizationId -> space != null ? getSpaceId(cloudFoundryClient, organizationId, space) : Mono.just(spaceId))
                .flatMap(spaceId1 -> getApplicationId(cloudFoundryClient, application, spaceId1));
    }

    private static Mono<String> getApplicationIdWhere(CloudFoundryClient cloudFoundryClient, String application, String spaceId, Predicate<AbstractApplicationResource> predicate) {
        return getApplication(cloudFoundryClient, application, spaceId)
            .filter(predicate)
            .map(ResourceUtils::getId);
    }

    private static Mono<ApplicationInstancesResponse> getApplicationInstances(CloudFoundryClient cloudFoundryClient, String applicationId) {
        return requestApplicationInstances(cloudFoundryClient, applicationId)
            .onErrorResume(ExceptionUtils.statusCode(CF_BUILDPACK_COMPILED_FAILED, CF_INSTANCES_ERROR, CF_STAGING_NOT_FINISHED, CF_STAGING_TIME_EXPIRED),
                t -> Mono.just(ApplicationInstancesResponse.builder().build()));
    }

    private static Mono<List<RouteResource>> getApplicationRoutes(CloudFoundryClient cloudFoundryClient, String applicationId) {
        return requestApplicationRoutes(cloudFoundryClient, applicationId)
            .collectList();
    }

    private static Mono<ApplicationStatisticsResponse> getApplicationStatistics(CloudFoundryClient cloudFoundryClient, String applicationId) {
        return requestApplicationStatistics(cloudFoundryClient, applicationId)
            .onErrorResume(ExceptionUtils.statusCode(CF_APP_STOPPED_STATS_ERROR), t -> Mono.just(ApplicationStatisticsResponse.builder().build()));
    }

    private static Mono<Tuple4<SummaryApplicationResponse, GetStackResponse, List<InstanceDetail>, List<String>>>
    getAuxiliaryContent(CloudFoundryClient cloudFoundryClient, AbstractApplicationResource applicationResource) {

        String applicationId = ResourceUtils.getId(applicationResource);
        String stackId = ResourceUtils.getEntity(applicationResource).getStackId();

        return Mono
            .when(
                getApplicationStatistics(cloudFoundryClient, applicationId),
                requestApplicationSummary(cloudFoundryClient, applicationId),
                getApplicationInstances(cloudFoundryClient, applicationId)
            )
            .flatMap(function((applicationStatisticsResponse, summaryApplicationResponse, applicationInstancesResponse) -> Mono.when(
                Mono.just(summaryApplicationResponse),
                requestStack(cloudFoundryClient, stackId),
                toInstanceDetailList(applicationInstancesResponse, applicationStatisticsResponse),
                toUrls(summaryApplicationResponse.getRoutes())
            )));
    }

    private static String getBuildpack(SummaryApplicationResponse response) {
        return Optional
            .ofNullable(response.getBuildpack())
            .orElse(response.getDetectedBuildpack());
    }

    private static Mono<String> getDefaultDomainId(CloudFoundryClient cloudFoundryClient) {
        return requestSharedDomains(cloudFoundryClient)
            .map(ResourceUtils::getId)
            .next()
            .switchIfEmpty(ExceptionUtils.illegalArgument("No default domain found"));
    }

    private static String getDomainId(List<DomainSummary> availableDomains, String domainName) {
        return availableDomains.stream()
            .filter(domain -> domainName.equals(domain.getName()))
            .map(DomainSummary::getId)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(String.format("Domain %s not found", domainName)));
    }

    private static Map<String, Object> getEnvironment(AbstractApplicationResource resource) {
        return ResourceUtils.getEntity(resource).getEnvironmentJsons();
    }

    private static Flux<LogMessage> getLogs(Mono<DopplerClient> dopplerClient, String applicationId, Boolean recent) {
        if (Optional.ofNullable(recent).orElse(false)) {
            return requestLogsRecent(dopplerClient, applicationId)
                .filter(e -> EventType.LOG_MESSAGE == e.getEventType())
                .map(Envelope::getLogMessage)
                .collectSortedList(LOG_MESSAGE_COMPARATOR)
                .flatMapIterable(d -> d);
        } else {
            return requestLogsStream(dopplerClient, applicationId)
                .filter(e -> EventType.LOG_MESSAGE == e.getEventType())
                .map(Envelope::getLogMessage)
                .compose(SortingUtils.timespan(LOG_MESSAGE_COMPARATOR, LOG_MESSAGE_TIMESPAN));
        }
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> getMetadataRequest(EventEntity entity) {
        Map<String, Optional<Object>> metadata = Optional
            .ofNullable(entity.getMetadatas())
            .orElse(Collections.emptyMap());

        if (metadata.get("request") != null) {
            return metadata.get("request")
                .map(m -> (Map<String, Object>) m)
                .orElse(Collections.emptyMap());
        } else {
            return Collections.emptyMap();
        }
    }

    private static Mono<Optional<List<org.cloudfoundry.client.v2.routes.Route>>> getOptionalRoutes(CloudFoundryClient cloudFoundryClient, boolean deleteRoutes, String applicationId) {
        if (deleteRoutes) {
            return getRoutes(cloudFoundryClient, applicationId)
                .map(Optional::of);
        } else {
            return Mono.just(Optional.empty());
        }
    }

    private static Mono<Optional<String>> getOptionalStackId(CloudFoundryClient cloudFoundryClient, String stack) {
        return Optional.ofNullable(stack)
            .map(stack1 -> getStackId(cloudFoundryClient, stack1)
                .map(Optional::of))
            .orElse(Mono.just(Optional.empty()));
    }

    private static Mono<OrganizationResource> getOrganization(CloudFoundryClient cloudFoundryClient, String organization) {
        return requestOrganizations(cloudFoundryClient, organization)
            .single()
            .onErrorResume(NoSuchElementException.class, t -> ExceptionUtils.illegalArgument("Organization %s not found", organization));
    }

    private static Mono<String> getOrganizationId(CloudFoundryClient cloudFoundryClient, String organization) {
        return getOrganization(cloudFoundryClient, organization)
            .map(ResourceUtils::getId);
    }

    private static Mono<SpaceResource> getOrganizationSpaceByName(CloudFoundryClient cloudFoundryClient, String organizationId, String space) {
        return requestOrganizationSpacesByName(cloudFoundryClient, organizationId, space)
            .single()
            .onErrorResume(NoSuchElementException.class, t -> ExceptionUtils.illegalArgument("Space %s not found", space));
    }

    private static Flux<String> getPushRouteIdFromDomain(CloudFoundryClient cloudFoundryClient, List<DomainSummary> availableDomains, String domainId, ApplicationManifest manifest,
                                                         RandomWords randomWords, String spaceId) {
        if (isTcpDomain(availableDomains, domainId)) {
            return requestCreateTcpRoute(cloudFoundryClient, domainId, spaceId)
                .map(ResourceUtils::getId)
                .flux();
        }

        List<String> hosts;
        if (Optional.ofNullable(manifest.getNoHostname()).orElse(false)) {
            hosts = Collections.singletonList("");
        } else if (Optional.ofNullable(manifest.getRandomRoute()).orElse(false) && manifest.getHosts() == null) {
            hosts = Collections.singletonList(String.join("-", manifest.getName(), randomWords.getAdjective(), randomWords.getNoun()));
        } else if (manifest.getHosts() == null || manifest.getHosts().isEmpty()) {
            hosts = Collections.singletonList(manifest.getName());
        } else {
            hosts = manifest.getHosts();
        }

        return Flux.fromIterable(hosts)
            .flatMap(host -> getRouteId(cloudFoundryClient, domainId, host, manifest.getRoutePath())
                .switchIfEmpty(requestCreateRoute(cloudFoundryClient, domainId, host, manifest.getRoutePath(), spaceId)
                    .map(ResourceUtils::getId)));
    }

    private static Flux<String> getPushRouteIdFromRoute(CloudFoundryClient cloudFoundryClient, List<DomainSummary> availableDomains, ApplicationManifest manifest, RandomWords randomWords,
                                                        String spaceId) {
        return Flux.fromIterable(manifest.getRoutes())
            .flatMap(route -> RouteUtils.decomposeRoute(availableDomains, route.getRoute(), manifest.getRoutePath()))
            .flatMap(decomposedRoute -> {
                String domainId = getDomainId(availableDomains, decomposedRoute.getDomain());
                if (isTcpDomain(availableDomains, domainId)) {
                    return getRouteIdForTcpRoute(cloudFoundryClient, decomposedRoute, domainId, manifest, spaceId);
                } else {
                    return getRouteIdForHttpRoute(cloudFoundryClient, decomposedRoute, domainId, manifest, randomWords, spaceId);
                }
            });
    }

    private static Mono<String> getRouteId(CloudFoundryClient cloudFoundryClient, String domainId, String host, String routePath) {
        return requestRoutes(cloudFoundryClient, domainId, host, null, routePath)
            .filter(resource -> isIdentical(host, ResourceUtils.getEntity(resource).getHost()))
            .filter(resource -> isIdentical(Optional.ofNullable(routePath).orElse(""), ResourceUtils.getEntity(resource).getPath()))
            .singleOrEmpty()
            .map(ResourceUtils::getId);
    }

    private static Mono<String> getRouteIdForHttpRoute(CloudFoundryClient cloudFoundryClient, DecomposedRoute decomposedRoute, String domainId, ApplicationManifest manifest, RandomWords randomWords,
                                                       String spaceId) {
        String derivedHost = deriveHostname(decomposedRoute.getHost(), manifest, randomWords);
        return getRouteId(cloudFoundryClient, domainId, derivedHost, decomposedRoute.getPath())
            .switchIfEmpty(requestCreateRoute(cloudFoundryClient, domainId, derivedHost, decomposedRoute.getPath(), spaceId)
                .map(ResourceUtils::getId));
    }

    private static Mono<String> getRouteIdForTcpRoute(CloudFoundryClient cloudFoundryClient, DecomposedRoute decomposedRoute, String domainId, ApplicationManifest manifest, String spaceId) {
        if (Optional.ofNullable(manifest.getRandomRoute()).orElse(false)) {
            return requestCreateTcpRoute(cloudFoundryClient, domainId, spaceId)
                .map(ResourceUtils::getId);
        }

        return getTcpRouteId(cloudFoundryClient, domainId, decomposedRoute.getPort())
            .switchIfEmpty(requestCreateTcpRoute(cloudFoundryClient, domainId, decomposedRoute.getPort(), spaceId)
                .map(ResourceUtils::getId));
    }

    private static Mono<List<org.cloudfoundry.client.v2.routes.Route>> getRoutes(CloudFoundryClient cloudFoundryClient, String applicationId) {
        return requestApplicationSummary(cloudFoundryClient, applicationId)
            .map(SummaryApplicationResponse::getRoutes);
    }

    private static Mono<Tuple2<Optional<List<org.cloudfoundry.client.v2.routes.Route>>, String>> getRoutesAndApplicationId(CloudFoundryClient cloudFoundryClient, DeleteApplicationRequest request,
                                                                                                                           String spaceId, boolean deleteRoutes) {
        return getApplicationId(cloudFoundryClient, request.getName(), spaceId)
            .flatMap(applicationId -> getOptionalRoutes(cloudFoundryClient, deleteRoutes, applicationId)
                .and(Mono.just(applicationId)));
    }

    private static Mono<String> getServiceId(CloudFoundryClient cloudFoundryClient, String serviceInstanceName, String spaceId) {
        return requestListServiceInstances(cloudFoundryClient, serviceInstanceName, spaceId)
            .map(ResourceUtils::getId)
            .single()
            .onErrorResume(NoSuchElementException.class, t -> ExceptionUtils.illegalArgument("Service instance %s could not be found", serviceInstanceName));
    }

    private static Mono<String> getSpaceId(CloudFoundryClient cloudFoundryClient, String organizationId, String space) {
        return getOrganizationSpaceByName(cloudFoundryClient, organizationId, space)
            .map(ResourceUtils::getId);
    }

    private static Mono<String> getSpaceOrganizationId(CloudFoundryClient cloudFoundryClient, String spaceId) {
        return requestSpace(cloudFoundryClient, spaceId)
            .map(response -> ResourceUtils.getEntity(response).getOrganizationId());
    }

    private static Mono<String> getStackId(CloudFoundryClient cloudFoundryClient, String stack) {
        return requestStacks(cloudFoundryClient, stack)
            .map(ResourceUtils::getId)
            .single()
            .onErrorResume(NoSuchElementException.class, t -> ExceptionUtils.illegalArgument("Stack %s does not exist", stack));
    }

    private static Mono<String> getStackName(CloudFoundryClient cloudFoundryClient, String stackId) {
        return requestStack(cloudFoundryClient, stackId)
            .map(getStackResponse -> getStackResponse.getEntity().getName());
    }

    private static Mono<String> getTcpRouteId(CloudFoundryClient cloudFoundryClient, String domainId, Integer port) {
        return requestRoutes(cloudFoundryClient, domainId, null, port, null)
            .singleOrEmpty()
            .map(ResourceUtils::getId);
    }

    private static boolean isIdentical(String s, String t) {
        return s == null ? t == null : s.equals(t);
    }

    private static Predicate<String> isInstanceComplete() {
        return state -> "RUNNING".equals(state) || "FAILED".equals(state);
    }

    private static Predicate<AbstractApplicationResource> isNotIn(String expectedState) {
        return resource -> isNotIn(resource, expectedState);
    }

    private static boolean isNotIn(AbstractApplicationResource resource, String expectedState) {
        return !expectedState.equals(ResourceUtils.getEntity(resource).getState());
    }

    private static boolean isRestartRequired(ScaleApplicationRequest request, AbstractApplicationResource applicationResource) {
        return (request.getDiskLimit() != null || request.getMemoryLimit() != null)
            && STARTED_STATE.equals(ResourceUtils.getEntity(applicationResource).getState());
    }

    private static Predicate<String> isRunning() {
        return "RUNNING"::equals;
    }

    private static Predicate<String> isStaged() {
        return "STAGED"::equals;
    }

    private static Predicate<String> isStagingComplete() {
        return state -> "STAGED".equals(state) || "FAILED".equals(state);
    }

    private static boolean isTcpDomain(List<DomainSummary> availableDomains, String domainId) {
        List<String> tcpDomainIds = availableDomains.stream()
            .filter(domain -> "tcp".equals(domain.getType()))
            .map(DomainSummary::getId)
            .collect(Collectors.toList());

        return tcpDomainIds.contains(domainId);
    }

    private static Mono<List<DomainSummary>> listAvailableDomains(CloudFoundryClient cloudFoundryClient, String organizationId) {
        return requestListPrivateDomains(cloudFoundryClient, organizationId)
            .map(DefaultApplications::toDomain)
            .mergeWith(requestListSharedDomains(cloudFoundryClient)
                .map(DefaultApplications::toDomain))
            .collectList();
    }

    private static Mono<Void> prepareDomainsAndRoutes(CloudFoundryClient cloudFoundryClient, String applicationId, List<DomainSummary> availableDomains, ApplicationManifest manifest,
                                                      List<RouteResource> existingRoutes, RandomWords randomWords, String spaceId) {
        if (Optional.ofNullable(manifest.getNoRoute()).orElse(false)) {
            return Flux.fromIterable(existingRoutes)
                .map(ResourceUtils::getId)
                .flatMap(routeId -> requestRemoveRouteFromApplication(cloudFoundryClient, applicationId, routeId))
                .then();
        }

        if (manifest.getRoutes() == null) {
            if (manifest.getDomains() == null) {
                if (existingRoutes.isEmpty()) {
                    return associateDefaultDomain(cloudFoundryClient, applicationId, availableDomains, manifest, randomWords, spaceId)
                        .then();
                }
                return Mono.empty(); // A route already exists for the application, do nothing
            }
            return Flux.fromIterable(manifest.getDomains())
                .flatMap(domain -> getPushRouteIdFromDomain(cloudFoundryClient, availableDomains, getDomainId(availableDomains, domain), manifest, randomWords, spaceId)
                    .flatMap(routeId -> requestAssociateRoute(cloudFoundryClient, applicationId, routeId)))
                .then();
        }

        List<String> existingRouteIds = existingRoutes.stream()
            .map(ResourceUtils::getId)
            .collect(Collectors.toList());

        return getPushRouteIdFromRoute(cloudFoundryClient, availableDomains, manifest, randomWords, spaceId)
            .filter(routeId -> !existingRouteIds.contains(routeId))
            .flatMapSequential(routeId -> requestAssociateRoute(cloudFoundryClient, applicationId, routeId), 1)
            .then();
    }

    private static Flux<Void> pushApplication(CloudFoundryClient cloudFoundryClient, List<DomainSummary> availableDomains, ApplicationManifest manifest, RandomWords randomWords,
                                              PushApplicationManifestRequest request, String spaceId) {
        return getOptionalStackId(cloudFoundryClient, manifest.getStack())
            .flatMapMany(stackId -> getApplicationId(cloudFoundryClient, manifest, spaceId, stackId.orElse(null)))
            .flatMap(applicationId -> Mono.when(
                Mono.just(applicationId),
                getApplicationRoutes(cloudFoundryClient, applicationId),
                ResourceMatchingUtils.getMatchedResources(cloudFoundryClient, manifest.getPath())
            ))
            .flatMap(function((applicationId, existingRoutes, matchedResources) -> prepareDomainsAndRoutes(cloudFoundryClient, applicationId, availableDomains, manifest, existingRoutes,
                randomWords, spaceId)
                .then(Mono.just(Tuples.of(applicationId, matchedResources)))))
            .flatMap(function((applicationId, matchedResources) -> Mono
                .when(
                    uploadApplicationAndWait(cloudFoundryClient, applicationId, manifest.getPath(), matchedResources, request.getStagingTimeout()),
                    bindServices(cloudFoundryClient, applicationId, manifest, spaceId)
                )
                .then(Mono.just(applicationId))))
            .flatMap(applicationId -> stopAndStartApplication(cloudFoundryClient, applicationId, manifest.getName(), request));
    }

    private static Flux<Void> pushDocker(CloudFoundryClient cloudFoundryClient, List<DomainSummary> availableDomains, ApplicationManifest manifest, RandomWords randomWords,
                                         PushApplicationManifestRequest request, String spaceId) {
        return getOptionalStackId(cloudFoundryClient, manifest.getStack())
            .flatMapMany(stackId -> getApplicationId(cloudFoundryClient, manifest, spaceId, stackId.orElse(null)))
            .flatMap(applicationId -> Mono.when(
                Mono.just(applicationId),
                getApplicationRoutes(cloudFoundryClient, applicationId)
            ))
            .flatMap(function((applicationId, existingRoutes) -> prepareDomainsAndRoutes(cloudFoundryClient, applicationId, availableDomains, manifest, existingRoutes, randomWords, spaceId)
                .then(Mono.just(applicationId))))
            .flatMap(applicationId -> bindServices(cloudFoundryClient, applicationId, manifest, spaceId)
                .then(Mono.just(applicationId)))
            .flatMap(applicationId -> stopAndStartApplication(cloudFoundryClient, applicationId, manifest.getName(), request));
    }

    private static Map<String, Object> removeFromEnvironment(Map<String, Object> environment, String variableName) {
        Map<String, Object> modified = new HashMap<>(environment);
        modified.remove(variableName);
        return modified;
    }

    private static Mono<Void> removeServiceBindings(CloudFoundryClient cloudFoundryClient, String applicationId) {
        return requestListServiceBindings(cloudFoundryClient, applicationId)
            .map(ResourceUtils::getId)
            .flatMap(serviceBindingId -> requestRemoveServiceBinding(cloudFoundryClient, applicationId, serviceBindingId))
            .then();
    }

    private static Mono<ApplicationEnvironmentResponse> requestApplicationEnvironment(CloudFoundryClient cloudFoundryClient, String applicationId) {
        return cloudFoundryClient.applicationsV2()
            .environment(ApplicationEnvironmentRequest.builder()
                .applicationId(applicationId)
                .build());
    }

    private static Mono<ApplicationInstancesResponse> requestApplicationInstances(CloudFoundryClient cloudFoundryClient, String applicationId) {
        return cloudFoundryClient.applicationsV2()
            .instances(ApplicationInstancesRequest.builder()
                .applicationId(applicationId)
                .build());
    }

    private static Flux<RouteResource> requestApplicationRoutes(CloudFoundryClient cloudFoundryClient, String applicationId) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.applicationsV2()
                .listRoutes(ListApplicationRoutesRequest.builder()
                    .applicationId(applicationId)
                    .page(page)
                    .build()));
    }

    private static Mono<ApplicationStatisticsResponse> requestApplicationStatistics(CloudFoundryClient cloudFoundryClient, String applicationId) {
        return cloudFoundryClient.applicationsV2()
            .statistics(ApplicationStatisticsRequest.builder()
                .applicationId(applicationId)
                .build());
    }

    private static Mono<SummaryApplicationResponse> requestApplicationSummary(CloudFoundryClient cloudFoundryClient, String applicationId) {
        return cloudFoundryClient.applicationsV2()
            .summary(SummaryApplicationRequest.builder()
                .applicationId(applicationId)
                .build());
    }

    private static Flux<AbstractApplicationResource> requestApplications(CloudFoundryClient cloudFoundryClient, String application, String spaceId) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.spaces()
                .listApplications(ListSpaceApplicationsRequest.builder()
                    .name(application)
                    .spaceId(spaceId)
                    .page(page)
                    .build()))
            .cast(AbstractApplicationResource.class);
    }

    private static Mono<AssociateApplicationRouteResponse> requestAssociateRoute(CloudFoundryClient cloudFoundryClient, String applicationId, String routeId) {
        return cloudFoundryClient.applicationsV2()
            .associateRoute(AssociateApplicationRouteRequest.builder()
                .applicationId(applicationId)
                .routeId(routeId)
                .build());
    }

    private static Mono<CopyApplicationResponse> requestCopyBits(CloudFoundryClient cloudFoundryClient, String sourceApplicationId, String targetApplicationId) {
        return cloudFoundryClient.applicationsV2()
            .copy(CopyApplicationRequest.builder()
                .applicationId(targetApplicationId)
                .sourceApplicationId(sourceApplicationId)
                .build());
    }

    private static Mono<CreateApplicationResponse> requestCreateApplication(CloudFoundryClient cloudFoundryClient, ApplicationManifest manifest, String spaceId, String stackId) {
        CreateApplicationRequest.Builder builder = CreateApplicationRequest.builder()
            .buildpack(manifest.getBuildpack())
            .command(manifest.getCommand())
            .diskQuota(manifest.getDisk())
            .environmentJsons(manifest.getEnvironmentVariables())
            .healthCheckHttpEndpoint(manifest.getHealthCheckHttpEndpoint())
            .healthCheckTimeout(manifest.getTimeout())
            .healthCheckType(Optional.ofNullable(manifest.getHealthCheckType()).map(ApplicationHealthCheck::getValue).orElse(null))
            .instances(manifest.getInstances())
            .memory(manifest.getMemory())
            .name(manifest.getName())
            .spaceId(spaceId)
            .stackId(stackId);

        Optional.ofNullable(manifest.getDockerImage())
            .ifPresent(dockerImage -> builder
                .diego(true)
                .dockerImage(dockerImage));

        return cloudFoundryClient.applicationsV2()
            .create(builder.build());
    }

    private static Mono<CreateRouteResponse> requestCreateRoute(CloudFoundryClient cloudFoundryClient, String domainId, String host, String routePath, String spaceId) {
        return cloudFoundryClient.routes()
            .create(org.cloudfoundry.client.v2.routes.CreateRouteRequest.builder()
                .domainId(domainId)
                .host(host)
                .path(routePath)
                .spaceId(spaceId)
                .build());
    }

    private static Mono<CreateServiceBindingResponse> requestCreateServiceBinding(CloudFoundryClient cloudFoundryClient, String applicationId, String serviceInstanceId) {
        return cloudFoundryClient.serviceBindingsV2()
            .create(CreateServiceBindingRequest.builder()
                .applicationId(applicationId)
                .serviceInstanceId(serviceInstanceId)
                .build());
    }

    private static Mono<CreateRouteResponse> requestCreateTcpRoute(CloudFoundryClient cloudFoundryClient, String domainId, String spaceId) {
        return cloudFoundryClient.routes()
            .create(org.cloudfoundry.client.v2.routes.CreateRouteRequest.builder()
                .domainId(domainId)
                .generatePort(true)
                .spaceId(spaceId)
                .build());
    }

    private static Mono<CreateRouteResponse> requestCreateTcpRoute(CloudFoundryClient cloudFoundryClient, String domainId, Integer port, String spaceId) {
        return cloudFoundryClient.routes()
            .create(org.cloudfoundry.client.v2.routes.CreateRouteRequest.builder()
                .domainId(domainId)
                .port(port)
                .spaceId(spaceId)
                .build());
    }

    private static Mono<Void> requestDeleteApplication(CloudFoundryClient cloudFoundryClient, String applicationId) {
        return cloudFoundryClient.applicationsV2()
            .delete(org.cloudfoundry.client.v2.applications.DeleteApplicationRequest.builder()
                .applicationId(applicationId)
                .build());
    }

    private static Mono<DeleteRouteResponse> requestDeleteRoute(CloudFoundryClient cloudFoundryClient, String routeId) {
        return cloudFoundryClient.routes()
            .delete(DeleteRouteRequest.builder()
                .async(true)
                .routeId(routeId)
                .build());
    }

    private static Flux<EventResource> requestEvents(String applicationId, CloudFoundryClient cloudFoundryClient) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.events()
                .list(ListEventsRequest.builder()
                    .actee(applicationId)
                    .orderDirection(OrderDirection.DESCENDING)
                    .resultsPerPage(50)
                    .page(page)
                    .build()));
    }

    private static Mono<AbstractApplicationResource> requestGetApplication(CloudFoundryClient cloudFoundryClient, String applicationId) {
        return cloudFoundryClient.applicationsV2()
            .get(org.cloudfoundry.client.v2.applications.GetApplicationRequest.builder()
                .applicationId(applicationId)
                .build())
            .cast(AbstractApplicationResource.class);
    }

    private static Flux<PrivateDomainResource> requestListPrivateDomains(CloudFoundryClient cloudFoundryClient, String organizationId) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.organizations()
                .listPrivateDomains(ListOrganizationPrivateDomainsRequest.builder()
                    .organizationId(organizationId)
                    .page(page)
                    .build()));
    }

    private static Flux<ServiceBindingResource> requestListServiceBindings(CloudFoundryClient cloudFoundryClient, String applicationId) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.applicationsV2()
                .listServiceBindings(ListApplicationServiceBindingsRequest.builder()
                    .applicationId(applicationId)
                    .page(page)
                    .build()));
    }

    private static Flux<UnionServiceInstanceResource> requestListServiceInstances(CloudFoundryClient cloudFoundryClient, String serviceInstanceName, String spaceId) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.spaces()
                .listServiceInstances(ListSpaceServiceInstancesRequest.builder()
                    .page(page)
                    .returnUserProvidedServiceInstances(true)
                    .name(serviceInstanceName)
                    .spaceId(spaceId)
                    .build()));
    }

    private static Flux<SharedDomainResource> requestListSharedDomains(CloudFoundryClient cloudFoundryClient) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.sharedDomains()
                .list(ListSharedDomainsRequest.builder()
                    .page(page)
                    .build()));
    }

    private static Flux<Envelope> requestLogsRecent(Mono<DopplerClient> dopplerClient, String applicationId) {
        return dopplerClient
            .flatMapMany(client -> client
                .recentLogs(RecentLogsRequest.builder()
                    .applicationId(applicationId)
                    .build()));
    }

    private static Flux<Envelope> requestLogsStream(Mono<DopplerClient> dopplerClient, String applicationId) {
        return dopplerClient
            .flatMapMany(client -> client
                .stream(StreamRequest.builder()
                    .applicationId(applicationId)
                    .build()));
    }

    private static Flux<SpaceResource> requestOrganizationSpacesByName(CloudFoundryClient cloudFoundryClient, String organizationId, String space) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.organizations()
                .listSpaces(ListOrganizationSpacesRequest.builder()
                    .name(space)
                    .organizationId(organizationId)
                    .page(page)
                    .build()));
    }

    private static Flux<OrganizationResource> requestOrganizations(CloudFoundryClient cloudFoundryClient, String organization) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.organizations()
                .list(ListOrganizationsRequest.builder()
                    .name(organization)
                    .page(page)
                    .build()));
    }

    private static Mono<Void> requestRemoveRouteFromApplication(CloudFoundryClient cloudFoundryClient, String applicationId, String routeId) {
        return cloudFoundryClient.applicationsV2()
            .removeRoute(RemoveApplicationRouteRequest.builder()
                .applicationId(applicationId)
                .routeId(routeId)
                .build());
    }

    private static Mono<Void> requestRemoveServiceBinding(CloudFoundryClient cloudFoundryClient, String applicationId, String serviceBindingId) {
        return cloudFoundryClient.applicationsV2()
            .removeServiceBinding(RemoveApplicationServiceBindingRequest.builder()
                .applicationId(applicationId)
                .serviceBindingId(serviceBindingId)
                .build());
    }

    private static Mono<RestageApplicationResponse> requestRestageApplication(CloudFoundryClient cloudFoundryClient, String applicationId) {
        return cloudFoundryClient.applicationsV2()
            .restage(org.cloudfoundry.client.v2.applications.RestageApplicationRequest.builder()
                .applicationId(applicationId)
                .build());
    }

    private static Flux<RouteResource> requestRoutes(CloudFoundryClient cloudFoundryClient, String domainId, String host, Integer port, String routePath) {
        ListRoutesRequest.Builder requestBuilder = ListRoutesRequest.builder()
            .domainId(domainId);
        Optional.ofNullable(host).ifPresent(requestBuilder::host);
        Optional.ofNullable(routePath).ifPresent(requestBuilder::path);
        Optional.ofNullable(port).ifPresent(requestBuilder::port);

        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.routes()
                .list(requestBuilder
                    .page(page)
                    .build()));
    }

    private static Flux<SharedDomainResource> requestSharedDomains(CloudFoundryClient cloudFoundryClient) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.sharedDomains()
                .list(ListSharedDomainsRequest.builder()
                    .page(page)
                    .build()));
    }

    private static Mono<GetSpaceResponse> requestSpace(CloudFoundryClient cloudFoundryClient, String spaceId) {
        return cloudFoundryClient.spaces()
            .get(GetSpaceRequest.builder()
                .spaceId(spaceId)
                .build());
    }

    private static Mono<GetSpaceSummaryResponse> requestSpaceSummary(CloudFoundryClient cloudFoundryClient, String spaceId) {
        return cloudFoundryClient.spaces()
            .getSummary(GetSpaceSummaryRequest.builder()
                .spaceId(spaceId)
                .build());
    }

    private static Mono<GetStackResponse> requestStack(CloudFoundryClient cloudFoundryClient, String stackId) {
        return cloudFoundryClient.stacks()
            .get(GetStackRequest.builder()
                .stackId(stackId)
                .build());
    }

    private static Flux<StackResource> requestStacks(CloudFoundryClient cloudFoundryClient, String stack) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.stacks()
                .list(ListStacksRequest.builder()
                    .page(page)
                    .name(stack)
                    .build()));
    }

    private static Mono<Void> requestTerminateApplicationInstance(CloudFoundryClient cloudFoundryClient, String applicationId, String instanceIndex) {
        return cloudFoundryClient.applicationsV2()
            .terminateInstance(TerminateApplicationInstanceRequest.builder()
                .applicationId(applicationId)
                .index(instanceIndex)
                .build());
    }

    private static Mono<AbstractApplicationResource> requestUpdateApplication(CloudFoundryClient cloudFoundryClient, String applicationId, Map<String, Object> environmentJsons,
                                                                              ApplicationManifest manifest, String stackId) {

        return requestUpdateApplication(cloudFoundryClient, applicationId, builder -> {
            builder
                .buildpack(manifest.getBuildpack())
                .command(manifest.getCommand())
                .diskQuota(manifest.getDisk())
                .environmentJsons(environmentJsons)
                .healthCheckHttpEndpoint(manifest.getHealthCheckHttpEndpoint())
                .healthCheckTimeout(manifest.getTimeout())
                .healthCheckType(Optional.ofNullable(manifest.getHealthCheckType()).map(ApplicationHealthCheck::getValue).orElse(null))
                .instances(manifest.getInstances())
                .memory(manifest.getMemory())
                .name(manifest.getName())
                .stackId(stackId);

            Optional.ofNullable(manifest.getDockerImage())
                .ifPresent(dockerImage -> builder
                    .diego(true)
                    .dockerImage(dockerImage));

            return builder;
        });
    }

    private static Mono<AbstractApplicationResource> requestUpdateApplication(CloudFoundryClient cloudFoundryClient, String applicationId, UnaryOperator<UpdateApplicationRequest.Builder> modifier) {
        return cloudFoundryClient.applicationsV2()
            .update(modifier.apply(UpdateApplicationRequest.builder()
                .applicationId(applicationId))
                .build())
            .cast(AbstractApplicationResource.class);
    }

    private static Mono<AbstractApplicationResource> requestUpdateApplicationEnvironment(CloudFoundryClient cloudFoundryClient, String applicationId, Map<String, Object> environment) {
        return requestUpdateApplication(cloudFoundryClient, applicationId, builder -> builder.environmentJsons(environment));
    }

    private static Mono<AbstractApplicationResource> requestUpdateApplicationHealthCheckType(CloudFoundryClient cloudFoundryClient, String applicationId, ApplicationHealthCheck type) {
        return requestUpdateApplication(cloudFoundryClient, applicationId, builder -> builder.healthCheckType(type.getValue()));
    }

    private static Mono<AbstractApplicationResource> requestUpdateApplicationName(CloudFoundryClient cloudFoundryClient, String applicationId, String name) {
        return requestUpdateApplication(cloudFoundryClient, applicationId, builder -> builder.name(name));
    }

    private static Mono<AbstractApplicationResource> requestUpdateApplicationScale(CloudFoundryClient cloudFoundryClient, String applicationId, Integer disk, Integer instances, Integer memory) {
        return requestUpdateApplication(cloudFoundryClient, applicationId, builder -> builder.diskQuota(disk).instances(instances).memory(memory));
    }

    private static Mono<AbstractApplicationResource> requestUpdateApplicationSsh(CloudFoundryClient cloudFoundryClient, String applicationId, Boolean enabled) {
        return requestUpdateApplication(cloudFoundryClient, applicationId, builder -> builder.enableSsh(enabled));
    }

    private static Mono<AbstractApplicationResource> requestUpdateApplicationState(CloudFoundryClient cloudFoundryClient, String applicationId, String state) {
        return requestUpdateApplication(cloudFoundryClient, applicationId, builder -> builder.state(state));
    }

    private static Mono<UploadApplicationResponse> requestUploadApplication(CloudFoundryClient cloudFoundryClient, String applicationId, Path application, List<ResourceMatchingUtils
        .ArtifactMetadata> matchedResources) {
        UploadApplicationRequest request = matchedResources.stream()
            .reduce(UploadApplicationRequest.builder()
                    .application(application)
                    .applicationId(applicationId)
                    .async(true),
                (builder, artifactMetadata) -> builder.resource(org.cloudfoundry.client.v2.applications.Resource.builder()
                    .hash(artifactMetadata.getHash())
                    .mode(artifactMetadata.getPermissions())
                    .path(artifactMetadata.getPath())
                    .size(artifactMetadata.getSize())
                    .build()),
                (a, b) -> a)
            .build();

        return cloudFoundryClient.applicationsV2()
            .upload(request);
    }

    private static Mono<Void> restageApplication(CloudFoundryClient cloudFoundryClient, String application, String applicationId, Duration stagingTimeout, Duration startupTimeout) {
        return requestRestageApplication(cloudFoundryClient, applicationId)
            .flatMap(response -> waitForStaging(cloudFoundryClient, application, applicationId, stagingTimeout))
            .then(waitForRunning(cloudFoundryClient, application, applicationId, startupTimeout));
    }

    private static Mono<Void> restartApplication(CloudFoundryClient cloudFoundryClient, String application, String applicationId, Duration stagingTimeout, Duration startupTimeout) {
        return stopApplication(cloudFoundryClient, applicationId)
            .then(startApplicationAndWait(cloudFoundryClient, application, applicationId, stagingTimeout, startupTimeout));
    }

    private static Predicate<AbstractApplicationResource> sshEnabled(Boolean enabled) {
        return resource -> enabled.equals(ResourceUtils.getEntity(resource).getEnableSsh());
    }

    private static Mono<Void> startApplicationAndWait(CloudFoundryClient cloudFoundryClient, String application, String applicationId, Duration stagingTimeout, Duration startupTimeout) {
        return requestUpdateApplicationState(cloudFoundryClient, applicationId, STARTED_STATE)
            .flatMap(response -> waitForStaging(cloudFoundryClient, application, applicationId, stagingTimeout))
            .then(waitForRunning(cloudFoundryClient, application, applicationId, startupTimeout));
    }

    private static Mono<Void> stopAndStartApplication(CloudFoundryClient cloudFoundryClient, String applicationId, String name, PushApplicationManifestRequest request) {
        return stopApplication(cloudFoundryClient, applicationId)
            .filter(resource -> !Optional.ofNullable(request.getNoStart()).orElse(false))
            .flatMap(resource -> startApplicationAndWait(cloudFoundryClient, name, applicationId, request.getStagingTimeout(), request.getStartupTimeout()));
    }

    private static Mono<AbstractApplicationResource> stopApplication(CloudFoundryClient cloudFoundryClient, String applicationId) {
        return requestUpdateApplicationState(cloudFoundryClient, applicationId, STOPPED_STATE);
    }

    private static Mono<AbstractApplicationResource> stopApplicationIfNotStopped(CloudFoundryClient cloudFoundryClient, AbstractApplicationResource resource) {
        return isNotIn(resource, STOPPED_STATE) ? stopApplication(cloudFoundryClient, ResourceUtils.getId(resource)) : Mono.just(resource);
    }

    private static ApplicationDetail toApplicationDetail(SummaryApplicationResponse summaryApplicationResponse, GetStackResponse getStackResponse, List<InstanceDetail> instanceDetails,
                                                         List<String> urls) {
        return ApplicationDetail.builder()
            .buildpack(getBuildpack(summaryApplicationResponse))
            .diskQuota(summaryApplicationResponse.getDiskQuota())
            .id(summaryApplicationResponse.getId())
            .instanceDetails(instanceDetails)
            .instances(summaryApplicationResponse.getInstances())
            .lastUploaded(toDate(summaryApplicationResponse.getPackageUpdatedAt()))
            .memoryLimit(summaryApplicationResponse.getMemory())
            .name(summaryApplicationResponse.getName())
            .requestedState(summaryApplicationResponse.getState())
            .runningInstances(summaryApplicationResponse.getRunningInstances())
            .stack(getStackResponse.getEntity().getName())
            .urls(urls)
            .build();
    }

    private static ApplicationEnvironments toApplicationEnvironments(ApplicationEnvironmentResponse response) {
        return ApplicationEnvironments.builder()
            .running(response.getRunningEnvironmentJsons())
            .staging(response.getStagingEnvironmentJsons())
            .systemProvided(response.getSystemEnvironmentJsons())
            .userProvided(response.getEnvironmentJsons())
            .build();
    }

    private static Mono<ApplicationManifest> toApplicationManifest(SummaryApplicationResponse response, String stackName) {
        ApplicationManifest.Builder manifestBuilder = ApplicationManifest.builder()
            .buildpack(response.getBuildpack())
            .command(response.getCommand())
            .disk(response.getDiskQuota())
            .environmentVariables(response.getEnvironmentJsons())
            .healthCheckHttpEndpoint(response.getHealthCheckHttpEndpoint())
            .healthCheckType(ApplicationHealthCheck.from(response.getHealthCheckType()))
            .instances(response.getInstances())
            .memory(response.getMemory())
            .name(response.getName())
            .stack(stackName)
            .timeout(response.getHealthCheckTimeout());

        for (org.cloudfoundry.client.v2.routes.Route route : Optional.ofNullable(response.getRoutes()).orElse(Collections.emptyList())) {
            manifestBuilder.route(Route.builder()
                .route(toUrl(route))
                .build());
        }

        if (Optional.ofNullable(response.getRoutes()).orElse(Collections.emptyList()).isEmpty()) {
            manifestBuilder.noRoute(true);
        }

        for (ServiceInstance service : Optional.ofNullable(response.getServices()).orElse(Collections.emptyList())) {
            Optional.ofNullable(service.getName()).ifPresent(manifestBuilder::service);
        }

        return Mono
            .just(manifestBuilder
                .build());
    }

    private static ApplicationSummary toApplicationSummary(SpaceApplicationSummary spaceApplicationSummary) {
        return ApplicationSummary.builder()
            .diskQuota(spaceApplicationSummary.getDiskQuota())
            .id(spaceApplicationSummary.getId())
            .instances(spaceApplicationSummary.getInstances())
            .memoryLimit(spaceApplicationSummary.getMemory())
            .name(spaceApplicationSummary.getName())
            .requestedState(spaceApplicationSummary.getState())
            .runningInstances(spaceApplicationSummary.getRunningInstances())
            .urls(spaceApplicationSummary.getUrls())
            .build();
    }

    private static Date toDate(String date) {
        return date == null ? null : DateUtils.parseFromIso8601(date);
    }

    private static Date toDate(Double date) {
        return date == null ? null : DateUtils.parseSecondsFromEpoch(date);
    }

    private static DomainSummary toDomain(SharedDomainResource resource) {
        SharedDomainEntity entity = ResourceUtils.getEntity(resource);

        return DomainSummary.builder()
            .id(ResourceUtils.getId(resource))
            .name(entity.getName())
            .type(entity.getRouterGroupType())
            .build();
    }

    private static DomainSummary toDomain(PrivateDomainResource resource) {
        PrivateDomainEntity entity = ResourceUtils.getEntity(resource);

        return DomainSummary.builder()
            .id(ResourceUtils.getId(resource))
            .name(entity.getName())
            .build();
    }

    private static ApplicationHealthCheck toHealthCheck(AbstractApplicationResource resource) {
        String type = resource.getEntity().getHealthCheckType();

        if (ApplicationHealthCheck.HTTP.getValue().equals(type)) {
            return ApplicationHealthCheck.HTTP;
        } else if (ApplicationHealthCheck.NONE.getValue().equals(type)) {
            return ApplicationHealthCheck.NONE;
        } else if (ApplicationHealthCheck.PORT.getValue().equals(type)) {
            return ApplicationHealthCheck.PORT;
        } else if (ApplicationHealthCheck.PROCESS.getValue().equals(type)) {
            return ApplicationHealthCheck.PROCESS;
        } else {
            return null;
        }
    }

    private static InstanceDetail toInstanceDetail(Map.Entry<String, ApplicationInstanceInfo> entry, ApplicationStatisticsResponse statisticsResponse) {
        InstanceStatistics instanceStatistics = Optional.ofNullable(statisticsResponse.getInstances().get(entry.getKey())).orElse(emptyInstanceStats());
        Statistics stats = Optional.ofNullable(instanceStatistics.getStatistics()).orElse(emptyApplicationStatistics());
        Usage usage = Optional.ofNullable(stats.getUsage()).orElse(emptyApplicationUsage());

        return InstanceDetail.builder()
            .index(entry.getKey())
            .state(entry.getValue().getState())
            .since(toDate(entry.getValue().getSince()))
            .cpu(usage.getCpu())
            .memoryUsage(usage.getMemory())
            .diskUsage(usage.getDisk())
            .diskQuota(stats.getDiskQuota())
            .memoryQuota(stats.getMemoryQuota())
            .build();
    }

    private static Mono<List<InstanceDetail>> toInstanceDetailList(ApplicationInstancesResponse instancesResponse, ApplicationStatisticsResponse statisticsResponse) {
        return Flux
            .fromIterable(instancesResponse.getInstances().entrySet())
            .map(entry -> toInstanceDetail(entry, statisticsResponse))
            .collectList();
    }

    private static String toUrl(org.cloudfoundry.client.v2.routes.Route route) {
        StringBuilder sb = new StringBuilder();
        if (route.getHost() != null && !route.getHost().isEmpty()) {
            sb.append(route.getHost()).append(".");
        }
        Optional.ofNullable(route.getDomain().getName())
            .ifPresent(sb::append);

        if (route.getPort() == null) {
            Optional.ofNullable(route.getPath())
                .ifPresent(sb::append);
        } else {
            sb.append(":").append(route.getPort());
        }

        return sb.toString();
    }

    private static Mono<List<String>> toUrls(List<org.cloudfoundry.client.v2.routes.Route> routes) {
        return Flux
            .fromIterable(routes)
            .map(DefaultApplications::toUrl)
            .collectList();
    }

    private static Mono<Void> uploadApplicationAndWait(CloudFoundryClient cloudFoundryClient, String applicationId, Path application, List<ResourceMatchingUtils.ArtifactMetadata> matchedResources,
                                                       Duration stagingTimeout) {
        return Mono
            .defer(() -> {
                if (matchedResources.isEmpty()) {
                    return requestUploadApplication(cloudFoundryClient, applicationId, application, matchedResources);
                } else {
                    List<String> paths = matchedResources.stream()
                        .map(ResourceMatchingUtils.ArtifactMetadata::getPath)
                        .collect(Collectors.toList());

                    return FileUtils.compress(application, p -> !paths.contains(p))
                        .flatMap(filteredApplication -> requestUploadApplication(cloudFoundryClient, applicationId, filteredApplication, matchedResources)
                            .doOnTerminate((v, t) -> {
                                try {
                                    Files.delete(filteredApplication);
                                } catch (IOException e) {
                                    throw Exceptions.propagate(e);
                                }
                            }));
                }
            })
            .flatMap(job -> JobUtils.waitForCompletion(cloudFoundryClient, stagingTimeout, job));
    }

    private static Mono<Void> waitForRunning(CloudFoundryClient cloudFoundryClient, String application, String applicationId, Duration startupTimeout) {
        Duration timeout = Optional.ofNullable(startupTimeout).orElse(Duration.ofMinutes(5));

        return requestApplicationInstances(cloudFoundryClient, applicationId)
            .flatMapMany(response -> Flux.fromIterable(response.getInstances().values()))
            .map(ApplicationInstanceInfo::getState)
            .reduce("UNKNOWN", collectStates())
            .filter(isInstanceComplete())
            .repeatWhenEmpty(exponentialBackOff(Duration.ofSeconds(1), Duration.ofSeconds(15), timeout))
            .filter(isRunning())
            .switchIfEmpty(ExceptionUtils.illegalState("Application %s failed during start", application))
            .onErrorResume(DelayTimeoutException.class, t -> ExceptionUtils.illegalState("Application %s timed out during start", application))
            .then();
    }

    private static Mono<Void> waitForStaging(CloudFoundryClient cloudFoundryClient, String application, String applicationId, Duration stagingTimeout) {
        Duration timeout = Optional.ofNullable(stagingTimeout).orElse(Duration.ofMinutes(15));

        return requestGetApplication(cloudFoundryClient, applicationId)
            .map(response -> ResourceUtils.getEntity(response).getPackageState())
            .filter(isStagingComplete())
            .repeatWhenEmpty(exponentialBackOff(Duration.ofSeconds(1), Duration.ofSeconds(15), timeout))
            .filter(isStaged())
            .switchIfEmpty(ExceptionUtils.illegalState("Application %s failed during staging", application))
            .onErrorResume(DelayTimeoutException.class, t -> ExceptionUtils.illegalState("Application %s timed out during staging", application))
            .then();
    }

}
