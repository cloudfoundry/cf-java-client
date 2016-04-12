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
import org.cloudfoundry.client.v2.Resource;
import org.cloudfoundry.client.v2.jobs.GetJobRequest;
import org.cloudfoundry.client.v2.jobs.GetJobResponse;
import org.cloudfoundry.client.v2.jobs.JobEntity;
import reactor.core.publisher.Mono;

import java.time.Duration;

import static org.cloudfoundry.util.DelayUtils.exponentialBackOff;

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
     * @param resource           the resource representing the job
     * @param <R>                the Job resource type
     * @return {@code onComplete} once job has completed
     */
    public static <R extends Resource<JobEntity>> Mono<Void> waitForCompletion(CloudFoundryClient cloudFoundryClient, R resource) {
        Mono<JobEntity> job;

        if (JobUtils.isComplete(ResourceUtils.getEntity(resource))) {
            job = Mono.just(ResourceUtils.getEntity(resource));
        } else {
            job = requestJob(cloudFoundryClient, ResourceUtils.getId(resource))
                .map(GetJobResponse::getEntity)
                .where(JobUtils::isComplete)
                .repeatWhenEmpty(exponentialBackOff(Duration.ofSeconds(1), Duration.ofSeconds(15), Duration.ofMinutes(5)));
        }

        return job
            .where(entity -> "failed".equals(entity.getStatus()))
            .then(JobUtils::getError)
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
