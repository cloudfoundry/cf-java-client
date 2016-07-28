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

package org.cloudfoundry.uaa;

import org.cloudfoundry.AbstractIntegrationTest;
import org.cloudfoundry.uaa.clients.ListMetadatasRequest;
import org.cloudfoundry.uaa.clients.ListMetadatasResponse;
import org.cloudfoundry.uaa.clients.UpdateMetadataRequest;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;

public final class ClientsTest extends AbstractIntegrationTest {

    @Autowired
    private String clientId;

    @Autowired
    private UaaClient uaaClient;

    @Ignore("TODO: Await https://www.pivotaltracker.com/story/show/125590231")
    @Test
    public void listMetadatas() {
        this.uaaClient.clients()
            .listMetadatas(ListMetadatasRequest.builder()
                .build())
            .subscribe(this.testSubscriber());
    }

    @Test
    public void updateMetadata() {

        this.uaaClient.clients()
            .updateMetadata(UpdateMetadataRequest.builder()
                .appLaunchUrl("http://test.app.launch.url")
                .clientId(this.clientId)
                .showOnHomePage(false)
                .build())
            .then(this.uaaClient.clients()
                .listMetadatas(ListMetadatasRequest.builder()
                    .build()))
            .flatMapIterable(ListMetadatasResponse::getMetadatas)
            .filter(metadata -> this.clientId.equals(metadata.getClientId()))
            .single()
            .subscribe(this.<org.cloudfoundry.uaa.clients.Metadata>testSubscriber()
                .assertThat(metadata -> {
                    assertEquals("", metadata.getAppIcon());
                    assertEquals("http://test.app.launch.url", metadata.getAppLaunchUrl());
                    assertEquals(this.clientId, metadata.getClientId());
                    assertEquals(false, metadata.getShowOnHomePage());
                }));
    }

}
