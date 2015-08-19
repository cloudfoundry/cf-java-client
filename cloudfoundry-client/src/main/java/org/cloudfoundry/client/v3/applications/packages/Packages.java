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

package org.cloudfoundry.client.v3.applications.packages;

import rx.Observable;

/**
 * Main entry point to the Cloud Foundry Packages Client API
 */
public interface Packages {

    /**
     * Makes the <a href="http://apidocs.cloudfoundry.org/215/packages_(experimental)/create_a_package.html">Create
     * Package</a> request
     *
     * @param request the Create Package request
     * @return the response from the Create Package request
     */
    Observable<CreatePackageResponse> create(CreatePackageRequest request);
}
