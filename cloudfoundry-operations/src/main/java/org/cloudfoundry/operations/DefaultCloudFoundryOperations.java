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

package org.cloudfoundry.operations;

import org.cloudfoundry.client.CloudFoundryClient;

final class DefaultCloudFoundryOperations implements CloudFoundryOperations {

    private final Applications applications;

    private final Organizations organizations;

    private final Spaces spaces;

    DefaultCloudFoundryOperations(CloudFoundryClient cloudFoundryClient, String organizationId, String spaceId) {
        this.applications = new DefaultApplications(cloudFoundryClient, spaceId);
        this.organizations = new DefaultOrganizations(cloudFoundryClient);
        this.spaces = new DefaultSpaces(cloudFoundryClient, organizationId);
    }

    @Override
    public Applications applications() {
        return this.applications;
    }

    @Override
    public Organizations organizations() {
        return this.organizations;
    }

    @Override
    public Spaces spaces() {
        return this.spaces;
    }

}
