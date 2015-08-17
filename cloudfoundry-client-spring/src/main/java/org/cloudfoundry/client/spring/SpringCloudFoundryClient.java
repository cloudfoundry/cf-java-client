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
import org.cloudfoundry.client.spring.v2.space.SpringSpace;
import org.cloudfoundry.client.spring.v3.application.SpringApplication;
import org.cloudfoundry.client.v2.info.Info;
import org.cloudfoundry.client.v2.space.Space;
import org.cloudfoundry.client.v3.application.Application;
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
    public Application application() {
        return new SpringApplication(this.restOperations, this.root);
    }

    @Override
    public Info info() {
        return new SpringInfo(this.restOperations, this.root);
    }

    @Override
    public Space space() {
        return new SpringSpace(this.restOperations, this.root);
    }

}
