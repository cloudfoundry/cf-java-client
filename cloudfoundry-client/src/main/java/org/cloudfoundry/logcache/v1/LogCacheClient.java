/*
 * Copyright 2013-2021 the original author or authors.
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

package org.cloudfoundry.logcache.v1;

import reactor.core.publisher.Mono;

/**
 * Main entry point to the Log Cache Client API
 */
public interface LogCacheClient {

    /**
     * Makes the Log Cache /api/v1/info request
     *
     * @param request the Info request
     * @return the Info response
     */
    Mono<InfoResponse> info(InfoRequest request);

    /**
     * Makes the Log Cache /api/v1/meta request
     *
     * @param meta the Meta request
     * @return the Log Cache metadata for the cached logs and metrics
     */
    Mono<MetaResponse> meta(MetaRequest meta);

    /**
     * Makes the Log Cache /api/v1/read request
     *
     * @param request the Read request
     * @return the read response
     */
    Mono<ReadResponse> read(ReadRequest request);

    /**
     * Makes the Log Cache RecentLogs /api/v1/read request
     *
     * @param request the Recent Logs request
     * @return the events from the recent logs
     */
    Mono<ReadResponse> recentLogs(ReadRequest request);
}
