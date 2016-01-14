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

package org.cloudfoundry.operations;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.applications.ApplicationInstanceInfo;
import org.cloudfoundry.client.v2.applications.ApplicationInstancesRequest;
import org.cloudfoundry.client.v2.applications.ApplicationInstancesResponse;
import org.cloudfoundry.client.v2.applications.ApplicationResource;
import org.cloudfoundry.client.v2.applications.ApplicationStatisticsRequest;
import org.cloudfoundry.client.v2.applications.ApplicationStatisticsResponse;
import org.cloudfoundry.client.v2.applications.SummaryApplicationRequest;
import org.cloudfoundry.client.v2.applications.SummaryApplicationResponse;
import org.cloudfoundry.client.v2.routes.Route;
import org.cloudfoundry.client.v2.spaces.GetSpaceSummaryRequest;
import org.cloudfoundry.client.v2.spaces.GetSpaceSummaryResponse;
import org.cloudfoundry.client.v2.spaces.ListSpaceApplicationsRequest;
import org.cloudfoundry.client.v2.spaces.ListSpaceApplicationsResponse;
import org.cloudfoundry.client.v2.spaces.SpaceApplicationSummary;
import org.cloudfoundry.client.v2.stacks.GetStackRequest;
import org.cloudfoundry.client.v2.stacks.GetStackResponse;
import org.cloudfoundry.operations.v2.Paginated;
import org.cloudfoundry.operations.v2.Resources;
import org.reactivestreams.Publisher;
import reactor.Mono;
import reactor.fn.Function;
import reactor.fn.tuple.Tuple2;
import reactor.fn.tuple.Tuple4;
import reactor.rx.Stream;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

final class DefaultApplications implements Applications {

    private final CloudFoundryClient cloudFoundryClient;

    private final Mono<String> spaceId;

    DefaultApplications(CloudFoundryClient cloudFoundryClient, Mono<String> spaceId) {
        this.cloudFoundryClient = cloudFoundryClient;
        this.spaceId = spaceId;
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

    private static String emptyNull(String s) {
        return s == null ? "" : s;
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

    private static String getBuildpack(SummaryApplicationResponse response) {
        return Optional
                .ofNullable(response.getBuildpack())
                .orElse(emptyNull(response.getDetectedBuildpack()));
    }

    private static Mono<ApplicationInstancesResponse> requestApplicationInstances(CloudFoundryClient cloudFoundryClient, String applicationId) {
        ApplicationInstancesRequest request = ApplicationInstancesRequest.builder()
                .id(applicationId)
                .build();

        return cloudFoundryClient.applicationsV2().instances(request);
    }

    private static Function<Tuple2<GetApplicationRequest, String>, Mono<ApplicationResource>> requestApplicationResource(final CloudFoundryClient cloudFoundryClient) {
        return new Function<Tuple2<GetApplicationRequest, String>, Mono<ApplicationResource>>() {

            @Override
            public Mono<ApplicationResource> apply(Tuple2<GetApplicationRequest, String> tuple) {
                return Paginated
                        .requestResources(requestListApplicationsPage(cloudFoundryClient, tuple))
                        .single();
            }

        };
    }

    private static Mono<ApplicationStatisticsResponse> requestApplicationStats(CloudFoundryClient cloudFoundryClient, String applicationId) {
        ApplicationStatisticsRequest request = ApplicationStatisticsRequest.builder()
                .id(applicationId)
                .build();

        return cloudFoundryClient.applicationsV2().statistics(request);
    }

    private static Mono<SummaryApplicationResponse> requestApplicationSummary(CloudFoundryClient cloudFoundryClient, String applicationId) {
        SummaryApplicationRequest request = SummaryApplicationRequest.builder()
                .id(applicationId)
                .build();

        return cloudFoundryClient.applicationsV2().summary(request);
    }

    private static Function<Integer, Mono<ListSpaceApplicationsResponse>> requestListApplicationsPage(final CloudFoundryClient cloudFoundryClient, final Tuple2<GetApplicationRequest, String> tuple) {
        return new Function<Integer, Mono<ListSpaceApplicationsResponse>>() {

            @Override
            public Mono<ListSpaceApplicationsResponse> apply(Integer page) {
                GetApplicationRequest getApplicationRequest = tuple.t1;
                String spaceId = tuple.t2;

                ListSpaceApplicationsRequest request = ListSpaceApplicationsRequest.builder()
                        .id(spaceId)
                        .name(getApplicationRequest.getName())
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
                        .id(targetedSpace)
                        .build();

                return cloudFoundryClient.spaces().getSummary(request);
            }

        };
    }

    private static Mono<GetStackResponse> requestStack(CloudFoundryClient cloudFoundryClient, String stackId) {
        GetStackRequest request = GetStackRequest.builder()
                .id(stackId)
                .build();

        return cloudFoundryClient.stacks().get(request);
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
        return new Function<Tuple4<ApplicationStatisticsResponse, SummaryApplicationResponse, GetStackResponse, ApplicationInstancesResponse>, ApplicationDetail>() {

            @Override
            public ApplicationDetail apply(Tuple4<ApplicationStatisticsResponse, SummaryApplicationResponse, GetStackResponse, ApplicationInstancesResponse> tuple) {
                ApplicationStatisticsResponse applicationStatisticsResponse = tuple.t1;
                SummaryApplicationResponse summaryApplicationResponse = tuple.t2;
                GetStackResponse getStackResponse = tuple.t3;
                ApplicationInstancesResponse applicationInstancesResponse = tuple.t4;

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

        };
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
