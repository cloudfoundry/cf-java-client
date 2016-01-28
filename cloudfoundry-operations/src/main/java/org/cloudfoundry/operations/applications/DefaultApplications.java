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
import org.cloudfoundry.client.v2.applications.ApplicationEntity;
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
import org.cloudfoundry.client.v2.routes.DeleteRouteResponse;
import org.cloudfoundry.client.v2.routes.Route;
import org.cloudfoundry.client.v2.spaces.GetSpaceSummaryRequest;
import org.cloudfoundry.client.v2.spaces.GetSpaceSummaryResponse;
import org.cloudfoundry.client.v2.spaces.ListSpaceApplicationsRequest;
import org.cloudfoundry.client.v2.spaces.ListSpaceApplicationsResponse;
import org.cloudfoundry.client.v2.spaces.SpaceApplicationSummary;
import org.cloudfoundry.client.v2.stacks.GetStackRequest;
import org.cloudfoundry.client.v2.stacks.GetStackResponse;
import org.cloudfoundry.operations.util.Dates;
import org.cloudfoundry.operations.util.Exceptions;
import org.cloudfoundry.operations.util.Function2;
import org.cloudfoundry.operations.util.Function4;
import org.cloudfoundry.operations.util.Optional;
import org.cloudfoundry.operations.util.Validators;
import org.cloudfoundry.operations.util.v2.Paginated;
import org.cloudfoundry.operations.util.v2.Resources;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import reactor.fn.Function;
import reactor.fn.Supplier;
import reactor.fn.tuple.Tuple2;
import reactor.fn.tuple.Tuple4;
import reactor.rx.Stream;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.cloudfoundry.operations.util.Tuples.function;

public final class DefaultApplications implements Applications {

    public static final Mono<List<Route>> NO_ROUTES = Mono.just((List<Route>) new ArrayList<Route>());

    private final CloudFoundryClient cloudFoundryClient;

    private final Mono<String> spaceId;

    public DefaultApplications(CloudFoundryClient cloudFoundryClient, Mono<String> spaceId) {
        this.cloudFoundryClient = cloudFoundryClient;
        this.spaceId = spaceId;
    }

    @Override
    public Mono<Void> delete(DeleteApplicationRequest request) {
        return Validators
                .validate(request)
                .and(this.spaceId)
                .then(determineApplicationAndRoutesToDelete(this.cloudFoundryClient))
                .then(deleteRoutes(this.cloudFoundryClient))
                .then(deleteApplication(this.cloudFoundryClient));
    }

    @Override
    public Mono<ApplicationDetail> get(GetApplicationRequest request) {
        return Validators
                .validate(request)
                .and(this.spaceId)
                .then(requestApplicationResource(this.cloudFoundryClient))
                .then(gatherApplicationInfo(this.cloudFoundryClient))
                .map(toApplicationDetail());
    }

    @Override
    public Publisher<ApplicationSummary> list() {
        return this.spaceId
                .then(requestSpaceSummary(this.cloudFoundryClient))
                .flatMap(extractApplications())
                .map(toApplication());
    }

    @Override
    public Publisher<Void> rename(RenameApplicationRequest request) {
        return Mono
                .when(Validators.validate(request), this.spaceId)
                .then(getApplicationResourceForRename(this.cloudFoundryClient))
                .then(renameApplication(this.cloudFoundryClient))
                .after();
    }

    @Override
    public Publisher<ApplicationScale> scale(final ScaleApplicationRequest request) {
        return Mono
                .when(Validators.validate(request), this.spaceId)
                .then(getApplicationResourceForScale(this.cloudFoundryClient))
                .then(conditionallyScaleApplication(this.cloudFoundryClient))
                .map(buildApplicationScale());
    }

    private static Function<ApplicationEntity, ApplicationScale> buildApplicationScale() {
        return new Function<ApplicationEntity, ApplicationScale>() {

            @Override
            public ApplicationScale apply(ApplicationEntity applicationEntity) {
                return ApplicationScale.builder()
                        .diskLimit(applicationEntity.getDiskQuota())
                        .instances(applicationEntity.getInstances())
                        .memoryLimit(applicationEntity.getMemory())
                        .build();
            }
        };
    }

    private static Function<Tuple2<ScaleApplicationRequest, ApplicationResource>, Mono<ApplicationEntity>> conditionallyScaleApplication(final CloudFoundryClient cloudFoundryClient) {
        return function(new Function2<ScaleApplicationRequest, ApplicationResource, Mono<ApplicationEntity>>() {

            @Override
            public Mono<ApplicationEntity> apply(ScaleApplicationRequest request, ApplicationResource applicationResource) {
                if (scaleModifiersPresent(request)) {
                    return scaleApplication(cloudFoundryClient,
                            Resources.getId(applicationResource),
                            request.getDiskLimit(),
                            request.getInstances(),
                            request.getMemoryLimit()
                    );
                } else {
                    return Mono.just(Resources.getEntity(applicationResource));
                }
            }
        });
    }

    private static Function<String, Mono<Void>> deleteApplication(final CloudFoundryClient cloudFoundryClient) {
        return new Function<String, Mono<Void>>() {

            @Override
            public Mono<Void> apply(String applicationId) {
                return cloudFoundryClient.applicationsV2().delete(org.cloudfoundry.client.v2.applications.DeleteApplicationRequest.builder()
                        .applicationId(applicationId)
                        .build());
            }

        };
    }

    private static Function<Tuple2<List<Route>, String>, Mono<String>> deleteRoutes(final CloudFoundryClient cloudFoundryClient) {
        return function(new Function2<List<Route>, String, Mono<String>>() {

            @Override
            public Mono<String> apply(List<Route> routes, final String applicationId) {
                return Stream.fromIterable(routes)
                        .map(new Function<Route, String>() {

                            @Override
                            public String apply(Route route) {
                                return route.getId();
                            }

                        })
                        .flatMap(new Function<String, Mono<DeleteRouteResponse>>() {

                            @Override
                            public Mono<DeleteRouteResponse> apply(String routeId) {
                                return cloudFoundryClient.routes().delete(DeleteRouteRequest.builder()
                                        .routeId(routeId)
                                        .build());
                            }

                        })
                        .after() // Convert to Mono preserving any error
                        .after(new Supplier<Mono<String>>() {

                            @Override
                            public Mono<String> get() {
                                return Mono.just(applicationId);
                            }

                        });
            }

        });
    }

    private static Function<Tuple2<DeleteApplicationRequest, String>, Mono<Tuple2<List<Route>, String>>> determineApplicationAndRoutesToDelete(final CloudFoundryClient cloudFoundryClient) {
        return function(new Function2<DeleteApplicationRequest, String, Mono<Tuple2<List<Route>, String>>>() {

            @Override
            public Mono<Tuple2<List<Route>, String>> apply(final DeleteApplicationRequest deleteApplicationRequest, final String spaceId) {
                String applicationName = deleteApplicationRequest.getName();
                boolean deleteRoutes = deleteApplicationRequest.getDeleteRoutes();

                return Paginated
                        .requestResources(requestListApplicationsPage(cloudFoundryClient, spaceId, applicationName))
                        .map(Resources.extractId())
                        .single()
                        .otherwise(Exceptions.<String>convert("Application %s does not exist", applicationName))
                        .then(determineRoutesToDelete(cloudFoundryClient, deleteRoutes));
            }

        });
    }

    private static Function<String, Mono<Tuple2<List<Route>, String>>> determineRoutesToDelete(final CloudFoundryClient cloudFoundryClient, final boolean deleteRoutes) {
        return new Function<String, Mono<Tuple2<List<Route>, String>>>() {

            @Override
            public Mono<Tuple2<List<Route>, String>> apply(final String applicationId) {

                return (deleteRoutes ? requestApplicationRoutes(cloudFoundryClient, applicationId) : NO_ROUTES)
                        .and(Mono.just(applicationId));
            }

        };
    }

    private static Function<GetSpaceSummaryResponse, Stream<SpaceApplicationSummary>> extractApplications() {
        return new Function<GetSpaceSummaryResponse, Stream<SpaceApplicationSummary>>() {

            @Override
            public Stream<SpaceApplicationSummary> apply(GetSpaceSummaryResponse getSpaceSummaryResponse) {
                return Stream.fromIterable(getSpaceSummaryResponse.getApplications());
            }

        };
    }

    private static Function<ApplicationResource, Mono<Tuple4<ApplicationStatisticsResponse, SummaryApplicationResponse, GetStackResponse, ApplicationInstancesResponse>>>
    gatherApplicationInfo(final CloudFoundryClient cloudFoundryClient) {
        return new Function<ApplicationResource, Mono<Tuple4<ApplicationStatisticsResponse, SummaryApplicationResponse, GetStackResponse, ApplicationInstancesResponse>>>() {

            @Override
            public Mono<Tuple4<ApplicationStatisticsResponse, SummaryApplicationResponse, GetStackResponse, ApplicationInstancesResponse>> apply(ApplicationResource applicationResource) {
                String applicationId = Resources.getId(applicationResource);
                String stackId = Resources.getEntity(applicationResource).getStackId();

                return Mono.when(requestApplicationStats(cloudFoundryClient, applicationId), requestApplicationSummary(cloudFoundryClient, applicationId), requestStack(cloudFoundryClient, stackId),
                        requestApplicationInstances(cloudFoundryClient, applicationId));
            }

        };
    }

    private static Function<Tuple2<RenameApplicationRequest, String>, Mono<Tuple2<RenameApplicationRequest, ApplicationResource>>>
    getApplicationResourceForRename(final CloudFoundryClient cloudFoundryClient) {
        return function(new Function2<RenameApplicationRequest, String, Mono<Tuple2<RenameApplicationRequest, ApplicationResource>>>() {

            @Override
            public Mono<Tuple2<RenameApplicationRequest, ApplicationResource>> apply(RenameApplicationRequest request, String spaceId) {

                return Mono.when(
                        Mono.just(request),
                        getSpaceApplication(cloudFoundryClient, spaceId, request.getName())
                );
            }
        });
    }

    private static Function<Tuple2<ScaleApplicationRequest, String>, Mono<Tuple2<ScaleApplicationRequest, ApplicationResource>>>
    getApplicationResourceForScale(final CloudFoundryClient cloudFoundryClient) {
        return function(new Function2<ScaleApplicationRequest, String, Mono<Tuple2<ScaleApplicationRequest, ApplicationResource>>>() {

            @Override
            public Mono<Tuple2<ScaleApplicationRequest, ApplicationResource>> apply(ScaleApplicationRequest request, String spaceId) {

                return Mono.when(
                        Mono.just(request),
                        getSpaceApplication(cloudFoundryClient, spaceId, request.getName())
                );
            }
        });
    }

    private static String getBuildpack(SummaryApplicationResponse response) {
        return Optional
                .ofNullable(response.getBuildpack())
                .orElse(response.getDetectedBuildpack());
    }

    private static Mono<ApplicationResource> getSpaceApplication(final CloudFoundryClient cloudFoundryClient, final String spaceId, final String name) {
        return Paginated
                .requestResources(new Function<Integer, Mono<ListSpaceApplicationsResponse>>() {
                    @Override
                    public Mono<ListSpaceApplicationsResponse> apply(Integer page) {
                        ListSpaceApplicationsRequest request = ListSpaceApplicationsRequest.builder()
                                .spaceId(spaceId)
                                .name(name)
                                .page(page)
                                .build();

                        return cloudFoundryClient.spaces().listApplications(request);
                    }
                })
                .single()
                .otherwise(Exceptions.<ApplicationResource>convert("Application %s does not exist", name));
    }

    private static Function<Tuple2<RenameApplicationRequest, ApplicationResource>, Mono<ApplicationEntity>> renameApplication(final CloudFoundryClient cloudFoundryClient) {
        return function(new Function2<RenameApplicationRequest, ApplicationResource, Mono<ApplicationEntity>>() {

            @Override
            public Mono<ApplicationEntity> apply(RenameApplicationRequest request, ApplicationResource applicationResource) {
                return renameApplication(cloudFoundryClient, Resources.getId(applicationResource), request.getNewName());
            }
        });
    }

    private static Mono<ApplicationEntity> renameApplication(final CloudFoundryClient cloudFoundryClient, final String applicationId, final String newName) {
        UpdateApplicationRequest request = UpdateApplicationRequest.builder()
                .applicationId(applicationId)
                .name(newName)
                .build();

        return cloudFoundryClient.applicationsV2().update(request)
                .map(new Function<UpdateApplicationResponse, ApplicationEntity>() {
                    @Override
                    public ApplicationEntity apply(UpdateApplicationResponse resource) {
                        return Resources.getEntity(resource);
                    }
                });
    }

    private static Mono<ApplicationInstancesResponse> requestApplicationInstances(CloudFoundryClient cloudFoundryClient, String applicationId) {
        ApplicationInstancesRequest request = ApplicationInstancesRequest.builder()
                .applicationId(applicationId)
                .build();

        return cloudFoundryClient.applicationsV2().instances(request);
    }

    private static Function<Tuple2<GetApplicationRequest, String>, Mono<ApplicationResource>> requestApplicationResource(final CloudFoundryClient cloudFoundryClient) {
        return function(new Function2<GetApplicationRequest, String, Mono<ApplicationResource>>() {

            @Override
            public Mono<ApplicationResource> apply(GetApplicationRequest getApplicationRequest, String spaceId) {
                return Paginated
                        .requestResources(requestListApplicationsPage(cloudFoundryClient, spaceId, getApplicationRequest.getName()))
                        .single();
            }

        });
    }

    private static Mono<List<Route>> requestApplicationRoutes(CloudFoundryClient cloudFoundryClient, String applicationId) {
        return cloudFoundryClient.applicationsV2().summary(SummaryApplicationRequest.builder()
                .applicationId(applicationId)
                .build())
                .map(new Function<SummaryApplicationResponse, List<Route>>() {

                    @Override
                    public List<Route> apply(SummaryApplicationResponse summaryApplicationResponse) {
                        return summaryApplicationResponse.getRoutes();
                    }

                });
    }

    private static Mono<ApplicationStatisticsResponse> requestApplicationStats(CloudFoundryClient cloudFoundryClient, String applicationId) {
        ApplicationStatisticsRequest request = ApplicationStatisticsRequest.builder()
                .applicationId(applicationId)
                .build();

        return cloudFoundryClient.applicationsV2().statistics(request);
    }

    private static Mono<SummaryApplicationResponse> requestApplicationSummary(CloudFoundryClient cloudFoundryClient, String applicationId) {
        SummaryApplicationRequest request = SummaryApplicationRequest.builder()
                .applicationId(applicationId)
                .build();

        return cloudFoundryClient.applicationsV2().summary(request);
    }

    private static Function<Integer, Mono<ListSpaceApplicationsResponse>> requestListApplicationsPage(final CloudFoundryClient cloudFoundryClient, final String spaceId, final String applicationName) {
        return new Function<Integer, Mono<ListSpaceApplicationsResponse>>() {

            @Override
            public Mono<ListSpaceApplicationsResponse> apply(Integer page) {
                ListSpaceApplicationsRequest request = ListSpaceApplicationsRequest.builder()
                        .name(applicationName)
                        .spaceId(spaceId)
                        .page(page)
                        .build();

                return cloudFoundryClient.spaces().listApplications(request);
            }

        };
    }

    private static Function<String, Mono<GetSpaceSummaryResponse>> requestSpaceSummary(final CloudFoundryClient cloudFoundryClient) {
        return new Function<String, Mono<GetSpaceSummaryResponse>>() {

            @Override
            public Mono<GetSpaceSummaryResponse> apply(String targetedSpace) {
                GetSpaceSummaryRequest request = GetSpaceSummaryRequest.builder()
                        .spaceId(targetedSpace)
                        .build();

                return cloudFoundryClient.spaces().getSummary(request);
            }

        };
    }

    private static Mono<GetStackResponse> requestStack(CloudFoundryClient cloudFoundryClient, String stackId) {
        GetStackRequest request = GetStackRequest.builder()
                .stackId(stackId)
                .build();

        return cloudFoundryClient.stacks().get(request);
    }

    private static Mono<ApplicationEntity> scaleApplication(final CloudFoundryClient cloudFoundryClient, final String applicationId, final Integer disk, final Integer
            instances, final Integer memory) {
        UpdateApplicationRequest request = UpdateApplicationRequest.builder()
                .applicationId(applicationId)
                .diskQuota(disk)
                .instances(instances)
                .memory(memory)
                .build();

        return cloudFoundryClient.applicationsV2().update(request)
                .map(new Function<UpdateApplicationResponse, ApplicationEntity>() {
                    @Override
                    public ApplicationEntity apply(UpdateApplicationResponse resource) {
                        return Resources.getEntity(resource);
                    }
                });
    }

    private static boolean scaleModifiersPresent(ScaleApplicationRequest request) {
        return request.getMemoryLimit() != null || request.getDiskLimit() != null || request.getInstances() != null;
    }

    private static Function<SpaceApplicationSummary, ApplicationSummary> toApplication() {
        return new Function<SpaceApplicationSummary, ApplicationSummary>() {

            @Override
            public ApplicationSummary apply(SpaceApplicationSummary spaceApplicationSummary) {
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

        };
    }

    private static Function<Tuple4<ApplicationStatisticsResponse, SummaryApplicationResponse, GetStackResponse, ApplicationInstancesResponse>, ApplicationDetail> toApplicationDetail() {
        return function(new Function4<ApplicationStatisticsResponse, SummaryApplicationResponse, GetStackResponse, ApplicationInstancesResponse, ApplicationDetail>() {

            @Override
            public ApplicationDetail apply(ApplicationStatisticsResponse applicationStatisticsResponse, SummaryApplicationResponse summaryApplicationResponse, GetStackResponse getStackResponse,
                                           ApplicationInstancesResponse applicationInstancesResponse) {

                List<String> urls = toUrls(summaryApplicationResponse.getRoutes());

                return ApplicationDetail.builder()
                        .id(summaryApplicationResponse.getId())
                        .diskQuota(summaryApplicationResponse.getDiskQuota())
                        .memoryLimit(summaryApplicationResponse.getMemory())
                        .requestedState(summaryApplicationResponse.getState())
                        .instances(summaryApplicationResponse.getInstances())
                        .urls(urls)
                        .lastUploaded(toDate(summaryApplicationResponse.getPackageUpdatedAt()))
                        .stack(getStackResponse.getEntity().getName())
                        .buildpack(getBuildpack(summaryApplicationResponse))
                        .instanceDetails(toInstanceDetailList(applicationInstancesResponse, applicationStatisticsResponse))
                        .build();
            }

        });
    }

    private static Date toDate(String date) {
        if (date == null) {
            return null;
        }

        try {
            return Dates.parse(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private static Date toDate(Double date) {
        if (date == null) {
            return null;
        }

        return new Date(TimeUnit.SECONDS.toMillis(date.longValue()));
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

    private static List<ApplicationDetail.InstanceDetail> toInstanceDetailList(ApplicationInstancesResponse instancesResponse, ApplicationStatisticsResponse statisticsResponse) {
        List<ApplicationDetail.InstanceDetail> instanceDetails = new ArrayList<>(instancesResponse.size());

        for (Map.Entry<String, ApplicationInstanceInfo> entry : instancesResponse.entrySet()) {
            instanceDetails.add(toInstanceDetail(entry, statisticsResponse));
        }

        return instanceDetails;
    }

    private static List<String> toUrls(List<Route> routes) {
        List<String> urls = new ArrayList<>(routes.size());

        for (Route route : routes) {
            String hostName = route.getHost();
            String domainName = route.getDomain().getName();

            urls.add(hostName.isEmpty() ? domainName : String.format("%s.%s", hostName, domainName));
        }

        return urls;
    }

}
