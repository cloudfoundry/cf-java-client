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
import org.cloudfoundry.client.spring.v2.events.SpringEvents;
import org.cloudfoundry.client.spring.v2.info.SpringInfo;
import org.cloudfoundry.client.spring.v2.organizations.SpringOrganizations;
import org.cloudfoundry.client.spring.v2.spaces.SpringSpaces;
import org.cloudfoundry.client.spring.v3.applications.SpringApplications;
import org.cloudfoundry.client.spring.v3.droplets.SpringDroplets;
import org.cloudfoundry.client.spring.v3.packages.SpringPackages;
import org.cloudfoundry.client.v2.events.Events;
import org.cloudfoundry.client.v2.info.Info;
import org.cloudfoundry.client.v2.organizations.Organizations;
import org.cloudfoundry.client.v2.spaces.Spaces;
import org.cloudfoundry.client.v3.applications.Applications;
import org.cloudfoundry.client.v3.droplets.Droplets;
import org.cloudfoundry.client.v3.packages.Packages;
import org.springframework.web.client.RestOperations;

import java.net.URI;

final class SpringCloudFoundryClient implements CloudFoundryClient {

    private final Applications applications;

    private final Droplets droplets;

    private final Events events;

    private final Info info;

    private final Organizations organizations;

    private final Packages packages;

    private final RestOperations restOperations;

    private final Spaces spaces;

    SpringCloudFoundryClient(RestOperations restOperations, URI root) {
        this.applications = new SpringApplications(restOperations, root);
        this.droplets = new SpringDroplets(restOperations, root);
        this.events = new SpringEvents(restOperations, root);
        this.info = new SpringInfo(restOperations, root);
        this.organizations = new SpringOrganizations(restOperations, root);
        this.packages = new SpringPackages(restOperations, root);
        this.spaces = new SpringSpaces(restOperations, root);

        this.restOperations = restOperations;
    }

    RestOperations getRestOperations() {
        return this.restOperations;
    }

    @Override
    public Applications applications() {
        return this.applications;
    }

    @Override
    public Droplets droplets() {
        return this.droplets;
    }

    @Override
    public Events events() {
        return this.events;
    }

    @Override
    public Info info() {
        return this.info;
    }

    @Override
    public Organizations organizations() {
        return this.organizations;
    }

    @Override
    public Packages packages() {
        return this.packages;
    }

    @Override
    public Spaces spaces() {
        return this.spaces;
    }

}
