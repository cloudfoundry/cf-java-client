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
import org.cloudfoundry.client.v2.PaginatedRequest;
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
import org.cloudfoundry.client.v2.events.EventEntity;
import org.cloudfoundry.client.v2.events.EventResource;
import org.cloudfoundry.client.v2.events.ListEventsRequest;
import org.cloudfoundry.client.v2.events.ListEventsResponse;
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
import org.cloudfoundry.utils.JobUtils;
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

import static org.cloudfoundry.utils.DateUtils.parseFromIso8601;
import static org.cloudfoundry.utils.OperationUtils.not;
import static org.cloudfoundry.utils.tuple.TupleUtils.function;
import static org.cloudfoundry.utils.tuple.TupleUtils.predicate;

public final class DefaultApplications implements Applications {

    private static final int MAX_NUMBER_OF_RECENT_EVENTS = 50;

    private static final String STARTED_STATE = "STARTED";

    private static final String STOPPED_STATE = "STOPPED";

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
                    return getRoutesAndApplicationId(cloudFoundryClient, request, spaceId);
                }

            }))
            .then(function(new Function2<Optional<List<Route>>, String, Mono<String>>() {

                @Override
                public Mono<String> apply(Optional<List<Route>> routes, final String applicationId) {
                    return deleteRoutes(cloudFoundryClient, routes)
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
                    return requestDeleteApplication(cloudFoundryClient, applicationId);
                }

            });
    }

    @Override
    public Mono<ApplicationDetail> get(GetApplicationRequest request) {
        return ValidationUtils
            .validate(request)
            .and(this.spaceId)
            .then(function(new Function2<GetApplicationRequest, String, Mono<AbstractApplicationResource>>() {

                @Override
                public Mono<AbstractApplicationResource> apply(GetApplicationRequest request, String spaceId) {
                    return getApplication(cloudFoundryClient, request.getName(), spaceId);
                }

            }))
            .then(new Function<AbstractApplicationResource, Mono<Tuple4<ApplicationStatisticsResponse, SummaryApplicationResponse, GetStackResponse, ApplicationInstancesResponse>>>() {

                @Override
                public Mono<Tuple4<ApplicationStatisticsResponse, SummaryApplicationResponse, GetStackResponse, ApplicationInstancesResponse>> apply(AbstractApplicationResource applicationResource) {
                    return getAuxiliaryContent(cloudFoundryClient, applicationResource);
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
    public Publisher<ApplicationEvent> getEvents(GetApplicationEventsRequest request) {
        return Mono
            .when(ValidationUtils.validate(request), this.spaceId)
            .then(function(new Function2<GetApplicationEventsRequest, String, Mono<Tuple2<GetApplicationEventsRequest, String>>>() {
                @Override
                public Mono<Tuple2<GetApplicationEventsRequest, String>> apply(GetApplicationEventsRequest request, String spaceId) {
                    return Mono.when(Mono.just(applyEventsDefaults(request)), Mono.just(spaceId));
                }
            }))
            .then(function(new Function2<GetApplicationEventsRequest, String, Mono<Tuple2<GetApplicationEventsRequest, String>>>() {

                @Override
                public Mono<Tuple2<GetApplicationEventsRequest, String>> apply(GetApplicationEventsRequest request, String spaceId) {
                    return Mono.when(Mono.just(request), getApplicationId(cloudFoundryClient, request.getName(), spaceId));
                }
            }))
            .flatMap(function(new Function2<GetApplicationEventsRequest, String, Stream<EventResource>>() {
                @Override
                public Stream<EventResource> apply(GetApplicationEventsRequest request, final String applicationId) {
                    return getEventResources(applicationId, cloudFoundryClient).take(request.getMaxNumberOfEvents());
                }
            }))
            .map(new Function<EventResource, ApplicationEvent>() {
                @Override
                public ApplicationEvent apply(EventResource resource) {
                    return convertToApplicationEvent(resource);
                }
            });
    }

    @Override
    public Publisher<ApplicationSummary> list() {
        return this.spaceId
            .then(new Function<String, Mono<GetSpaceSummaryResponse>>() {

                @Override
                public Mono<GetSpaceSummaryResponse> apply(String spaceId) {
                    return requestSpaceSummary(cloudFoundryClient, spaceId);
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
                    return getApplicationId(cloudFoundryClient, request.getName(), spaceId)
                        .and(Mono.just(request));
                }
            }))
            .then(function(new Function2<String, RenameApplicationRequest, Mono<UpdateApplicationResponse>>() {

                @Override
                public Mono<UpdateApplicationResponse> apply(String applicationId, RenameApplicationRequest request) {
                    return requestUpdateApplicationRename(cloudFoundryClient, applicationId, request.getNewName());
                }

            }))
            .after();
    }

    @Override
    public Mono<Void> restart(RestartApplicationRequest request) {
        return Mono
            .when(ValidationUtils.validate(request), this.spaceId)
            .then(function(new Function2<RestartApplicationRequest, String, Mono<AbstractApplicationResource>>() {
                @Override
                public Mono<AbstractApplicationResource> apply(RestartApplicationRequest request, String spaceId) {
                    return getApplication(cloudFoundryClient, request.getName(), spaceId);
                }
            }))
            .then(new Function<AbstractApplicationResource, Mono<AbstractApplicationResource>>() {
                @Override
                public Mono<AbstractApplicationResource> apply(final AbstractApplicationResource resource) {
                    return Mono.just(resource)
                        .as(ifThen(not(isIn(STOPPED_STATE)), new Function<AbstractApplicationResource, Mono<AbstractApplicationResource>>() {
                            @Override
                            public Mono<AbstractApplicationResource> apply(AbstractApplicationResource resource) {
                                return stopApplication(cloudFoundryClient, ResourceUtils.getId(resource));
                            }
                        }))
                        .then(new Function<AbstractApplicationResource, Mono<AbstractApplicationResource>>() {

                            @Override
                            public Mono<AbstractApplicationResource> apply(AbstractApplicationResource resource) {
                                return startApplication(cloudFoundryClient, ResourceUtils.getId(resource));
                            }
                        });
                }
            })
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
            .then(function(new Function2<ScaleApplicationRequest, String, Mono<Tuple2<ScaleApplicationRequest, String>>>() {

                @Override
                public Mono<Tuple2<ScaleApplicationRequest, String>> apply(ScaleApplicationRequest request, String spaceId) {
                    return Mono
                        .when(
                            Mono.just(request),
                            getApplicationId(cloudFoundryClient, request.getName(), spaceId)
                        );
                }
            }))
            .then(function(new Function2<ScaleApplicationRequest, String, Mono<Tuple2<ScaleApplicationRequest, AbstractApplicationResource>>>() {

                @Override
                public Mono<Tuple2<ScaleApplicationRequest, AbstractApplicationResource>> apply(ScaleApplicationRequest request, String applicationId) {
                    return Mono
                        .when(
                            Mono.just(request),
                            requestUpdateApplicationScale(cloudFoundryClient, applicationId, request.getDiskLimit(), request.getInstances(), request.getMemoryLimit())
                        );
                }
            }))
            .where(predicate(new Predicate2<ScaleApplicationRequest, AbstractApplicationResource>() {

                @Override
                public boolean test(ScaleApplicationRequest request, AbstractApplicationResource resource) {
                    return isRestartRequired(request, resource);
                }
            }))
            .then(function(new Function2<ScaleApplicationRequest, AbstractApplicationResource, Mono<AbstractApplicationResource>>() {

                @Override
                public Mono<AbstractApplicationResource> apply(ScaleApplicationRequest request, AbstractApplicationResource resource) {
                    return restartApplication(cloudFoundryClient, resource);
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
                    return getApplicationIdWhere(cloudFoundryClient, application, spaceId, not(isIn(STARTED_STATE)));
                }

            }))
            .then(new Function<String, Mono<AbstractApplicationResource>>() {

                @Override
                public Mono<AbstractApplicationResource> apply(String applicationId) {
                    return startApplication(cloudFoundryClient, applicationId);
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
                    return getApplicationIdWhere(cloudFoundryClient, application, spaceId, not(isIn(STOPPED_STATE)));
                }

            }))
            .then(new Function<String, Mono<AbstractApplicationResource>>() {

                @Override
                public Mono<AbstractApplicationResource> apply(String applicationId) {
                    return stopApplication(cloudFoundryClient, applicationId);
                }

            })
            .after();
    }

    private static GetApplicationEventsRequest applyEventsDefaults(GetApplicationEventsRequest request) {
        return (request.getMaxNumberOfEvents() != null) ? request :
            GetApplicationEventsRequest.builder()
                .name(request.getName())
                .maxNumberOfEvents(MAX_NUMBER_OF_RECENT_EVENTS)
                .build();
    }

    private static boolean areModifiersPresent(ScaleApplicationRequest request) {
        return request.getMemoryLimit() != null || request.getDiskLimit() != null || request.getInstances() != null;
    }

    private static ApplicationEvent convertToApplicationEvent(EventResource resource) {
        EventEntity entity = resource.getEntity();
        Date timestamp = null;
        try {
            timestamp = parseFromIso8601(entity.getTimestamp());
        } catch (IllegalArgumentException iae) {
            // do not set time
        }
        return ApplicationEvent.builder()
            .actor(entity.getActorName())
            .description(eventDescription(getMetadataRequest(entity), "instances", "memory", "state", "environment_json"))
            .event(entity.getType())
            .time(timestamp)
            .build();
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
                    return deleteRoute(cloudFoundryClient, routeId);
                }

            })
            .after();
    }

    private static String eventDescription(Map<String, Object> request, String... entryNames) {
        if (request == null) return "";
        boolean first = true;
        StringBuilder sb = new StringBuilder();
        for (String entryName : entryNames) {
            Object value = request.get(entryName);
            if (value == null) continue;
            if (!first) {
                sb.append(", ");
            }
            first = false;
            sb.append(entryName).append(": ").append(String.valueOf(value));
        }
        return sb.toString();
    }

    private static Stream<SpaceApplicationSummary> extractApplications(GetSpaceSummaryResponse getSpaceSummaryResponse) {
        return Stream.fromIterable(getSpaceSummaryResponse.getApplications());
    }

    private static Mono<AbstractApplicationResource> getApplication(CloudFoundryClient cloudFoundryClient, String application, String spaceId) {
        return requestApplications(cloudFoundryClient, application, spaceId)
            .single()
            .otherwise(ExceptionUtils.<AbstractApplicationResource>convert("Application %s does not exist", application));
    }

    private static Mono<String> getApplicationId(CloudFoundryClient cloudFoundryClient, String application, String spaceId) {
        return getApplication(cloudFoundryClient, application, spaceId)
            .map(ResourceUtils.extractId());
    }

    private static Mono<String> getApplicationIdWhere(CloudFoundryClient cloudFoundryClient, String application, String spaceId, Predicate<AbstractApplicationResource> predicate) {
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

    private static Stream<EventResource> getEventResources(final String applicationId, final CloudFoundryClient cloudFoundryClient) {
        return PaginationUtils.requestResources(new Function<Integer, Mono<ListEventsResponse>>() {
            @Override
            public Mono<ListEventsResponse> apply(Integer page) {
                return cloudFoundryClient.events()
                    .list(ListEventsRequest.builder()
                        .actee(applicationId)
                        .orderDirection(PaginatedRequest.OrderDirection.DESC)
                        .resultsPerPage(50)
                        .page(page)
                        .build());
            }
        });
    }

    private static Map<String, Object> getMetadataRequest(EventEntity entity) {
        Map<String, Object> metadataMap = safeCastToMap(entity.getMetadatas());
        return (metadataMap == null) ? null : safeCastToMap(metadataMap.get("request"));
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

    /**
     * Produces a Mono transformer that preserves the type of the source {@code Mono<IN>}.
     *
     * <p> The Mono produced expects a single element from the source, passes this to the predicate and, if this returns <b>{@code true}</b>, it builds a {@code Mono<IN>} with the {@code thenFunction}
     * and returns this. If the predicate reutrns <b>{@code false}</b> the input value is passed through unchanged.</p>
     *
     * <p> <b>Usage:</b> Can be used inline thus: {@code .as(ifThen(in -> test(in), in -> funcOf(in)))} </p>
     *
     * @param predicate    from source input element to <b>{@code true}</b> or <b>{@code false}</b>
     * @param thenFunction from source input element to some {@code Mono<IN>}
     * @param <IN>         the source element type and the element type of the resulting {@code Mono}.
     * @return a Mono transformer
     */
    private static <IN> Function<Mono<IN>, Mono<IN>> ifThen(final Predicate<IN> predicate, final Function<IN, Mono<IN>> thenFunction) {
        return new Function<Mono<IN>, Mono<IN>>() {
            @Override
            public Mono<IN> apply(Mono<IN> source) {
                return source
                    .then(new Function<IN, Mono<IN>>() {
                        @Override
                        public Mono<IN> apply(IN in) {
                            return predicate.test(in) ? thenFunction.apply(in) : Mono.just(in);
                        }
                    });
            }
        };
    }

    private static Predicate<AbstractApplicationResource> isIn(final String state) {
        return new Predicate<AbstractApplicationResource>() {

            @Override
            public boolean test(AbstractApplicationResource resource) {
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

    private static Stream<AbstractApplicationResource> requestApplications(final CloudFoundryClient cloudFoundryClient, final String application, final String spaceId) {
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

            })
            .map(new Function<ApplicationResource, AbstractApplicationResource>() {
                @Override
                public AbstractApplicationResource apply(ApplicationResource x) {
                    return x;
                }
            });
    }

    private static Mono<Void> requestDeleteApplication(CloudFoundryClient cloudFoundryClient, String applicationId) {
        return cloudFoundryClient.applicationsV2()
            .delete(org.cloudfoundry.client.v2.applications.DeleteApplicationRequest.builder()
                .applicationId(applicationId)
                .build());
    }

    private static Mono<Void> deleteRoute(final CloudFoundryClient cloudFoundryClient, String routeId) {
        return requestDeleteRoute(cloudFoundryClient, routeId)
            .map(ResourceUtils.extractId())
            .then(new Function<String, Mono<Void>>() {

                @Override
                public Mono<Void> apply(String jobId) {
                    return JobUtils.waitForCompletion(cloudFoundryClient, jobId);
                }

            });
    }

    private static Mono<DeleteRouteResponse> requestDeleteRoute(CloudFoundryClient cloudFoundryClient, String routeId) {
        return cloudFoundryClient.routes()
            .delete(DeleteRouteRequest.builder()
                .async(true)
                .routeId(routeId)
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

    private static Mono<UpdateApplicationResponse> requestUpdateApplicationRename(CloudFoundryClient cloudFoundryClient, String applicationId, final String name) {
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
            .map(new Function<UpdateApplicationResponse, AbstractApplicationResource>() {
                @Override
                public AbstractApplicationResource apply(UpdateApplicationResponse x) {
                    return x;
                }
            });
    }

    private static Mono<AbstractApplicationResource> requestUpdateApplicationState(CloudFoundryClient cloudFoundryClient, String applicationId, String state) {
        return cloudFoundryClient.applicationsV2()
            .update(UpdateApplicationRequest.builder()
                .applicationId(applicationId)
                .state(state)
                .build())
            .map(new Function<UpdateApplicationResponse, AbstractApplicationResource>() {
                @Override
                public AbstractApplicationResource apply(UpdateApplicationResponse x) {
                    return x;
                }
            });
    }

    private static Mono<AbstractApplicationResource> restartApplication(final CloudFoundryClient cloudFoundryClient, final AbstractApplicationResource resource) {
        return stopApplication(cloudFoundryClient, ResourceUtils.getId(resource))
            .then(new Function<AbstractApplicationResource, Mono<AbstractApplicationResource>>() {

                @Override
                public Mono<AbstractApplicationResource> apply(AbstractApplicationResource abstractApplicationResource) {
                    return startApplication(cloudFoundryClient, ResourceUtils.getId(resource));
                }
            });
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> safeCastToMap(Object request) {
        if (request instanceof Map)
            return (Map<String, Object>) request;
        else
            return null;
    }

    private static Mono<AbstractApplicationResource> startApplication(CloudFoundryClient cloudFoundryClient, String applicationId) {
        return requestUpdateApplicationState(cloudFoundryClient, applicationId, STARTED_STATE);
    }

    private static Mono<AbstractApplicationResource> stopApplication(CloudFoundryClient cloudFoundryClient, String applicationId) {
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
        return date == null ? null : parseFromIso8601(date);
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
