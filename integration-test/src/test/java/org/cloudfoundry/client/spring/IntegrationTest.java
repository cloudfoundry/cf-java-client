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
import org.cloudfoundry.client.v2.Resource;
import org.cloudfoundry.client.v2.spaces.ListSpacesRequest;
import org.cloudfoundry.client.v2.spaces.ListSpacesResponse;
import org.cloudfoundry.client.v3.applications.CreateApplicationRequest;
import org.cloudfoundry.client.v3.applications.CreateApplicationResponse;
import org.cloudfoundry.client.v3.applications.DeleteApplicationRequest;
import org.cloudfoundry.client.v3.applications.DeleteApplicationResponse;
import org.cloudfoundry.client.v3.applications.ListApplicationsRequest;
import org.cloudfoundry.client.v3.applications.ListApplicationsResponse;
import org.cloudfoundry.client.v3.droplets.GetDropletRequest;
import org.cloudfoundry.client.v3.droplets.GetDropletResponse;
import org.cloudfoundry.client.v3.packages.CreatePackageRequest;
import org.cloudfoundry.client.v3.packages.CreatePackageResponse;
import org.cloudfoundry.client.v3.packages.GetPackageRequest;
import org.cloudfoundry.client.v3.packages.GetPackageResponse;
import org.cloudfoundry.client.v3.packages.StagePackageRequest;
import org.cloudfoundry.client.v3.packages.StagePackageResponse;
import org.cloudfoundry.client.v3.packages.UploadPackageRequest;
import org.cloudfoundry.client.v3.packages.UploadPackageResponse;
import org.junit.Before;
import org.junit.Test;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.core.env.StandardEnvironment;
import reactor.Publishers;
import reactor.rx.Streams;

import java.io.File;

import static org.cloudfoundry.client.v3.packages.CreatePackageRequest.PackageType.BITS;

public final class IntegrationTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private volatile String application;

    private volatile File bits;

    private volatile CloudFoundryClient client;

    private volatile String space;

    @Before
    public void configure() throws Exception {
        RelaxedPropertyResolver resolver = new RelaxedPropertyResolver(new StandardEnvironment(), null);

        this.application = resolver.getRequiredProperty("test.application");
        this.bits = resolver.getRequiredProperty("test.bits", File.class);
        this.space = resolver.getRequiredProperty("test.space");

        this.client = new SpringCloudFoundryClientBuilder()
                .withApi(resolver.getRequiredProperty("test.host"))
                .withCredentials(
                        resolver.getRequiredProperty("test.username"),
                        resolver.getRequiredProperty("test.password"))
                .withSkipSslValidation(resolver.getProperty("test.skipSslValidation", Boolean.class, false))
                .build();
    }

    @Test
    public void test() throws InterruptedException {
        Streams.wrap(listApplications())
                .flatMap(this::split)
                .flatMap(this::deleteApplication)
                .consume(response -> {
                        }, this::handleError,
                        r -> this.logger.info("All existing applications deleted"));

        Streams.wrap(listSpaces())
                .flatMap(this::createApplication)
                .observe(response -> this.logger.info("Application created"))
                .flatMap(this::createPackage)
                .observe(response -> this.logger.info("Package created"))
                .flatMap(this::uploadBits)
                .observe(response -> this.logger.info("Package uploading"))
                .flatMap(this::waitForUploadProcessing)
                .observe(response -> this.logger.info("Package uploaded"))
                .flatMap(this::stagePackage)
                .observe(response -> this.logger.info("Package staging"))
                .flatMap(this::waitForStaging)
                .observe(response -> this.logger.info("Package staged"))
                .keepAlive()
                .consume(response -> {
                    this.logger.info(response.getState());
                }, this::handleError);
    }

    private Publisher<CreateApplicationResponse> createApplication(ListSpacesResponse response) {
        Resource.Metadata metadata = response.getResources().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Could not find space " + this.space))
                .getMetadata();

        CreateApplicationRequest request = new CreateApplicationRequest()
                .withSpaceId(metadata.getId())
                .withName(this.application);

        return this.client.applications().create(request);
    }

    private Publisher<CreatePackageResponse> createPackage(CreateApplicationResponse response) {
        CreatePackageRequest request = new CreatePackageRequest()
                .withApplicationId(response.getId())
                .withType(BITS);

        return this.client.packages().create(request);
    }

    private Publisher<DeleteApplicationResponse> deleteApplication(ListApplicationsResponse.Resource resource) {
        DeleteApplicationRequest request = new DeleteApplicationRequest()
                .withId(resource.getId());

        return this.client.applications().delete(request);
    }

    private void handleError(Throwable exception) {
        this.logger.error("Error encountered: {}", exception.getMessage());
    }

    private Publisher<ListApplicationsResponse> listApplications() {
        return this.client.applications().list(new ListApplicationsRequest());
    }

    private Publisher<ListSpacesResponse> listSpaces() {
        ListSpacesRequest request = new ListSpacesRequest().withName(this.space);

        return this.client.spaces().list(request);
    }

    private Publisher<ListApplicationsResponse.Resource> split(ListApplicationsResponse response) {
        return Streams.from(response.getResources());
    }

    private Publisher<StagePackageResponse> stagePackage(GetPackageResponse response) {
        StagePackageRequest request = new StagePackageRequest()
                .withId(response.getId())
                .withBuildpack("https://github.com/cloudfoundry/java-buildpack.git");

        return this.client.packages().stage(request);
    }

    private Publisher<UploadPackageResponse> uploadBits(CreatePackageResponse response) {
        UploadPackageRequest request = new UploadPackageRequest()
                .withId(response.getId())
                .withFile(this.bits);

        return this.client.packages().upload(request);
    }

    private Publisher<GetDropletResponse> waitForStaging(StagePackageResponse stagePackageResponse) {
        return Streams.wrap(Publishers.<GetDropletResponse>create(subscriber -> {
            for (; ; ) {
                GetDropletRequest request = new GetDropletRequest()
                        .withId(stagePackageResponse.getId());

                Streams.wrap(this.client.droplets().get(request))
                        .observe(response -> this.logger.debug("Package staging: {}", response.getState()))
                        .observe(response -> this.logger.info("Waiting for package staging"))
                        .consume(subscriber::onNext);

                if (subscriber.isCancelled()) {
                    subscriber.onComplete();
                    break;
                }

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        })).observe(getPackageResponse -> {
            if ("FAILED".equals(getPackageResponse.getState())) {
                throw new IllegalStateException(getPackageResponse.getError());
            }
        }).filter(getDropletResponse -> "STAGED".equals(getDropletResponse.getState()));
    }

    private Publisher<GetPackageResponse> waitForUploadProcessing(UploadPackageResponse uploadPackageResponse) {
        return Streams.wrap(Publishers.<GetPackageResponse>create(subscriber -> {
            for (; ; ) {
                GetPackageRequest request = new GetPackageRequest()
                        .withId(uploadPackageResponse.getId());

                Streams.wrap(this.client.packages().get(request))
                        .observe(response -> this.logger.debug("Package upload processing: {}", response.getState()))
                        .observe(response -> this.logger.info("Waiting for package upload processing"))
                        .consume(subscriber::onNext);

                if (subscriber.isCancelled()) {
                    subscriber.onComplete();
                    break;
                }

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        })).observe(getPackageResponse -> {
            if ("FAILED".equals(getPackageResponse.getState())) {
                throw new IllegalStateException(getPackageResponse.getError());
            }
        }).filter(getPackageResponse -> "READY".equals(getPackageResponse.getState()));
    }

}
