/*
 * Copyright 2013-2017 the original author or authors.
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

package org.cloudfoundry.bosh.tasks;

import reactor.core.publisher.Mono;

/**
 * Main entry point to the BOSH Tasks API
 */
public interface Tasks {

    /**
     * Makes the <a href="https://bosh.io/docs/director-api-v1.html#get-task">Get Task</a> request
     *
     * @param request the Get Task Request
     * @return the response from the Get Task request
     */
    Mono<GetTaskResponse> get(GetTaskRequest request);

    /**
     * Makes the <a href="">Get Task Output</a> request
     *
     * @param request the Get Task Output request
     * @return the response from the Get Task Output request
     */
    Mono<String> getOutput(GetTaskOutputRequest request);

    /**
     * Makes the <a href="https://bosh.io/docs/director-api-v1.html#list-tasks">List Tasks</a> request
     *
     * @param request the List Tasks Request
     * @return the response from the List Tasks request
     */
    Mono<ListTasksResponse> list(ListTasksRequest request);

}
