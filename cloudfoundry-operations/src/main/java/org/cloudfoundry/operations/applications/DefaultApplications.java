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
import org.cloudfoundry.client.v2.OrderDirection;
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
import org.cloudfoundry.client.v2.applications.InstanceStatistics;
import org.cloudfoundry.client.v2.applications.ListApplicationServiceBindingsRequest;
import org.cloudfoundry.client.v2.applications.RemoveApplicationServiceBindingRequest;
import org.cloudfoundry.client.v2.applications.RestageApplicationResponse;
import org.cloudfoundry.client.v2.applications.Statistics;
import org.cloudfoundry.client.v2.applications.SummaryApplicationRequest;
import org.cloudfoundry.client.v2.applications.SummaryApplicationResponse;
import org.cloudfoundry.client.v2.applications.TerminateApplicationInstanceRequest;
import org.cloudfoundry.client.v2.applications.UpdateApplicationRequest;
import org.cloudfoundry.client.v2.applications.UpdateApplicationResponse;
import org.cloudfoundry.client.v2.applications.UploadApplicationRequest;
import org.cloudfoundry.client.v2.applications.UploadApplicationResponse;
import org.cloudfoundry.client.v2.applications.Usage;
import org.cloudfoundry.client.v2.domains.Domain;
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
import org.cloudfoundry.doppler.DopplerClient;
import org.cloudfoundry.doppler.Envelope;
import org.cloudfoundry.doppler.EventType;
import org.cloudfoundry.doppler.LogMessage;
import org.cloudfoundry.doppler.RecentLogsRequest;
import org.cloudfoundry.doppler.StreamRequest;
import org.cloudfoundry.util.DateUtils;
import org.cloudfoundry.util.DelayTimeoutException;
import org.cloudfoundry.util.ExceptionUtils;
import org.cloudfoundry.util.FileUtils;
import org.cloudfoundry.util.FluentMap;
import org.cloudfoundry.util.JobUtils;
import org.cloudfoundry.util.OperationUtils;
import org.cloudfoundry.util.PaginationUtils;
import org.cloudfoundry.util.ResourceUtils;
import org.cloudfoundry.util.SortingUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuple6;
import reactor.util.function.Tuples;

import java.io.InputStream;
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
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import static org.cloudfoundry.util.DelayUtils.exponentialBackOff;
import static org.cloudfoundry.util.tuple.TupleUtils.function;
import static org.cloudfoundry.util.tuple.TupleUtils.predicate;

public final class DefaultApplications implements Applications {

    private static final int CF_APP_STOPPED_STATS_ERROR = 200003;

    private static final int CF_BUILDPACK_COMPILED_FAILED = 170004;

    private static final int CF_INSTANCES_ERROR = 220001;

    private static final int CF_STAGING_NOT_FINISHED = 170002;

    private static final Comparator<LogMessage> LOG_MESSAGE_COMPARATOR = (a, b) -> a.getTimestamp().compareTo(b.getTimestamp());

    private static final Duration LOG_MESSAGE_TIMESPAN = Duration.ofMillis(500);

    private static final int MAX_NUMBER_OF_RECENT_EVENTS = 50;

    private static final String STARTED_STATE = "STARTED";

    private static final String STOPPED_STATE = "STOPPED";

    private final Mono<CloudFoundryClient> cloudFoundryClient;

    private final Mono<DopplerClient> dopplerClient;

    private final Function<Path, InputStream> pathTransformer;

    private final RandomWords randomWords;

    private final Mono<String> spaceId;

    public DefaultApplications(Mono<CloudFoundryClient> cloudFoundryClient, Mono<DopplerClient> dopplerClient, Mono<String> spaceId) {
        this(cloudFoundryClient, dopplerClient, FileUtils::toInputStream, spaceId, new WordListRandomWords());
    }

    DefaultApplications(Mono<CloudFoundryClient> cloudFoundryClient, Mono<DopplerClient> dopplerClient, Function<Path, InputStream> pathTransformer, Mono<String> spaceId, RandomWords randomWords) {
        this.cloudFoundryClient = cloudFoundryClient;
        this.dopplerClient = dopplerClient;
        this.pathTransformer = pathTransformer;
        this.spaceId = spaceId;
        this.randomWords = randomWords;
    }

    @Override
    public Mono<Void> copySource(CopySourceApplicationRequest request) {
        return Mono
            .when(this.cloudFoundryClient, this.spaceId)
            .then(function((cloudFoundryClient, spaceId) -> Mono.when(
                Mono.just(cloudFoundryClient),
                getApplicationId(cloudFoundryClient, request.getName(), spaceId),
                getApplicationIdFromOrgSpace(cloudFoundryClient, request.getTargetName(), spaceId, request.getTargetOrganization(), request.getTargetSpace())
            )))
            .then(function((cloudFoundryClient, sourceApplicationId, targetApplicationId) -> copyBits(cloudFoundryClient, sourceApplicationId, targetApplicationId)
                .then(Mono.just(Tuples.of(cloudFoundryClient, targetApplicationId)))))
            .filter(predicate((cloudFoundryClient, targetApplicationId) -> Optional.ofNullable(request.getRestart()).orElse(false)))
            .then(function((cloudFoundryClient, targetApplicationId) -> restartApplication(cloudFoundryClient, request.getTargetName(), targetApplicationId, request.getStagingTimeout(),
                request.getStartupTimeout())));
    }

    @Override
    public Mono<Void> delete(DeleteApplicationRequest request) {
        return Mono
            .when(this.cloudFoundryClient, this.spaceId)
            .then(function((cloudFoundryClient, spaceId) -> getRoutesAndApplicationId(cloudFoundryClient, request, spaceId, Optional.ofNullable(request.getDeleteRoutes()).orElse(false))
                .map(function((routes, applicationId) -> Tuples.of(cloudFoundryClient, routes, applicationId)))))
            .then(function((cloudFoundryClient, routes, applicationId) -> deleteRoutes(cloudFoundryClient, routes)
                .then(Mono.just(Tuples.of(cloudFoundryClient, applicationId)))))
            .then(function((cloudFoundryClient, applicationId) -> removeServiceBindings(cloudFoundryClient, applicationId)
                .then(Mono.just(Tuples.of(cloudFoundryClient, applicationId)))))
            .then(function(DefaultApplications::requestDeleteApplication));
    }

    @Override
    public Mono<Void> disableSsh(DisableApplicationSshRequest request) {
        return Mono
            .when(this.cloudFoundryClient, this.spaceId)
            .then(function((cloudFoundryClient, spaceId) -> Mono.when(
                Mono.just(cloudFoundryClient),
                getApplicationIdWhere(cloudFoundryClient, request.getName(), spaceId, sshEnabled(true))
            )))
            .then(function((cloudFoundryClient, applicationId) -> requestUpdateApplicationSsh(cloudFoundryClient, applicationId, false)))
            .then();
    }

    @Override
    public Mono<Void> enableSsh(EnableApplicationSshRequest request) {
        return Mono
            .when(this.cloudFoundryClient, this.spaceId)
            .then(function((cloudFoundryClient, spaceId) -> Mono.when(
                Mono.just(cloudFoundryClient),
                getApplicationIdWhere(cloudFoundryClient, request.getName(), spaceId, sshEnabled(false))
            )))
            .then(function((cloudFoundryClient, applicationId) -> requestUpdateApplicationSsh(cloudFoundryClient, applicationId, true)))
            .then();
    }

    @Override
    public Mono<ApplicationDetail> get(GetApplicationRequest request) {
        return Mono
            .when(this.cloudFoundryClient, this.spaceId)
            .then(function((cloudFoundryClient, spaceId) -> Mono.when(
                Mono.just(cloudFoundryClient),
                getApplication(cloudFoundryClient, request.getName(), spaceId)
            )))
            .then(function(DefaultApplications::getAuxiliaryContent))
            .map(function(DefaultApplications::toApplicationDetail));
    }

    @Override
    public Mono<ApplicationManifest> getApplicationManifest(GetApplicationManifestRequest request) {
        return Mono
            .when(this.cloudFoundryClient, this.spaceId)
            .then(function((cloudFoundryClient, spaceId) -> Mono.when(
                Mono.just(cloudFoundryClient),
                getApplicationId(cloudFoundryClient, request.getName(), spaceId)
            )))
            .then(function((cloudFoundryClient, applicationId) -> Mono.when(
                Mono.just(cloudFoundryClient),
                requestApplicationSummary(cloudFoundryClient, applicationId)
            )))
            .then(function((cloudFoundryClient, response) -> Mono.when(
                Mono.just(response),
                getStackName(cloudFoundryClient, response.getStackId())
            )))
            .then(function(DefaultApplications::toApplicationManifest));
    }

    @Override
    public Mono<ApplicationEnvironments> getEnvironments(GetApplicationEnvironmentsRequest request) {
        return Mono
            .when(this.cloudFoundryClient, this.spaceId)
            .then(function((cloudFoundryClient, spaceId) -> Mono.when(
                Mono.just(cloudFoundryClient),
                getApplicationId(cloudFoundryClient, request.getName(), spaceId)
            )))
            .then(function(DefaultApplications::requestApplicationEnvironment))
            .map(DefaultApplications::toApplicationEnvironments);
    }

    @Override
    public Flux<ApplicationEvent> getEvents(GetApplicationEventsRequest request) {
        return Mono
            .when(this.cloudFoundryClient, this.spaceId)
            .then(function((cloudFoundryClient, spaceId) -> Mono.when(
                Mono.just(cloudFoundryClient),
                getApplicationId(cloudFoundryClient, request.getName(), spaceId)
            )))
            .flatMap(function((cloudFoundryClient, applicationId) -> requestEvents(applicationId, cloudFoundryClient)
                .take(Optional.ofNullable(request.getMaxNumberOfEvents()).orElse(MAX_NUMBER_OF_RECENT_EVENTS))))
            .map(DefaultApplications::convertToApplicationEvent);
    }

    @Override
    public Mono<ApplicationHealthCheck> getHealthCheck(GetApplicationHealthCheckRequest request) {
        return Mono
            .when(this.cloudFoundryClient, this.spaceId)
            .then(function((cloudFoundryClient, spaceId) -> getApplication(cloudFoundryClient, request.getName(), spaceId)))
            .map(DefaultApplications::toHealthCheck);
    }

    @Override
    public Flux<ApplicationSummary> list() {
        return Mono
            .when(this.cloudFoundryClient, this.spaceId)
            .then(function(DefaultApplications::requestSpaceSummary))
            .flatMap(DefaultApplications::extractApplications)
            .map(DefaultApplications::toApplicationSummary);
    }

    @Override
    public Flux<LogMessage> logs(LogsRequest request) {
        return Mono
            .when(this.cloudFoundryClient, this.spaceId)
            .then(function((cloudFoundryClient, spaceId) -> getApplicationId(cloudFoundryClient, request.getName(), spaceId)))
            .flatMap(applicationId -> getLogs(this.dopplerClient, applicationId, request.getRecent()));
    }

    @Override
    public Mono<Void> push(PushApplicationRequest request) {
        if (request.getApplication() != null) {
            return this.cloudFoundryClient
                .then(cloudFoundryClient -> Mono.when(
                    Mono.just(cloudFoundryClient),
                    this.spaceId,
                    getOptionalStackId(cloudFoundryClient, request.getStack())
                ))
                .then(function((cloudFoundryClient, spaceId, stackId) -> Mono.when(
                    Mono.just(cloudFoundryClient),
                    getApplicationId(cloudFoundryClient, request, spaceId, stackId.orElse(null)),
                    Mono.just(this.pathTransformer.apply(request.getApplication())),
                    Mono.just(spaceId)
                )))
                .then(function((cloudFoundryClient, applicationId, inputStream, spaceId) -> prepareDomainsAndRoutes(cloudFoundryClient, request, applicationId, spaceId, this.randomWords)
                    .then(Mono.just(Tuples.of(cloudFoundryClient, applicationId, inputStream)))))
                .then(function((cloudFoundryClient, applicationId, inputStream) -> uploadApplicationAndWait(cloudFoundryClient, applicationId, inputStream)
                    .then(Mono.just(Tuples.of(cloudFoundryClient, applicationId)))))
                .then(function((cloudFoundryClient, applicationId) -> stopAndStartApplication(cloudFoundryClient, applicationId, request)));
        } else if (request.getDockerImage() != null) {
            return this.cloudFoundryClient
                .then(cloudFoundryClient -> Mono.when(
                    Mono.just(cloudFoundryClient),
                    this.spaceId,
                    getOptionalStackId(cloudFoundryClient, request.getStack())
                ))
                .then(function((cloudFoundryClient, spaceId, stackId) -> Mono.when(
                    Mono.just(cloudFoundryClient),
                    getApplicationId(cloudFoundryClient, request, spaceId, stackId.orElse(null)),
                    Mono.just(spaceId)
                )))
                .then(function((cloudFoundryClient, applicationId, spaceId) -> prepareDomainsAndRoutes(cloudFoundryClient, request, applicationId, spaceId, this.randomWords)
                    .then(Mono.just(Tuples.of(cloudFoundryClient, applicationId)))))
                .then(function((cloudFoundryClient, applicationId) -> stopAndStartApplication(cloudFoundryClient, applicationId, request)));
        } else {
            throw new IllegalStateException("One of application or dockerImage must be supplied");
        }
    }

    @Override
    public Mono<Void> rename(RenameApplicationRequest request) {
        return Mono
            .when(this.cloudFoundryClient, this.spaceId)
            .then(function((cloudFoundryClient, spaceId) -> Mono.when(
                Mono.just(cloudFoundryClient),
                getApplicationId(cloudFoundryClient, request.getName(), spaceId)
            )))
            .then(function((cloudFoundryClient, applicationId) -> requestUpdateApplicationName(cloudFoundryClient, applicationId, request.getNewName())))
            .then();
    }

    @Override
    public Mono<Void> restage(RestageApplicationRequest request) {
        return Mono
            .when(this.cloudFoundryClient, this.spaceId)
            .then(function((cloudFoundryClient, spaceId) -> Mono.when(
                Mono.just(cloudFoundryClient),
                getApplicationId(cloudFoundryClient, request.getName(), spaceId)
            )))
            .then(function((cloudFoundryClient, applicationId) -> restageApplication(cloudFoundryClient, request.getName(), applicationId, request.getStagingTimeout(), request.getStartupTimeout())));
    }

    @Override
    public Mono<Void> restart(RestartApplicationRequest request) {
        return Mono
            .when(this.cloudFoundryClient, this.spaceId)
            .then(function((cloudFoundryClient, spaceId) -> Mono.when(
                Mono.just(cloudFoundryClient),
                getApplication(cloudFoundryClient, request.getName(), spaceId)
            )))
            .then(function((cloudFoundryClient, resource) -> Mono.when(
                Mono.just(cloudFoundryClient),
                stopApplicationIfNotStopped(cloudFoundryClient, resource)
            )))
            .then(function((cloudFoundryClient, stoppedApplication) -> startApplicationAndWait(cloudFoundryClient, request.getName(), ResourceUtils.getId(stoppedApplication),
                request.getStagingTimeout(), request.getStartupTimeout())));
    }

    @Override
    public Mono<Void> restartInstance(RestartApplicationInstanceRequest request) {
        return Mono
            .when(this.cloudFoundryClient, this.spaceId)
            .then(function((cloudFoundryClient, spaceId) -> Mono.when(
                Mono.just(cloudFoundryClient),
                getApplicationId(cloudFoundryClient, request.getName(), spaceId)
            )))
            .then(function((cloudFoundryClient, applicationId) -> requestTerminateApplicationInstance(cloudFoundryClient, applicationId, String.valueOf(request.getInstanceIndex()))));
    }

    @Override
    public Mono<Void> scale(ScaleApplicationRequest request) {
        return Mono
            .when(this.cloudFoundryClient, this.spaceId)
            .filter(predicate((cloudFoundryClient, spaceId) -> areModifiersPresent(request)))
            .then(function((cloudFoundryClient, spaceId) -> Mono.when(
                Mono.just(cloudFoundryClient),
                getApplicationId(cloudFoundryClient, request.getName(), spaceId)
            )))
            .then(function((cloudFoundryClient, applicationId) -> Mono.when(
                Mono.just(cloudFoundryClient),
                requestUpdateApplicationScale(cloudFoundryClient, applicationId, request.getDiskLimit(), request.getInstances(), request.getMemoryLimit())
            )))
            .filter(predicate((cloudFoundryClient, resource) -> isRestartRequired(request, resource)))
            .then(function((cloudFoundryClient, resource) -> restartApplication(cloudFoundryClient, request.getName(), ResourceUtils.getId(resource), request.getStagingTimeout(),
                request.getStartupTimeout())));
    }

    @Override
    public Mono<Void> setEnvironmentVariable(SetEnvironmentVariableApplicationRequest request) {
        return Mono
            .when(this.cloudFoundryClient, this.spaceId)
            .then(function((cloudFoundryClient, spaceId) -> Mono.when(
                Mono.just(cloudFoundryClient),
                getApplication(cloudFoundryClient, request.getName(), spaceId)
            )))
            .then(function((cloudFoundryClient, resource) -> requestUpdateApplicationEnvironment(cloudFoundryClient, ResourceUtils.getId(resource), addToEnvironment(getEnvironment(resource),
                request.getVariableName(), request.getVariableValue()))))
            .then();
    }

    @Override
    public Mono<Void> setHealthCheck(SetApplicationHealthCheckRequest request) {
        return Mono
            .when(this.cloudFoundryClient, this.spaceId)
            .then(function((cloudFoundryClient, spaceId) -> Mono.when(
                Mono.just(cloudFoundryClient),
                getApplicationId(cloudFoundryClient, request.getName(), spaceId)
            )))
            .then(function((cloudFoundryClient, applicationId) -> requestUpdateApplicationHealthCheckType(cloudFoundryClient, applicationId, request.getType())))
            .then();
    }

    @Override
    public Mono<Boolean> sshEnabled(ApplicationSshEnabledRequest request) {
        return Mono
            .when(this.cloudFoundryClient, this.spaceId)
            .then(function((cloudFoundryClient, spaceId) -> getApplication(cloudFoundryClient, request.getName(), spaceId)))
            .map(applicationResource -> ResourceUtils.getEntity(applicationResource).getEnableSsh());
    }

    @Override
    public Mono<Void> start(StartApplicationRequest request) {
        return Mono
            .when(this.cloudFoundryClient, this.spaceId)
            .then(function((cloudFoundryClient, spaceId) -> Mono.when(
                Mono.just(cloudFoundryClient),
                getApplicationIdWhere(cloudFoundryClient, request.getName(), spaceId, isNotIn(STARTED_STATE))
            )))
            .then(function((cloudFoundryClient, applicationId) -> startApplicationAndWait(cloudFoundryClient, request.getName(), applicationId, request.getStagingTimeout(),
                request.getStartupTimeout())));
    }

    @Override
    public Mono<Void> stop(StopApplicationRequest request) {
        return Mono
            .when(this.cloudFoundryClient, this.spaceId)
            .then(function((cloudFoundryClient, spaceId) -> Mono.when(
                Mono.just(cloudFoundryClient),
                getApplicationIdWhere(cloudFoundryClient, request.getName(), spaceId, isNotIn(STOPPED_STATE))
            )))
            .then(function(DefaultApplications::stopApplication))
            .then();
    }

    @Override
    public Mono<Void> unsetEnvironmentVariable(UnsetEnvironmentVariableApplicationRequest request) {
        return Mono
            .when(this.cloudFoundryClient, this.spaceId)
            .then(function((cloudFoundryClient, spaceId) -> Mono.when(
                Mono.just(cloudFoundryClient),
                getApplication(cloudFoundryClient, request.getName(), spaceId)
            )))
            .then(function((cloudFoundryClient, resource) -> requestUpdateApplicationEnvironment(cloudFoundryClient, ResourceUtils.getId(resource), removeFromEnvironment(getEnvironment(resource),
                request.getVariableName()))))
            .then();
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
            .then();
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
            .otherwise(NoSuchElementException.class, t -> ExceptionUtils.illegalArgument("Application %s does not exist", application));
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
                .map(ResourceUtils::getId));
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
            .filter(predicate)
            .map(ResourceUtils::getId);
    }

    private static Mono<ApplicationInstancesResponse> getApplicationInstances(CloudFoundryClient cloudFoundryClient, String applicationId) {
        return requestApplicationInstances(cloudFoundryClient, applicationId)
            .otherwise(ExceptionUtils.statusCode(CF_BUILDPACK_COMPILED_FAILED), t -> Mono.just(ApplicationInstancesResponse.builder().build()))
            .otherwise(ExceptionUtils.statusCode(CF_INSTANCES_ERROR), t -> Mono.just(ApplicationInstancesResponse.builder().build()))
            .otherwise(ExceptionUtils.statusCode(CF_STAGING_NOT_FINISHED), t -> Mono.just(ApplicationInstancesResponse.builder().build()));
    }

    private static Mono<ApplicationStatisticsResponse> getApplicationStatistics(CloudFoundryClient cloudFoundryClient, String applicationId) {
        return requestApplicationStatistics(cloudFoundryClient, applicationId)
            .otherwise(ExceptionUtils.statusCode(CF_APP_STOPPED_STATS_ERROR), t -> Mono.just(ApplicationStatisticsResponse.builder().build()));
    }

    private static Mono<Tuple6<ApplicationStatisticsResponse, SummaryApplicationResponse, GetStackResponse, ApplicationInstancesResponse, List<InstanceDetail>, List<String>>>
    getAuxiliaryContent(CloudFoundryClient cloudFoundryClient, AbstractApplicationResource applicationResource) {

        String applicationId = ResourceUtils.getId(applicationResource);
        String stackId = ResourceUtils.getEntity(applicationResource).getStackId();

        return Mono
            .when(
                getApplicationStatistics(cloudFoundryClient, applicationId),
                requestApplicationSummary(cloudFoundryClient, applicationId),
                getApplicationInstances(cloudFoundryClient, applicationId)
            )
            .then(function((applicationStatisticsResponse, summaryApplicationResponse, applicationInstancesResponse) -> Mono.when(
                Mono.just(applicationStatisticsResponse),
                Mono.just(summaryApplicationResponse),
                requestStack(cloudFoundryClient, stackId),
                Mono.just(applicationInstancesResponse),
                toInstanceDetailList(applicationInstancesResponse, applicationStatisticsResponse),
                toUrls(summaryApplicationResponse.getRoutes())
            )));
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
                .otherwiseIfEmpty(ExceptionUtils.illegalArgument("Domain not found"));
        } else {
            return getPrivateDomainId(cloudFoundryClient, domain, organizationId)
                .otherwiseIfEmpty(getSharedDomainId(cloudFoundryClient, domain))
                .otherwiseIfEmpty(ExceptionUtils.illegalArgument("Domain %s not found", domain));
        }
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

    private static Mono<Optional<List<Route>>> getOptionalRoutes(CloudFoundryClient cloudFoundryClient, boolean deleteRoutes, String applicationId) {
        if (deleteRoutes) {
            return getRoutes(cloudFoundryClient, applicationId)
                .map(Optional::of);
        } else {
            return Mono.just(Optional.empty());
        }
    }

    private static Mono<Optional<String>> getOptionalStackId(CloudFoundryClient cloudFoundryClient, String stack) {
        return Optional.ofNullable(stack)
            .map(stack1 -> requestStackId(cloudFoundryClient, stack1)
                .map(ResourceUtils::getId)
                .map(Optional::of)
                .otherwiseIfEmpty(ExceptionUtils.illegalState("Stack %s not found", stack1)))
            .orElse(Mono.just(Optional.empty()));
    }

    private static Mono<OrganizationResource> getOrganization(CloudFoundryClient cloudFoundryClient, String organization) {
        return requestOrganizations(cloudFoundryClient, organization)
            .single()
            .otherwise(NoSuchElementException.class, t -> ExceptionUtils.illegalArgument("Organization %s not found", organization));
    }

    private static Mono<String> getOrganizationId(CloudFoundryClient cloudFoundryClient, String organization) {
        return getOrganization(cloudFoundryClient, organization)
            .map(ResourceUtils::getId);
    }

    private static Mono<SpaceResource> getOrganizationSpaceByName(CloudFoundryClient cloudFoundryClient, String organizationId, String space) {
        return requestOrganizationSpacesByName(cloudFoundryClient, organizationId, space)
            .single()
            .otherwise(NoSuchElementException.class, t -> ExceptionUtils.illegalArgument("Space %s not found", space));
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

    private static Mono<Tuple2<Optional<List<Route>>, String>> getRoutesAndApplicationId(CloudFoundryClient cloudFoundryClient, DeleteApplicationRequest request, String spaceId,
                                                                                         boolean deleteRoutes) {
        return getApplicationId(cloudFoundryClient, request.getName(), spaceId)
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

    private static Mono<Void> prepareDomainsAndRoutes(CloudFoundryClient cloudFoundryClient, PushApplicationRequest validRequest, String applicationId, String spaceId, RandomWords randomWords) {
        if (Optional.ofNullable(validRequest.getNoRoute()).orElse(false)) {
            return Mono.empty();
        }

        return getSpaceOrganizationId(cloudFoundryClient, spaceId)
            .then(organizationId -> getDomainId(cloudFoundryClient, validRequest.getDomain(), organizationId))
            .then(domainId -> getPushRouteId(cloudFoundryClient, domainId, validRequest, spaceId, randomWords))
            .then(routeId -> requestAssociateRoute(cloudFoundryClient, applicationId, routeId))
            .then();
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
        CreateApplicationRequest.Builder builder = CreateApplicationRequest.builder()
            .buildpack(request.getBuildpack())
            .command(request.getCommand())
            .diskQuota(request.getDiskQuota())
            .healthCheckTimeout(request.getTimeout())
            .healthCheckType(Optional.ofNullable(request.getHealthCheckType()).map(ApplicationHealthCheck::getValue).orElse(null))
            .instances(request.getInstances())
            .memory(request.getMemory())
            .name(request.getName())
            .spaceId(spaceId)
            .stackId(stackId);

        Optional.ofNullable(request.getDockerImage())
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
            .map(OperationUtils.<GetApplicationResponse, AbstractApplicationResource>cast());
    }

    private static Flux<ServiceBindingResource> requestListServiceBindings(CloudFoundryClient cloudFoundryClient, String applicationId) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.applicationsV2()
                .listServiceBindings(ListApplicationServiceBindingsRequest.builder()
                    .applicationId(applicationId)
                    .page(page)
                    .build()));
    }

    private static Flux<Envelope> requestLogsRecent(Mono<DopplerClient> dopplerClient, String applicationId) {
        return dopplerClient
            .flatMap(client -> client
                .recentLogs(RecentLogsRequest.builder()
                    .applicationId(applicationId)
                    .build()));
    }

    private static Flux<Envelope> requestLogsStream(Mono<DopplerClient> dopplerClient, String applicationId) {
        return dopplerClient
            .flatMap(client -> client
                .stream(StreamRequest.builder()
                    .applicationId(applicationId)
                    .build()));
    }

    private static Flux<SpaceResource> requestOrganizationSpacesByName(CloudFoundryClient cloudFoundryClient, String organizationId, String space) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.organizations()
                .listSpaces(ListOrganizationSpacesRequest.builder()
                    .page(page)
                    .organizationId(organizationId)
                    .name(space)
                    .build()));
    }

    private static Flux<OrganizationResource> requestOrganizations(CloudFoundryClient cloudFoundryClient, String organization) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.organizations()
                .list(ListOrganizationsRequest.builder()
                    .page(page)
                    .name(organization)
                    .build()));
    }

    private static Flux<PrivateDomainResource> requestPrivateDomain(CloudFoundryClient cloudFoundryClient, String domain, String organizationId) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.organizations()
                .listPrivateDomains(ListOrganizationPrivateDomainsRequest.builder()
                    .name(domain)
                    .organizationId(organizationId)
                    .page(page)
                    .build()));
    }

    private static Flux<PrivateDomainResource> requestPrivateDomains(CloudFoundryClient cloudFoundryClient, String organizationId) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.organizations()
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
        ListRoutesRequest.Builder requestBuilder = ListRoutesRequest.builder()
            .domainId(domainId);
        Optional.ofNullable(host).ifPresent(requestBuilder::host);
        Optional.ofNullable(routePath).ifPresent(requestBuilder::path);

        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.routes()
                .list(requestBuilder
                    .page(page)
                    .build()));
    }

    private static Flux<SharedDomainResource> requestSharedDomain(CloudFoundryClient cloudFoundryClient, String domain) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.sharedDomains()
                .list(ListSharedDomainsRequest.builder()
                    .name(domain)
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

    private static Mono<StackResource> requestStackId(CloudFoundryClient cloudFoundryClient, String stack) {
        return PaginationUtils
            .requestClientV2Resources(page -> cloudFoundryClient.stacks()
                .list(ListStacksRequest.builder()
                    .page(page)
                    .name(stack)
                    .build()))
            .single()
            .otherwise(NoSuchElementException.class, t -> ExceptionUtils.illegalArgument("Stack %s does not exist", stack));
    }

    private static Mono<Void> requestTerminateApplicationInstance(CloudFoundryClient cloudFoundryClient, String applicationId, String instanceIndex) {
        return cloudFoundryClient.applicationsV2()
            .terminateInstance(TerminateApplicationInstanceRequest.builder()
                .applicationId(applicationId)
                .index(instanceIndex)
                .build());
    }

    private static Mono<AbstractApplicationResource> requestUpdateApplication(CloudFoundryClient cloudFoundryClient, String applicationId, PushApplicationRequest request, String stackId) {
        return requestUpdateApplication(cloudFoundryClient, applicationId,
            builder -> {
                builder
                    .buildpack(request.getBuildpack())
                    .command(request.getCommand())
                    .diskQuota(request.getDiskQuota())
                    .healthCheckTimeout(request.getTimeout())
                    .healthCheckType(Optional.ofNullable(request.getHealthCheckType()).map(ApplicationHealthCheck::getValue).orElse(null))
                    .instances(request.getInstances())
                    .memory(request.getMemory())
                    .name(request.getName())
                    .stackId(stackId);

                Optional.ofNullable(request.getDockerImage())
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
            .map(OperationUtils.<UpdateApplicationResponse, AbstractApplicationResource>cast());
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

    private static Mono<UploadApplicationResponse> requestUploadApplication(CloudFoundryClient cloudFoundryClient, String applicationId, InputStream application) {
        return cloudFoundryClient.applicationsV2()
            .upload(UploadApplicationRequest.builder()
                .applicationId(applicationId)
                .async(true)
                .application(application)
                .build());
    }

    private static Mono<Void> restageApplication(CloudFoundryClient cloudFoundryClient, String application, String applicationId, Duration stagingTimeout, Duration startupTimeout) {
        return requestRestageApplication(cloudFoundryClient, applicationId)
            .then(response -> waitForStaging(cloudFoundryClient, application, applicationId, stagingTimeout))
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
            .then(response -> waitForStaging(cloudFoundryClient, application, applicationId, stagingTimeout))
            .then(waitForRunning(cloudFoundryClient, application, applicationId, startupTimeout));
    }

    private static Mono<Void> stopAndStartApplication(CloudFoundryClient cloudFoundryClient, String applicationId, PushApplicationRequest request) {
        return stopApplication(cloudFoundryClient, applicationId)
            .filter(resource -> !Optional.ofNullable(request.getNoStart()).orElse(false))
            .then(resource -> startApplicationAndWait(cloudFoundryClient, request.getName(), applicationId, request.getStagingTimeout(), request.getStartupTimeout()));
    }

    private static Mono<AbstractApplicationResource> stopApplication(CloudFoundryClient cloudFoundryClient, String applicationId) {
        return requestUpdateApplicationState(cloudFoundryClient, applicationId, STOPPED_STATE);
    }

    private static Mono<AbstractApplicationResource> stopApplicationIfNotStopped(CloudFoundryClient cloudFoundryClient, AbstractApplicationResource resource) {
        return isNotIn(resource, STOPPED_STATE) ? stopApplication(cloudFoundryClient, ResourceUtils.getId(resource)) : Mono.just(resource);
    }

    private static ApplicationDetail toApplicationDetail(ApplicationStatisticsResponse applicationStatisticsResponse, SummaryApplicationResponse summaryApplicationResponse,
                                                         GetStackResponse getStackResponse, ApplicationInstancesResponse applicationInstancesResponse,
                                                         List<InstanceDetail> instanceDetails, List<String> urls) {
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
            .instances(response.getInstances())
            .memory(response.getMemory())
            .name(response.getName())
            .stack(stackName)
            .timeout(response.getHealthCheckTimeout());

        for (Route route : Optional.ofNullable(response.getRoutes()).orElse(Collections.emptyList())) {
            Optional.ofNullable(route.getDomain()).map(Domain::getName).ifPresent(manifestBuilder::domain);
            Optional.ofNullable(route.getHost()).ifPresent(manifestBuilder::host);
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

    private static ApplicationHealthCheck toHealthCheck(AbstractApplicationResource resource) {
        String type = resource.getEntity().getHealthCheckType();

        if (ApplicationHealthCheck.NONE.getValue().equals(type)) {
            return ApplicationHealthCheck.NONE;
        } else if (ApplicationHealthCheck.PORT.getValue().equals(type)) {
            return ApplicationHealthCheck.PORT;
        } else {
            return null;
        }
    }

    private static InstanceDetail toInstanceDetail(Map.Entry<String, ApplicationInstanceInfo> entry, ApplicationStatisticsResponse statisticsResponse) {
        InstanceStatistics instanceStatistics = Optional.ofNullable(statisticsResponse.getInstances().get(entry.getKey())).orElse(emptyInstanceStats());
        Statistics stats = Optional.ofNullable(instanceStatistics.getStatistics()).orElse(emptyApplicationStatistics());
        Usage usage = Optional.ofNullable(stats.getUsage()).orElse(emptyApplicationUsage());

        return InstanceDetail.builder()
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

    private static String toUrl(Route route) {
        String hostName = route.getHost();
        String domainName = route.getDomain().getName();

        return hostName.isEmpty() ? domainName : String.format("%s.%s", hostName, domainName);
    }

    private static Mono<List<String>> toUrls(List<Route> routes) {
        return Flux
            .fromIterable(routes)
            .map(DefaultApplications::toUrl)
            .collectList();
    }

    private static Mono<Void> uploadApplicationAndWait(CloudFoundryClient cloudFoundryClient, String applicationId, InputStream application) {
        return requestUploadApplication(cloudFoundryClient, applicationId, application)
            .then(job -> JobUtils.waitForCompletion(cloudFoundryClient, job));
    }

    private static Mono<Void> waitForRunning(CloudFoundryClient cloudFoundryClient, String application, String applicationId, Duration startupTimeout) {
        Duration timeout = Optional.ofNullable(startupTimeout).orElse(Duration.ofMinutes(5));

        return requestApplicationInstances(cloudFoundryClient, applicationId)
            .flatMap(response -> Flux.fromIterable(response.getInstances().values()))
            .map(ApplicationInstanceInfo::getState)
            .reduce("UNKNOWN", collectStates())
            .filter(isInstanceComplete())
            .repeatWhenEmpty(exponentialBackOff(Duration.ofSeconds(1), Duration.ofSeconds(15), timeout))
            .filter(isRunning())
            .otherwiseIfEmpty(ExceptionUtils.illegalState("Application %s failed during start", application))
            .otherwise(DelayTimeoutException.class, t -> ExceptionUtils.illegalState("Application %s timed out during start", application))
            .then();
    }

    private static Mono<Void> waitForStaging(CloudFoundryClient cloudFoundryClient, String application, String applicationId, Duration stagingTimeout) {
        Duration timeout = Optional.ofNullable(stagingTimeout).orElse(Duration.ofMinutes(15));

        return requestGetApplication(cloudFoundryClient, applicationId)
            .map(response -> ResourceUtils.getEntity(response).getPackageState())
            .filter(isStagingComplete())
            .repeatWhenEmpty(exponentialBackOff(Duration.ofSeconds(1), Duration.ofSeconds(15), timeout))
            .filter(isStaged())
            .otherwiseIfEmpty(ExceptionUtils.illegalState("Application %s failed during staging", application))
            .otherwise(DelayTimeoutException.class, t -> ExceptionUtils.illegalState("Application %s timed out during staging", application))
            .then();
    }

}
