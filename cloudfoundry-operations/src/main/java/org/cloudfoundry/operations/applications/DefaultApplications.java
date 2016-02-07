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
import org.cloudfoundry.client.v2.applications.AbstractApplicationResource;
import org.cloudfoundry.client.v2.applications.ApplicationInstanceInfo;
import org.cloudfoundry.client.v2.applications.ApplicationInstancesRequest;
import org.cloudfoundry.client.v2.applications.ApplicationInstancesResponse;
import org.cloudfoundry.client.v2.applications.ApplicationResource;
import org.cloudfoundry.client.v2.applications.ApplicationStatisticsRequest;
import org.cloudfoundry.client.v2.applications.ApplicationStatisticsResponse;
import org.cloudfoundry.client.v2.applications.SummaryApplicationRequest;
import org.cloudfoundry.client.v2.applications.SummaryApplicationResponse;
import org.cloudfoundry.client.v2.applications.UpdateApplicationRequest;
import org.cloudfoundry.client.v2.applications.UpdateApplicationResponse;
import org.cloudfoundry.client.v2.routes.DeleteRouteRequest;
import org.cloudfoundry.client.v2.routes.Route;
import org.cloudfoundry.client.v2.spaces.GetSpaceSummaryRequest;
import org.cloudfoundry.client.v2.spaces.GetSpaceSummaryResponse;
import org.cloudfoundry.client.v2.spaces.ListSpaceApplicationsRequest;
import org.cloudfoundry.client.v2.spaces.ListSpaceApplicationsResponse;
import org.cloudfoundry.client.v2.spaces.SpaceApplicationSummary;
import org.cloudfoundry.client.v2.stacks.GetStackRequest;
import org.cloudfoundry.client.v2.stacks.GetStackResponse;
import org.cloudfoundry.utils.DateUtils;
import org.cloudfoundry.utils.ExceptionUtils;
import org.cloudfoundry.utils.ValidationUtils;
import org.cloudfoundry.utils.tuple.Function2;
import org.cloudfoundry.utils.tuple.Function4;
import org.cloudfoundry.utils.OperationUtils;
import org.cloudfoundry.utils.Optional;
import org.cloudfoundry.utils.OptionalUtils;
import org.cloudfoundry.utils.PaginationUtils;
import org.cloudfoundry.utils.ResourceUtils;
import org.cloudfoundry.utils.ValidationUtils;
import org.cloudfoundry.utils.tuple.Function2;
import org.cloudfoundry.utils.tuple.Function4;
import org.cloudfoundry.utils.tuple.Predicate2;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import reactor.fn.Function;
import reactor.fn.Predicate;
import reactor.fn.Supplier;
import reactor.fn.tuple.Tuple2;
import reactor.fn.tuple.Tuple4;
import reactor.rx.Stream;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.cloudfoundry.utils.OperationUtils.not;
import static org.cloudfoundry.utils.tuple.TupleUtils.function;
import static org.cloudfoundry.utils.tuple.TupleUtils.predicate;

public final class DefaultApplications implements Applications {

    public static final String STARTED_STATE = "STARTED";

    public static final String STOPPED_STATE = "STOPPED";

    private final CloudFoundryClient cloudFoundryClient;

    private final Mono<String> spaceId;

    public DefaultApplications(CloudFoundryClient cloudFoundryClient, Mono<String> spaceId) {
        this.cloudFoundryClient = cloudFoundryClient;
        this.spaceId = spaceId;
    }

    @Override
    public Mono<Void> delete(DeleteApplicationRequest request) {
        return ValidationUtils
            .validate(request)
            .and(this.spaceId)
            .then(function(new Function2<DeleteApplicationRequest, String, Mono<Tuple2<Optional<List<Route>>, String>>>() {

                @Override
                public Mono<Tuple2<Optional<List<Route>>, String>> apply(DeleteApplicationRequest request, String spaceId) {
                    return getRoutesAndApplicationId(DefaultApplications.this.cloudFoundryClient, request, spaceId);
                }

            }))
            .then(function(new Function2<Optional<List<Route>>, String, Mono<String>>() {

                @Override
                public Mono<String> apply(Optional<List<Route>> routes, final String applicationId) {
                    return deleteRoutes(DefaultApplications.this.cloudFoundryClient, routes)
                        .after(new Supplier<Mono<String>>() {

                            @Override
                            public Mono<String> get() {
                                return Mono.just(applicationId);
                            }

                        });
                }

            }))
            .then(new Function<String, Mono<Void>>() {

                @Override
                public Mono<Void> apply(String applicationId) {
                    return requestDeleteApplication(DefaultApplications.this.cloudFoundryClient, applicationId);
                }

            });
    }

    @Override
    public Mono<ApplicationDetail> get(GetApplicationRequest request) {
        return ValidationUtils
            .validate(request)
            .and(this.spaceId)
            .then(function(new Function2<GetApplicationRequest, String, Mono<ApplicationResource>>() {

                @Override
                public Mono<ApplicationResource> apply(GetApplicationRequest request, String spaceId) {
                    return getApplication(DefaultApplications.this.cloudFoundryClient, request.getName(), spaceId);
                }

            }))
            .then(new Function<ApplicationResource, Mono<Tuple4<ApplicationStatisticsResponse, SummaryApplicationResponse, GetStackResponse, ApplicationInstancesResponse>>>() {

                @Override
                public Mono<Tuple4<ApplicationStatisticsResponse, SummaryApplicationResponse, GetStackResponse, ApplicationInstancesResponse>> apply(ApplicationResource applicationResource) {
                    return getAuxiliaryContent(DefaultApplications.this.cloudFoundryClient, applicationResource);
                }

            })
            .map(function(new Function4<ApplicationStatisticsResponse, SummaryApplicationResponse, GetStackResponse, ApplicationInstancesResponse, ApplicationDetail>() {

                @Override
                public ApplicationDetail apply(ApplicationStatisticsResponse applicationStatisticsResponse, SummaryApplicationResponse summaryApplicationResponse, GetStackResponse getStackResponse,
                                               ApplicationInstancesResponse applicationInstancesResponse) {
                    return toApplicationDetail(applicationStatisticsResponse, summaryApplicationResponse, getStackResponse, applicationInstancesResponse);
                }

            }));
    }

    @Override
    public Publisher<ApplicationSummary> list() {
        return this.spaceId
            .then(new Function<String, Mono<GetSpaceSummaryResponse>>() {

                @Override
                public Mono<GetSpaceSummaryResponse> apply(String spaceId) {
                    return requestSpaceSummary(DefaultApplications.this.cloudFoundryClient, spaceId);
                }

            })
            .flatMap(new Function<GetSpaceSummaryResponse, Stream<SpaceApplicationSummary>>() {

                @Override
                public Stream<SpaceApplicationSummary> apply(GetSpaceSummaryResponse response) {
                    return extractApplications(response);
                }

            })
            .map(new Function<SpaceApplicationSummary, ApplicationSummary>() {

                @Override
                public ApplicationSummary apply(SpaceApplicationSummary spaceApplicationSummary) {
                    return toApplicationSummary(spaceApplicationSummary);
                }

            });
    }

    @Override
    public Mono<Void> rename(RenameApplicationRequest request) {
        return Mono
            .when(ValidationUtils.validate(request), this.spaceId)
            .then(function(new Function2<RenameApplicationRequest, String, Mono<Tuple2<String, RenameApplicationRequest>>>() {

                @Override
                public Mono<Tuple2<String, RenameApplicationRequest>> apply(RenameApplicationRequest request, String spaceId) {
                    return getApplicationId(DefaultApplications.this.cloudFoundryClient, request.getName(), spaceId)
                        .and(Mono.just(request));
                }
            }))
            .then(function(new Function2<String, RenameApplicationRequest, Mono<UpdateApplicationResponse>>() {

                @Override
                public Mono<UpdateApplicationResponse> apply(String applicationId, RenameApplicationRequest request) {
                    return requestUpdateApplicationRename(DefaultApplications.this.cloudFoundryClient, applicationId, request.getNewName());
                }

            }))
            .after();
    }

    @Override
    public Mono<Void> scale(final ScaleApplicationRequest request) {
        return Mono
            .when(ValidationUtils.validate(request), this.spaceId)
            .where(predicate(new Predicate2<ScaleApplicationRequest, String>() {

                @Override
                public boolean test(ScaleApplicationRequest request, String spaceId) {
                    return areModifiersPresent(request);
                }

            }))
            .then(function(new Function2<ScaleApplicationRequest, String, Mono<Tuple2<String, ScaleApplicationRequest>>>() {

                @Override
                public Mono<Tuple2<String, ScaleApplicationRequest>> apply(ScaleApplicationRequest request, String spaceId) {
                    return getApplicationId(DefaultApplications.this.cloudFoundryClient, request.getName(), spaceId)
                        .and(Mono.just(request));
                }

            }))
            .then(function(new Function2<String, ScaleApplicationRequest, Mono<Tuple2<UpdateApplicationResponse, ScaleApplicationRequest>>>() {

                @Override
                public Mono<Tuple2<UpdateApplicationResponse, ScaleApplicationRequest>> apply(String applicationId, ScaleApplicationRequest request) {
                    return requestUpdateApplicationScale(cloudFoundryClient, applicationId, request.getDiskLimit(), request.getInstances(), request.getMemoryLimit())
                        .and(Mono.just(request));
                }
            }))
            .where(predicate(new Predicate2<UpdateApplicationResponse, ScaleApplicationRequest>() {

                @Override
                public boolean test(UpdateApplicationResponse resource, ScaleApplicationRequest request) {
                    return isRestartRequired(request, resource);
                }

            }))
            .then(function(new Function2<UpdateApplicationResponse, ScaleApplicationRequest, Mono<UpdateApplicationResponse>>() {

                @Override
                public Mono<UpdateApplicationResponse> apply(UpdateApplicationResponse resource, ScaleApplicationRequest request) {
                    return restartApplication(cloudFoundryClient, ResourceUtils.getId(resource));
                }

            }))
            .after();
    }

    @Override
    public Mono<Void> start(StartApplicationRequest request) {
        return ValidationUtils
            .validate(request)
            .map(new Function<StartApplicationRequest, String>() {

                @Override
                public String apply(StartApplicationRequest request) {
                    return request.getName();
                }

            })
            .and(this.spaceId)
            .then(function(new Function2<String, String, Mono<String>>() {

                @Override
                public Mono<String> apply(String application, String spaceId) {
                    return getApplicationIdWhere(DefaultApplications.this.cloudFoundryClient, application, spaceId, not(isIn(STARTED_STATE)));
                }

            }))
            .then(new Function<String, Mono<UpdateApplicationResponse>>() {

                @Override
                public Mono<UpdateApplicationResponse> apply(String applicationId) {
                    return startApplication(DefaultApplications.this.cloudFoundryClient, applicationId);
                }

            })
            .after();
    }

    @Override
    public Mono<Void> stop(StopApplicationRequest request) {
        return ValidationUtils
            .validate(request)
            .map(new Function<StopApplicationRequest, String>() {

                @Override
                public String apply(StopApplicationRequest request) {
                    return request.getName();
                }

            })
            .and(this.spaceId)
            .then(function(new Function2<String, String, Mono<String>>() {

                @Override
                public Mono<String> apply(String application, String spaceId) {
                    return getApplicationIdWhere(DefaultApplications.this.cloudFoundryClient, application, spaceId, not(isIn(STOPPED_STATE)));
                }

            }))
            .then(new Function<String, Mono<UpdateApplicationResponse>>() {

                @Override
                public Mono<UpdateApplicationResponse> apply(String applicationId) {
                    return stopApplication(DefaultApplications.this.cloudFoundryClient, applicationId);
                }

            })
            .after();
    }

    private static boolean areModifiersPresent(ScaleApplicationRequest request) {
        return request.getMemoryLimit() != null || request.getDiskLimit() != null || request.getInstances() != null;
    }

    private static Mono<Void> deleteRoutes(final CloudFoundryClient cloudFoundryClient, Optional<List<Route>> routes) {
        return routes
            .map(new Function<List<Route>, Stream<Route>>() {

                @Override
                public Stream<Route> apply(List<Route> routes) {
                    return Stream.fromIterable(routes);

                }
            })
            .orElse(Stream.<Route>empty())
            .map(new Function<Route, String>() {

                @Override
                public String apply(Route route) {
                    return route.getId();
                }

            })
            .flatMap(new Function<String, Mono<Void>>() {

                @Override
                public Mono<Void> apply(String routeId) {
                    return requestDeleteRoute(cloudFoundryClient, routeId);
                }

            })
            .after();
    }

    private static Stream<SpaceApplicationSummary> extractApplications(GetSpaceSummaryResponse getSpaceSummaryResponse) {
        return Stream.fromIterable(getSpaceSummaryResponse.getApplications());
    }

    private static Mono<ApplicationResource> getApplication(CloudFoundryClient cloudFoundryClient, String application, String spaceId) {
        return requestApplications(cloudFoundryClient, application, spaceId)
            .single()
            .otherwise(ExceptionUtils.<ApplicationResource>convert("Application %s does not exist", application));
    }

    private static Mono<String> getApplicationId(CloudFoundryClient cloudFoundryClient, String application, String spaceId) {
        return getApplication(cloudFoundryClient, application, spaceId)
            .map(ResourceUtils.extractId());
    }

    private static Mono<String> getApplicationIdWhere(CloudFoundryClient cloudFoundryClient, String application, String spaceId, Predicate<ApplicationResource> predicate) {
        return getApplication(cloudFoundryClient, application, spaceId)
            .where(predicate)
            .map(ResourceUtils.extractId());
    }

    private static Mono<Tuple4<ApplicationStatisticsResponse, SummaryApplicationResponse, GetStackResponse, ApplicationInstancesResponse>> getAuxiliaryContent(
        CloudFoundryClient cloudFoundryClient, AbstractApplicationResource applicationResource) {

        String applicationId = ResourceUtils.getId(applicationResource);
        String stackId = ResourceUtils.getEntity(applicationResource).getStackId();

        return Mono
            .when(
                requestApplicationStats(cloudFoundryClient, applicationId),
                requestApplicationSummary(cloudFoundryClient, applicationId),
                requestStack(cloudFoundryClient, stackId),
                requestApplicationInstances(cloudFoundryClient, applicationId)
            );
    }

    private static String getBuildpack(SummaryApplicationResponse response) {
        return Optional
            .ofNullable(response.getBuildpack())
            .orElse(response.getDetectedBuildpack());
    }

    private static Mono<Optional<List<Route>>> getOptionalRoutes(final CloudFoundryClient cloudFoundryClient, boolean deleteRoutes, final String applicationId) {
        return Mono
            .just(deleteRoutes)
            .where(OperationUtils.identity())
            .then(new Function<Boolean, Mono<List<Route>>>() {

                @Override
                public Mono<List<Route>> apply(Boolean deleteRoutes) {
                    return getRoutes(cloudFoundryClient, applicationId);
                }

            })
            .map(OptionalUtils.<List<Route>>toOptional())
            .defaultIfEmpty(Optional.<List<Route>>empty());
    }

    private static Mono<List<Route>> getRoutes(CloudFoundryClient cloudFoundryClient, String applicationId) {
        return requestApplicationSummary(cloudFoundryClient, applicationId)
            .map(new Function<SummaryApplicationResponse, List<Route>>() {

                @Override
                public List<Route> apply(SummaryApplicationResponse summaryApplicationResponse) {
                    return summaryApplicationResponse.getRoutes();
                }

            });
    }

    private static Mono<Tuple2<Optional<List<Route>>, String>> getRoutesAndApplicationId(final CloudFoundryClient cloudFoundryClient, final DeleteApplicationRequest deleteApplicationRequest,
                                                                                         String spaceId) {
        return getApplicationId(cloudFoundryClient, deleteApplicationRequest.getName(), spaceId)
            .then(new Function<String, Mono<Tuple2<Optional<List<Route>>, String>>>() {

                @Override
                public Mono<Tuple2<Optional<List<Route>>, String>> apply(final String applicationId) {
                    return getOptionalRoutes(cloudFoundryClient, deleteApplicationRequest.getDeleteRoutes(), applicationId)
                        .and(Mono.just(applicationId));
                }

            });
    }

    private static Predicate<ApplicationResource> isIn(final String state) {
        return new Predicate<ApplicationResource>() {

            @Override
            public boolean test(ApplicationResource resource) {
                return state.equals(ResourceUtils.getEntity(resource).getState());
            }

        };
    }

    private static boolean isRestartRequired(ScaleApplicationRequest request, AbstractApplicationResource applicationResource) {
        return (request.getDiskLimit() != null || request.getMemoryLimit() != null)
            && STARTED_STATE.equals(ResourceUtils.getEntity(applicationResource).getState());
    }

    private static Mono<ApplicationInstancesResponse> requestApplicationInstances(CloudFoundryClient cloudFoundryClient, String applicationId) {
        return cloudFoundryClient.applicationsV2()
            .instances(ApplicationInstancesRequest.builder()
                .applicationId(applicationId)
                .build());
    }

    private static Mono<ApplicationStatisticsResponse> requestApplicationStats(CloudFoundryClient cloudFoundryClient, String applicationId) {
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

    private static Stream<ApplicationResource> requestApplications(final CloudFoundryClient cloudFoundryClient, final String application, final String spaceId) {
        return PaginationUtils
            .requestResources(new Function<Integer, Mono<ListSpaceApplicationsResponse>>() {

                @Override
                public Mono<ListSpaceApplicationsResponse> apply(Integer page) {
                    return cloudFoundryClient.spaces()
                        .listApplications(ListSpaceApplicationsRequest.builder()
                            .name(application)
                            .spaceId(spaceId)
                            .page(page)
                            .build());
                }

            });
    }

    private static Mono<Void> requestDeleteApplication(CloudFoundryClient cloudFoundryClient, String applicationId) {
        return cloudFoundryClient.applicationsV2()
            .delete(org.cloudfoundry.client.v2.applications.DeleteApplicationRequest.builder()
                .applicationId(applicationId)
                .build());
    }

    private static Mono<Void> requestDeleteRoute(CloudFoundryClient cloudFoundryClient, String routeId) {
        return cloudFoundryClient.routes()
            .delete(DeleteRouteRequest.builder()
                .routeId(routeId)
                .build())
            .after();
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

    private static Mono<UpdateApplicationResponse> requestUpdateApplicationRename(CloudFoundryClient cloudFoundryClient, String applicationId, final String name) {
        return cloudFoundryClient.applicationsV2()
            .update(UpdateApplicationRequest.builder()
                .applicationId(applicationId)
                .name(name)
                .build());
    }

    private static Mono<UpdateApplicationResponse> requestUpdateApplicationScale(CloudFoundryClient cloudFoundryClient, String applicationId, Integer disk, Integer instances, Integer memory) {
        return cloudFoundryClient.applicationsV2()
            .update(UpdateApplicationRequest.builder()
                .applicationId(applicationId)
                .diskQuota(disk)
                .instances(instances)
                .memory(memory)
                .build());
    }

    private static Mono<UpdateApplicationResponse> requestUpdateApplicationState(CloudFoundryClient cloudFoundryClient, String applicationId, String state) {
        return cloudFoundryClient.applicationsV2()
            .update(UpdateApplicationRequest.builder()
                .applicationId(applicationId)
                .state(state)
                .build());
    }

    private static Mono<UpdateApplicationResponse> restartApplication(final CloudFoundryClient cloudFoundryClient, final String applicationId) {
        return stopApplication(cloudFoundryClient, applicationId)
            .then(new Function<AbstractApplicationResource, Mono<UpdateApplicationResponse>>() {

                @Override
                public Mono<UpdateApplicationResponse> apply(AbstractApplicationResource abstractApplicationResource) {
                    return startApplication(cloudFoundryClient, applicationId);
                }

            });
    }

    private static Mono<UpdateApplicationResponse> startApplication(CloudFoundryClient cloudFoundryClient, String applicationId) {
        return requestUpdateApplicationState(cloudFoundryClient, applicationId, STARTED_STATE);
    }

    private static Mono<UpdateApplicationResponse> stopApplication(CloudFoundryClient cloudFoundryClient, String applicationId) {
        return requestUpdateApplicationState(cloudFoundryClient, applicationId, STOPPED_STATE);
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
        ApplicationStatisticsResponse.InstanceStats.Statistics stats = statisticsResponse.get(entry.getKey()).getStatistics();
        ApplicationStatisticsResponse.InstanceStats.Statistics.Usage usage = stats.getUsage();

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

    private static List<ApplicationDetail.InstanceDetail> toInstanceDetailList(ApplicationInstancesResponse instancesResponse, final ApplicationStatisticsResponse statisticsResponse) {
        return Stream
            .fromIterable(instancesResponse.entrySet())
            .map(new Function<Map.Entry<String, ApplicationInstanceInfo>, ApplicationDetail.InstanceDetail>() {

                @Override
                public ApplicationDetail.InstanceDetail apply(Map.Entry<String, ApplicationInstanceInfo> entry) {
                    return toInstanceDetail(entry, statisticsResponse);
                }

            })
            .toList()
            .get();
    }

    private static String toUrl(Route route) {
        String hostName = route.getHost();
        String domainName = route.getDomain().getName();

        return hostName.isEmpty() ? domainName : String.format("%s.%s", hostName, domainName);
    }

    private static List<String> toUrls(List<Route> routes) {
        return Stream
            .fromIterable(routes)
            .map(new Function<Route, String>() {

                @Override
                public String apply(Route route) {
                    return toUrl(route);
                }

            })
            .toList()
            .get();
    }

}
