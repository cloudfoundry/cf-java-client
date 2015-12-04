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

package org.cloudfoundry.demo;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.LoggregatorClient;
import org.cloudfoundry.client.loggregator.LoggregatorMessage;
import org.cloudfoundry.client.loggregator.StreamLogsRequest;
import org.cloudfoundry.client.spring.SpringCloudFoundryClient;
import org.cloudfoundry.client.spring.SpringLoggregatorClient;
import org.cloudfoundry.client.v2.Resource;
import org.cloudfoundry.client.v2.spaces.ListSpacesRequest;
import org.cloudfoundry.client.v2.spaces.ListSpacesResponse;
import org.cloudfoundry.client.v3.applications.CreateApplicationRequest;
import org.cloudfoundry.client.v3.applications.CreateApplicationResponse;
import org.cloudfoundry.client.v3.applications.DeleteApplicationRequest;
import org.cloudfoundry.client.v3.applications.ListApplicationsRequest;
import org.cloudfoundry.client.v3.applications.ListApplicationsResponse;
import org.cloudfoundry.client.v3.droplets.GetDropletRequest;
import org.cloudfoundry.client.v3.packages.CreatePackageRequest;
import org.cloudfoundry.client.v3.packages.CreatePackageResponse;
import org.cloudfoundry.client.v3.packages.GetPackageRequest;
import org.cloudfoundry.client.v3.packages.StagePackageRequest;
import org.cloudfoundry.client.v3.packages.StagePackageResponse;
import org.cloudfoundry.client.v3.packages.UploadPackageRequest;
import org.cloudfoundry.client.v3.packages.UploadPackageResponse;
import org.reactivestreams.Processor;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import reactor.Processors;
import reactor.Publishers;
import reactor.rx.Stream;
import reactor.rx.Streams;
import reactor.rx.action.Control;

import java.io.File;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.cloudfoundry.client.v3.packages.CreatePackageRequest.PackageType.BITS;

@Configuration
@EnableAutoConfiguration
public class PushApplication {

    public static void main(String[] args) throws InterruptedException {
        new SpringApplicationBuilder(ApplicationEvents.class).web(false).run(args)
                .getBean(Runner.class).run();
    }

    @Bean
    SpringCloudFoundryClient cloudFoundryClient(@Value("${test.host}") String host,
                                                @Value("${test.username}") String username,
                                                @Value("${test.password}") String password,
                                                @Value("${test.skipSslValidation:false}") Boolean skipSslValidation) {

        return SpringCloudFoundryClient.builder()
                .host(host)
                .username(username)
                .password(password)
                .skipSslValidation(skipSslValidation)
                .build();
    }

    @Bean
    SpringLoggregatorClient loggregatorClient(SpringCloudFoundryClient cloudFoundryClient) {
        return SpringLoggregatorClient.builder()
                .cloudFoundryClient(cloudFoundryClient)
                .build();
    }

    @Component
    private static final class Runner {

        private final Logger logger = LoggerFactory.getLogger(this.getClass());

        private final String application;

        private final File bits;

        private final CloudFoundryClient cloudFoundryClient;

        private final LoggregatorClient loggregatorClient;

        private final String space;

        @Autowired
        private Runner(@Value("${test.application}") String application,
                       @Value("${test.bits}") File bits,
                       CloudFoundryClient cloudFoundryClient,
                       LoggregatorClient loggregatorClient,
                       @Value("${test.space}") String space) {

            this.application = application;
            this.bits = bits;
            this.cloudFoundryClient = cloudFoundryClient;
            this.loggregatorClient = loggregatorClient;
            this.space = space;
        }

        private Stream<CreateApplicationResponse> createApplication(ListSpacesResponse response) {
            Resource.Metadata metadata = response.getResources().stream()
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Could not find space " + this.space))
                    .getMetadata();

            CreateApplicationRequest request = CreateApplicationRequest.builder()
                    .spaceId(metadata.getId())
                    .name(this.application)
                    .build();

            return Streams.wrap(this.cloudFoundryClient.applicationsV3().create(request))
                    .observeStart(s -> this.logger.info("Creating application"))
                    .observe(r -> this.logger.info("Created application"));
        }

        private Publisher<CreatePackageResponse> createPackage(CreateApplicationResponse response) {
            CreatePackageRequest request = CreatePackageRequest.builder()
                    .applicationId(response.getId())
                    .type(BITS)
                    .build();

            return Streams.wrap(this.cloudFoundryClient.packages().create(request))
                    .observeStart(s -> this.logger.info("Creating package"))
                    .observe(r -> this.logger.info("Created package"));
        }

        private Publisher<Void> deleteApplication(ListApplicationsResponse.Resource resource) {
            DeleteApplicationRequest request = DeleteApplicationRequest.builder()
                    .id(resource.getId())
                    .build();

            return Streams.wrap(this.cloudFoundryClient.applicationsV3().delete(request))
                    .observeStart(s -> this.logger.info("Deleting application"))
                    .observe(r -> this.logger.info("Deleted application"));
        }

        private void handleError(Throwable exception) {
            this.logger.error("Error encountered: {}", exception.getMessage());
        }

        private void ignoreResponse(Object response) {
        }

        private Stream<ListApplicationsResponse> listApplications() {
            ListApplicationsRequest request = ListApplicationsRequest.builder()
                    .build();

            return Streams.wrap(this.cloudFoundryClient.applicationsV3().list(request));
        }

        private Stream<ListSpacesResponse> listSpaces() {
            ListSpacesRequest request = ListSpacesRequest.builder()
                    .name(this.space)
                    .build();

            return Streams.wrap(this.cloudFoundryClient.spaces().list(request));
        }

        private void printLog(LoggregatorMessage m) {
            this.logger.info("[{}/{}] {} {}", m.getSourceName(), m.getSourceId(), m.getMessageType(), m.getMessage());
        }

        private void run() throws InterruptedException {
            listApplications()
                    .flatMap(response -> Streams.from(response.getResources()))
                    .flatMap(this::deleteApplication)
                    .consume(this::ignoreResponse, this::handleError,
                            r -> this.logger.info("All existing applications deleted"));

            Stream<StagePackageResponse> stagePackageStream = listSpaces()
                    .flatMap(this::createApplication)
                    .flatMap(this::createPackage)
                    .flatMap(this::uploadPackage)
                    .flatMap(this::waitForPackageUploadProcessing)
                    .flatMap(this::stagePackage);

            Processor<StagePackageResponse, StagePackageResponse> async = Processors.<StagePackageResponse>asyncGroup()
                    .get();

            Control loggregatorControl = Streams.wrap(async)
                    .flatMap(this::streamLogs)
                    .consume(this::printLog, this::handleError, r -> {
                        this.logger.info("Logging finished");
                    });

            Streams.wrap(async)
                    .flatMap(this::waitForPackageStagingProcessing)
                    .consume(this::ignoreResponse, this::handleError, r -> {
                        this.logger.info("Integration test finished");
                        loggregatorControl.cancel();
                    });

            stagePackageStream.subscribe(async);
        }

        private Publisher<StagePackageResponse> stagePackage(UploadPackageResponse response) {
            StagePackageRequest request = StagePackageRequest.builder()
                    .id(response.getId())
                    .buildpack("https://github.com/cloudfoundry/java-buildpack.git")
                    .build();

            return Streams.wrap(this.cloudFoundryClient.packages().stage(request))
                    .observe(r -> this.logger.info("Staging package"));
        }

        private Publisher<LoggregatorMessage> streamLogs(StagePackageResponse response) {
            StreamLogsRequest request = StreamLogsRequest.builder()
                    .id(response.getId())
                    .build();

            return this.loggregatorClient.stream(request);
        }

        private Publisher<UploadPackageResponse> uploadPackage(CreatePackageResponse response) {
            UploadPackageRequest request = UploadPackageRequest.builder()
                    .id(response.getId())
                    .file(this.bits)
                    .build();

            return Streams.wrap(this.cloudFoundryClient.packages().upload(request))
                    .observeStart(s -> this.logger.info("Uploading package"));
        }

        private Publisher<StagePackageResponse> waitForPackageStagingProcessing(StagePackageResponse response) {
            return Streams.wrap(Publishers.<StagePackageResponse>create(subscriber -> {
                GetDropletRequest request = GetDropletRequest.builder()
                        .id(response.getId())
                        .build();

                Streams.wrap(this.cloudFoundryClient.droplets().get(request))
                        .observe(r -> this.logger.debug("Staging package processing: {}", r.getState()))
                        .consume(r -> {
                            if ("STAGED".equals(r.getState())) {
                                subscriber.onNext(response);
                                subscriber.onComplete();
                            } else if ("FAILED".equals(r.getState())) {
                                subscriber.onError(new ProcessingFailed(r.getError()));
                            } else {
                                subscriber.onError(new ProcessingIncomplete());
                            }
                        }, subscriber::onError);
            })).retryWhen(errors -> errors.flatMap(throwable -> {
                        if (throwable instanceof ProcessingIncomplete) {
                            return Streams.timer(1, SECONDS)
                                    .observeStart(s -> this.logger.info("Waiting for package staging processing"));
                        } else {
                            return Publishers.error(throwable);
                        }
                    })
            ).observe(r -> this.logger.info("Staged package"));
        }

        private Publisher<UploadPackageResponse> waitForPackageUploadProcessing(UploadPackageResponse response) {
            return Streams.wrap(Publishers.<UploadPackageResponse>create(subscriber -> {
                GetPackageRequest request = GetPackageRequest.builder()
                        .id(response.getId())
                        .build();

                Streams.wrap(this.cloudFoundryClient.packages().get(request))
                        .observe(r -> this.logger.warn("Upload package processing: {}", r.getState()))
                        .consume(r -> {
                            if ("READY".equals(r.getState())) {
                                subscriber.onNext(response);
                                subscriber.onComplete();
                            } else if ("FAILED".equals(r.getState())) {
                                subscriber.onError(new ProcessingFailed(r.getError()));
                            } else {
                                subscriber.onError(new ProcessingIncomplete());
                            }
                        }, subscriber::onError);
            })).retryWhen(errors -> errors.flatMap(throwable -> {
                        if (throwable instanceof ProcessingIncomplete) {
                            return Streams.timer(1, SECONDS)
                                    .observeStart(s -> this.logger.info("Waiting for package upload processing"));
                        } else {
                            return Publishers.error(throwable);
                        }
                    })
            ).observe(r -> this.logger.info("Uploaded package"));
        }

    }

}
