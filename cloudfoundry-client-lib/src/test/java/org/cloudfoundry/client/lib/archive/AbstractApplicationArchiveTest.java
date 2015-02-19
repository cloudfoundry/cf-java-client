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

package org.cloudfoundry.client.lib.archive;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.cloudfoundry.client.lib.SampleProjects;
import org.cloudfoundry.client.lib.archive.ApplicationArchive.Entry;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.util.FileCopyUtils;

/**
 * Abstract base class for {@link ApplicationArchive} tests. All tests are based against
 * {@link SampleProjects#springTravel()}.
 *
 * @see ZipApplicationArchiveTest
 * @see DirectoryApplicationArchiveTest
 *
 * @author Phillip Webb
 */
public abstract class AbstractApplicationArchiveTest {

    private ZipFile zipFile;

    private ApplicationArchive archive;

    private HashMap<String, Entry> archiveEntries;

    @Before
    public void setup() throws ZipException, IOException {
        this.zipFile = new ZipFile(SampleProjects.springTravel());
        this.archive = newApplicationArchive(zipFile);
        this.archiveEntries = new HashMap<String, ApplicationArchive.Entry>();
        for (ApplicationArchive.Entry entry : archive.getEntries()) {
            archiveEntries.put(entry.getName(), entry);
        }
    }

    /**
     * Factory method used to create a new {@link ApplicationArchive}.
     *
     * @param fileFile the zip file used for comparisons
     * @return the archive
     * @throws IOException
     */
    protected abstract ApplicationArchive newApplicationArchive(ZipFile fileFile) throws IOException;

    @After
    public void tearDown() throws IOException {
        zipFile.close();
    }

    @Test
    @Ignore
    public void shouldAdaptEntries() throws Exception {
        Enumeration<? extends ZipEntry> zipEntries = zipFile.entries();
        while (zipEntries.hasMoreElements()) {
            ZipEntry zipEntry = zipEntries.nextElement();
            ApplicationArchive.Entry archiveEntry = archiveEntries.remove(zipEntry.getName());
            assertThat(archiveEntry, is(notNullValue()));
            assertThat(archiveEntry.getSize(), is(zipEntry.getSize()));
            assertThat(archiveEntry.isDirectory(), is(zipEntry.isDirectory()));
        }
        assertThat(archiveEntries.size(), is(0));
    }

    @Test
    public void shouldGetNameFromZipFileNameWithoutPath() throws Exception {
        assertThat(archive.getFilename(), is("swf-booking-mvc.war"));
    }

    @Test
    public void shouldCalculateSha1() throws Exception {
        byte[] digest = archiveEntries.get("index.html").getSha1Digest();
        String digestString = String.format("%x", new BigInteger(digest));
        assertThat(digestString, is("677e1b9bca206d6534054348511bf41129744839"));
    }

    @Test
    public void shouldBeAbleToGetInputStreamTwice() throws Exception {
        Entry entry = archiveEntries.get("index.html");
        ByteArrayOutputStream s1 = new ByteArrayOutputStream();
        FileCopyUtils.copy(entry.getInputStream(), s1);
        ByteArrayOutputStream s2 = new ByteArrayOutputStream();
        FileCopyUtils.copy(entry.getInputStream(), s2);
        assertThat(s1.toByteArray().length, is(93));
        assertThat(s2.toByteArray().length, is(93));
        assertThat(s1.toByteArray(), is(equalTo(s2.toByteArray())));
    }
}
