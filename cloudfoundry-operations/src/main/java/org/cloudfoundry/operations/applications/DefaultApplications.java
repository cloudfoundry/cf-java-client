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

package org.cloudfoundry.operations.applications;

import static org.cloudfoundry.client.v3.LifecycleType.BUILDPACK;
import static org.cloudfoundry.util.DelayUtils.exponentialBackOff;
import static org.cloudfoundry.util.tuple.TupleUtils.function;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.applications.AbstractApplicationResource;
import org.cloudfoundry.client.v2.applications.ApplicationEntity;
import org.cloudfoundry.client.v2.applications.AssociateApplicationRouteRequest;
import org.cloudfoundry.client.v2.applications.AssociateApplicationRouteResponse;
import org.cloudfoundry.client.v2.applications.CopyApplicationRequest;
import org.cloudfoundry.client.v2.applications.CopyApplicationResponse;
import org.cloudfoundry.client.v2.applications.CreateApplicationRequest;
import org.cloudfoundry.client.v2.applications.CreateApplicationResponse;
import org.cloudfoundry.client.v2.applications.DockerCredentials;
import org.cloudfoundry.client.v2.applications.InstanceStatistics;
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
import org.cloudfoundry.client.v3.BuildpackData;
import org.cloudfoundry.client.v3.CnbData;
import org.cloudfoundry.client.v3.Lifecycle;
import org.cloudfoundry.client.v3.LifecycleData;
import org.cloudfoundry.client.v3.Relationship;
import org.cloudfoundry.client.v3.Resource;
import org.cloudfoundry.client.v3.ToOneRelationship;
import org.cloudfoundry.client.v3.applications.ApplicationFeature;
import org.cloudfoundry.client.v3.applications.ApplicationResource;
import org.cloudfoundry.client.v3.applications.GetApplicationEnvironmentRequest;
import org.cloudfoundry.client.v3.applications.GetApplicationEnvironmentResponse;
import org.cloudfoundry.client.v3.applications.GetApplicationProcessRequest;
import org.cloudfoundry.client.v3.applications.GetApplicationProcessResponse;
import org.cloudfoundry.client.v3.applications.GetApplicationProcessStatisticsRequest;
import org.cloudfoundry.client.v3.applications.GetApplicationProcessStatisticsResponse;
import org.cloudfoundry.client.v3.applications.GetApplicationResponse;
import org.cloudfoundry.client.v3.applications.GetApplicationSshEnabledRequest;
import org.cloudfoundry.client.v3.applications.GetApplicationSshEnabledResponse;
import org.cloudfoundry.client.v3.applications.ListApplicationRoutesRequest;
import org.cloudfoundry.client.v3.applications.ListApplicationsRequest;
import org.cloudfoundry.client.v3.applications.SetApplicationCurrentDropletRequest;
import org.cloudfoundry.client.v3.applications.UpdateApplicationEnvironmentVariablesRequest;
import org.cloudfoundry.client.v3.applications.UpdateApplicationEnvironmentVariablesResponse;
import org.cloudfoundry.client.v3.applications.UpdateApplicationFeatureRequest;
import org.cloudfoundry.client.v3.auditevents.AuditEventResource;
import org.cloudfoundry.client.v3.auditevents.ListAuditEventsRequest;
import org.cloudfoundry.client.v3.builds.BuildState;
import org.cloudfoundry.client.v3.builds.CreateBuildRequest;
import org.cloudfoundry.client.v3.builds.CreateBuildResponse;
import org.cloudfoundry.client.v3.builds.GetBuildRequest;
import org.cloudfoundry.client.v3.builds.GetBuildResponse;
import org.cloudfoundry.client.v3.packages.BitsData;
import org.cloudfoundry.client.v3.packages.CreatePackageRequest;
import org.cloudfoundry.client.v3.packages.CreatePackageResponse;
import org.cloudfoundry.client.v3.packages.DockerData;
import org.cloudfoundry.client.v3.packages.GetPackageRequest;
import org.cloudfoundry.client.v3.packages.GetPackageResponse;
import org.cloudfoundry.client.v3.packages.PackageRelationships;
import org.cloudfoundry.client.v3.packages.PackageState;
import org.cloudfoundry.client.v3.packages.PackageType;
import org.cloudfoundry.client.v3.packages.UploadPackageRequest;
import org.cloudfoundry.client.v3.processes.HealthCheck;
import org.cloudfoundry.client.v3.processes.HealthCheckType;
import org.cloudfoundry.client.v3.processes.ProcessState;
import org.cloudfoundry.client.v3.processes.ProcessStatisticsResource;
import org.cloudfoundry.client.v3.processes.ProcessUsage;
import org.cloudfoundry.client.v3.processes.UpdateProcessRequest;
import org.cloudfoundry.client.v3.resourcematch.MatchedResource;
import org.cloudfoundry.client.v3.spaces.ApplyManifestRequest;
import org.cloudfoundry.client.v3.stacks.GetStackRequest;
import org.cloudfoundry.client.v3.stacks.GetStackResponse;
import org.cloudfoundry.client.v3.stacks.ListStacksRequest;
import org.cloudfoundry.client.v3.stacks.StackResource;
import org.cloudfoundry.client.v3.tasks.CancelTaskRequest;
import org.cloudfoundry.client.v3.tasks.CancelTaskResponse;
import org.cloudfoundry.client.v3.tasks.CreateTaskRequest;
import org.cloudfoundry.client.v3.tasks.CreateTaskResponse;
import org.cloudfoundry.client.v3.tasks.TaskResource;
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
import org.cloudfoundry.util.JobUtils;
import org.cloudfoundry.util.PaginationUtils;
import org.cloudfoundry.util.ResourceMatchingUtils;
import org.cloudfoundry.util.ResourceMatchingUtilsV3;
import org.cloudfoundry.util.ResourceUtils;
import org.cloudfoundry.util.SortingUtils;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuple4;
import reactor.util.function.Tuples;

public final class DefaultApplications implements Applications {

    private static final int CF_APP_STOPPED_STATS_ERROR = 200003;

    private static final int CF_BUILDPACK_COMPILED_FAILED = 170004;

    private static final int CF_INSTANCES_ERROR = 220001;

    private static final int CF_SERVICE_ALREADY_BOUND = 90003;

    private static final int CF_STAGING_ERROR = 170001;

    private static final int CF_STAGING_NOT_FINISHED = 170002;

    private static final int CF_STAGING_TIME_EXPIRED = 170007;

    private static final int CF_INSUFFICIENT_RESOURCES = 170008;

    private static final String[] ENTRY_FIELDS_CRASH = {"index", "reason", "exit_description"};

    private static final String[] ENTRY_FIELDS_NORMAL = {
        "instances", "memory", "state", "environment_json"
    };

    private static final Comparator<LogMessage> LOG_MESSAGE_COMPARATOR =
            Comparator.comparing(LogMessage::getTimestamp);

    private static final Duration LOG_MESSAGE_TIMESPAN = Duration.ofMillis(500);

    private static final int MAX_NUMBER_OF_RECENT_EVENTS = 50;

    private static final String STARTED_STATE = "STARTED";

    private static final String STOPPED_STATE = "STOPPED";

    private static final String APP_FEATURE_SSH = "ssh";

    private final CloudFoundryClient cloudFoundryClient;

    private final DopplerClient dopplerClient;

    private final RandomWords randomWords;

    private final String spaceId;

    public DefaultApplications(
            CloudFoundryClient cloudFoundryClient, DopplerClient dopplerClient, String spaceId) {
        this(cloudFoundryClient, dopplerClient, new WordListRandomWords(), spaceId);
    }

    /**
     * @deprecated Please use {@link DefaultApplications(CloudFoundryClient, DopplerClient, String)} instead.
     */
    @Deprecated
    public DefaultApplications(
            Mono<CloudFoundryClient> cloudFoundryClient,
            Mono<DopplerClient> dopplerClient,
            Mono<String> spaceId) {
        this(cloudFoundryClient.block(), dopplerClient.block(), spaceId.block());
    }

    DefaultApplications(
            CloudFoundryClient cloudFoundryClient,
            DopplerClient dopplerClient,
            RandomWords randomWords,
            String spaceId) {
        this.cloudFoundryClient = cloudFoundryClient;
        this.dopplerClient = dopplerClient;
        this.randomWords = randomWords;
        this.spaceId = spaceId;
    }

    @Override
    public Mono<Void> copySource(CopySourceApplicationRequest request) {
        return Mono.zip(
                        getApplicationId(request.getName()),
                        getApplicationIdFromOrgSpace(
                                request.getTargetName(),
                                this.spaceId,
                                request.getTargetOrganization(),
                                request.getTargetSpace()))
                .flatMap(
                        function(
                                (sourceApplicationId, targetApplicationId) ->
                                        copyBits(
                                                        request.getStagingTimeout(),
                                                        sourceApplicationId,
                                                        targetApplicationId)
                                                .thenReturn(targetApplicationId)))
                .filter(
                        targetApplicationId ->
                                Optional.ofNullable(request.getRestart()).orElse(false))
                .flatMap(
                        targetApplicationId ->
                                restartApplication(
                                        request.getTargetName(),
                                        targetApplicationId,
                                        request.getStagingTimeout(),
                                        request.getStartupTimeout()))
                .transform(OperationsLogging.log("Copy Application Source"))
                .checkpoint();
    }

    @Override
    public Mono<Void> delete(DeleteApplicationRequest request) {
        return getRoutesAndApplicationId(
                        request, Optional.ofNullable(request.getDeleteRoutes()).orElse(false))
                .flatMap(
                        function(
                                (routes, applicationId) ->
                                        deleteRoutes(request.getCompletionTimeout(), routes)
                                                .thenReturn(applicationId)))
                .delayUntil(this::removeServiceBindings)
                .flatMap(this::requestDeleteApplication)
                .transform(OperationsLogging.log("Delete Application"))
                .checkpoint();
    }

    @Override
    public Mono<Void> disableSsh(DisableApplicationSshRequest request) {
        return getApplicationIdV3(request.getName())
                // TODO dgarnier: is this correct?
                .filterWhen(applicationId -> getSshEnabled(applicationId))
                .flatMap(applicationId -> requestUpdateApplicationSsh(applicationId, false))
                .then()
                .transform(OperationsLogging.log("Disable Application SSH"))
                .checkpoint();
    }

    @Override
    public Mono<Void> enableSsh(EnableApplicationSshRequest request) {
        return getApplicationIdV3(request.getName())
                .filterWhen(applicationId -> getSshEnabled(applicationId).map(enabled -> !enabled))
                .flatMap(applicationId -> requestUpdateApplicationSsh(applicationId, true))
                .then()
                .transform(OperationsLogging.log("Enable Application SSH"))
                .checkpoint();
    }

    @Override
    public Mono<ApplicationDetail> get(GetApplicationRequest request) {
        return getApplicationV3(request.getName())
                .flatMap(this::getAuxiliaryContent)
                .map(function(DefaultApplications::toApplicationDetail))
                .transform(OperationsLogging.log("Get Application"))
                .checkpoint();
    }

    // TODO dgarnier: manifest v3?
    @Override
    public Mono<ApplicationManifest> getApplicationManifest(GetApplicationManifestRequest request) {
        return getApplicationId(request.getName())
                .flatMap(
                        applicationId ->
                                Mono.zip(
                                        Mono.just(applicationId),
                                        requestApplicationSummary(applicationId)))
                .flatMap(
                        function(
                                (applicationId, summary) ->
                                        Mono.zip(
                                                getApplicationBuildpacks(applicationId),
                                                Mono.just(summary),
                                                getStackName(summary.getStackId()))))
                .flatMap(function(this::toApplicationManifest))
                .transform(OperationsLogging.log("Get Application Manifest"))
                .checkpoint();
    }

    @Override
    public Mono<ApplicationEnvironments> getEnvironments(
            GetApplicationEnvironmentsRequest request) {
        return getApplicationIdV3(request.getName())
                .flatMap(applicationId -> requestApplicationEnvironment(applicationId))
                .map(DefaultApplications::toApplicationEnvironments)
                .transform(OperationsLogging.log("Get Application Environments"))
                .checkpoint();
    }

    @Override
    public Flux<ApplicationEvent> getEvents(GetApplicationEventsRequest request) {
        return getApplicationIdV3(request.getName())
                .flatMapMany(
                        applicationId ->
                                requestEvents(applicationId)
                                        .take(
                                                Optional.ofNullable(request.getMaxNumberOfEvents())
                                                        .orElse(MAX_NUMBER_OF_RECENT_EVENTS)))
                .map(DefaultApplications::convertToApplicationEvent)
                .transform(OperationsLogging.log("Get Application Events"))
                .checkpoint();
    }

    @Override
    public Mono<ApplicationHealthCheck> getHealthCheck(GetApplicationHealthCheckRequest request) {
        return getApplicationV3(request.getName())
                .map(ApplicationResource::getId)
                .flatMap(this::requestApplicationWebProcess)
                .map(GetApplicationProcessResponse::getHealthCheck)
                .map(HealthCheck::getType)
                .map(DefaultApplications::toHealthCheck)
                .transform(OperationsLogging.log("Get Application Health Check"))
                .checkpoint();
    }

    @Override
    public Flux<ApplicationSummary> list() {
        return requestSpaceSummary()
                .flatMapMany(DefaultApplications::extractApplications)
                .map(DefaultApplications::toApplicationSummary)
                .transform(OperationsLogging.log("List Applications"))
                .checkpoint();
    }

    @Override
    public Flux<Task> listTasks(ListApplicationTasksRequest request) {
        return getApplicationIdV3(request.getName())
                .flatMapMany(applicationId -> requestListTasks(applicationId))
                .map(DefaultApplications::toTask)
                .transform(OperationsLogging.log("List Application Tasks"))
                .checkpoint();
    }

    @Override
    public Flux<LogMessage> logs(LogsRequest request) {
        return getApplicationIdV3(request.getName())
                .flatMapMany(applicationId -> getLogs(applicationId, request.getRecent()))
                .transform(OperationsLogging.log("Get Application Logs"))
                .checkpoint();
    }

    @Override
    public Flux<ApplicationLog> logs(ApplicationLogsRequest request) {
        return logs(LogsRequest.builder()
                        .name(request.getName())
                        .recent(request.getRecent())
                        .build())
                .map(
                        logMessage ->
                                ApplicationLog.builder()
                                        .sourceId(logMessage.getApplicationId())
                                        .sourceType(logMessage.getSourceType())
                                        .instanceId(logMessage.getSourceInstance())
                                        .message(logMessage.getMessage())
                                        .timestamp(logMessage.getTimestamp())
                                        .logType(
                                                ApplicationLogType.from(
                                                        logMessage.getMessageType().name()))
                                        .build());
    }

    @Override
    @SuppressWarnings("deprecation")
    public Mono<Void> push(PushApplicationRequest request) {
        ApplicationManifest.Builder builder =
                ApplicationManifest.builder()
                        .buildpacks(request.getBuildpacks())
                        .command(request.getCommand())
                        .disk(request.getDiskQuota())
                        .docker(
                                Docker.builder()
                                        .image(request.getDockerImage())
                                        .password(request.getDockerPassword())
                                        .username(request.getDockerUsername())
                                        .build())
                        .healthCheckHttpEndpoint(request.getHealthCheckHttpEndpoint())
                        .healthCheckType(request.getHealthCheckType())
                        .instances(request.getInstances())
                        .memory(request.getMemory())
                        .name(request.getName())
                        .noHostname(request.getNoHostname())
                        .noRoute(request.getNoRoute())
                        .path(
                                Optional.ofNullable(request.getPath())
                                        .orElse(request.getApplication()))
                        .randomRoute(request.getRandomRoute())
                        .routePath(request.getRoutePath())
                        .stack(request.getStack())
                        .timeout(request.getTimeout());

        Optional.ofNullable(request.getDomain()).ifPresent(builder::domain);

        Optional.ofNullable(request.getHost()).ifPresent(builder::host);

        return pushManifest(
                        PushApplicationManifestRequest.builder()
                                .manifest(builder.build())
                                .dockerPassword(request.getDockerPassword())
                                .dockerUsername(request.getDockerUsername())
                                .noStart(request.getNoStart())
                                .stagingTimeout(request.getStagingTimeout())
                                .startupTimeout(request.getStartupTimeout())
                                .build())
                .transform(OperationsLogging.log("Push"))
                .checkpoint();
    }

    // TODO dgarnier: pass orgId to constructor?
    @Override
    public Mono<Void> pushManifest(PushApplicationManifestRequest request) {
        return getSpaceOrganizationId(this.spaceId)
                .flatMap(organizationId -> listAvailableDomains(organizationId))
                .flatMapMany(
                        availableDomains ->
                                Flux.fromIterable(request.getManifests())
                                        .flatMap(
                                                manifest -> {
                                                    if (manifest.getPath() != null) {
                                                        return pushApplication(
                                                                availableDomains,
                                                                manifest,
                                                                this.randomWords,
                                                                request);
                                                    } else if (!manifest.getDocker()
                                                            .getImage()
                                                            .isEmpty()) {
                                                        return pushDocker(
                                                                availableDomains,
                                                                manifest,
                                                                this.randomWords,
                                                                request);
                                                    } else {
                                                        throw new IllegalStateException(
                                                                "One of application or"
                                                                        + " dockerImage must be"
                                                                        + " supplied");
                                                    }
                                                }))
                .then()
                .transform(OperationsLogging.log("Push Manifest"))
                .checkpoint();
    }

    @Override
    public Mono<Void> pushManifestV3(PushManifestV3Request request) {
        byte[] manifestSerialized;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ApplicationManifestUtilsV3.write(baos, request.getManifest());
            manifestSerialized = baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Could not serialize manifest", e);
        }

        return applyManifestAndWaitForCompletion(manifestSerialized)
                .flatMapMany(ignored -> Flux.fromIterable(request.getManifest().getApplications()))
                .flatMap(
                        manifestApp ->
                                getApplicationIdV3(manifestApp.getName())
                                        .flatMap(
                                                appId ->
                                                        Mono.zip(
                                                                Mono.just(appId),
                                                                createPackage(appId, manifestApp)))
                                        .flatMap(
                                                function(
                                                        (appId, packageId) ->
                                                                buildAndStageAndWaitForRunning(
                                                                        manifestApp,
                                                                        packageId,
                                                                        appId))))
                .then();
    }

    @Override
    public Mono<Void> rename(RenameApplicationRequest request) {
        return getApplicationIdV3(request.getName())
                .flatMap(
                        applicationId ->
                                requestUpdateApplicationV3(
                                        applicationId,
                                        builder -> builder.name(request.getNewName())))
                .then()
                .transform(OperationsLogging.log("Rename Application"))
                .checkpoint();
    }

    @Override
    public Mono<Void> restage(RestageApplicationRequest request) {
        return getApplicationId(request.getName())
                .flatMap(
                        applicationId ->
                                restageApplication(
                                        request.getName(),
                                        applicationId,
                                        request.getStagingTimeout(),
                                        request.getStartupTimeout()))
                .transform(OperationsLogging.log("Restage Application"))
                .checkpoint();
    }

    @Override
    public Mono<Void> restart(RestartApplicationRequest request) {
        return getApplication(request.getName())
                .flatMap(resource -> stopApplicationIfNotStopped(resource))
                .flatMap(
                        stoppedApplication ->
                                startApplicationAndWait(
                                        request.getName(),
                                        ResourceUtils.getId(stoppedApplication),
                                        request.getStagingTimeout(),
                                        request.getStartupTimeout()))
                .transform(OperationsLogging.log("Restart Application"))
                .checkpoint();
    }

    @Override
    public Mono<Void> restartInstance(RestartApplicationInstanceRequest request) {
        return getApplicationId(request.getName())
                .flatMap(
                        applicationId ->
                                requestTerminateApplicationInstance(
                                        applicationId, String.valueOf(request.getInstanceIndex())))
                .transform(OperationsLogging.log("Restart Application Instance"))
                .checkpoint();
    }

    @Override
    public Mono<Task> runTask(RunApplicationTaskRequest request) {
        return getApplicationIdV3(request.getApplicationName())
                .flatMap(applicationId -> requestCreateTask(applicationId, request))
                .map(DefaultApplications::toTask)
                .transform(OperationsLogging.log("Run Application Task Instance"))
                .checkpoint();
    }

    @Override
    public Mono<Void> scale(ScaleApplicationRequest request) {
        if (!areModifiersPresent(request)) {
            return Mono.empty();
        }
        return getApplicationId(request.getName())
                .flatMap(
                        applicationId ->
                                requestUpdateApplicationScale(
                                        applicationId,
                                        request.getDiskLimit(),
                                        request.getInstances(),
                                        request.getMemoryLimit()))
                .filter(resource -> isRestartRequired(request, resource))
                .flatMap(
                        resource ->
                                restartApplication(
                                        request.getName(),
                                        ResourceUtils.getId(resource),
                                        request.getStagingTimeout(),
                                        request.getStartupTimeout()))
                .transform(OperationsLogging.log("Scale Application"))
                .checkpoint();
    }

    @Override
    public Mono<Void> setHealthCheck(SetApplicationHealthCheckRequest request) {
        return getApplicationIdV3(request.getName())
                .flatMap(this::requestApplicationWebProcess)
                .flatMap(
                        webProcess ->
                                this.requestUpdateProcessHealthCheckType(
                                        webProcess.getId(), fromHealthCheck(request.getType())))
                .transform(OperationsLogging.log("Set Application Health Check"))
                .checkpoint();
    }

    @Override
    public Mono<Boolean> sshEnabled(ApplicationSshEnabledRequest request) {
        return getApplicationIdV3(request.getName())
                .flatMap(applicationId -> getSshEnabled(applicationId))
                .transform(OperationsLogging.log("Is Application SSH Enabled"))
                .checkpoint();
    }

    @Override
    public Mono<Void> start(StartApplicationRequest request) {
        return getApplicationIdWhere(request.getName(), isNotIn(STARTED_STATE))
                .flatMap(
                        applicationId ->
                                startApplicationAndWait(
                                        request.getName(),
                                        applicationId,
                                        request.getStagingTimeout(),
                                        request.getStartupTimeout()))
                .transform(OperationsLogging.log("Start Application"))
                .checkpoint();
    }

    @Override
    public Mono<Void> stop(StopApplicationRequest request) {
        return getApplicationIdWhere(request.getName(), isNotIn(STOPPED_STATE))
                .flatMap(applicationId -> stopApplication(applicationId))
                .then()
                .transform(OperationsLogging.log("Stop Application"))
                .checkpoint();
    }

    @Override
    public Mono<Void> terminateTask(TerminateApplicationTaskRequest request) {
        return getApplicationIdV3(request.getApplicationName())
                .flatMap(applicationId -> getTaskId(applicationId, request.getSequenceId()))
                .flatMap(taskId -> requestTerminateTask(taskId))
                .then()
                .transform(OperationsLogging.log("Terminate Application Task Instance"))
                .checkpoint();
    }

    @Override
    public Mono<Void> setEnvironmentVariable(SetEnvironmentVariableApplicationRequest request) {
        return getApplicationIdV3(request.getName())
                .flatMap(
                        id ->
                                requestSetEnvironmentVariable(
                                        id, request.getVariableName(), request.getVariableValue()))
                .then()
                .transform(OperationsLogging.log("Set Application Environment Variable"))
                .checkpoint();
    }

    @Override
    public Mono<Void> unsetEnvironmentVariable(UnsetEnvironmentVariableApplicationRequest request) {
        return getApplicationIdV3(request.getName())
                .flatMap(id -> requestUnsetEnvironmentVariable(id, request.getVariableName()))
                .then()
                .transform(OperationsLogging.log("Unset Application Environment Variable"))
                .checkpoint();
    }

    private Mono<Boolean> getSshEnabled(String applicationId) {
        return this.cloudFoundryClient
                .applicationsV3()
                .getSshEnabled(
                        GetApplicationSshEnabledRequest.builder()
                                .applicationId(applicationId)
                                .build())
                .map(GetApplicationSshEnabledResponse::getEnabled);
    }

    private Mono<Void> applyDropletAndWaitForRunning(
            String appname, String appId, String dropletId) {
        return this.cloudFoundryClient
                .applicationsV3()
                .setCurrentDroplet(
                        SetApplicationCurrentDropletRequest.builder()
                                .applicationId(appId)
                                .data(Relationship.builder().id(dropletId).build())
                                .build())
                .then(
                        this.cloudFoundryClient
                                .applicationsV3()
                                .restart(
                                        org.cloudfoundry.client.v3.applications
                                                .RestartApplicationRequest.builder()
                                                .applicationId(appId)
                                                .build()))
                .then(waitForRunningV3(appname, appId, null));
    }

    private Mono<Void> applyManifestAndWaitForCompletion(byte[] manifestSerialized) {
        return this.cloudFoundryClient
                .spacesV3()
                .applyManifest(
                        ApplyManifestRequest.builder()
                                .manifest(manifestSerialized)
                                .spaceId(this.spaceId)
                                .build())
                .map(
                        response ->
                                response.getJobId()
                                        .orElseThrow(
                                                () ->
                                                        new IllegalStateException(
                                                                "No jobId returned for applying v3"
                                                                        + " manifest")))
                .flatMap(
                        jobId ->
                                JobUtils.waitForCompletion(
                                        this.cloudFoundryClient, Duration.ofMinutes(5), jobId));
    }

    private static boolean areModifiersPresent(ScaleApplicationRequest request) {
        return request.getMemoryLimit() != null
                || request.getDiskLimit() != null
                || request.getInstances() != null;
    }

    private Flux<String> associateDefaultDomain(
            String applicationId,
            List<DomainSummary> availableDomains,
            ApplicationManifest manifest,
            RandomWords randomWords) {
        return getDefaultDomainId()
                .flatMapMany(
                        domainId ->
                                getPushRouteIdFromDomain(
                                        availableDomains, domainId, manifest, randomWords))
                .flatMap(routeId -> requestAssociateRoute(applicationId, routeId))
                .map(ResourceUtils::getId);
    }

    private Mono<Void> bindServices(String applicationId, ApplicationManifest manifest) {
        if (manifest.getServices() == null || manifest.getServices().size() == 0) {
            return Mono.empty();
        }

        return Flux.fromIterable(manifest.getServices())
                .flatMap(serviceInstanceName -> getServiceId(serviceInstanceName))
                .flatMap(
                        serviceInstanceId ->
                                requestCreateServiceBinding(applicationId, serviceInstanceId)
                                        .onErrorResume(
                                                ExceptionUtils.statusCode(CF_SERVICE_ALREADY_BOUND),
                                                t -> Mono.empty()))
                .then();
    }

    private Mono<Void> buildAndStageAndWaitForRunning(
            ManifestV3Application manifestApp, String packageId, String appId) {
        return buildAndStage(manifestApp, packageId)
                .flatMap(
                        dropletId ->
                                applyDropletAndWaitForRunning(
                                        manifestApp.getName(), appId, dropletId));
    }

    private Mono<String> buildAndStage(ManifestV3Application manifestApp, String packageId) {
        return this.cloudFoundryClient
                .builds()
                .create(
                        CreateBuildRequest.builder()
                                .getPackage(Relationship.builder().id(packageId).build())
                                .build())
                .map(CreateBuildResponse::getId)
                .flatMap(buildId -> waitForBuildStaging(buildId, manifestApp.getName(), null))
                .map(build -> build.getDroplet().getId());
    }

    private static String cleanName(ApplicationManifest manifest) {
        return manifest.getName().replaceAll("\\.", "");
    }

    public static BiFunction<ProcessState, ProcessState, ProcessState> collectStates() {
        return (totalState, instanceState) -> {
            if (ProcessState.RUNNING.equals(instanceState)
                    || ProcessState.RUNNING.equals(totalState)) {
                return ProcessState.RUNNING;
            }

            if (ProcessState.CRASHED.equals(instanceState)) {
                return ProcessState.CRASHED;
            }

            return totalState;
        };
    }

    private static ApplicationEvent convertToApplicationEvent(AuditEventResource entity) {
        Date timestamp = null;
        try {
            timestamp = DateUtils.parseFromIso8601(entity.getCreatedAt());
        } catch (IllegalArgumentException iae) {
            // do not set time
        }
        return ApplicationEvent.builder()
                .actor(entity.getAuditEventActor().getName())
                .description(
                        eventDescription(
                                getMetadataRequest(entity), getEntryNames(entity.getType())))
                .id(entity.getId())
                .event(entity.getType())
                .time(timestamp)
                .build();
    }

    private Mono<Void> copyBits(
            Duration completionTimeout, String sourceApplicationId, String targetApplicationId) {
        return requestCopyBits(sourceApplicationId, targetApplicationId)
                .flatMap(
                        job ->
                                JobUtils.waitForCompletion(
                                        this.cloudFoundryClient, completionTimeout, job));
    }

    private Mono<String> createPackage(String appId, ManifestV3Application manifestApp) {
        if (manifestApp.getDocker() != null) {
            return this.cloudFoundryClient
                    .packages()
                    .create(
                            CreatePackageRequest.builder()
                                    .type(PackageType.DOCKER)
                                    .data(
                                            DockerData.builder()
                                                    .image(manifestApp.getDocker().getImage())
                                                    .username(manifestApp.getDocker().getUsername())
                                                    .password(manifestApp.getDocker().getPassword())
                                                    .build())
                                    .relationships(
                                            PackageRelationships.builder()
                                                    .application(
                                                            ToOneRelationship.builder()
                                                                    .data(
                                                                            Relationship.builder()
                                                                                    .id(appId)
                                                                                    .build())
                                                                    .build())
                                                    .build())
                                    .build())
                    .map(CreatePackageResponse::getId);
        } else {
            return this.cloudFoundryClient
                    .packages()
                    .create(
                            CreatePackageRequest.builder()
                                    .type(PackageType.BITS)
                                    .data(BitsData.builder().build())
                                    .relationships(
                                            PackageRelationships.builder()
                                                    .application(
                                                            ToOneRelationship.builder()
                                                                    .data(
                                                                            Relationship.builder()
                                                                                    .id(appId)
                                                                                    .build())
                                                                    .build())
                                                    .build())
                                    .build())
                    .filter(p -> p.getState() == PackageState.AWAITING_UPLOAD)
                    .switchIfEmpty(
                            ExceptionUtils.illegalState(
                                    "Newly created package of application %s is not in state"
                                            + " AWAITING_UPLOAD",
                                    manifestApp.getName()))
                    .map(CreatePackageResponse::getId)
                    .flatMap(
                            packageId ->
                                    ResourceMatchingUtilsV3.getMatchedResources(
                                                    this.cloudFoundryClient, manifestApp.getPath())
                                            .flatMap(
                                                    matchedResources ->
                                                            uploadPackageBitsAndWait(
                                                                    packageId,
                                                                    manifestApp.getPath(),
                                                                    matchedResources,
                                                                    Duration.ofMinutes(5)))
                                            .map(GetPackageResponse::getId));
        }
    }

    private Mono<Void> deleteRoute(String routeId, Duration completionTimeout) {
        return requestDeleteRoute(routeId)
                .flatMap(
                        job ->
                                JobUtils.waitForCompletion(
                                        this.cloudFoundryClient, completionTimeout, job));
    }

    private Mono<Void> deleteRoutes(
            Duration completionTimeout,
            Optional<List<org.cloudfoundry.client.v2.routes.Route>> routes) {
        return routes.map(Flux::fromIterable)
                .orElse(Flux.empty())
                .map(org.cloudfoundry.client.v2.routes.Route::getId)
                .flatMap(routeId -> deleteRoute(routeId, completionTimeout))
                .then();
    }

    private static String deriveHostname(
            String host, ApplicationManifest manifest, RandomWords randomWords) {
        if (Optional.ofNullable(manifest.getNoHostname()).orElse(false)) {
            return "";
        } else if (host != null) {
            return host;
        } else if (Optional.ofNullable(manifest.getRandomRoute()).orElse(false)) {
            return String.join(
                    "-", cleanName(manifest), randomWords.getAdjective(), randomWords.getNoun());
        } else {
            return cleanName(manifest);
        }
    }

    private static Statistics emptyApplicationStatistics() {
        return Statistics.builder().usage(emptyApplicationUsage()).build();
    }

    private static Usage emptyApplicationUsage() {
        return Usage.builder().build();
    }

    private static InstanceStatistics emptyInstanceStats() {
        return InstanceStatistics.builder().statistics(emptyApplicationStatistics()).build();
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
            sb.append(entryName).append(": ").append(value);
        }
        return sb.toString();
    }

    private static Flux<SpaceApplicationSummary> extractApplications(
            GetSpaceSummaryResponse getSpaceSummaryResponse) {
        return Flux.fromIterable(getSpaceSummaryResponse.getApplications());
    }

    private Mono<AbstractApplicationResource> getApplication(String application) {
        return getApplication(application, spaceId);
    }

    private Mono<AbstractApplicationResource> getApplication(String application, String spaceId) {
        return requestApplications(application, spaceId)
                .single()
                .onErrorResume(
                        NoSuchElementException.class,
                        t ->
                                ExceptionUtils.illegalArgument(
                                        "Application %s does not exist", application));
    }

    private Mono<List<String>> getApplicationBuildpacks(String applicationId) {
        return this.cloudFoundryClient
                .applicationsV3()
                .get(
                        org.cloudfoundry.client.v3.applications.GetApplicationRequest.builder()
                                .applicationId(applicationId)
                                .build())
                .map(GetApplicationResponse::getLifecycle)
                .filter(lifecycle -> BUILDPACK == lifecycle.getType())
                .map(Lifecycle::getData)
                .cast(BuildpackData.class)
                .map(BuildpackData::getBuildpacks)
                .defaultIfEmpty(Collections.emptyList());
    }

    private Mono<String> getApplicationId(String application) {
        return getApplicationId(application, spaceId);
    }

    private Mono<String> getApplicationId(String application, String spaceId) {
        return getApplication(application, spaceId).map(ResourceUtils::getId);
    }

    private Mono<String> getApplicationId(ApplicationManifest manifest, String stackId) {
        return requestApplications(manifest.getName())
                .singleOrEmpty()
                .flatMap(
                        application -> {
                            HashMap<String, Object> merge = new HashMap<>();
                            Optional.ofNullable(
                                            ResourceUtils.getEntity(application)
                                                    .getEnvironmentJsons())
                                    .ifPresent(merge::putAll);
                            Optional.ofNullable(manifest.getEnvironmentVariables())
                                    .ifPresent(merge::putAll);

                            return requestUpdateApplication(
                                            ResourceUtils.getId(application),
                                            merge,
                                            manifest,
                                            stackId)
                                    .map(ResourceUtils::getId);
                        })
                .switchIfEmpty(
                        requestCreateApplication(manifest, stackId).map(ResourceUtils::getId));
    }

    private Mono<String> getApplicationIdFromOrgSpace(
            String application, String spaceId, String organization, String space) {
        return getSpaceOrganizationId(spaceId)
                .flatMap(
                        organizationId ->
                                organization != null
                                        ? getOrganizationId(organization)
                                        : Mono.just(organizationId))
                .flatMap(
                        organizationId ->
                                space != null
                                        ? getSpaceId(organizationId, space)
                                        : Mono.just(spaceId))
                .flatMap(spaceId1 -> getApplicationId(application, spaceId1));
    }

    private Mono<String> getApplicationIdV3(String applicationName) {
        return getApplicationV3(applicationName).map(ApplicationResource::getId);
    }

    private Mono<String> getApplicationIdWhere(
            String application, Predicate<AbstractApplicationResource> predicate) {
        return getApplication(application).filter(predicate).map(ResourceUtils::getId);
    }

    private Mono<List<org.cloudfoundry.client.v3.routes.RouteResource>> getApplicationRoutes(
            String applicationId) {
        return requestApplicationRoutes(applicationId).collectList();
    }

    private Mono<ApplicationResource> getApplicationV3(String application) {
        return requestApplicationsV3(application)
                .single()
                .onErrorResume(
                        NoSuchElementException.class,
                        t ->
                                ExceptionUtils.illegalArgument(
                                        "Application %s does not exist", application));
    }

    private Mono<Tuple4<List<String>, SummaryApplicationResponse, String, List<InstanceDetail>>>
            getAuxiliaryContent(ApplicationResource applicationResource) {
        String applicationId = applicationResource.getId();
        LifecycleData data = applicationResource.getLifecycle().getData();
        String stackName = "<UNKNOWN>";
        if (data instanceof CnbData) {
            stackName = ((CnbData) data).getStack();
        } else if (data instanceof BuildpackData) {
            stackName = ((BuildpackData) data).getStack();
        }

        Mono<List<InstanceDetail>> appInstanceDetails =
                requestApplicationStatisticsV3(applicationId).map(this::toInstanceDetailList);

        return Mono.zip(
                getApplicationBuildpacks(applicationId),
                requestApplicationSummary(applicationId),
                Mono.just(stackName),
                appInstanceDetails);
    }

    private Mono<String> getDefaultDomainId() {
        return requestSharedDomains()
                .filter(
                        resource ->
                                !Optional.ofNullable(
                                                ResourceUtils.getEntity(resource).getInternal())
                                        .orElse(false))
                .map(ResourceUtils::getId)
                .next()
                .switchIfEmpty(ExceptionUtils.illegalArgument("No default domain found"));
    }

    private static String getDomainId(List<DomainSummary> availableDomains, String domainName) {
        return availableDomains.stream()
                .filter(domain -> domainName.equals(domain.getName()))
                .map(DomainSummary::getId)
                .findFirst()
                .orElseThrow(
                        () ->
                                new IllegalArgumentException(
                                        String.format("Domain %s not found", domainName)));
    }

    private static String[] getEntryNames(String type) {
        return type.contains("crash") ? ENTRY_FIELDS_CRASH : ENTRY_FIELDS_NORMAL;
    }

    private static int getInstances(AbstractApplicationResource resource) {
        return Optional.ofNullable(resource.getEntity())
                .map(ApplicationEntity::getInstances)
                .orElse(0);
    }

    private Flux<LogMessage> getLogs(String applicationId, Boolean recent) {
        if (Optional.ofNullable(recent).orElse(false)) {
            return requestLogsRecent(applicationId)
                    .filter(e -> EventType.LOG_MESSAGE == e.getEventType())
                    .map(Envelope::getLogMessage)
                    .collectSortedList(LOG_MESSAGE_COMPARATOR)
                    .flatMapIterable(d -> d);
        } else {
            return requestLogsStream(applicationId)
                    .filter(e -> EventType.LOG_MESSAGE == e.getEventType())
                    .map(Envelope::getLogMessage)
                    .transformDeferred(
                            SortingUtils.timespan(LOG_MESSAGE_COMPARATOR, LOG_MESSAGE_TIMESPAN));
        }
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> getMetadataRequest(AuditEventResource entity) {
        Map<String, Object> metadata =
                Optional.ofNullable(entity.getData()).orElse(Collections.emptyMap());

        if (metadata.get("request") != null) {
            return (Map<String, Object>) metadata.getOrDefault("request", Collections.emptyMap());
        } else if (metadata.get("instance") != null) {
            return Collections.unmodifiableMap(metadata);
        } else {
            return Collections.emptyMap();
        }
    }

    private Mono<Optional<List<org.cloudfoundry.client.v2.routes.Route>>> getOptionalRoutes(
            boolean deleteRoutes, String applicationId) {
        if (deleteRoutes) {
            return getRoutes(applicationId).map(Optional::of);
        } else {
            return Mono.just(Optional.empty());
        }
    }

    private Mono<Optional<String>> getOptionalStackId(String stack) {
        return Optional.ofNullable(stack)
                .map(stack1 -> getStackId(stack1).map(Optional::of))
                .orElse(Mono.just(Optional.empty()));
    }

    private Mono<OrganizationResource> getOrganization(String organization) {
        return requestOrganizations(organization)
                .single()
                .onErrorResume(
                        NoSuchElementException.class,
                        t ->
                                ExceptionUtils.illegalArgument(
                                        "Organization %s not found", organization));
    }

    private Mono<String> getOrganizationId(String organization) {
        return getOrganization(organization).map(ResourceUtils::getId);
    }

    private Mono<SpaceResource> getOrganizationSpaceByName(String organizationId, String space) {
        return requestOrganizationSpacesByName(organizationId, space)
                .single()
                .onErrorResume(
                        NoSuchElementException.class,
                        t -> ExceptionUtils.illegalArgument("Space %s not found", space));
    }

    private static String getPassword(DockerCredentials dockerCredentials) {
        return Optional.ofNullable(dockerCredentials)
                .map(DockerCredentials::getPassword)
                .orElse(null);
    }

    private Flux<String> getPushRouteIdFromDomain(
            List<DomainSummary> availableDomains,
            String domainId,
            ApplicationManifest manifest,
            RandomWords randomWords) {
        if (isTcpDomain(availableDomains, domainId)) {
            return requestCreateTcpRoute(domainId).map(ResourceUtils::getId).flux();
        }

        List<String> hosts;
        if (Optional.ofNullable(manifest.getNoHostname()).orElse(false)) {
            hosts = Collections.singletonList("");
        } else if (Optional.ofNullable(manifest.getRandomRoute()).orElse(false)
                && manifest.getHosts() == null) {
            hosts =
                    Collections.singletonList(
                            String.join(
                                    "-",
                                    cleanName(manifest),
                                    randomWords.getAdjective(),
                                    randomWords.getNoun()));
        } else if (manifest.getHosts() == null || manifest.getHosts().isEmpty()) {
            hosts = Collections.singletonList(cleanName(manifest));
        } else {
            hosts = manifest.getHosts();
        }

        return Flux.fromIterable(hosts)
                .flatMap(
                        host ->
                                getRouteId(domainId, host, manifest.getRoutePath())
                                        .switchIfEmpty(
                                                requestCreateRoute(
                                                                domainId,
                                                                host,
                                                                manifest.getRoutePath())
                                                        .map(ResourceUtils::getId)));
    }

    private Flux<String> getPushRouteIdFromRoute(
            List<DomainSummary> availableDomains,
            ApplicationManifest manifest,
            RandomWords randomWords) {
        return Flux.fromIterable(manifest.getRoutes())
                .flatMap(
                        route ->
                                RouteUtils.decomposeRoute(
                                        availableDomains,
                                        route.getRoute(),
                                        manifest.getRoutePath()))
                .flatMap(
                        decomposedRoute -> {
                            String domainId =
                                    getDomainId(availableDomains, decomposedRoute.getDomain());
                            if (isTcpDomain(availableDomains, domainId)) {
                                return getRouteIdForTcpRoute(decomposedRoute, domainId, manifest);
                            } else {
                                return getRouteIdForHttpRoute(
                                        decomposedRoute, domainId, manifest, randomWords);
                            }
                        });
    }

    private Mono<String> getRouteId(String domainId, String host, String routePath) {
        return requestRoutes(domainId, host, null, routePath)
                .filter(resource -> isIdentical(host, ResourceUtils.getEntity(resource).getHost()))
                .filter(
                        resource ->
                                isIdentical(
                                        Optional.ofNullable(routePath).orElse(""),
                                        ResourceUtils.getEntity(resource).getPath()))
                .singleOrEmpty()
                .map(ResourceUtils::getId);
    }

    private Mono<String> getRouteIdForHttpRoute(
            DecomposedRoute decomposedRoute,
            String domainId,
            ApplicationManifest manifest,
            RandomWords randomWords) {
        String derivedHost = deriveHostname(decomposedRoute.getHost(), manifest, randomWords);
        return getRouteId(domainId, derivedHost, decomposedRoute.getPath())
                .switchIfEmpty(
                        requestCreateRoute(domainId, derivedHost, decomposedRoute.getPath())
                                .map(ResourceUtils::getId));
    }

    private Mono<String> getRouteIdForTcpRoute(
            DecomposedRoute decomposedRoute, String domainId, ApplicationManifest manifest) {
        if (Optional.ofNullable(manifest.getRandomRoute()).orElse(false)) {
            return requestCreateTcpRoute(domainId).map(ResourceUtils::getId);
        }

        return getTcpRouteId(domainId, decomposedRoute.getPort())
                .switchIfEmpty(
                        requestCreateTcpRoute(domainId, decomposedRoute.getPort())
                                .map(ResourceUtils::getId));
    }

    private Mono<List<org.cloudfoundry.client.v2.routes.Route>> getRoutes(String applicationId) {
        return requestApplicationSummary(applicationId).map(SummaryApplicationResponse::getRoutes);
    }

    private Mono<Tuple2<Optional<List<org.cloudfoundry.client.v2.routes.Route>>, String>>
            getRoutesAndApplicationId(DeleteApplicationRequest request, boolean deleteRoutes) {
        return getApplicationId(request.getName())
                .flatMap(
                        applicationId ->
                                getOptionalRoutes(deleteRoutes, applicationId)
                                        .zipWith(Mono.just(applicationId)));
    }

    private Mono<String> getServiceId(String serviceInstanceName) {
        return requestListServiceInstances(serviceInstanceName)
                .map(ResourceUtils::getId)
                .single()
                .onErrorResume(
                        NoSuchElementException.class,
                        t ->
                                ExceptionUtils.illegalArgument(
                                        "Service instance %s could not be found",
                                        serviceInstanceName));
    }

    private Mono<String> getSpaceId(String organizationId, String space) {
        return getOrganizationSpaceByName(organizationId, space).map(ResourceUtils::getId);
    }

    private Mono<String> getSpaceOrganizationId(String spaceId) {
        return requestSpace(spaceId)
                .map(response -> ResourceUtils.getEntity(response).getOrganizationId());
    }

    private Mono<String> getStackId(String stackName) {
        return requestStacks(stackName)
                .map(StackResource::getId)
                .single()
                .onErrorResume(
                        NoSuchElementException.class,
                        t -> ExceptionUtils.illegalArgument("Stack %s does not exist", stackName));
    }

    private Mono<String> getStackName(String stackId) {
        return this.cloudFoundryClient
                .stacksV3()
                .get(GetStackRequest.builder().stackId(stackId).build())
                .map(GetStackResponse::getName);
    }

    private Mono<String> getTaskId(String applicationId, Integer sequenceId) {
        return listTasks(applicationId, sequenceId)
                .single()
                .map(Resource::getId)
                .onErrorResume(
                        NoSuchElementException.class,
                        t ->
                                ExceptionUtils.illegalArgument(
                                        "Task with sequence id of %s does not exist", sequenceId));
    }

    private Mono<String> getTcpRouteId(String domainId, Integer port) {
        return requestRoutes(domainId, null, port, null).singleOrEmpty().map(ResourceUtils::getId);
    }

    private static String getUsername(DockerCredentials dockerCredentials) {
        return Optional.ofNullable(dockerCredentials)
                .map(DockerCredentials::getUsername)
                .orElse(null);
    }

    private static boolean isIdentical(String s, String t) {
        return Objects.equals(s, t);
    }

    private static Predicate<ProcessState> isInstanceComplete() {
        return state -> ProcessState.RUNNING.equals(state) || ProcessState.CRASHED.equals(state);
    }

    private static Predicate<AbstractApplicationResource> isNotIn(String expectedState) {
        return resource -> isNotIn(resource, expectedState);
    }

    private static boolean isNotIn(AbstractApplicationResource resource, String expectedState) {
        return !expectedState.equals(ResourceUtils.getEntity(resource).getState());
    }

    private static boolean isRestartRequired(
            ScaleApplicationRequest request, AbstractApplicationResource applicationResource) {
        return (request.getDiskLimit() != null || request.getMemoryLimit() != null)
                && STARTED_STATE.equals(ResourceUtils.getEntity(applicationResource).getState());
    }

    private static Predicate<ProcessState> isRunning() {
        return ProcessState.RUNNING::equals;
    }

    private static Predicate<String> isStaged() {
        return "STAGED"::equals;
    }

    private static Predicate<String> isStagingComplete() {
        return state -> "STAGED".equals(state) || "FAILED".equals(state);
    }

    private static boolean isTcpDomain(List<DomainSummary> availableDomains, String domainId) {
        List<String> tcpDomainIds =
                availableDomains.stream()
                        .filter(domain -> "tcp".equals(domain.getType()))
                        .map(DomainSummary::getId)
                        .collect(Collectors.toList());

        return tcpDomainIds.contains(domainId);
    }

    private Mono<List<DomainSummary>> listAvailableDomains(String organizationId) {
        return requestListPrivateDomains(organizationId)
                .map(DefaultApplications::toDomain)
                .mergeWith(requestListSharedDomains().map(DefaultApplications::toDomain))
                .collectList();
    }

    private Flux<org.cloudfoundry.client.v3.tasks.Task> listTasks(
            String applicationId, Integer sequenceId) {
        return requestListTasks(applicationId, sequenceId)
                .cast(org.cloudfoundry.client.v3.tasks.Task.class);
    }

    private Mono<Void> prepareDomainsAndRoutes(
            String applicationId,
            List<DomainSummary> availableDomains,
            ApplicationManifest manifest,
            List<org.cloudfoundry.client.v3.routes.RouteResource> existingRoutes,
            RandomWords randomWords) {
        if (Optional.ofNullable(manifest.getNoRoute()).orElse(false)) {
            return Flux.fromIterable(existingRoutes)
                    .map(org.cloudfoundry.client.v3.routes.RouteResource::getId)
                    .flatMap(routeId -> requestRemoveRouteFromApplication(applicationId, routeId))
                    .then();
        }

        if (manifest.getRoutes() == null) {
            if (manifest.getDomains() == null) {
                if (existingRoutes.isEmpty()) {
                    return associateDefaultDomain(
                                    applicationId, availableDomains, manifest, randomWords)
                            .then();
                }
                return Mono.empty(); // A route already exists for the application, do nothing
            }
            return Flux.fromIterable(manifest.getDomains())
                    .flatMap(
                            domain ->
                                    getPushRouteIdFromDomain(
                                                    availableDomains,
                                                    getDomainId(availableDomains, domain),
                                                    manifest,
                                                    randomWords)
                                            .flatMap(
                                                    routeId ->
                                                            requestAssociateRoute(
                                                                    applicationId, routeId)))
                    .then();
        }

        List<String> existingRouteIds =
                existingRoutes.stream()
                        .map(org.cloudfoundry.client.v3.routes.RouteResource::getId)
                        .collect(Collectors.toList());

        return getPushRouteIdFromRoute(availableDomains, manifest, randomWords)
                .filter(routeId -> !existingRouteIds.contains(routeId))
                .flatMapSequential(routeId -> requestAssociateRoute(applicationId, routeId), 1)
                .then();
    }

    private Flux<Void> pushApplication(
            List<DomainSummary> availableDomains,
            ApplicationManifest manifest,
            RandomWords randomWords,
            PushApplicationManifestRequest request) {

        return getOptionalStackId(manifest.getStack())
                .flatMapMany(stackId -> getApplicationId(manifest, stackId.orElse(null)))
                .flatMap(
                        applicationId ->
                                Mono.zip(
                                        Mono.just(applicationId),
                                        getApplicationRoutes(applicationId),
                                        ResourceMatchingUtils.getMatchedResources(
                                                this.cloudFoundryClient, manifest.getPath())))
                .flatMap(
                        function(
                                (applicationId, existingRoutes, matchedResources) ->
                                        prepareDomainsAndRoutes(
                                                        applicationId,
                                                        availableDomains,
                                                        manifest,
                                                        existingRoutes,
                                                        randomWords)
                                                .thenReturn(
                                                        Tuples.of(
                                                                applicationId, matchedResources))))
                .flatMap(
                        function(
                                (applicationId, matchedResources) ->
                                        Mono.when(
                                                        bindServices(applicationId, manifest),
                                                        updateBuildpacks(applicationId, manifest),
                                                        uploadApplicationAndWait(
                                                                applicationId,
                                                                manifest.getPath(),
                                                                matchedResources,
                                                                request.getStagingTimeout()))
                                                .thenReturn(applicationId)))
                .flatMap(
                        applicationId ->
                                stopAndStartApplication(
                                        applicationId, manifest.getName(), request));
    }

    private Flux<Void> pushDocker(
            List<DomainSummary> availableDomains,
            ApplicationManifest manifest,
            RandomWords randomWords,
            PushApplicationManifestRequest request) {

        return getOptionalStackId(manifest.getStack())
                .flatMapMany(stackId -> getApplicationId(manifest, stackId.orElse(null)))
                .flatMap(
                        applicationId ->
                                Mono.zip(
                                        Mono.just(applicationId),
                                        getApplicationRoutes(applicationId)))
                .flatMap(
                        function(
                                (applicationId, existingRoutes) ->
                                        prepareDomainsAndRoutes(
                                                        applicationId,
                                                        availableDomains,
                                                        manifest,
                                                        existingRoutes,
                                                        randomWords)
                                                .thenReturn(applicationId)))
                .delayUntil(applicationId -> bindServices(applicationId, manifest))
                .flatMap(
                        applicationId ->
                                stopAndStartApplication(
                                        applicationId, manifest.getName(), request));
    }

    private Mono<Void> removeServiceBindings(String applicationId) {
        return requestListServiceBindings(applicationId)
                .map(ResourceUtils::getId)
                .flatMap(
                        serviceBindingId ->
                                requestRemoveServiceBinding(applicationId, serviceBindingId))
                .then();
    }

    private Mono<GetApplicationEnvironmentResponse> requestApplicationEnvironment(
            String applicationId) {
        return this.cloudFoundryClient
                .applicationsV3()
                .getEnvironment(
                        GetApplicationEnvironmentRequest.builder()
                                .applicationId(applicationId)
                                .build());
    }

    private Mono<GetApplicationProcessStatisticsResponse> requestApplicationStatisticsV3(
            String applicationId) {
        return this.cloudFoundryClient
                .applicationsV3()
                .getProcessStatistics(
                        GetApplicationProcessStatisticsRequest.builder()
                                .applicationId(applicationId)
                                .type("web")
                                .build())
                .onErrorResume(
                        ExceptionUtils.statusCodeV3(
                                CF_BUILDPACK_COMPILED_FAILED,
                                CF_INSTANCES_ERROR,
                                CF_STAGING_NOT_FINISHED,
                                CF_STAGING_TIME_EXPIRED,
                                CF_INSUFFICIENT_RESOURCES,
                                CF_STAGING_ERROR,
                                // NOTE: this used to be an error from the v2 API apps/instances
                                // endpoint. It probably does not apply to the v3 processes/stats
                                // endpoint, but it is hard to test. Leaving it in, worst case this
                                // never gets triggered.
                                CF_APP_STOPPED_STATS_ERROR),
                        t ->
                                Mono.just(
                                        GetApplicationProcessStatisticsResponse.builder()
                                                .resources()
                                                .build()));
    }

    private Mono<GetApplicationProcessResponse> requestApplicationWebProcess(String applicationId) {
        return this.cloudFoundryClient
                .applicationsV3()
                .getProcess(
                        GetApplicationProcessRequest.builder()
                                .applicationId(applicationId)
                                .type("web")
                                .build());
    }

    private Mono<Void> requestUpdateProcessHealthCheckType(
            String processId, HealthCheckType healthCheckType) {
        return this.cloudFoundryClient
                .processes()
                .update(
                        UpdateProcessRequest.builder()
                                .processId(processId)
                                .healthCheck(HealthCheck.builder().type(healthCheckType).build())
                                .build())
                .then();
    }

    private Flux<org.cloudfoundry.client.v3.routes.RouteResource> requestApplicationRoutes(
            String applicationId) {
        return PaginationUtils.requestClientV3Resources(
                page ->
                        this.cloudFoundryClient
                                .applicationsV3()
                                .listRoutes(
                                        ListApplicationRoutesRequest.builder()
                                                .applicationId(applicationId)
                                                .page(page)
                                                .build()));
    }

    private Mono<SummaryApplicationResponse> requestApplicationSummary(String applicationId) {
        return this.cloudFoundryClient
                .applicationsV2()
                .summary(SummaryApplicationRequest.builder().applicationId(applicationId).build());
    }

    private Flux<AbstractApplicationResource> requestApplications(String application) {
        return requestApplications(application, spaceId);
    }

    private Flux<AbstractApplicationResource> requestApplications(
            String application, String spaceId) {
        return PaginationUtils.requestClientV2Resources(
                        page ->
                                this.cloudFoundryClient
                                        .spaces()
                                        .listApplications(
                                                ListSpaceApplicationsRequest.builder()
                                                        .name(application)
                                                        .spaceId(spaceId)
                                                        .page(page)
                                                        .build()))
                .cast(AbstractApplicationResource.class);
    }

    private Flux<ApplicationResource> requestApplicationsV3(String application) {
        return PaginationUtils.requestClientV3Resources(
                page ->
                        this.cloudFoundryClient
                                .applicationsV3()
                                .list(
                                        ListApplicationsRequest.builder()
                                                .name(application)
                                                .spaceId(this.spaceId)
                                                .page(page)
                                                .build()));
    }

    private Mono<AssociateApplicationRouteResponse> requestAssociateRoute(
            String applicationId, String routeId) {
        return this.cloudFoundryClient
                .applicationsV2()
                .associateRoute(
                        AssociateApplicationRouteRequest.builder()
                                .applicationId(applicationId)
                                .routeId(routeId)
                                .build());
    }

    private Mono<CopyApplicationResponse> requestCopyBits(
            String sourceApplicationId, String targetApplicationId) {
        return this.cloudFoundryClient
                .applicationsV2()
                .copy(
                        CopyApplicationRequest.builder()
                                .applicationId(targetApplicationId)
                                .sourceApplicationId(sourceApplicationId)
                                .build());
    }

    private Mono<CreateApplicationResponse> requestCreateApplication(
            ApplicationManifest manifest, String stackId) {
        CreateApplicationRequest.Builder builder =
                CreateApplicationRequest.builder()
                        .command(manifest.getCommand())
                        .diskQuota(manifest.getDisk())
                        .environmentJsons(manifest.getEnvironmentVariables())
                        .healthCheckHttpEndpoint(manifest.getHealthCheckHttpEndpoint())
                        .healthCheckTimeout(manifest.getTimeout())
                        .healthCheckType(
                                Optional.ofNullable(manifest.getHealthCheckType())
                                        .map(ApplicationHealthCheck::getValue)
                                        .orElse(null))
                        .instances(manifest.getInstances())
                        .memory(manifest.getMemory())
                        .name(manifest.getName())
                        .spaceId(this.spaceId)
                        .stackId(stackId);

        if (manifest.getBuildpacks() != null && manifest.getBuildpacks().size() == 1) {
            builder.buildpack(manifest.getBuildpacks().get(0));
        }

        if (manifest.getDocker() != null) {
            Optional.ofNullable(manifest.getDocker().getImage())
                    .ifPresent(
                            image -> {
                                builder.diego(true).dockerImage(image);

                                String username = manifest.getDocker().getUsername();
                                String password = manifest.getDocker().getPassword();
                                builder.dockerCredentials(
                                        DockerCredentials.builder()
                                                .username(username)
                                                .password(password)
                                                .build());
                            });
        }

        return this.cloudFoundryClient.applicationsV2().create(builder.build());
    }

    private Mono<CreateRouteResponse> requestCreateRoute(
            String domainId, String host, String routePath) {
        return this.cloudFoundryClient
                .routes()
                .create(
                        org.cloudfoundry.client.v2.routes.CreateRouteRequest.builder()
                                .domainId(domainId)
                                .host(host)
                                .path(routePath)
                                .spaceId(this.spaceId)
                                .build());
    }

    private Mono<CreateServiceBindingResponse> requestCreateServiceBinding(
            String applicationId, String serviceInstanceId) {
        return this.cloudFoundryClient
                .serviceBindingsV2()
                .create(
                        CreateServiceBindingRequest.builder()
                                .applicationId(applicationId)
                                .serviceInstanceId(serviceInstanceId)
                                .build());
    }

    private Mono<CreateTaskResponse> requestCreateTask(
            String applicationId, RunApplicationTaskRequest request) {
        return this.cloudFoundryClient
                .tasks()
                .create(
                        CreateTaskRequest.builder()
                                .applicationId(applicationId)
                                .command(request.getCommand())
                                .diskInMb(request.getDisk())
                                .memoryInMb(request.getMemory())
                                .name(request.getTaskName())
                                .build());
    }

    private Mono<CreateRouteResponse> requestCreateTcpRoute(String domainId) {
        return this.cloudFoundryClient
                .routes()
                .create(
                        org.cloudfoundry.client.v2.routes.CreateRouteRequest.builder()
                                .domainId(domainId)
                                .generatePort(true)
                                .spaceId(this.spaceId)
                                .build());
    }

    private Mono<CreateRouteResponse> requestCreateTcpRoute(String domainId, Integer port) {
        return this.cloudFoundryClient
                .routes()
                .create(
                        org.cloudfoundry.client.v2.routes.CreateRouteRequest.builder()
                                .domainId(domainId)
                                .port(port)
                                .spaceId(this.spaceId)
                                .build());
    }

    private Mono<Void> requestDeleteApplication(String applicationId) {
        return this.cloudFoundryClient
                .applicationsV2()
                .delete(
                        org.cloudfoundry.client.v2.applications.DeleteApplicationRequest.builder()
                                .applicationId(applicationId)
                                .build());
    }

    private Mono<DeleteRouteResponse> requestDeleteRoute(String routeId) {
        return this.cloudFoundryClient
                .routes()
                .delete(DeleteRouteRequest.builder().async(true).routeId(routeId).build());
    }

    private Flux<AuditEventResource> requestEvents(String applicationId) {
        return PaginationUtils.requestClientV3Resources(
                page ->
                        this.cloudFoundryClient
                                .auditEventsV3()
                                .list(
                                        ListAuditEventsRequest.builder()
                                                .targetId(applicationId)
                                                .orderBy("-created_at")
                                                .perPage(50)
                                                .page(page)
                                                .build()));
    }

    private Mono<AbstractApplicationResource> requestGetApplication(String applicationId) {
        return this.cloudFoundryClient
                .applicationsV2()
                .get(
                        org.cloudfoundry.client.v2.applications.GetApplicationRequest.builder()
                                .applicationId(applicationId)
                                .build())
                .cast(AbstractApplicationResource.class);
    }

    private Flux<PrivateDomainResource> requestListPrivateDomains(String organizationId) {
        return PaginationUtils.requestClientV2Resources(
                page ->
                        this.cloudFoundryClient
                                .organizations()
                                .listPrivateDomains(
                                        ListOrganizationPrivateDomainsRequest.builder()
                                                .organizationId(organizationId)
                                                .page(page)
                                                .build()));
    }

    private Flux<ServiceBindingResource> requestListServiceBindings(String applicationId) {
        return PaginationUtils.requestClientV2Resources(
                page ->
                        this.cloudFoundryClient
                                .applicationsV2()
                                .listServiceBindings(
                                        ListApplicationServiceBindingsRequest.builder()
                                                .applicationId(applicationId)
                                                .page(page)
                                                .build()));
    }

    private Flux<UnionServiceInstanceResource> requestListServiceInstances(
            String serviceInstanceName) {
        return PaginationUtils.requestClientV2Resources(
                page ->
                        this.cloudFoundryClient
                                .spaces()
                                .listServiceInstances(
                                        ListSpaceServiceInstancesRequest.builder()
                                                .page(page)
                                                .returnUserProvidedServiceInstances(true)
                                                .name(serviceInstanceName)
                                                .spaceId(this.spaceId)
                                                .build()));
    }

    private Flux<SharedDomainResource> requestListSharedDomains() {
        return PaginationUtils.requestClientV2Resources(
                page ->
                        this.cloudFoundryClient
                                .sharedDomains()
                                .list(ListSharedDomainsRequest.builder().page(page).build()));
    }

    private Flux<TaskResource> requestListTasks(String applicationId) {
        return PaginationUtils.requestClientV3Resources(
                page ->
                        this.cloudFoundryClient
                                .applicationsV3()
                                .listTasks(
                                        org.cloudfoundry.client.v3.applications
                                                .ListApplicationTasksRequest.builder()
                                                .applicationId(applicationId)
                                                .page(page)
                                                .build()));
    }

    private Flux<TaskResource> requestListTasks(String applicationId, Integer sequenceId) {
        return PaginationUtils.requestClientV3Resources(
                page ->
                        this.cloudFoundryClient
                                .applicationsV3()
                                .listTasks(
                                        org.cloudfoundry.client.v3.applications
                                                .ListApplicationTasksRequest.builder()
                                                .applicationId(applicationId)
                                                .page(page)
                                                .sequenceId(sequenceId.toString())
                                                .build()));
    }

    private Flux<Envelope> requestLogsRecent(String applicationId) {
        return dopplerClient.recentLogs(
                RecentLogsRequest.builder().applicationId(applicationId).build());
    }

    private Flux<Envelope> requestLogsStream(String applicationId) {
        return dopplerClient.stream(StreamRequest.builder().applicationId(applicationId).build());
    }

    private Flux<SpaceResource> requestOrganizationSpacesByName(
            String organizationId, String space) {
        return PaginationUtils.requestClientV2Resources(
                page ->
                        this.cloudFoundryClient
                                .organizations()
                                .listSpaces(
                                        ListOrganizationSpacesRequest.builder()
                                                .name(space)
                                                .organizationId(organizationId)
                                                .page(page)
                                                .build()));
    }

    private Flux<OrganizationResource> requestOrganizations(String organization) {
        return PaginationUtils.requestClientV2Resources(
                page ->
                        this.cloudFoundryClient
                                .organizations()
                                .list(
                                        ListOrganizationsRequest.builder()
                                                .name(organization)
                                                .page(page)
                                                .build()));
    }

    private Mono<Void> requestRemoveRouteFromApplication(String applicationId, String routeId) {
        return this.cloudFoundryClient
                .applicationsV2()
                .removeRoute(
                        RemoveApplicationRouteRequest.builder()
                                .applicationId(applicationId)
                                .routeId(routeId)
                                .build());
    }

    private Mono<Void> requestRemoveServiceBinding(String applicationId, String serviceBindingId) {
        return this.cloudFoundryClient
                .applicationsV2()
                .removeServiceBinding(
                        RemoveApplicationServiceBindingRequest.builder()
                                .applicationId(applicationId)
                                .serviceBindingId(serviceBindingId)
                                .build());
    }

    private Mono<RestageApplicationResponse> requestRestageApplication(String applicationId) {
        return this.cloudFoundryClient
                .applicationsV2()
                .restage(
                        org.cloudfoundry.client.v2.applications.RestageApplicationRequest.builder()
                                .applicationId(applicationId)
                                .build());
    }

    private Flux<RouteResource> requestRoutes(
            String domainId, String host, Integer port, String routePath) {
        ListRoutesRequest.Builder requestBuilder = ListRoutesRequest.builder().domainId(domainId);
        Optional.ofNullable(host).ifPresent(requestBuilder::host);
        Optional.ofNullable(routePath).ifPresent(requestBuilder::path);
        Optional.ofNullable(port).ifPresent(requestBuilder::port);

        return PaginationUtils.requestClientV2Resources(
                page -> this.cloudFoundryClient.routes().list(requestBuilder.page(page).build()));
    }

    private Flux<SharedDomainResource> requestSharedDomains() {
        return PaginationUtils.requestClientV2Resources(
                page ->
                        this.cloudFoundryClient
                                .sharedDomains()
                                .list(ListSharedDomainsRequest.builder().page(page).build()));
    }

    private Mono<GetSpaceResponse> requestSpace(String spaceId) {
        return this.cloudFoundryClient
                .spaces()
                .get(GetSpaceRequest.builder().spaceId(spaceId).build());
    }

    private Mono<GetSpaceSummaryResponse> requestSpaceSummary() {
        return this.cloudFoundryClient
                .spaces()
                .getSummary(GetSpaceSummaryRequest.builder().spaceId(this.spaceId).build());
    }

    private Flux<StackResource> requestStacks(String stack) {
        return PaginationUtils.requestClientV3Resources(
                page ->
                        this.cloudFoundryClient
                                .stacksV3()
                                .list(ListStacksRequest.builder().page(page).name(stack).build()));
    }

    private Mono<Void> requestTerminateApplicationInstance(
            String applicationId, String instanceIndex) {
        return this.cloudFoundryClient
                .applicationsV2()
                .terminateInstance(
                        TerminateApplicationInstanceRequest.builder()
                                .applicationId(applicationId)
                                .index(instanceIndex)
                                .build());
    }

    private Mono<CancelTaskResponse> requestTerminateTask(String taskId) {
        return this.cloudFoundryClient
                .tasks()
                .cancel(CancelTaskRequest.builder().taskId(taskId).build());
    }

    private Mono<AbstractApplicationResource> requestUpdateApplication(
            String applicationId,
            Map<String, Object> environmentJsons,
            ApplicationManifest manifest,
            String stackId) {
        return requestUpdateApplication(
                applicationId,
                builder -> {
                    builder.command(manifest.getCommand())
                            .diskQuota(manifest.getDisk())
                            .environmentJsons(environmentJsons)
                            .healthCheckHttpEndpoint(manifest.getHealthCheckHttpEndpoint())
                            .healthCheckTimeout(manifest.getTimeout())
                            .healthCheckType(
                                    Optional.ofNullable(manifest.getHealthCheckType())
                                            .map(ApplicationHealthCheck::getValue)
                                            .orElse(null))
                            .instances(manifest.getInstances())
                            .memory(manifest.getMemory())
                            .name(manifest.getName())
                            .stackId(stackId);

                    if (manifest.getBuildpacks() != null && manifest.getBuildpacks().size() == 1) {
                        builder.buildpack(manifest.getBuildpacks().get(0));
                    }

                    if (manifest.getDocker() != null) {
                        Optional.ofNullable(manifest.getDocker().getImage())
                                .ifPresent(
                                        image -> {
                                            builder.diego(true).dockerImage(image);

                                            String username = manifest.getDocker().getUsername();
                                            String password = manifest.getDocker().getPassword();
                                            builder.dockerCredentials(
                                                    DockerCredentials.builder()
                                                            .username(username)
                                                            .password(password)
                                                            .build());
                                        });
                    }

                    return builder;
                });
    }

    private Mono<AbstractApplicationResource> requestUpdateApplication(
            String applicationId, UnaryOperator<UpdateApplicationRequest.Builder> modifier) {
        return this.cloudFoundryClient
                .applicationsV2()
                .update(
                        modifier.apply(
                                        UpdateApplicationRequest.builder()
                                                .applicationId(applicationId))
                                .build())
                .cast(AbstractApplicationResource.class);
    }

    private Mono<Void> requestUpdateApplicationV3(
            String applicationId,
            UnaryOperator<org.cloudfoundry.client.v3.applications.UpdateApplicationRequest.Builder>
                    modifier) {
        return this.cloudFoundryClient
                .applicationsV3()
                .update(
                        modifier.apply(
                                        org.cloudfoundry.client.v3.applications
                                                .UpdateApplicationRequest.builder()
                                                .applicationId(applicationId))
                                .build())
                .then();
    }

    private Mono<UpdateApplicationEnvironmentVariablesResponse> requestSetEnvironmentVariable(
            String applicationId, String name, String value) {
        return this.cloudFoundryClient
                .applicationsV3()
                .updateEnvironmentVariables(
                        UpdateApplicationEnvironmentVariablesRequest.builder()
                                .applicationId(applicationId)
                                .var(name, value)
                                .build());
    }

    private Mono<UpdateApplicationEnvironmentVariablesResponse> requestUnsetEnvironmentVariable(
            String applicationId, String name) {
        return this.cloudFoundryClient
                .applicationsV3()
                .updateEnvironmentVariables(
                        UpdateApplicationEnvironmentVariablesRequest.builder()
                                .applicationId(applicationId)
                                .var(name, null)
                                .build());
    }

    private Mono<ApplicationFeature> requestUpdateApplicationSsh(
            String applicationId, boolean enabled) {
        return requestUpdateApplicationFeature(
                applicationId, builder -> builder.featureName(APP_FEATURE_SSH).enabled(enabled));
    }

    private Mono<ApplicationFeature> requestUpdateApplicationFeature(
            String applicationId, UnaryOperator<UpdateApplicationFeatureRequest.Builder> modifier) {
        return this.cloudFoundryClient
                .applicationsV3()
                .updateFeature(
                        modifier.apply(
                                        org.cloudfoundry.client.v3.applications
                                                .UpdateApplicationFeatureRequest.builder()
                                                .applicationId(applicationId))
                                .build())
                .cast(ApplicationFeature.class);
    }

    private Mono<AbstractApplicationResource> requestUpdateApplicationScale(
            String applicationId, Integer disk, Integer instances, Integer memory) {
        return requestUpdateApplication(
                applicationId,
                builder -> builder.diskQuota(disk).instances(instances).memory(memory));
    }

    private Mono<AbstractApplicationResource> requestUpdateApplicationState(
            String applicationId, String state) {
        return requestUpdateApplication(applicationId, builder -> builder.state(state));
    }

    private Mono<UploadApplicationResponse> requestUploadApplication(
            String applicationId,
            Path application,
            List<ResourceMatchingUtils.ArtifactMetadata> matchedResources) {
        UploadApplicationRequest request =
                matchedResources.stream()
                        .reduce(
                                UploadApplicationRequest.builder()
                                        .application(application)
                                        .applicationId(applicationId)
                                        .async(true),
                                (builder, artifactMetadata) ->
                                        builder.resource(
                                                org.cloudfoundry.client.v2.applications.Resource
                                                        .builder()
                                                        .hash(artifactMetadata.getHash())
                                                        .mode(artifactMetadata.getPermissions())
                                                        .path(artifactMetadata.getPath())
                                                        .size(artifactMetadata.getSize())
                                                        .build()),
                                (a, b) -> a)
                        .build();

        return this.cloudFoundryClient.applicationsV2().upload(request);
    }

    private Mono<Void> requestUploadPackage(
            String packageId, Path bits, List<MatchedResource> matchedResources) {
        return this.cloudFoundryClient
                .packages()
                .upload(
                        UploadPackageRequest.builder()
                                .packageId(packageId)
                                .bits(bits)
                                .resources(matchedResources)
                                .build())
                .then();
    }

    private Mono<Void> restageApplication(
            String application,
            String applicationId,
            Duration stagingTimeout,
            Duration startupTimeout) {
        return requestRestageApplication(applicationId)
                .flatMap(response -> waitForStaging(application, applicationId, stagingTimeout))
                .then(waitForRunning(application, applicationId, startupTimeout));
    }

    private Mono<Void> restartApplication(
            String application,
            String applicationId,
            Duration stagingTimeout,
            Duration startupTimeout) {
        return stopApplication(applicationId)
                .then(
                        startApplicationAndWait(
                                application, applicationId, stagingTimeout, startupTimeout));
    }

    private static boolean shouldStartApplication(
            PushApplicationManifestRequest request, AbstractApplicationResource resource) {
        return shouldStartApplication(request) && getInstances(resource) > 0;
    }

    private static boolean shouldStartApplication(PushApplicationManifestRequest request) {
        return !Optional.ofNullable(request.getNoStart()).orElse(false);
    }

    private Mono<Void> startApplicationAndWait(
            String application,
            String applicationId,
            Duration stagingTimeout,
            Duration startupTimeout) {
        return requestUpdateApplicationState(applicationId, STARTED_STATE)
                .flatMap(response -> waitForStaging(application, applicationId, stagingTimeout))
                .then(waitForRunning(application, applicationId, startupTimeout));
    }

    private Mono<Void> stopAndStartApplication(
            String applicationId, String name, PushApplicationManifestRequest request) {
        return stopApplication(applicationId)
                .filter(resource -> shouldStartApplication(request, resource))
                .flatMap(
                        resource ->
                                startApplicationAndWait(
                                        name,
                                        applicationId,
                                        request.getStagingTimeout(),
                                        request.getStartupTimeout()));
    }

    private Mono<AbstractApplicationResource> stopApplication(String applicationId) {
        return requestUpdateApplicationState(applicationId, STOPPED_STATE);
    }

    private Mono<AbstractApplicationResource> stopApplicationIfNotStopped(
            AbstractApplicationResource resource) {
        return isNotIn(resource, STOPPED_STATE)
                ? stopApplication(ResourceUtils.getId(resource))
                : Mono.just(resource);
    }

    private static ApplicationDetail toApplicationDetail(
            List<String> buildpacks,
            SummaryApplicationResponse summaryApplicationResponse,
            String stackName,
            List<InstanceDetail> instanceDetails) {
        if (buildpacks.size() == 0) {
            buildpacks =
                    Collections.singletonList(summaryApplicationResponse.getDetectedBuildpack());
        }

        return ApplicationDetail.builder()
                .buildpacks(buildpacks)
                .diskQuota(summaryApplicationResponse.getDiskQuota())
                .id(summaryApplicationResponse.getId())
                .instanceDetails(instanceDetails)
                .instances(summaryApplicationResponse.getInstances())
                .lastUploaded(toDate(summaryApplicationResponse.getPackageUpdatedAt()))
                .memoryLimit(summaryApplicationResponse.getMemory())
                .name(summaryApplicationResponse.getName())
                .requestedState(summaryApplicationResponse.getState())
                .runningInstances(summaryApplicationResponse.getRunningInstances())
                .stack(stackName)
                .urls(toUrls(summaryApplicationResponse.getRoutes()))
                .build();
    }

    private static ApplicationEnvironments toApplicationEnvironments(
            GetApplicationEnvironmentResponse response) {
        return ApplicationEnvironments.builder()
                .running(response.getRunningEnvironmentVariables())
                .staging(response.getStagingEnvironmentVariables())
                .systemProvided(response.getSystemEnvironmentVariables())
                .userProvided(response.getEnvironmentVariables())
                .build();
    }

    private Mono<ApplicationManifest> toApplicationManifest(
            List<String> buildpacks, SummaryApplicationResponse response, String stackName) {
        ApplicationManifest.Builder builder =
                ApplicationManifest.builder()
                        .command(response.getCommand())
                        .disk(response.getDiskQuota())
                        .environmentVariables(response.getEnvironmentJsons())
                        .healthCheckHttpEndpoint(response.getHealthCheckHttpEndpoint())
                        .healthCheckType(ApplicationHealthCheck.from(response.getHealthCheckType()))
                        .instances(response.getInstances())
                        .memory(response.getMemory())
                        .docker(toDocker(response))
                        .name(response.getName())
                        .stack(stackName)
                        .timeout(response.getHealthCheckTimeout());

        if (buildpacks != null && !buildpacks.isEmpty()) {
            builder.buildpacks(buildpacks);
        }

        for (org.cloudfoundry.client.v2.routes.Route route :
                Optional.ofNullable(response.getRoutes()).orElse(Collections.emptyList())) {
            builder.route(Route.builder().route(toUrl(route)).build());
        }

        if (Optional.ofNullable(response.getRoutes()).orElse(Collections.emptyList()).isEmpty()) {
            builder.noRoute(true);
        }

        for (ServiceInstance service :
                Optional.ofNullable(response.getServices()).orElse(Collections.emptyList())) {
            Optional.ofNullable(service.getName()).ifPresent(builder::service);
        }

        return Mono.just(builder.build());
    }

    private static ApplicationSummary toApplicationSummary(
            SpaceApplicationSummary spaceApplicationSummary) {
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

    private static Docker toDocker(SummaryApplicationResponse response) {
        if (response.getDockerImage() == null) {
            return null;
        }
        return Docker.builder()
                .image(response.getDockerImage())
                .username(getUsername(response.getDockerCredentials()))
                .password(getPassword(response.getDockerCredentials()))
                .build();
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

    private static ApplicationHealthCheck toHealthCheck(HealthCheckType type) {
        if (type == HealthCheckType.HTTP) {
            return ApplicationHealthCheck.HTTP;
        } else if (type == HealthCheckType.PORT) {
            return ApplicationHealthCheck.PORT;
        } else if (type == HealthCheckType.PROCESS) {
            return ApplicationHealthCheck.PROCESS;
        } else if (type == HealthCheckType.NONE) {
            return ApplicationHealthCheck.NONE;
        } else {
            return null;
        }
    }

    private static HealthCheckType fromHealthCheck(ApplicationHealthCheck type) {
        if (type == ApplicationHealthCheck.HTTP) {
            return HealthCheckType.HTTP;
        } else if (type == ApplicationHealthCheck.PORT) {
            return HealthCheckType.PORT;
        } else if (type == ApplicationHealthCheck.PROCESS) {
            return HealthCheckType.PROCESS;
        } else if (type == ApplicationHealthCheck.NONE) {
            return HealthCheckType.NONE;
        } else {
            return null;
        }
    }

    private List<InstanceDetail> toInstanceDetailList(
            GetApplicationProcessStatisticsResponse statisticsResponse) {
        return statisticsResponse.getResources().stream()
                .map(
                        statisticsResource -> {
                            ProcessUsage usage =
                                    Optional.ofNullable(statisticsResource.getUsage())
                                            .orElse(ProcessUsage.builder().build());
                            return InstanceDetail.builder()
                                    .index(statisticsResource.getIndex().toString())
                                    .state(statisticsResource.getState().getValue())
                                    .cpu(usage.getCpu())
                                    .memoryUsage(usage.getMemory())
                                    .diskUsage(usage.getDisk())
                                    .diskQuota(statisticsResource.getDiskQuota())
                                    .memoryQuota(statisticsResource.getMemoryQuota())
                                    .build();
                        })
                .collect(Collectors.toList());
    }

    private static Task toTask(org.cloudfoundry.client.v3.tasks.Task task) {
        return Task.builder()
                .command(task.getCommand())
                .sequenceId(task.getSequenceId())
                .name(task.getName())
                .startTime(task.getCreatedAt())
                .state(TaskState.valueOf(task.getState().getValue()))
                .build();
    }

    private static String toUrl(org.cloudfoundry.client.v2.routes.Route route) {
        StringBuilder sb = new StringBuilder();
        if (route.getHost() != null && !route.getHost().isEmpty()) {
            sb.append(route.getHost()).append(".");
        }
        Optional.ofNullable(route.getDomain().getName()).ifPresent(sb::append);

        if (route.getPort() == null) {
            Optional.ofNullable(route.getPath()).ifPresent(sb::append);
        } else {
            sb.append(":").append(route.getPort());
        }

        return sb.toString();
    }

    private static List<String> toUrls(List<org.cloudfoundry.client.v2.routes.Route> routes) {
        return routes.stream().map(DefaultApplications::toUrl).collect(Collectors.toList());
    }

    private Mono<Void> updateBuildpacks(String applicationId, ApplicationManifest manifest) {
        if (manifest.getBuildpacks() == null || manifest.getBuildpacks().size() < 2) {
            return Mono.empty();
        }

        return this.cloudFoundryClient
                .applicationsV3()
                .update(
                        org.cloudfoundry.client.v3.applications.UpdateApplicationRequest.builder()
                                .applicationId(applicationId)
                                .lifecycle(
                                        Lifecycle.builder()
                                                .data(
                                                        BuildpackData.builder()
                                                                .addAllBuildpacks(
                                                                        manifest.getBuildpacks())
                                                                .build())
                                                .type(BUILDPACK)
                                                .build())
                                .build())
                .then();
    }

    private Mono<Void> uploadApplicationAndWait(
            String applicationId,
            Path application,
            List<ResourceMatchingUtils.ArtifactMetadata> matchedResources,
            Duration stagingTimeout) {
        return Mono.defer(
                        () -> {
                            if (matchedResources.isEmpty()) {
                                return requestUploadApplication(
                                        applicationId, application, matchedResources);
                            } else {
                                List<String> paths =
                                        matchedResources.stream()
                                                .map(
                                                        ResourceMatchingUtils.ArtifactMetadata
                                                                ::getPath)
                                                .collect(Collectors.toList());

                                return FileUtils.compress(application, p -> !paths.contains(p))
                                        .flatMap(
                                                filteredApplication ->
                                                        requestUploadApplication(
                                                                        applicationId,
                                                                        filteredApplication,
                                                                        matchedResources)
                                                                .doOnTerminate(
                                                                        () -> {
                                                                            try {
                                                                                Files.delete(
                                                                                        filteredApplication);
                                                                            } catch (
                                                                                    IOException e) {
                                                                                throw Exceptions
                                                                                        .propagate(
                                                                                                e);
                                                                            }
                                                                        }));
                            }
                        })
                .flatMap(
                        job ->
                                JobUtils.waitForCompletion(
                                        this.cloudFoundryClient, stagingTimeout, job));
    }

    private Mono<GetPackageResponse> uploadPackageBitsAndWait(
            String packageId,
            Path application,
            List<MatchedResource> matchedResources,
            Duration processingTimeout) {
        return Mono.defer(
                        () -> {
                            if (matchedResources.isEmpty()) {
                                return requestUploadPackage(
                                        packageId, application, matchedResources);
                            } else {
                                List<String> paths =
                                        matchedResources.stream()
                                                .map(MatchedResource::getPath)
                                                .collect(Collectors.toList());

                                return FileUtils.compress(application, p -> !paths.contains(p))
                                        .flatMap(
                                                filteredApplication ->
                                                        requestUploadPackage(
                                                                        packageId,
                                                                        filteredApplication,
                                                                        matchedResources)
                                                                .doOnTerminate(
                                                                        () -> {
                                                                            try {
                                                                                Files.delete(
                                                                                        filteredApplication);
                                                                            } catch (
                                                                                    IOException e) {
                                                                                throw Exceptions
                                                                                        .propagate(
                                                                                                e);
                                                                            }
                                                                        }));
                            }
                        })
                .then(waitForUploadProcessingCompleted(packageId, processingTimeout));
    }

    private Mono<GetBuildResponse> waitForBuildStaging(
            String buildId, String applicationName, Duration stagingTimeout) {
        Duration timeout = Optional.ofNullable(stagingTimeout).orElse(Duration.ofMinutes(15));
        return this.cloudFoundryClient
                .builds()
                .get(GetBuildRequest.builder().buildId(buildId).build())
                .filter(
                        build ->
                                EnumSet.of(BuildState.STAGED, BuildState.FAILED)
                                        .contains(build.getState()))
                .repeatWhenEmpty(
                        exponentialBackOff(Duration.ofSeconds(1), Duration.ofSeconds(15), timeout))
                .filter(build -> build.getState() == BuildState.STAGED)
                .switchIfEmpty(
                        ExceptionUtils.illegalState(
                                "Build %s of Application %s failed during staging",
                                buildId, applicationName))
                .onErrorResume(
                        DelayTimeoutException.class,
                        t ->
                                ExceptionUtils.illegalState(
                                        "Build %s of Application %s timed out during staging",
                                        buildId, applicationName));
    }

    private Mono<Void> waitForRunning(
            String application, String applicationId, Duration startupTimeout) {
        Duration timeout = Optional.ofNullable(startupTimeout).orElse(Duration.ofMinutes(5));

        return requestApplicationStatisticsV3(applicationId)
                .flatMapIterable(GetApplicationProcessStatisticsResponse::getResources)
                .map(ProcessStatisticsResource::getState)
                .reduce(ProcessState.STARTING, collectStates())
                .filter(isInstanceComplete())
                .repeatWhenEmpty(
                        exponentialBackOff(Duration.ofSeconds(1), Duration.ofSeconds(15), timeout))
                .filter(isRunning())
                .switchIfEmpty(
                        ExceptionUtils.illegalState(
                                "Application %s failed during start", application))
                .onErrorResume(
                        DelayTimeoutException.class,
                        t ->
                                ExceptionUtils.illegalState(
                                        "Application %s timed out during start", application))
                .then();
    }

    private Mono<Void> waitForRunningV3(
            String applicationName, String applicationId, Duration startupTimeout) {
        return waitForRunning(applicationName, applicationId, startupTimeout);
    }

    private Mono<Void> waitForStaging(
            String application, String applicationId, Duration stagingTimeout) {
        Duration timeout = Optional.ofNullable(stagingTimeout).orElse(Duration.ofMinutes(15));

        return requestGetApplication(applicationId)
                .map(response -> ResourceUtils.getEntity(response).getPackageState())
                .filter(isStagingComplete())
                .repeatWhenEmpty(
                        exponentialBackOff(Duration.ofSeconds(1), Duration.ofSeconds(15), timeout))
                .filter(isStaged())
                .switchIfEmpty(
                        ExceptionUtils.illegalState(
                                "Application %s failed during staging", application))
                .onErrorResume(
                        DelayTimeoutException.class,
                        t ->
                                ExceptionUtils.illegalState(
                                        "Application %s timed out during staging", application))
                .then();
    }

    private Mono<GetPackageResponse> waitForUploadProcessingCompleted(
            String packageId, Duration processingTimeout) {
        return this.cloudFoundryClient
                .packages()
                .get(GetPackageRequest.builder().packageId(packageId).build())
                .filter(
                        packageResponse ->
                                EnumSet.of(
                                                PackageState.READY,
                                                PackageState.FAILED,
                                                PackageState.EXPIRED)
                                        .contains(packageResponse.getState()))
                .repeatWhenEmpty(
                        exponentialBackOff(
                                Duration.ofSeconds(1), Duration.ofSeconds(15), processingTimeout))
                .filter(packageResponse -> packageResponse.getState() == PackageState.READY)
                .switchIfEmpty(
                        ExceptionUtils.illegalState(
                                "Package %s failed upload processing", packageId))
                .onErrorResume(
                        DelayTimeoutException.class,
                        t ->
                                ExceptionUtils.illegalState(
                                        "Package %s timed out during upload processing",
                                        packageId));
    }
}
