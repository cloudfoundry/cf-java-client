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

package org.cloudfoundry.client.v3.isolationsegments;

import org.cloudfoundry.client.v3.servicebindings.CreateServiceBindingRequest;
import org.cloudfoundry.client.v3.servicebindings.CreateServiceBindingResponse;
import org.cloudfoundry.client.v3.servicebindings.DeleteServiceBindingRequest;
import org.cloudfoundry.client.v3.servicebindings.GetServiceBindingRequest;
import org.cloudfoundry.client.v3.servicebindings.GetServiceBindingResponse;
import org.cloudfoundry.client.v3.servicebindings.ListServiceBindingsRequest;
import org.cloudfoundry.client.v3.servicebindings.ListServiceBindingsResponse;
import reactor.core.publisher.Mono;

/**
 * Main entry point to the Cloud Foundry Isolation Zones Client API
 */
public interface IsolationSegments {

    /**
     * Makes the <a href="http://v3-apidocs.cloudfoundry.org/version/release-candidate/index.html#create-an-isolation-segment">Create an Isolation Segment</a> request
     *
     * @param request the Create Isolation Segment request
     * @return the response from the Create Isolation Segment request
     */
    Mono<CreateIsolationSegmentResponse> create(CreateIsolationSegmentRequest request);

}
