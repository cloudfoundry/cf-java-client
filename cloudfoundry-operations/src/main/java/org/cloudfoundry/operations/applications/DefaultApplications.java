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

package org.cloudfoundry.operations.applications;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.CloudFoundryException;
import org.cloudfoundry.client.v2.PaginatedRequest;
import org.cloudfoundry.client.v2.applications.AbstractApplicationResource;
import org.cloudfoundry.client.v2.applications.ApplicationEnvironmentRequest;
import org.cloudfoundry.client.v2.applications.ApplicationEnvironmentResponse;
import org.cloudfoundry.client.v2.applications.ApplicationInstanceInfo;
import org.cloudfoundry.client.v2.applications.ApplicationInstancesRequest;
import org.cloudfoundry.client.v2.applications.ApplicationInstancesResponse;
import org.cloudfoundry.client.v2.applications.ApplicationResource;
import org.cloudfoundry.client.v2.applications.ApplicationStatisticsRequest;
import org.cloudfoundry.client.v2.applications.ApplicationStatisticsResponse;
import org.cloudfoundry.client.v2.applications.AssociateApplicationRouteRequest;
import org.cloudfoundry.client.v2.applications.AssociateApplicationRouteResponse;
import org.cloudfoundry.client.v2.applications.CopyApplicationRequest;
import org.cloudfoundry.client.v2.applications.CopyApplicationResponse;
import org.cloudfoundry.client.v2.applications.CreateApplicationRequest;
import org.cloudfoundry.client.v2.applications.CreateApplicationResponse;
import org.cloudfoundry.client.v2.applications.GetApplicationResponse;
import org.cloudfoundry.client.v2.applications.ListApplicationServiceBindingsRequest;
import org.cloudfoundry.client.v2.applications.RemoveApplicationServiceBindingRequest;
import org.cloudfoundry.client.v2.applications.RestageApplicationResponse;
import org.cloudfoundry.client.v2.applications.SummaryApplicationRequest;
import org.cloudfoundry.client.v2.applications.SummaryApplicationResponse;
import org.cloudfoundry.client.v2.applications.TerminateApplicationInstanceRequest;
import org.cloudfoundry.client.v2.applications.UpdateApplicationRequest;
import org.cloudfoundry.client.v2.applications.UpdateApplicationResponse;
import org.cloudfoundry.client.v2.applications.UploadApplicationRequest;
import org.cloudfoundry.client.v2.applications.UploadApplicationResponse;
import org.cloudfoundry.client.v2.events.EventEntity;
import org.cloudfoundry.client.v2.events.EventResource;
import org.cloudfoundry.client.v2.events.ListEventsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationPrivateDomainsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationSpacesRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsRequest;
import org.cloudfoundry.client.v2.organizations.OrganizationResource;
import org.cloudfoundry.client.v2.privatedomains.PrivateDomainResource;
import org.cloudfoundry.client.v2.routes.CreateRouteResponse;
import org.cloudfoundry.client.v2.routes.DeleteRouteRequest;
import org.cloudfoundry.client.v2.routes.DeleteRouteResponse;
import org.cloudfoundry.client.v2.routes.ListRoutesRequest;
import org.cloudfoundry.client.v2.routes.Route;
import org.cloudfoundry.client.v2.routes.RouteResource;
import org.cloudfoundry.client.v2.servicebindings.ServiceBindingResource;
import org.cloudfoundry.client.v2.serviceinstances.ServiceInstance;
import org.cloudfoundry.client.v2.shareddomains.ListSharedDomainsRequest;
import org.cloudfoundry.client.v2.shareddomains.SharedDomainResource;
import org.cloudfoundry.client.v2.spaces.GetSpaceRequest;
import org.cloudfoundry.client.v2.spaces.GetSpaceResponse;
import org.cloudfoundry.client.v2.spaces.GetSpaceSummaryRequest;
import org.cloudfoundry.client.v2.spaces.GetSpaceSummaryResponse;
import org.cloudfoundry.client.v2.spaces.ListSpaceApplicationsRequest;
import org.cloudfoundry.client.v2.spaces.SpaceApplicationSummary;
import org.cloudfoundry.client.v2.spaces.SpaceResource;
import org.cloudfoundry.client.v2.stacks.GetStackRequest;
import org.cloudfoundry.client.v2.stacks.GetStackResponse;
import org.cloudfoundry.client.v2.stacks.ListStacksRequest;
import org.cloudfoundry.client.v2.stacks.StackResource;
import org.cloudfoundry.logging.LogMessage;
import org.cloudfoundry.logging.LoggingClient;
import org.cloudfoundry.logging.RecentLogsRequest;
import org.cloudfoundry.logging.StreamLogsRequest;
import org.cloudfoundry.util.DateUtils;
import org.cloudfoundry.util.DelayTimeoutException;
import org.cloudfoundry.util.ExceptionUtils;
import org.cloudfoundry.util.JobUtils;
import org.cloudfoundry.util.OperationUtils;
import org.cloudfoundry.util.PaginationUtils;
import org.cloudfoundry.util.ResourceUtils;
import org.cloudfoundry.util.StringMap;
import org.cloudfoundry.util.ValidationUtils;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.tuple.Tuple2;
import reactor.core.tuple.Tuple4;

import java.io.InputStream;
import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Predicate;

import static org.cloudfoundry.util.DelayUtils.exponentialBackOff;
import static org.cloudfoundry.util.OperationUtils.thenKeep;
import static org.cloudfoundry.util.tuple.TupleUtils.function;
import static org.cloudfoundry.util.tuple.TupleUtils.predicate;

public final class DefaultApplications implements Applications {

    private static final int CF_APP_STOPPED_STATS_ERROR = 200003;

    private static final int CF_INSTANCES_ERROR = 220001;

    private static final int CF_STAGING_NOT_FINISHED = 170002;

    private static final int MAX_NUMBER_OF_RECENT_EVENTS = 50;

    private static final String STARTED_STATE = "STARTED";

    private static final String STOPPED_STATE = "STOPPED";

    private final CloudFoundryClient cloudFoundryClient;

    private final Mono<LoggingClient> loggingClient;

    private final RandomWords randomWords;

    private final Mono<String> spaceId;

    public DefaultApplications(CloudFoundryClient cloudFoundryClient, Mono<LoggingClient> loggingClient, Mono<String> spaceId) {
        this(cloudFoundryClient, loggingClient, spaceId, new WordListRandomWords());
    }

    DefaultApplications(CloudFoundryClient cloudFoundryClient, Mono<LoggingClient> loggingClient, Mono<String> spaceId, RandomWords randomWords) {
        this.cloudFoundryClient = cloudFoundryClient;
        this.loggingClient = loggingClient;
        this.spaceId = spaceId;
        this.randomWords = randomWords;
    }

    @Override
    public Mono<Void> copySource(CopySourceApplicationRequest request) {
        return Mono
            .when(ValidationUtils.validate(request), this.spaceId)
            .then(function((request1, spaceId) -> Mono
                .when(
                    Mono.just(request1),
                    getApplicationId(this.cloudFoundryClient, request1.getName(), spaceId),
                    getApplicationIdFromOrgSpace(this.cloudFoundryClient, request1.getTargetName(), spaceId, request1.getTargetOrganization(), request1.getTargetSpace())
                )))
            .then(function((request1, sourceApplicationId, targetApplicationId) -> Mono
                .when(
                    Mono.just(request1),
                    copyBits(this.cloudFoundryClient, sourceApplicationId, targetApplicationId)
                        .after(() -> Mono.just(targetApplicationId))
                )))
            .where(predicate((request1, targetApplicationId) -> Optional.ofNullable(request1.getRestart()).orElse(false)))
            .then(function((request1, targetApplicationId) ->
                restartApplication(this.cloudFoundryClient, request1.getTargetName(), targetApplicationId, request1.getStagingTimeout(), request1.getStartupTimeout())))
            .after();
    }

    @Override
    public Mono<Void> delete(DeleteApplicationRequest request) {
        return Mono
            .when(ValidationUtils.validate(request), this.spaceId)
            .then(function((request1, spaceId) -> getRoutesAndApplicationId(this.cloudFoundryClient, request1, spaceId, Optional.ofNullable(request.getDeleteRoutes()).orElse(false))))
            .then(function((routes, applicationId) -> deleteRoutes(this.cloudFoundryClient, routes)
                .after(() -> Mono.just(applicationId))))
            .as(thenKeep((applicationId -> removeServiceBindings(this.cloudFoundryClient, applicationId))))
            .then(applicationId -> requestDeleteApplication(this.cloudFoundryClient, applicationId));
    }

    @Override
    public Mono<Void> disableSsh(DisableApplicationSshRequest request) {
        return Mono
            .when(ValidationUtils.validate(request), this.spaceId)
            .then(function((request1, spaceId) -> getApplicationIdWhere(this.cloudFoundryClient, request1.getName(), spaceId, sshEnabled(true))))
            .then(applicationId -> requestUpdateApplicationSsh(this.cloudFoundryClient, applicationId, false))
            .after();
    }

    @Override
    public Mono<Void> enableSsh(EnableApplicationSshRequest request) {
        return Mono
            .when(ValidationUtils.validate(request), this.spaceId)
            .then(function((request1, spaceId) -> getApplicationIdWhere(this.cloudFoundryClient, request1.getName(), spaceId, sshEnabled(false))))
            .then(applicationId -> requestUpdateApplicationSsh(this.cloudFoundryClient, applicationId, true))
            .after();
    }

    @Override
    public Mono<ApplicationDetail> get(GetApplicationRequest request) {
        return Mono
            .when(ValidationUtils.validate(request), this.spaceId)
            .then(function((request1, spaceId) -> getApplication(this.cloudFoundryClient, request1.getName(), spaceId)))
            .then(applicationResource -> getAuxiliaryContent(this.cloudFoundryClient, applicationResource))
            .map(function(DefaultApplications::toApplicationDetail));
    }

    @Override
    public Mono<ApplicationManifest> getApplicationManifest(GetApplicationManifestRequest request) {
        return Mono
            .when(ValidationUtils.validate(request), this.spaceId)
            .then(function((request1, spaceId) -> getApplicationId(this.cloudFoundryClient, request1.getName(), spaceId)))
            .then(applicationId -> requestApplicationSummary(this.cloudFoundryClient, applicationId))
            .then(response -> Mono
                .just(response)
                .and(getStackName(this.cloudFoundryClient, response.getStackId())))
            .then(function(DefaultApplications::toApplicationManifest));
    }

    @Override
    public Mono<ApplicationEnvironments> getEnvironments(GetApplicationEnvironmentsRequest request) {
        return Mono
            .when(ValidationUtils.validate(request), this.spaceId)
            .then(function((request1, spaceId) -> getApplicationId(this.cloudFoundryClient, request1.getName(), spaceId)))
            .then(applicationId -> requestApplicationEnvironment(this.cloudFoundryClient, applicationId))
            .map(DefaultApplications::toApplicationEnvironments);
    }

    @Override
    public Flux<ApplicationEvent> getEvents(GetApplicationEventsRequest request) {
        return Mono
            .when(ValidationUtils.validate(request), this.spaceId)
            .then(function((request1, spaceId) -> Mono
                .when(
                    Mono.just(request1),
                    getApplicationId(this.cloudFoundryClient, request1.getName(), spaceId)
                )))
            .flatMap(function((request1, applicationId) -> requestEvents(applicationId, this.cloudFoundryClient)
                .take(Optional.ofNullable(request1.getMaxNumberOfEvents()).orElse(MAX_NUMBER_OF_RECENT_EVENTS))))
            .map(DefaultApplications::convertToApplicationEvent);
    }

    @Override
    public Flux<ApplicationSummary> list() {
        return this.spaceId
            .then(spaceId -> requestSpaceSummary(this.cloudFoundryClient, spaceId))
            .flatMap(DefaultApplications::extractApplications)
            .map(DefaultApplications::toApplicationSummary);
    }

    @Override
    public Flux<LogMessage> logs(LogsRequest request) {
        return Mono
            .when(ValidationUtils.validate(request), this.spaceId)
            .then(function((request1, spaceId) -> getApplicationId(this.cloudFoundryClient, request1.getName(), spaceId)
                .and(Mono.just(request1))))
            .flatMap(function((applicationId, logsRequest) -> getLogs(this.loggingClient, applicationId, logsRequest.getRecent())));
    }

    @Override
    public Mono<Void> push(PushApplicationRequest request) {
        return ValidationUtils
            .validate(request)
            .then(request1 -> Mono
                .when(
                    Mono.just(request1),
                    DefaultApplications.this.spaceId,
                    getOptionalStackId(this.cloudFoundryClient, request1.getStack())
                ))
            .then(function((request1, spaceId, stackId) -> Mono
                .when(
                    getApplicationId(this.cloudFoundryClient, request1, spaceId, stackId.orElse(null)),
                    Mono.just(request1),
                    Mono.just(spaceId)
                )))
            .as(thenKeep(function((applicationId, request1, spaceId) -> prepareDomainsAndRoutes(this.cloudFoundryClient, request1, applicationId, spaceId, this.randomWords))))
            .map(function((applicationId, request1, spaceId) -> Tuple2.of(applicationId, request1)))
            .as(thenKeep(function((applicationId, request1) -> uploadApplicationAndWait(this.cloudFoundryClient, applicationId, request1.getApplication()))))
            .as(thenKeep(function((applicationId, request1) -> stopApplication(this.cloudFoundryClient, applicationId))))
            .where(predicate((applicationId, request1) -> !Optional.ofNullable(request1.getNoStart()).orElse(false)))
            .then(function((applicationId, request1) -> startApplicationAndWait(this.cloudFoundryClient, request1.getName(), applicationId, request1.getStagingTimeout(),
                request1.getStartupTimeout())))
            .after();
    }

    @Override
    public Mono<Void> rename(RenameApplicationRequest request) {
        return Mono
            .when(ValidationUtils.validate(request), this.spaceId)
            .then(function((request1, spaceId) -> Mono
                .when(
                    getApplicationId(this.cloudFoundryClient, request1.getName(), spaceId),
                    Mono.just(request1.getNewName())
                )))
            .then(function((applicationId, newName) -> requestUpdateApplicationRename(this.cloudFoundryClient, applicationId, newName)))
            .after();
    }

    @Override
    public Mono<Void> restage(RestageApplicationRequest request) {
        return Mono
            .when(ValidationUtils.validate(request), this.spaceId)
            .then(function((request1, spaceId) -> Mono
                .just(request1)
                .and(getApplicationId(this.cloudFoundryClient, request1.getName(), spaceId))))
            .then(function((request1, applicationId) -> restageApplication(this.cloudFoundryClient, request1.getName(), applicationId, request1.getStagingTimeout(), request1.getStartupTimeout())))
            .after();
    }

    @Override
    public Mono<Void> restart(RestartApplicationRequest request) {
        return Mono
            .when(ValidationUtils.validate(request), this.spaceId)
            .then(function((request1, spaceId) -> getApplication(this.cloudFoundryClient, request1.getName(), spaceId)
                .and(Mono.just(request1))))
            .then(function((resource, request1) -> stopApplicationIfNotStopped(cloudFoundryClient, resource)
                .then(resource1 -> startApplicationAndWait(this.cloudFoundryClient, request1.getName(), ResourceUtils.getId(resource1), request1.getStagingTimeout(), request1.getStartupTimeout()))))
            .after();
    }

    @Override
    public Mono<Void> restartInstance(RestartApplicationInstanceRequest request) {
        return Mono
            .when(ValidationUtils.validate(request), this.spaceId)
            .then(function((request1, spaceId) -> Mono
                .when(
                    getApplicationId(this.cloudFoundryClient, request1.getName(), spaceId),
                    Mono.just(String.valueOf(request1.getInstanceIndex()))
                )))
            .then(function((applicationId, instanceIndex) -> requestTerminateApplicationInstance(this.cloudFoundryClient, applicationId, instanceIndex)));
    }

    @Override
    public Mono<Void> scale(ScaleApplicationRequest request) {
        return Mono
            .when(
                ValidationUtils
                    .validate(request)
                    .where(DefaultApplications::areModifiersPresent),
                this.spaceId
            )
            .then(function((request1, spaceId) -> Mono
                .when(
                    Mono.just(request1),
                    getApplicationId(this.cloudFoundryClient, request1.getName(), spaceId)
                )))
            .then(function((request1, applicationId) -> Mono
                .when(
                    Mono.just(request1),
                    requestUpdateApplicationScale(this.cloudFoundryClient, applicationId, request1.getDiskLimit(), request1.getInstances(), request1.getMemoryLimit())
                )))
            .where(predicate(DefaultApplications::isRestartRequired))
            .then(function((request1, resource) ->
                restartApplication(this.cloudFoundryClient, request1.getName(), ResourceUtils.getId(resource), request1.getStagingTimeout(), request1.getStartupTimeout())))
            .after();
    }

    @Override
    public Mono<Void> setEnvironmentVariable(SetEnvironmentVariableApplicationRequest request) {
        return Mono
            .when(ValidationUtils.validate(request), this.spaceId)
            .then(function((request1, spaceId) -> Mono
                .when(
                    Mono.just(request1),
                    getApplication(this.cloudFoundryClient, request1.getName(), spaceId)
                )))
            .then(function((request1, resource) ->
                requestUpdateApplicationEnvironment(
                    this.cloudFoundryClient,
                    ResourceUtils.getId(resource),
                    addToEnvironment(getEnvironment(resource), request1.getVariableName(), request1.getVariableValue())
                )))
            .after();
    }

    @Override
    public Mono<Boolean> sshEnabled(ApplicationSshEnabledRequest request) {
        return Mono
            .when(ValidationUtils.validate(request), this.spaceId)
            .then(function((request1, spaceId) -> getApplication(this.cloudFoundryClient, request1.getName(), spaceId)))
            .map(applicationResource -> ResourceUtils.getEntity(applicationResource).getEnableSsh());
    }

    @Override
    public Mono<Void> start(StartApplicationRequest request) {
        return Mono
            .when(ValidationUtils.validate(request), this.spaceId)
            .then(function((request1, spaceId) -> getApplicationIdWhere(this.cloudFoundryClient, request1.getName(), spaceId, isNotIn(STARTED_STATE))
                .and(Mono.just(request1))))
            .then(function((applicationId, request1) ->
                startApplicationAndWait(this.cloudFoundryClient, request1.getName(), applicationId, request1.getStagingTimeout(), request1.getStartupTimeout())))
            .after();
    }

    @Override
    public Mono<Void> stop(StopApplicationRequest request) {
        return Mono
            .when(ValidationUtils.validate(request), this.spaceId)
            .then(function((request1, spaceId) -> getApplicationIdWhere(this.cloudFoundryClient, request1.getName(), spaceId, isNotIn(STOPPED_STATE))))
            .then(applicationId -> stopApplication(this.cloudFoundryClient, applicationId))
            .after();
    }

    @Override
    public Mono<Void> unsetEnvironmentVariable(UnsetEnvironmentVariableApplicationRequest request) {
        return Mono
            .when(ValidationUtils.validate(request), this.spaceId)
            .then(function((request1, spaceId) -> Mono.when(Mono.just(request1), getApplication(this.cloudFoundryClient, request1.getName(), spaceId))))
            .then(function((request1, resource) ->
                requestUpdateApplicationEnvironment(
                    this.cloudFoundryClient,
                    ResourceUtils.getId(resource),
                    removeFromEnvironment(getEnvironment(resource), request1.getVariableName())
                )))
            .after();
    }

    private static boolean applicationInstancesUnavailable(Throwable throwable) {
        return throwable instanceof CloudFoundryException &&
            (((CloudFoundryException) throwable).getCode() == CF_INSTANCES_ERROR || ((CloudFoundryException) throwable).getCode() == CF_STAGING_NOT_FINISHED);
    }

    private static boolean applicationStatisticsUnavailable(Throwable throwable) {
        return throwable instanceof CloudFoundryException && ((CloudFoundryException) throwable).getCode() == CF_APP_STOPPED_STATS_ERROR;
    }

    private static Map<String, Object> addToEnvironment(Map<String, Object> environment, String variableName, Object variableValue) {
        return StringMap.builder()
            .entries(environment)
            .entry(variableName, variableValue)
            .build();
    }

    private static boolean areModifiersPresent(ScaleApplicationRequest request) {
        return request.getMemoryLimit() != null || request.getDiskLimit() != null || request.getInstances() != null;
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

    private static Mono<Void> copyBits(CloudFoundryClient cloudFoundryClient, String sourceApplicationId, String targetApplicationId) {
        return requestCopyBits(cloudFoundryClient, sourceApplicationId, targetApplicationId)
            .then(job -> JobUtils.waitForCompletion(cloudFoundryClient, job));
    }

    private static Mono<Void> deleteRoute(CloudFoundryClient cloudFoundryClient, String routeId) {
        return requestDeleteRoute(cloudFoundryClient, routeId)
            .then(job -> JobUtils.waitForCompletion(cloudFoundryClient, job));
    }

    private static Mono<Void> deleteRoutes(CloudFoundryClient cloudFoundryClient, Optional<List<Route>> routes) {
        return routes
            .map(Flux::fromIterable)
            .orElse(Flux.empty())
            .map(Route::getId)
            .flatMap(routeId -> deleteRoute(cloudFoundryClient, routeId))
            .after();
    }

    private static String deriveHostname(PushApplicationRequest request, RandomWords randomWords) {
        if (Optional.ofNullable(request.getNoHostname()).orElse(false)) {
            return null;
        } else if (request.getHost() != null) {
            return request.getHost();
        } else if (Optional.ofNullable(request.getRandomRoute()).orElse(false)) {
            return String.join("-", request.getName(), randomWords.getAdjective(), randomWords.getNoun());
        } else {
            return request.getName();
        }
    }

    private static ApplicationStatisticsResponse.InstanceStats.Statistics emptyApplicationStatistics() {
        return ApplicationStatisticsResponse.InstanceStats.Statistics.builder()
            .usage(emptyApplicationUsage())
            .build();
    }

    private static ApplicationStatisticsResponse.InstanceStats.Statistics.Usage emptyApplicationUsage() {
        return ApplicationStatisticsResponse.InstanceStats.Statistics.Usage.builder()
            .build();
    }

    private static ApplicationStatisticsResponse.InstanceStats emptyInstanceStats() {
        return ApplicationStatisticsResponse.InstanceStats.builder()
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
            .otherwise(ExceptionUtils.replace(NoSuchElementException.class, () -> ExceptionUtils.illegalArgument("Application %s does not exist", application)));
    }

    private static Mono<String> getApplicationId(CloudFoundryClient cloudFoundryClient, String application, String spaceId) {
        return getApplication(cloudFoundryClient, application, spaceId)
            .map(ResourceUtils::getId);
    }

    private static Mono<String> getApplicationId(CloudFoundryClient cloudFoundryClient, PushApplicationRequest request, String spaceId, String stackId) {
        return requestApplications(cloudFoundryClient, request.getName(), spaceId)
            .singleOrEmpty()
            .map(ResourceUtils::getId)
            .then(applicationId -> requestUpdateApplication(cloudFoundryClient, applicationId, request, stackId)
                .map(ResourceUtils::getId))
            .otherwiseIfEmpty(requestCreateApplication(cloudFoundryClient, request, spaceId, stackId)
                .map(ResourceUtils::getId)
            );
    }

    private static Mono<String> getApplicationIdFromOrgSpace(CloudFoundryClient cloudFoundryClient, String application, String spaceId, String organization, String space) {
        return
            getSpaceOrganizationId(cloudFoundryClient, spaceId)
                .then(organizationId -> organization != null ? getOrganizationId(cloudFoundryClient, organization) : Mono.just(organizationId))
                .then(organizationId -> space != null ? getSpaceId(cloudFoundryClient, organizationId, space) : Mono.just(spaceId))
                .then(spaceId1 -> getApplicationId(cloudFoundryClient, application, spaceId1));
    }

    private static Mono<String> getApplicationIdWhere(CloudFoundryClient cloudFoundryClient, String application, String spaceId, Predicate<AbstractApplicationResource> predicate) {
        return getApplication(cloudFoundryClient, application, spaceId)
            .where(predicate)
            .map(ResourceUtils::getId);
    }

    private static Mono<ApplicationInstancesResponse> getApplicationInstances(CloudFoundryClient cloudFoundryClient, String applicationId) {
        return requestApplicationInstances(cloudFoundryClient, applicationId)
            .otherwise(throwable -> {
                if (applicationInstancesUnavailable(throwable)) {
                    return Mono.just(ApplicationInstancesResponse.builder().build());
                } else {
                    return Mono.error(throwable);
                }
            });
    }

    private static Mono<ApplicationStatisticsResponse> getApplicationStatistics(CloudFoundryClient cloudFoundryClient, String applicationId) {
        return requestApplicationStatistics(cloudFoundryClient, applicationId)
            .otherwise(throwable -> {
                if (applicationStatisticsUnavailable(throwable)) {
                    return Mono.just(ApplicationStatisticsResponse.builder().build());
                } else {
                    return Mono.error(throwable);
                }
            });
    }

    private static Mono<Tuple4<ApplicationStatisticsResponse, SummaryApplicationResponse, GetStackResponse, ApplicationInstancesResponse>> getAuxiliaryContent(
        CloudFoundryClient cloudFoundryClient, AbstractApplicationResource applicationResource) {

        String applicationId = ResourceUtils.getId(applicationResource);
        String stackId = ResourceUtils.getEntity(applicationResource).getStackId();

        return Mono
            .when(
                getApplicationStatistics(cloudFoundryClient, applicationId),
                requestApplicationSummary(cloudFoundryClient, applicationId),
                requestStack(cloudFoundryClient, stackId),
                getApplicationInstances(cloudFoundryClient, applicationId)
            );
    }

    private static String getBuildpack(SummaryApplicationResponse response) {
        return Optional
            .ofNullable(response.getBuildpack())
            .orElse(response.getDetectedBuildpack());
    }

    private static Mono<String> getDomainId(CloudFoundryClient cloudFoundryClient, String domain, String organizationId) {
        if (domain == null) {
            return getSharedDomainIds(cloudFoundryClient)
                .switchIfEmpty(getPrivateDomainIds(cloudFoundryClient, organizationId))
                .next()
                .otherwiseIfEmpty(ExceptionUtils.illegalState("Domain not found"));
        } else {
            return getPrivateDomainId(cloudFoundryClient, domain, organizationId)
                .otherwiseIfEmpty(getSharedDomainId(cloudFoundryClient, domain))
                .otherwiseIfEmpty(ExceptionUtils.illegalState("Domain %s not found", domain));
        }
    }

    private static Map<String, Object> getEnvironment(AbstractApplicationResource resource) {
        return ResourceUtils.getEntity(resource).getEnvironmentJsons();
    }

    private static Publisher<LogMessage> getLogs(Mono<LoggingClient> loggingClient, String applicationId, Boolean recent) {
        if (Optional.ofNullable(recent).orElse(false)) {
            return requestLogsRecent(loggingClient, applicationId);
        } else {
            return requestLogsStream(loggingClient, applicationId);
        }
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> getMetadataRequest(EventEntity entity) {
        Map<String, Object> metadata = Optional
            .ofNullable(entity.getMetadatas())
            .orElse(Collections.emptyMap());

        return Optional
            .ofNullable((Map<String, Object>) metadata.get("request"))
            .orElse(Collections.emptyMap());
    }

    private static Mono<Optional<List<Route>>> getOptionalRoutes(CloudFoundryClient cloudFoundryClient, boolean deleteRoutes, String applicationId) {
        if (deleteRoutes) {
            return getRoutes(cloudFoundryClient, applicationId)
                .map(Optional::of);
        } else {
            return Mono.just(Optional.empty());
        }
    }

    private static Mono<Optional<String>> getOptionalStackId(CloudFoundryClient cloudFoundryClient, String stack) {
        if (stack == null) {
            return Mono.just(Optional.empty());
        }
        return requestStackId(cloudFoundryClient, stack)
            .map(ResourceUtils::getId)
            .map(Optional::of)
            .otherwiseIfEmpty(ExceptionUtils.illegalState("Stack %s not found", stack));
    }

    private static Mono<OrganizationResource> getOrganization(CloudFoundryClient cloudFoundryClient, String organization) {
        return requestOrganizations(cloudFoundryClient, organization)
            .single()
            .otherwise(ExceptionUtils.replace(NoSuchElementException.class, () -> ExceptionUtils.illegalArgument("Organization %s not found", organization)));
    }

    private static Mono<String> getOrganizationId(CloudFoundryClient cloudFoundryClient, String organization) {
        return getOrganization(cloudFoundryClient, organization)
            .map(ResourceUtils::getId);
    }

    private static Mono<SpaceResource> getOrganizationSpaceByName(CloudFoundryClient cloudFoundryClient, String organizationId, String space) {
        return requestOrganizationSpacesByName(cloudFoundryClient, organizationId, space)
            .single()
            .otherwise(ExceptionUtils.replace(NoSuchElementException.class, () -> ExceptionUtils.illegalArgument("Space %s not found", space)));
    }

    private static Mono<String> getPrivateDomainId(CloudFoundryClient cloudFoundryClient, String domain, String organizationId) {
        return requestPrivateDomain(cloudFoundryClient, domain, organizationId)
            .map(ResourceUtils::getId)
            .singleOrEmpty();
    }

    private static Flux<String> getPrivateDomainIds(CloudFoundryClient cloudFoundryClient, String organizationId) {
        return requestPrivateDomains(cloudFoundryClient, organizationId)
            .map(ResourceUtils::getId);
    }

    private static Mono<String> getPushRouteId(CloudFoundryClient cloudFoundryClient, String domainId, PushApplicationRequest request, String spaceId, RandomWords randomWords) {
        String routePath = request.getRoutePath();
        String host = deriveHostname(request, randomWords);

        return getRouteId(cloudFoundryClient, domainId, host, routePath)
            .otherwiseIfEmpty(requestCreateRoute(cloudFoundryClient, domainId, host, routePath, spaceId)
                .map(ResourceUtils::getId));
    }

    private static Mono<String> getRouteId(CloudFoundryClient cloudFoundryClient, String domainId, String host, String routePath) {
        return requestRoutes(cloudFoundryClient, domainId, host, routePath)
            .filter(resource -> isIdentical(host, ResourceUtils.getEntity(resource).getHost()))
            .filter(resource -> isIdentical(Optional.ofNullable(routePath).orElse(""), ResourceUtils.getEntity(resource).getPath()))
            .singleOrEmpty()
            .map(ResourceUtils::getId);
    }

    private static Mono<List<Route>> getRoutes(CloudFoundryClient cloudFoundryClient, String applicationId) {
        return requestApplicationSummary(cloudFoundryClient, applicationId)
            .map(SummaryApplicationResponse::getRoutes);
    }

    private static Mono<Tuple2<Optional<List<Route>>, String>> getRoutesAndApplicationId(CloudFoundryClient cloudFoundryClient, DeleteApplicationRequest deleteApplicationRequest, String spaceId,
                                                                                         boolean deleteRoutes) {
        return getApplicationId(cloudFoundryClient, deleteApplicationRequest.getName(), spaceId)
            .then(applicationId -> getOptionalRoutes(cloudFoundryClient, deleteRoutes, applicationId)
                .and(Mono.just(applicationId)));
    }

    private static Mono<String> getSharedDomainId(CloudFoundryClient cloudFoundryClient, String domain) {
        return requestSharedDomain(cloudFoundryClient, domain)
            .map(ResourceUtils::getId)
            .singleOrEmpty();
    }

    private static Flux<String> getSharedDomainIds(CloudFoundryClient cloudFoundryClient) {
        return requestSharedDomains(cloudFoundryClient)
            .map(ResourceUtils::getId);
    }

    private static Mono<String> getSpaceId(CloudFoundryClient cloudFoundryClient, String organizationId, String space) {
        return getOrganizationSpaceByName(cloudFoundryClient, organizationId, space)
            .map(ResourceUtils::getId);
    }

    private static Mono<String> getSpaceOrganizationId(CloudFoundryClient cloudFoundryClient, String spaceId) {
        return requestSpace(cloudFoundryClient, spaceId)
            .map(response -> ResourceUtils.getEntity(response).getOrganizationId());
    }

    private static Mono<String> getStackName(CloudFoundryClient cloudFoundryClient, String stackId) {
        return requestStack(cloudFoundryClient, stackId)
            .map(getStackResponse -> getStackResponse.getEntity().getName());
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

    private static Mono<Void> prepareDomainsAndRoutes(CloudFoundryClient cloudFoundryClient, PushApplicationRequest request1, String applicationId, String spaceId, RandomWords randomWords) {
        if (Optional.ofNullable(request1.getNoRoute()).orElse(false)) {
            return Mono.empty();
        }

        return getSpaceOrganizationId(cloudFoundryClient, spaceId)
            .then(organizationId -> getDomainId(cloudFoundryClient, request1.getDomain(), organizationId))
            .then(domainId -> getPushRouteId(cloudFoundryClient, domainId, request1, spaceId, randomWords))
            .then(routeId -> requestAssociateRoute(cloudFoundryClient, applicationId, routeId))
            .after();
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
            .after();
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
            .requestResources(page -> cloudFoundryClient.spaces()
                .listApplications(ListSpaceApplicationsRequest.builder()
                    .name(application)
                    .spaceId(spaceId)
                    .page(page)
                    .build()))
            .map(OperationUtils.<ApplicationResource, AbstractApplicationResource>cast());
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

    private static Mono<CreateApplicationResponse> requestCreateApplication(CloudFoundryClient cloudFoundryClient, PushApplicationRequest request, String spaceId, String stackId) {
        return cloudFoundryClient.applicationsV2()
            .create(CreateApplicationRequest.builder()
                .buildpack(request.getBuildpack())
                .command(request.getCommand())
                .diskQuota(request.getDiskQuota())
                .dockerImage(request.getDockerImage())
                .healthCheckTimeout(request.getTimeout())
                .healthCheckType(request.getHealthCheckType())
                .instances(request.getInstances())
                .memory(request.getMemory())
                .name(request.getName())
                .spaceId(spaceId)
                .stackId(stackId)
                .build());
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
            .requestResources(page -> cloudFoundryClient.events()
                .list(ListEventsRequest.builder()
                    .actee(applicationId)
                    .orderDirection(PaginatedRequest.OrderDirection.DESC)
                    .resultsPerPage(50)
                    .page(page)
                    .build()));
    }

    private static Mono<AbstractApplicationResource> requestGetApplication(CloudFoundryClient cloudFoundryClient, String applicationId) {
        return cloudFoundryClient.applicationsV2()
            .get(org.cloudfoundry.client.v2.applications.GetApplicationRequest.builder()
                .applicationId(applicationId)
                .build())
            .map(OperationUtils.<GetApplicationResponse, AbstractApplicationResource>cast());
    }

    private static Flux<ServiceBindingResource> requestListServiceBindings(CloudFoundryClient cloudFoundryClient, String applicationId) {
        return PaginationUtils
            .requestResources(page -> cloudFoundryClient.applicationsV2()
                .listServiceBindings(ListApplicationServiceBindingsRequest.builder()
                    .applicationId(applicationId)
                    .page(page)
                    .build()));
    }

    private static Publisher<LogMessage> requestLogsRecent(Mono<LoggingClient> loggingClient, String applicationId) {
        return loggingClient
            .flatMap(loggingClient1 -> loggingClient1
                .recent(RecentLogsRequest.builder()
                    .applicationId(applicationId)
                    .build()));
    }

    private static Publisher<LogMessage> requestLogsStream(Mono<LoggingClient> loggingClient, String applicationId) {
        return loggingClient
            .flatMap(loggingClient1 -> loggingClient1
                .stream(StreamLogsRequest.builder()
                    .applicationId(applicationId)
                    .build()));
    }

    private static Flux<SpaceResource> requestOrganizationSpacesByName(CloudFoundryClient cloudFoundryClient, String organizationId, String space) {
        return PaginationUtils
            .requestResources(page -> cloudFoundryClient.organizations()
                .listSpaces(ListOrganizationSpacesRequest.builder()
                    .page(page)
                    .organizationId(organizationId)
                    .name(space)
                    .build()));
    }

    private static Flux<OrganizationResource> requestOrganizations(CloudFoundryClient cloudFoundryClient, String organization) {
        return PaginationUtils
            .requestResources(page -> cloudFoundryClient.organizations()
                .list(ListOrganizationsRequest.builder()
                    .page(page)
                    .name(organization)
                    .build()));
    }

    private static Flux<PrivateDomainResource> requestPrivateDomain(CloudFoundryClient cloudFoundryClient, String domain, String organizationId) {
        return PaginationUtils
            .requestResources(page -> cloudFoundryClient.organizations()
                .listPrivateDomains(ListOrganizationPrivateDomainsRequest.builder()
                    .name(domain)
                    .organizationId(organizationId)
                    .page(page)
                    .build()));
    }

    private static Flux<PrivateDomainResource> requestPrivateDomains(CloudFoundryClient cloudFoundryClient, String organizationId) {
        return PaginationUtils
            .requestResources(page -> cloudFoundryClient.organizations()
                .listPrivateDomains(ListOrganizationPrivateDomainsRequest.builder()
                    .organizationId(organizationId)
                    .page(page)
                    .build()));
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

    private static Flux<RouteResource> requestRoutes(CloudFoundryClient cloudFoundryClient, String domainId, String host, String routePath) {
        ListRoutesRequest.ListRoutesRequestBuilder requestBuilder = ListRoutesRequest.builder()
            .domainId(domainId);

        if (host != null) {
            requestBuilder.host(host);
        }

        if (routePath != null) {
            requestBuilder.path(routePath);
        }

        return PaginationUtils
            .requestResources(page -> cloudFoundryClient.routes()
                .list(requestBuilder
                    .page(page)
                    .build()));
    }

    private static Flux<SharedDomainResource> requestSharedDomain(CloudFoundryClient cloudFoundryClient, String domain) {
        return PaginationUtils
            .requestResources(page -> cloudFoundryClient.sharedDomains()
                .list(ListSharedDomainsRequest.builder()
                    .name(domain)
                    .page(page)
                    .build()));
    }

    private static Flux<SharedDomainResource> requestSharedDomains(CloudFoundryClient cloudFoundryClient) {
        return PaginationUtils
            .requestResources(page -> cloudFoundryClient.sharedDomains()
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

    private static Mono<StackResource> requestStackId(CloudFoundryClient cloudFoundryClient, String stack) {
        return PaginationUtils
            .requestResources(page -> cloudFoundryClient.stacks()
                .list(ListStacksRequest.builder()
                    .page(page)
                    .name(stack)
                    .build()))
            .single()
            .otherwise(ExceptionUtils.replace(NoSuchElementException.class, () -> ExceptionUtils.illegalArgument("Stack %s does not exist", stack)));
    }

    private static Mono<Void> requestTerminateApplicationInstance(CloudFoundryClient cloudFoundryClient, String applicationId, String instanceIndex) {
        return cloudFoundryClient.applicationsV2()
            .terminateInstance(TerminateApplicationInstanceRequest.builder()
                .applicationId(applicationId)
                .index(instanceIndex)
                .build());
    }

    private static Mono<UpdateApplicationResponse> requestUpdateApplication(CloudFoundryClient cloudFoundryClient, String applicationId, PushApplicationRequest request, String stackId) {
        return cloudFoundryClient.applicationsV2()
            .update(UpdateApplicationRequest.builder()
                .applicationId(applicationId)
                .buildpack(request.getBuildpack())
                .command(request.getCommand())
                .diskQuota(request.getDiskQuota())
                .dockerImage(request.getDockerImage())
                .healthCheckTimeout(request.getTimeout())
                .healthCheckType(request.getHealthCheckType())
                .instances(request.getInstances())
                .memory(request.getMemory())
                .name(request.getName())
                .stackId(stackId)
                .build());
    }

    private static Mono<UpdateApplicationResponse> requestUpdateApplicationEnvironment(CloudFoundryClient cloudFoundryClient, String applicationId, Map<String, Object> environment) {
        return cloudFoundryClient.applicationsV2()
            .update(UpdateApplicationRequest.builder()
                .applicationId(applicationId)
                .environmentJsons(environment)
                .build());
    }

    private static Mono<UpdateApplicationResponse> requestUpdateApplicationRename(CloudFoundryClient cloudFoundryClient, String applicationId, String name) {
        return cloudFoundryClient.applicationsV2()
            .update(UpdateApplicationRequest.builder()
                .applicationId(applicationId)
                .name(name)
                .build());
    }

    private static Mono<AbstractApplicationResource> requestUpdateApplicationScale(CloudFoundryClient cloudFoundryClient, String applicationId, Integer disk, Integer instances, Integer memory) {
        return cloudFoundryClient.applicationsV2()
            .update(UpdateApplicationRequest.builder()
                .applicationId(applicationId)
                .diskQuota(disk)
                .instances(instances)
                .memory(memory)
                .build())
            .map(OperationUtils.<UpdateApplicationResponse, AbstractApplicationResource>cast());
    }

    private static Mono<AbstractApplicationResource> requestUpdateApplicationSsh(CloudFoundryClient cloudFoundryClient, String applicationId, Boolean enabled) {
        return cloudFoundryClient.applicationsV2()
            .update(UpdateApplicationRequest.builder()
                .applicationId(applicationId)
                .enableSsh(enabled)
                .build())
            .map(OperationUtils.<UpdateApplicationResponse, AbstractApplicationResource>cast());
    }

    private static Mono<AbstractApplicationResource> requestUpdateApplicationState(CloudFoundryClient cloudFoundryClient, String applicationId, String state) {
        return cloudFoundryClient.applicationsV2()
            .update(UpdateApplicationRequest.builder()
                .applicationId(applicationId)
                .state(state)
                .build())
            .map(OperationUtils.<UpdateApplicationResponse, AbstractApplicationResource>cast());
    }

    private static Mono<UploadApplicationResponse> requestUploadApplication(CloudFoundryClient cloudFoundryClient, String applicationId, InputStream application) {
        return cloudFoundryClient.applicationsV2()
            .upload(UploadApplicationRequest.builder()
                .applicationId(applicationId)
                .async(true)
                .application(application)
                .build());
    }

    private static Mono<String> restageApplication(CloudFoundryClient cloudFoundryClient, String application, String applicationId, Duration stagingTimeout, Duration startupTimeout) {
        return requestRestageApplication(cloudFoundryClient, applicationId)
            .then(response -> waitForStaging(cloudFoundryClient, application, applicationId, stagingTimeout))
            .then(state -> waitForRunning(cloudFoundryClient, application, applicationId, startupTimeout));
    }

    private static Mono<String> restartApplication(CloudFoundryClient cloudFoundryClient, String application, String applicationId, Duration stagingTimeout, Duration startupTimeout) {
        return stopApplication(cloudFoundryClient, applicationId)
            .then(abstractApplicationResource -> startApplicationAndWait(cloudFoundryClient, application, applicationId, stagingTimeout, startupTimeout));
    }

    private static Predicate<AbstractApplicationResource> sshEnabled(Boolean enabled) {
        return resource -> enabled.equals(ResourceUtils.getEntity(resource).getEnableSsh());
    }

    private static Mono<String> startApplicationAndWait(CloudFoundryClient cloudFoundryClient, String application, String applicationId, Duration stagingTimeout, Duration startupTimeout) {
        return requestUpdateApplicationState(cloudFoundryClient, applicationId, STARTED_STATE)
            .then(response -> waitForStaging(cloudFoundryClient, application, applicationId, stagingTimeout))
            .then(state -> waitForRunning(cloudFoundryClient, application, applicationId, startupTimeout));
    }

    private static Mono<AbstractApplicationResource> stopApplication(CloudFoundryClient cloudFoundryClient, String applicationId) {
        return requestUpdateApplicationState(cloudFoundryClient, applicationId, STOPPED_STATE);
    }

    private static Mono<AbstractApplicationResource> stopApplicationIfNotStopped(CloudFoundryClient cloudFoundryClient, AbstractApplicationResource resource) {
        return isNotIn(resource, STOPPED_STATE) ? stopApplication(cloudFoundryClient, ResourceUtils.getId(resource)) : Mono.just(resource);
    }

    private static ApplicationDetail toApplicationDetail(ApplicationStatisticsResponse applicationStatisticsResponse, SummaryApplicationResponse summaryApplicationResponse,
                                                         GetStackResponse getStackResponse, ApplicationInstancesResponse applicationInstancesResponse) {
        return ApplicationDetail.builder()
            .buildpack(getBuildpack(summaryApplicationResponse))
            .diskQuota(summaryApplicationResponse.getDiskQuota())
            .id(summaryApplicationResponse.getId())
            .instanceDetails(toInstanceDetailList(applicationInstancesResponse, applicationStatisticsResponse))
            .instances(summaryApplicationResponse.getInstances())
            .lastUploaded(toDate(summaryApplicationResponse.getPackageUpdatedAt()))
            .memoryLimit(summaryApplicationResponse.getMemory())
            .name(summaryApplicationResponse.getName())
            .requestedState(summaryApplicationResponse.getState())
            .runningInstances(summaryApplicationResponse.getRunningInstances())
            .stack(getStackResponse.getEntity().getName())
            .urls(toUrls(summaryApplicationResponse.getRoutes()))
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
        ApplicationManifest.ApplicationManifestBuilder manifestBuilder = ApplicationManifest.builder()
            .buildpack(response.getBuildpack())
            .command(response.getCommand())
            .disk(response.getDiskQuota())
            .environmentVariables(response.getEnvironmentJsons())
            .instances(response.getInstances())
            .memory(response.getMemory())
            .name(response.getName())
            .stack(stackName)
            .timeout(response.getHealthCheckTimeout());

        for (Route route : response.getRoutes()) {
            manifestBuilder
                .domain(route.getDomain().getName())
                .host((route.getHost()));
        }

        for (ServiceInstance service : response.getServices()) {
            manifestBuilder
                .service(service.getName());
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

    private static ApplicationDetail.InstanceDetail toInstanceDetail(Map.Entry<String, ApplicationInstanceInfo> entry, ApplicationStatisticsResponse statisticsResponse) {
        ApplicationStatisticsResponse.InstanceStats instanceStats = Optional.ofNullable(statisticsResponse.get(entry.getKey())).orElse(emptyInstanceStats());
        ApplicationStatisticsResponse.InstanceStats.Statistics stats = Optional.ofNullable(instanceStats.getStatistics()).orElse(emptyApplicationStatistics());
        ApplicationStatisticsResponse.InstanceStats.Statistics.Usage usage = Optional.ofNullable(stats.getUsage()).orElse(emptyApplicationUsage());

        return ApplicationDetail.InstanceDetail.builder()
            .state(entry.getValue().getState())
            .since(toDate(entry.getValue().getSince()))
            .cpu(usage.getCpu())
            .memoryUsage(usage.getMemory())
            .diskUsage(usage.getDisk())
            .diskQuota(stats.getDiskQuota())
            .memoryQuota(stats.getMemoryQuota())
            .build();
    }

    private static List<ApplicationDetail.InstanceDetail> toInstanceDetailList(ApplicationInstancesResponse instancesResponse, ApplicationStatisticsResponse statisticsResponse) {
        return Flux
            .fromIterable(instancesResponse.entrySet())
            .map(entry -> toInstanceDetail(entry, statisticsResponse))
            .toList()
            .get();
    }

    private static String toUrl(Route route) {
        String hostName = route.getHost();
        String domainName = route.getDomain().getName();

        return hostName.isEmpty() ? domainName : String.format("%s.%s", hostName, domainName);
    }

    private static List<String> toUrls(List<Route> routes) {
        return Flux
            .fromIterable(routes)
            .map(DefaultApplications::toUrl)
            .toList()
            .get();
    }

    private static Mono<Void> uploadApplicationAndWait(CloudFoundryClient cloudFoundryClient, String applicationId, InputStream application) {
        return requestUploadApplication(cloudFoundryClient, applicationId, application)
            .then(job -> JobUtils.waitForCompletion(cloudFoundryClient, job));
    }

    private static Mono<String> waitForRunning(CloudFoundryClient cloudFoundryClient, String application, String applicationId, Duration startupTimeout) {
        Duration timeout = Optional.ofNullable(startupTimeout).orElse(Duration.ofMinutes(5));

        return requestApplicationInstances(cloudFoundryClient, applicationId)
            .flatMap(response -> Flux.fromIterable(response.values()))
            .map(ApplicationInstanceInfo::getState)
            .reduce("UNKNOWN", collectStates())
            .where(isInstanceComplete())
            .repeatWhenEmpty(exponentialBackOff(Duration.ofSeconds(1), Duration.ofSeconds(15), timeout))
            .where(isRunning())
            .otherwiseIfEmpty(ExceptionUtils.illegalState("Application %s failed during start", application))
            .otherwise(ExceptionUtils.replace(DelayTimeoutException.class, () -> ExceptionUtils.illegalState("Application %s timed out during start", application)));
    }

    private static Mono<String> waitForStaging(CloudFoundryClient cloudFoundryClient, String application, String applicationId, Duration stagingTimeout) {
        Duration timeout = Optional.ofNullable(stagingTimeout).orElse(Duration.ofMinutes(15));

        return requestGetApplication(cloudFoundryClient, applicationId)
            .map(response -> ResourceUtils.getEntity(response).getPackageState())
            .where(isStagingComplete())
            .repeatWhenEmpty(exponentialBackOff(Duration.ofSeconds(1), Duration.ofSeconds(15), timeout))
            .where(isStaged())
            .otherwiseIfEmpty(ExceptionUtils.illegalState("Application %s failed during staging", application))
            .otherwise(ExceptionUtils.replace(DelayTimeoutException.class, () -> ExceptionUtils.illegalState("Application %s timed out during staging", application)));
    }

}
