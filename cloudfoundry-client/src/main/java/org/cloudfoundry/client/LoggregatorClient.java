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

package org.cloudfoundry.client;

import org.cloudfoundry.client.loggregator.LoggregatorMessage;
import org.cloudfoundry.client.loggregator.RecentLogsRequest;
import org.cloudfoundry.client.loggregator.StreamLogsRequest;
import org.reactivestreams.Publisher;

/**
 * Main entry point to the Loggregator Client API
 */
public interface LoggregatorClient {

    /**
     * Makes the Recent Logs request
     *
     * @param request the Recent Logs request
     * @return the response from the Recent Logs request
     */
    Publisher<LoggregatorMessage> recent(RecentLogsRequest request);

    /**
     * Makes the Stream Logs request
     *
     * @param request the Stream Logs request
     * @return the response from the Stream Logs request
     */
    Publisher<LoggregatorMessage> stream(StreamLogsRequest request);

}
