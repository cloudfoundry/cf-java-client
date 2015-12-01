/*
 * Copyright 2009-2012 the original author or authors.
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

package org.cloudfoundry.client.lib;

import org.cloudfoundry.client.lib.archive.ApplicationArchive;
import org.cloudfoundry.client.lib.archive.ZipApplicationArchive;
import org.cloudfoundry.client.lib.domain.CloudResource;
import org.cloudfoundry.client.lib.domain.CloudResources;
import org.cloudfoundry.client.lib.domain.UploadApplicationPayload;
import org.junit.Test;
import org.springframework.util.FileCopyUtils;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipFile;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link org.cloudfoundry.client.lib.domain.UploadApplicationPayload}.
 *
 * @author Phillip Webb
 */
public class UploadApplicationPayloadTest {

    @Test
    public void shouldPackOnlyMissingResources() throws Exception {
        ZipFile zipFile = new ZipFile(SampleProjects.springTravel());
        try {
            ApplicationArchive archive = new ZipApplicationArchive(zipFile);
            CloudResources allResources = new CloudResources(archive);
            List<CloudResource> resources = new ArrayList<CloudResource>(allResources.asList());
            resources.remove(0);
            CloudResources knownRemoteResources = new CloudResources(resources);
            UploadApplicationPayload payload = new UploadApplicationPayload(archive, knownRemoteResources);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            FileCopyUtils.copy(payload.getInputStream(), bos);
            assertThat(payload.getArchive(), is(archive));
            assertThat(payload.getTotalUncompressedSize(), is(93));
            assertThat(bos.toByteArray().length, is(2451));
        } finally {
            zipFile.close();
        }
    }

}
