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

package org.cloudfoundry.operations.buildpacks;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.buildpacks.BuildpackResource;
import org.cloudfoundry.client.v2.buildpacks.CreateBuildpackResponse;
import org.cloudfoundry.client.v2.buildpacks.ListBuildpacksRequest;
import org.cloudfoundry.client.v2.buildpacks.ListBuildpacksResponse;
import org.cloudfoundry.client.v2.buildpacks.UploadBuildpackResponse;
import org.cloudfoundry.operations.AbstractOperationsApiTest;
import org.cloudfoundry.util.RequestValidationException;
import org.cloudfoundry.util.test.TestSubscriber;
import org.junit.Before;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.cloudfoundry.util.test.TestObjects.fill;
import static org.cloudfoundry.util.test.TestObjects.fillPage;
import static org.mockito.Mockito.when;

public final class DefaultBuildpacksTest {

    private static void requestBuildpacks(CloudFoundryClient cloudFoundryClient) {
        when(cloudFoundryClient.buildpacks()
            .list(fillPage(ListBuildpacksRequest.builder())
                .build()))
            .thenReturn(Mono
                .just(fillPage(ListBuildpacksResponse.builder())
                    .resource(fill(BuildpackResource.builder(), "buildpack-")
                        .build())
                    .build()));
    }

    private static void requestCreateBuildpack(CloudFoundryClient cloudFoundryClient, String name, Integer position, Boolean enable) {
        when(cloudFoundryClient.buildpacks()
            .create(org.cloudfoundry.client.v2.buildpacks.CreateBuildpackRequest.builder()
                .name(name)
                .position(position)
                .enabled(enable)
                .build()))
            .thenReturn(Mono
                .just(fill(CreateBuildpackResponse.builder(), "buildpack-")
                    .build()));
    }

    private static void requestUploadBuildpack(CloudFoundryClient cloudFoundryClient, String buildpackId, InputStream buildpack, String filename) {
        when(cloudFoundryClient.buildpacks()
            .upload(org.cloudfoundry.client.v2.buildpacks.UploadBuildpackRequest.builder()
                .buildpackId(buildpackId)
                .buildpack(buildpack)
                .filename(filename)
                .build()))
            .thenReturn(Mono
                .just(fill(UploadBuildpackResponse.builder())
                    .build()));
    }

    public static final class Create extends AbstractOperationsApiTest<Void> {

        private final DefaultBuildpacks buildpacks = new DefaultBuildpacks(this.cloudFoundryClient);

        private static final String BUILDPACK_ID = "test-buildpack-id";

        private static final String BUILDPACK_NAME = "go-buildpack";

        private static final String FILE_NAME = "gobuildpack.zip";

        private static final Integer POSITION = 1;

        private static final Boolean ENABLE = true;

        private static final ByteArrayInputStream EMPTY_STREAM = new ByteArrayInputStream(new byte[0]);

        @Before
        public void setUp() throws Exception {
            requestCreateBuildpack(this.cloudFoundryClient, BUILDPACK_NAME, POSITION, ENABLE);
            requestUploadBuildpack(this.cloudFoundryClient, BUILDPACK_ID, EMPTY_STREAM, FILE_NAME);
        }

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) {
            // Expects onComplete() with no onNext()
        }

        @Override
        protected Publisher<Void> invoke() {
            return this.buildpacks
                .create(CreateBuildpackRequest.builder()
                    .buildpack(EMPTY_STREAM)
                    .fileName(FILE_NAME)
                    .name(BUILDPACK_NAME)
                    .enable(ENABLE)
                    .position(POSITION)
                    .build());
        }

    }

    public static final class CreateInvalid extends AbstractOperationsApiTest<Void> {

        private final DefaultBuildpacks buildpacks = new DefaultBuildpacks(this.cloudFoundryClient);

        @Override
        protected void assertions(TestSubscriber<Void> testSubscriber) {
            testSubscriber
                .assertError(RequestValidationException.class, "Request is invalid: name must be specified, file name must be specified, " +
                    "buildpack must be specified, position must be specified");
        }

        @Override
        protected Publisher<Void> invoke() {
            return this.buildpacks
                .create(CreateBuildpackRequest.builder()
                    .build());
        }

    }

    public static final class List extends AbstractOperationsApiTest<Buildpack> {

        private final DefaultBuildpacks buildpacks = new DefaultBuildpacks(this.cloudFoundryClient);

        @Before
        public void setUp() throws Exception {
            requestBuildpacks(this.cloudFoundryClient);
        }

        @Override
        protected void assertions(TestSubscriber<Buildpack> testSubscriber) {
            testSubscriber
                .assertEquals(Buildpack.builder()
                    .enabled(true)
                    .filename("test-buildpack-filename")
                    .id("test-buildpack-id")
                    .locked(true)
                    .name("test-buildpack-name")
                    .position(1)
                    .build());
        }

        @Override
        protected Publisher<Buildpack> invoke() {
            return this.buildpacks.list();
        }
    }

}
