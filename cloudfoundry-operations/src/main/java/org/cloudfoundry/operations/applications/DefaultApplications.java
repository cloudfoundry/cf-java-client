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
import org.cloudfoundry.utils.DateUtils;
import org.cloudfoundry.utils.ExceptionUtils;
import org.cloudfoundry.utils.Optional;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.cloudfoundry.utils.tuple.TupleUtils.function;
import static org.cloudfoundry.utils.tuple.TupleUtils.predicate;

public final class DefaultApplications implements Applications {

    public static final Mono<List<Route>> NO_ROUTES = Mono.just((List<Route>) new ArrayList<Route>());

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
            .then(determineApplicationAndRoutesToDelete(this.cloudFoundryClient))
            .then(deleteRoutes(this.cloudFoundryClient))
            .then(deleteApplication(this.cloudFoundryClient));
    }

    @Override
    public Mono<ApplicationDetail> get(GetApplicationRequest request) {
        return ValidationUtils
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
    public Mono<Void> rename(RenameApplicationRequest request) {
        return Mono
            .when(ValidationUtils.validate(request), this.spaceId)
            .then(getApplicationResourceForRename(this.cloudFoundryClient))
            .then(renameApplication(this.cloudFoundryClient))
            .after();
    }

    @Override
    public Mono<Void> scale(final ScaleApplicationRequest request) {
        return ValidationUtils.validate(request)
            .where(new Predicate<ScaleApplicationRequest>() {
                @Override
                public boolean test(ScaleApplicationRequest request) {
                    return scaleModifiersPresent(request);
                }
            })
            .and(this.spaceId)
            .then(function(new Function2<ScaleApplicationRequest, String, Mono<Tuple2<ScaleApplicationRequest, AbstractApplicationResource>>>() {

                @Override
                public Mono<Tuple2<ScaleApplicationRequest, AbstractApplicationResource>> apply(ScaleApplicationRequest request1, String spaceId1) {
                    return Mono
                        .when(
                            Mono.just(request1),
                            getSpaceApplication(DefaultApplications.this.cloudFoundryClient, spaceId1, request1.getName())
                        );
                }
            }))
            .then(function(new Function2<ScaleApplicationRequest, AbstractApplicationResource, Mono<Tuple2<ScaleApplicationRequest, AbstractApplicationResource>>>() {

                @Override
                public Mono<Tuple2<ScaleApplicationRequest, AbstractApplicationResource>> apply(ScaleApplicationRequest request1, AbstractApplicationResource applicationResource) {
                    return Mono
                        .when(
                            Mono.just(request1),
                            scaleApplication(DefaultApplications.this.cloudFoundryClient,
                                ResourceUtils.getId(applicationResource),
                                request1.getDiskLimit(),
                                request1.getInstances(),
                                request1.getMemoryLimit())
                        );
                }
            }))
            .where(predicate(new Predicate2<ScaleApplicationRequest, AbstractApplicationResource>() {
                @Override
                public boolean test(ScaleApplicationRequest request, AbstractApplicationResource applicationResource) {
                    return scaleRestartRequired(request, applicationResource);
                }
            }))
            .then(function(new Function2<ScaleApplicationRequest, AbstractApplicationResource, Mono<AbstractApplicationResource>>() {

                @Override
                public Mono<AbstractApplicationResource> apply(ScaleApplicationRequest request, AbstractApplicationResource applicationResource) {
                    return restartApplication(DefaultApplications.this.cloudFoundryClient, applicationResource);
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
            .then(getApplicationWhere(this.cloudFoundryClient, applicationNotInState(STARTED_STATE)))
            .then(startApplication(this.cloudFoundryClient))
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
            .then(getApplicationWhere(this.cloudFoundryClient, applicationNotInState(STOPPED_STATE)))
            .then(stopApplication(this.cloudFoundryClient))
            .after();
    }

    private static Predicate<AbstractApplicationResource> applicationNotInState(final String state) {
        return new Predicate<AbstractApplicationResource>() {

            @Override
            public boolean test(AbstractApplicationResource resource) {
                return !state.equals(ResourceUtils.getEntity(resource).getState());
            }

        };
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

                return PaginationUtils
                    .requestResources(requestListApplicationsPage(cloudFoundryClient, spaceId, applicationName))
                    .map(ResourceUtils.extractId())
                    .single()
                    .otherwise(ExceptionUtils.<String>convert("Application %s does not exist", applicationName))
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

    private static Function<AbstractApplicationResource, Mono<Tuple4<ApplicationStatisticsResponse, SummaryApplicationResponse, GetStackResponse, ApplicationInstancesResponse>>> gatherApplicationInfo(
        final CloudFoundryClient cloudFoundryClient) {
        return new Function<AbstractApplicationResource, Mono<Tuple4<ApplicationStatisticsResponse, SummaryApplicationResponse, GetStackResponse, ApplicationInstancesResponse>>>() {

            @Override
            public Mono<Tuple4<ApplicationStatisticsResponse, SummaryApplicationResponse, GetStackResponse, ApplicationInstancesResponse>> apply(AbstractApplicationResource applicationResource) {
                String applicationId = ResourceUtils.getId(applicationResource);
                String stackId = ResourceUtils.getEntity(applicationResource).getStackId();

                return Mono.when(requestApplicationStats(cloudFoundryClient, applicationId), requestApplicationSummary(cloudFoundryClient, applicationId), requestStack(cloudFoundryClient, stackId),
                    requestApplicationInstances(cloudFoundryClient, applicationId));
            }

        };
    }

    private static Function<Tuple2<RenameApplicationRequest, String>, Mono<Tuple2<RenameApplicationRequest, AbstractApplicationResource>>> getApplicationResourceForRename(
        final CloudFoundryClient cloudFoundryClient) {
        return function(new Function2<RenameApplicationRequest, String, Mono<Tuple2<RenameApplicationRequest, AbstractApplicationResource>>>() {

            @Override
            public Mono<Tuple2<RenameApplicationRequest, AbstractApplicationResource>> apply(RenameApplicationRequest request, String spaceId) {

                return Mono.when(
                    Mono.just(request),
                    getSpaceApplication(cloudFoundryClient, spaceId, request.getName())
                );
            }
        });
    }

    private static Function<Tuple2<String, String>, Mono<String>> getApplicationWhere(final CloudFoundryClient cloudFoundryClient, final Predicate<AbstractApplicationResource> predicate) {
        return function(new Function2<String, String, Mono<String>>() {

            @Override
            public Mono<String> apply(String applicationName, String spaceId) {

                return PaginationUtils
                    .requestResources(requestListApplicationsPage(cloudFoundryClient, spaceId, applicationName))
                    .single()
                    .map(new Function<ApplicationResource, AbstractApplicationResource>() {
                        @Override
                        public AbstractApplicationResource apply(ApplicationResource x) {
                            return x;
                        }
                    })
                    .otherwise(ExceptionUtils.<AbstractApplicationResource>convert("Application %s does not exist", applicationName))
                    .where(predicate)
                    .map(ResourceUtils.extractId());
            }

        });
    }

    private static String getBuildpack(SummaryApplicationResponse response) {
        return Optional
            .ofNullable(response.getBuildpack())
            .orElse(response.getDetectedBuildpack());
    }

    private static Mono<AbstractApplicationResource> getSpaceApplication(final CloudFoundryClient cloudFoundryClient, final String spaceId, final String name) {
        return PaginationUtils
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
            .map(new Function<ApplicationResource, AbstractApplicationResource>() {
                @Override
                public AbstractApplicationResource apply(ApplicationResource x) {
                    return x;
                }
            })
            .otherwise(ExceptionUtils.<AbstractApplicationResource>convert("Application %s does not exist", name));
    }

    private static Function<Tuple2<RenameApplicationRequest, AbstractApplicationResource>, Mono<ApplicationEntity>> renameApplication(final CloudFoundryClient cloudFoundryClient) {
        return function(new Function2<RenameApplicationRequest, AbstractApplicationResource, Mono<ApplicationEntity>>() {

            @Override
            public Mono<ApplicationEntity> apply(RenameApplicationRequest request, AbstractApplicationResource applicationResource) {
                return renameApplication(cloudFoundryClient, ResourceUtils.getId(applicationResource), request.getNewName());
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
                    return ResourceUtils.getEntity(resource);
                }
            });
    }

    private static Mono<ApplicationInstancesResponse> requestApplicationInstances(CloudFoundryClient cloudFoundryClient, String applicationId) {
        ApplicationInstancesRequest request = ApplicationInstancesRequest.builder()
            .applicationId(applicationId)
            .build();

        return cloudFoundryClient.applicationsV2().instances(request);
    }

    private static Function<Tuple2<GetApplicationRequest, String>, Mono<AbstractApplicationResource>> requestApplicationResource(final CloudFoundryClient cloudFoundryClient) {
        return function(new Function2<GetApplicationRequest, String, Mono<AbstractApplicationResource>>() {

            @Override
            public Mono<AbstractApplicationResource> apply(GetApplicationRequest getApplicationRequest, String spaceId) {
                return PaginationUtils
                    .requestResources(requestListApplicationsPage(cloudFoundryClient, spaceId, getApplicationRequest.getName()))
                    .single()
                    .map(new Function<ApplicationResource, AbstractApplicationResource>() {
                        @Override
                        public AbstractApplicationResource apply(ApplicationResource x) {
                            return x;
                        }
                    });
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

    private static Mono<AbstractApplicationResource> restartApplication(final CloudFoundryClient cloudFoundryClient, AbstractApplicationResource
        applicationResource) {
        return stopApplication(cloudFoundryClient).apply(ResourceUtils.getId(applicationResource))
            .then(new Function<AbstractApplicationResource, Mono<? extends AbstractApplicationResource>>() {
                @Override
                public Mono<? extends AbstractApplicationResource> apply(AbstractApplicationResource applicationResource) {
                    return startApplication(cloudFoundryClient).apply(ResourceUtils.getId(applicationResource));
                }
            });
    }

    private static Mono<AbstractApplicationResource> scaleApplication(final CloudFoundryClient cloudFoundryClient, final String applicationId, final Integer disk, final Integer
        instances, final Integer memory) {
        UpdateApplicationRequest request = UpdateApplicationRequest.builder()
            .applicationId(applicationId)
            .diskQuota(disk)
            .instances(instances)
            .memory(memory)
            .build();

        return cloudFoundryClient.applicationsV2().update(request)
            .map(new Function<UpdateApplicationResponse, AbstractApplicationResource>() {
                @Override
                public AbstractApplicationResource apply(UpdateApplicationResponse x) {
                    return x;
                }
            });
    }

    private static boolean scaleModifiersPresent(ScaleApplicationRequest request) {
        return request.getMemoryLimit() != null || request.getDiskLimit() != null || request.getInstances() != null;
    }

    private static boolean scaleRestartRequired(ScaleApplicationRequest request, AbstractApplicationResource applicationResource) {
        return (request.getDiskLimit() != null || request.getMemoryLimit() != null)
            && STARTED_STATE.equals(ResourceUtils.getEntity(applicationResource).getState());
    }

    private static Function<String, Mono<AbstractApplicationResource>> startApplication(final CloudFoundryClient cloudFoundryClient) {
        return new Function<String, Mono<AbstractApplicationResource>>() {

            @Override
            public Mono<AbstractApplicationResource> apply(String applicationId) {
                return cloudFoundryClient.applicationsV2().update(UpdateApplicationRequest.builder()
                    .applicationId(applicationId)
                    .state(STARTED_STATE)
                    .build())
                    .map(new Function<UpdateApplicationResponse, AbstractApplicationResource>() {
                        @Override
                        public AbstractApplicationResource apply(UpdateApplicationResponse x) {
                            return x;
                        }
                    });
            }

        };
    }

    private static Function<String, Mono<AbstractApplicationResource>> stopApplication(final CloudFoundryClient cloudFoundryClient) {
        return new Function<String, Mono<AbstractApplicationResource>>() {

            @Override
            public Mono<AbstractApplicationResource> apply(String applicationId) {
                return cloudFoundryClient.applicationsV2().update(UpdateApplicationRequest.builder()
                    .applicationId(applicationId)
                    .state(STOPPED_STATE)
                    .build())
                    .map(new Function<UpdateApplicationResponse, AbstractApplicationResource>() {
                        @Override
                        public AbstractApplicationResource apply(UpdateApplicationResponse x) {
                            return x;
                        }
                    });
            }

        };

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

        return DateUtils.parseFromIso8601(date);
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
