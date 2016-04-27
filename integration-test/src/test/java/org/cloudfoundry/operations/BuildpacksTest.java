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

package org.cloudfoundry.operations;

import org.cloudfoundry.AbstractIntegrationTest;
import org.cloudfoundry.operations.buildpacks.CreateBuildpackRequest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;

public final class BuildpacksTest extends AbstractIntegrationTest {

    @Autowired
    private CloudFoundryOperations cloudFoundryOperations;

    @Test
    public void createBuildpack() throws Exception {
        String buildpackName = getBuildpackName();

        this.cloudFoundryOperations.buildpacks()
            .create(CreateBuildpackRequest.builder()
                .buildpack(getBuildpackBits())
                .fileName("test-buildpack.zip")
                .name(buildpackName)
                .position(Integer.MAX_VALUE)
                .build())
            .flux()
            .after(this.cloudFoundryOperations.buildpacks()
                .list())
            .filter(buildpack -> buildpackName.equals(buildpack.getName()))
            .subscribe(testSubscriber()
                .assertCount(1));

    }

    private static InputStream getBuildpackBits() {
        try {
            return new ClassPathResource("test-buildpack.zip").getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
