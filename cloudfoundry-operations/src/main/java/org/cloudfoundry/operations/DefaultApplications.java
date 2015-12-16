/*
 * Copyright 2013-2015 the original author or authors.
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
import org.cloudfoundry.client.v2.spaces.GetSpaceSummaryRequest;
import org.cloudfoundry.client.v2.spaces.GetSpaceSummaryResponse;
import org.cloudfoundry.client.v2.spaces.SpaceApplicationSummary;
import org.reactivestreams.Publisher;
import reactor.fn.Function;
import reactor.rx.Streams;

final class DefaultApplications extends AbstractOperations implements Applications {

    private final CloudFoundryClient cloudFoundryClient;

    DefaultApplications(CloudFoundryClient cloudFoundryClient, String spaceId) {
        super(null, spaceId);
        this.cloudFoundryClient = cloudFoundryClient;
    }

    @Override
    public Publisher<Application> list() {
        final GetSpaceSummaryRequest request = GetSpaceSummaryRequest.builder()
                .id(DefaultApplications.this.getTargetedSpace()).build();

        return Streams.wrap(this.cloudFoundryClient
                .spaces()
                .getSummary(request))
                .flatMap(new Function<GetSpaceSummaryResponse, Publisher<SpaceApplicationSummary>>() {

                    @Override
                    public Publisher<SpaceApplicationSummary> apply(GetSpaceSummaryResponse getSpaceSummaryResponse) {
                        return Streams.from(getSpaceSummaryResponse.getApplications());
                    }

                }).map(new Function<SpaceApplicationSummary, Application>() {

                    @Override
                    public Application apply(SpaceApplicationSummary applicationSummary) {
                        return Application.builder()
                                .disk(applicationSummary.getDiskQuota())
                                .id(applicationSummary.getId())
                                .instances(applicationSummary.getInstances())
                                .memory(applicationSummary.getMemory())
                                .name(applicationSummary.getName())
                                .requestedState(applicationSummary.getState())
                                .runningInstances(applicationSummary.getRunningInstances())
                                .urls(applicationSummary.getUrls())
                                .build();
                    }

                });
    }

}
