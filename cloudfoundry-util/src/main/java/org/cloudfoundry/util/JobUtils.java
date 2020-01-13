/*
 * Copyright 2013-2020 the original author or authors.
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
import org.cloudfoundry.client.v2.ClientV2Exception;
import org.cloudfoundry.client.v2.Resource;
import org.cloudfoundry.client.v2.jobs.ErrorDetails;
import org.cloudfoundry.client.v2.jobs.GetJobRequest;
import org.cloudfoundry.client.v2.jobs.GetJobResponse;
import org.cloudfoundry.client.v2.jobs.JobEntity;
import org.cloudfoundry.client.v3.ClientV3Exception;
import org.cloudfoundry.client.v3.Error;
import org.cloudfoundry.client.v3.jobs.Job;
import org.cloudfoundry.client.v3.jobs.JobState;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

import static org.cloudfoundry.util.DelayUtils.exponentialBackOff;

/**
 * Utilities for Jobs
 */
public final class JobUtils {

    private static final Integer STATUS_OK = 200;

    private JobUtils() {
    }

    /**
     * Waits for a job to complete
     *
     * @param cloudFoundryClient the client to use to request job status
     * @param completionTimeout  the amount of time to wait for the job to complete.
     * @param resource           the resource representing the job
     * @param <R>                the Job resource type
     * @return {@code onComplete} once job has completed
     */
    public static <R extends Resource<JobEntity>> Mono<Void> waitForCompletion(CloudFoundryClient cloudFoundryClient, Duration completionTimeout, R resource) {
        return waitForCompletion(cloudFoundryClient, completionTimeout, ResourceUtils.getEntity(resource));
    }

    /**
     * Waits for a job to complete
     *
     * @param cloudFoundryClient the client to use to request job status
     * @param completionTimeout  the amount of time to wait for the job to complete.
     * @param jobEntity          the entity representing the job
     * @return {@code onComplete} once job has completed
     */
    public static Mono<Void> waitForCompletion(CloudFoundryClient cloudFoundryClient, Duration completionTimeout, JobEntity jobEntity) {
        Mono<JobEntity> job;

        if (JobUtils.isComplete(jobEntity)) {
            job = Mono.just(jobEntity);
        } else {
            job = requestJobV2(cloudFoundryClient, jobEntity.getId())
                .map(GetJobResponse::getEntity)
                .filter(JobUtils::isComplete)
                .repeatWhenEmpty(exponentialBackOff(Duration.ofSeconds(1), Duration.ofSeconds(15), completionTimeout));
        }

        return job
            .filter(entity -> "failed".equals(entity.getStatus()))
            .flatMap(JobUtils::getError);
    }

    /**
     * Waits for a job V3 to complete
     *
     * @param cloudFoundryClient the client to use to request job status
     * @param completionTimeout  the amount of time to wait for the job to complete.
     * @param jobId              the id of the job
     * @return {@code onComplete} once job has completed
     */
    public static Mono<Void> waitForCompletion(CloudFoundryClient cloudFoundryClient, Duration completionTimeout, String jobId) {
        return requestJobV3(cloudFoundryClient, jobId)
            .filter(job -> JobState.PROCESSING != job.getState())
            .repeatWhenEmpty(exponentialBackOff(Duration.ofSeconds(1), Duration.ofSeconds(15), completionTimeout))
            .filter(job -> JobState.FAILED == job.getState())
            .flatMap(JobUtils::getError);
    }

    private static Mono<Void> getError(JobEntity entity) {
        ErrorDetails errorDetails = entity.getErrorDetails();
        return Mono.error(new ClientV2Exception(null, errorDetails.getCode(), errorDetails.getDescription(), errorDetails.getErrorCode()));
    }

    private static Mono<Void> getError(Job job) {
        List<Error> errors = job.getErrors();
        return Mono.error(new ClientV3Exception(STATUS_OK, errors));
    }

    private static boolean isComplete(JobEntity entity) {
        String status = entity.getStatus();
        return "finished".equals(status) || "failed".equals(status);
    }

    private static Mono<GetJobResponse> requestJobV2(CloudFoundryClient cloudFoundryClient, String jobId) {
        return cloudFoundryClient.jobs()
            .get(GetJobRequest.builder()
                .jobId(jobId)
                .build());
    }

    private static Mono<org.cloudfoundry.client.v3.jobs.GetJobResponse> requestJobV3(CloudFoundryClient cloudFoundryClient, String jobId) {
        return cloudFoundryClient.jobsV3()
            .get(org.cloudfoundry.client.v3.jobs.GetJobRequest.builder()
                .jobId(jobId)
                .build());
    }

}
