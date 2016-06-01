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

package org.cloudfoundry.doppler;

import reactor.core.publisher.Flux;

/**
 * Main entry point to the Doppler Client API
 */
public interface DopplerClient {

    /**
     * Makes the <a href="https://github.com/cloudfoundry/loggregator/tree/develop/src/trafficcontroller#endpoints">Container Metrics</a> request
     *
     * @param request the Container Metrics request
     * @return the container metrics
     */
    Flux<ContainerMetric> containerMetrics(ContainerMetricsRequest request);

    /**
     * Makes the <a href="https://github.com/cloudfoundry/loggregator/tree/develop/src/trafficcontroller#endpoints">Firehose</a> request
     *
     * @param request the Firehose request
     * @return the events from the firehose
     */
    Flux<Event> firehose(FirehoseRequest request);

    /**
     * Makes the <a href="https://github.com/cloudfoundry/loggregator/tree/develop/src/trafficcontroller#endpoints">Firehose</a> request
     *
     * @param request the Firehose request
     * @return the events wrapped with an Envelope holding metadata from the firehose
     */
    Flux<Envelope<? extends Event>> envelopedFirehose(FirehoseRequest request);

    /**
     * Makes the <a href="https://github.com/cloudfoundry/loggregator/tree/develop/src/trafficcontroller#endpoints">Recent Logs</a> request
     *
     * @param request the Recent Logs request
     * @return the events from the recent logs
     */
    Flux<LogMessage> recentLogs(RecentLogsRequest request);

    /**
     * Makes the <a href="https://github.com/cloudfoundry/loggregator/tree/develop/src/trafficcontroller#endpoints">Stream</a> request
     *
     * @param request the Stream request
     * @return the events from the stream
     */
    Flux<Event> stream(StreamRequest request);

}
