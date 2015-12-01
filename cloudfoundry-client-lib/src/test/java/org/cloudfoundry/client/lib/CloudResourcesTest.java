/*
 * Copyright 2009-2011 the original author or authors.
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
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipFile;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link org.cloudfoundry.client.lib.domain.CloudResources}.
 *
 * @author Phillip Webb
 */
public class CloudResourcesTest {

    private static final String SHA = "677E1B9BCA206D6534054348511BF41129744839";

    private static final String JSON = "[{\"sha1\":\"" + SHA + "\",\"size\":93,\"fn\":\"index.html\"}]";

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void shouldCreateFromIterator() throws Exception {
        Iterator<? extends CloudResource> i = Collections.singleton(new CloudResource("index.html", 93L, SHA))
                .iterator();
        CloudResources o = new CloudResources(i);
        List<CloudResource> l = o.asList();
        assertThat(l.size(), is(1));
        CloudResource r = l.get(0);
        assertThat(r.getFilename(), is("index.html"));
        assertThat(r.getSha1(), is(SHA));
        assertThat(r.getSize(), is(93L));
    }

    @Test
    public void shouldDeserialize() throws JsonParseException, JsonMappingException, IOException {
        CloudResources o = mapper.readValue(JSON, CloudResources.class);
        List<CloudResource> l = o.asList();
        assertThat(l.size(), is(1));
        CloudResource r = l.get(0);
        assertThat(r.getFilename(), is("index.html"));
        assertThat(r.getSha1(), is(SHA));
        assertThat(r.getSize(), is(93L));
    }

    @Test
    public void shouldGetFilenames() throws Exception {
        List<CloudResource> resources = new ArrayList<CloudResource>();
        resources.add(new CloudResource("1", 93L, SHA));
        resources.add(new CloudResource("2", 93L, SHA));
        CloudResources o = new CloudResources(resources);
        Set<String> expected = new HashSet<String>(Arrays.asList("1", "2"));
        assertThat(o.getFilenames(), is(expected));
    }

    @Test
    public void shouldGetFromArchive() throws Exception {
        ZipFile zipFile = new ZipFile(SampleProjects.springTravel());
        try {
            ApplicationArchive archive = new ZipApplicationArchive(zipFile);
            CloudResources o = new CloudResources(archive);
            List<CloudResource> l = o.asList();
            assertThat(l.size(), is(96));
            assertThat(l.get(0).getFilename(), is("index.html"));
        } finally {
            zipFile.close();
        }
    }

    @Test
    public void shouldSerialize() throws Exception {
        CloudResource r = new CloudResource("index.html", 93L, SHA);
        CloudResources o = new CloudResources(Collections.singleton(r));
        String s = mapper.writeValueAsString(o);
        assertThat(s, is(equalTo(JSON)));
    }
}
