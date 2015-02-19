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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipFile;

import org.cloudfoundry.client.lib.SampleProjects;
import org.cloudfoundry.client.lib.archive.ApplicationArchive.Entry;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

/**
 * Tests for {@link DirectoryApplicationArchive}.
 *
 * @author Phillip Webb
 */
public class DirectoryApplicationArchiveTest extends AbstractApplicationArchiveTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Override
    protected ApplicationArchive newApplicationArchive(ZipFile fileFile) throws IOException {
        return new DirectoryApplicationArchive(SampleProjects.springTravelUnpacked(temporaryFolder));
    }

    @Test
    public void shouldNeedNonNullFolder() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Directory must not be null");
        new DirectoryApplicationArchive(null);
    }

    @Test
    public void shouldNeedFileThatIsADirectory() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("File must reference a directory");
        new DirectoryApplicationArchive(temporaryFolder.newFile("noadirectory"));
    }
    
    @Test
    public void archiveShouldNotIncludeScmMetadataDirectories() throws IOException{
      ApplicationArchive archive = new DirectoryApplicationArchive(SampleProjects.appWithScmMetaData(temporaryFolder));
      assertNoScmDirectories(archive);
      assertContainsAppFiles(archive);
    }

    private void assertNoScmDirectories(ApplicationArchive archive) {
        boolean containsGit = false;
        boolean containsSvn = false;
        for (Entry entry : archive.getEntries()) {
            if (entry.getName().contains(".svn"+File.separator)) {
                containsSvn = true;
            }
            if (entry.getName().contains(".git"+File.separator)) {
                containsGit = true;
            }
        }
        assertFalse("The archive should not contain Git metadata", containsGit);
        assertFalse("The archive should not contain SVN metadata", containsSvn);
    }

    private void assertContainsAppFiles(ApplicationArchive archive) {
    	final String subProjectPath = "sub-project"+File.separator;
        int fileCount = 0;
        boolean containsApp = false;
        boolean containsPackage = false;
        boolean containsSubproject = false;
        boolean containsExample = false;
        for (Entry entry : archive.getEntries()) {
            fileCount++;
            if (entry.getName().equals("app.js")) {
                containsApp = true;
            }
            if (entry.getName().equals("package.json")) {
                containsPackage = true;
            }
			if (entry.getName().equals(subProjectPath)) {
                containsSubproject = true;
            }
            if (entry.getName().equals(subProjectPath+"example.txt")) {
                containsExample = true;
            }
        }
        assertEquals("The archive should not contain any extraneous files", 4, fileCount);
        assertTrue("The archive should contain 'app.js'", containsApp);
        assertTrue("The archive should contain 'package.json'", containsPackage);
        assertTrue("The archive should contain '"+subProjectPath+"'", containsSubproject);
        assertTrue("The archive should contain '"+subProjectPath+"example.txt'", containsExample);
    }
}
