package org.cloudfoundry.util;

import static org.cloudfoundry.util.ResourceMatchingUtilsV3.getMatchedResources;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v3.resourcematch.ListMatchingResourcesResponse;
import org.cloudfoundry.client.v3.resourcematch.MatchedResource;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import reactor.core.publisher.Mono;

class ResourceMatchingUtilsV3Test {

    @Test
    void requestListMatchingResources2() throws IOException {
        CloudFoundryClient cloudFoundryClient = mock(CloudFoundryClient.class);
        when(cloudFoundryClient.resourceMatchV3())
                .thenReturn(
                        request ->
                                Mono.just(
                                        ListMatchingResourcesResponse.builder()
                                                .addAllResources(request.getResources())
                                                .build()));

        Path testApplication = new ClassPathResource("test-application.zip").getFile().toPath();

        List<MatchedResource> result =
                getMatchedResources(cloudFoundryClient, testApplication).block();
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void requestListMatchingResources15001() throws IOException {
        CloudFoundryClient cloudFoundryClient = mock(CloudFoundryClient.class);
        when(cloudFoundryClient.resourceMatchV3())
                .thenReturn(
                        request ->
                                Mono.just(
                                        ListMatchingResourcesResponse.builder()
                                                .addAllResources(request.getResources())
                                                .build()));
        Path testApplication = new ClassPathResource("15001_files.zip").getFile().toPath();

        List<MatchedResource> result =
                getMatchedResources(cloudFoundryClient, testApplication).block();
        assertNotNull(result);
        assertEquals(15001, result.size());
    }
}
