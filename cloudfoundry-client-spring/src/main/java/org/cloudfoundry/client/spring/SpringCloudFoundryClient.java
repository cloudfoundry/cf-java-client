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

package org.cloudfoundry.client.spring;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.spring.v2.info.SpringInfo;
import org.cloudfoundry.client.spring.v2.spaces.SpringSpaces;
import org.cloudfoundry.client.spring.v3.applications.SpringApplications;
import org.cloudfoundry.client.spring.v3.packages.SpringPackages;
import org.cloudfoundry.client.v2.info.Info;
import org.cloudfoundry.client.v2.spaces.Spaces;
import org.cloudfoundry.client.v3.applications.Applications;
import org.cloudfoundry.client.v3.packages.Packages;
import org.springframework.web.client.RestOperations;

import java.net.URI;

final class SpringCloudFoundryClient implements CloudFoundryClient {

    private final RestOperations restOperations;

    private final URI root;

    SpringCloudFoundryClient(RestOperations restOperations, URI root) {
        this.restOperations = restOperations;
        this.root = root;
    }

    RestOperations getRestOperations() {
        return this.restOperations;
    }

    @Override
    public Applications applications() {
        return new SpringApplications(this.restOperations, this.root);
    }

    @Override
    public Info info() {
        return new SpringInfo(this.restOperations, this.root);
    }

    @Override
    public Packages packages() {
        return new SpringPackages(this.restOperations, this.root);
    }

    @Override
    public Spaces spaces() {
        return new SpringSpaces(this.restOperations, this.root);
    }

}
