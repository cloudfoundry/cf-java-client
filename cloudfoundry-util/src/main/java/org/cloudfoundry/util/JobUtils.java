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

package org.cloudfoundry.util;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.CloudFoundryException;
import org.cloudfoundry.client.v2.job.GetJobRequest;
import org.cloudfoundry.client.v2.job.GetJobResponse;
import org.cloudfoundry.client.v2.job.JobEntity;
import reactor.core.publisher.Mono;
import reactor.fn.Function;
import reactor.fn.Predicate;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Utilities for Jobs
 */
public final class JobUtils {

    private JobUtils() {
    }

    /**
     * Waits for a job to complete
     *
     * @param cloudFoundryClient the client to use to request job status
     * @param jobId              the id of the job
     * @return {@code onComplete} once job has completed
     */
    public static Mono<Void> waitForCompletion(CloudFoundryClient cloudFoundryClient, String jobId) {
        return requestJob(cloudFoundryClient, jobId)
            .map(new Function<GetJobResponse, JobEntity>() {

                @Override
                public JobEntity apply(GetJobResponse response) {
                    return response.getEntity();
                }
            })
            .where(new Predicate<JobEntity>() {

                @Override
                public boolean test(JobEntity entity) {
                    return isComplete(entity);
                }

            })
            .as(OperationUtils.<JobEntity>repeatWhen(DelayUtils.exponentialBackOff(1, 10, SECONDS, 10)))  // TODO: Remove once Mono.repeatWhen()
            .where(new Predicate<JobEntity>() {

                @Override
                public boolean test(JobEntity entity) {
                    return "failed".equals(entity.getStatus());
                }

            })
            .flatMap(new Function<JobEntity, Mono<Void>>() {

                @Override
                public Mono<Void> apply(JobEntity entity) {
                    return getError(entity);
                }

            })
            .after();
    }

    private static Mono<Void> getError(JobEntity entity) {
        JobEntity.ErrorDetails errorDetails = entity.getErrorDetails();
        return Mono.error(new CloudFoundryException(errorDetails.getCode(), errorDetails.getDescription(), errorDetails.getErrorCode()));
    }

    private static boolean isComplete(JobEntity entity) {
        String status = entity.getStatus();
        return "finished".equals(status) || "failed".equals(status);
    }

    private static Mono<GetJobResponse> requestJob(CloudFoundryClient cloudFoundryClient, String jobId) {
        return cloudFoundryClient.jobs()
            .get(GetJobRequest.builder()
                .jobId(jobId)
                .build());
    }

}
