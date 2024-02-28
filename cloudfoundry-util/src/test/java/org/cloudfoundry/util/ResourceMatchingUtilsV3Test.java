package org.cloudfoundry.util;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v3.Checksum;
import org.cloudfoundry.client.v3.resourcematch.ListMatchingResourcesRequest;
import org.cloudfoundry.client.v3.resourcematch.ListMatchingResourcesResponse;
import org.cloudfoundry.client.v3.resourcematch.ResourceMatchV3;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.IntStream;

import static org.cloudfoundry.util.ResourceMatchingUtilsV3.requestListMatchingResources;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ResourceMatchingUtilsV3Test {


	@Test
	void requestListMatchingResources5() {
		CloudFoundryClient cloudFoundryClient = mock(CloudFoundryClient.class);
		Collection<ResourceMatchingUtilsV3.ArtifactMetadata> artifactMetadatas = new ArrayList<>();

		IntStream.range(0,5).forEach(value -> {
			artifactMetadatas.add(new ResourceMatchingUtilsV3.ArtifactMetadata(Checksum.builder().value("pif" + value).build(), "path"+value, "0644", value));
		});
		when(cloudFoundryClient.resourceMatchV3()).thenReturn(new ResourceMatchV3() {
			@Override
			public Mono<ListMatchingResourcesResponse> list(ListMatchingResourcesRequest request) {
				return Mono.just(ListMatchingResourcesResponse.builder().addAllResources(request.getResources()).build());
			}
		});

		ListMatchingResourcesResponse listMatchingResourcesResponse = requestListMatchingResources(cloudFoundryClient, artifactMetadatas).block();
		assertEquals(5, listMatchingResourcesResponse.getResources().size());

	}

	@Test
	void requestListMatchingResources15001() {
		CloudFoundryClient cloudFoundryClient = mock(CloudFoundryClient.class);
		Collection<ResourceMatchingUtilsV3.ArtifactMetadata> artifactMetadatas = new ArrayList<>();

		IntStream.range(0,15001).forEach(value -> {
			artifactMetadatas.add(new ResourceMatchingUtilsV3.ArtifactMetadata(Checksum.builder().value("pif" + value).build(), "path"+value, "0644", value));
		});
		when(cloudFoundryClient.resourceMatchV3()).thenReturn(new ResourceMatchV3() {
			@Override
			public Mono<ListMatchingResourcesResponse> list(ListMatchingResourcesRequest request) {
				return Mono.just(ListMatchingResourcesResponse.builder().addAllResources(request.getResources()).build());
			}
		});

		ListMatchingResourcesResponse listMatchingResourcesResponse = requestListMatchingResources(cloudFoundryClient, artifactMetadatas).block();
		assertEquals(15001, listMatchingResourcesResponse.getResources().size());

	}

}