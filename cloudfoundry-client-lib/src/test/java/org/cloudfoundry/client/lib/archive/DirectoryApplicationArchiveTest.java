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

import java.io.IOException;
import java.util.zip.ZipFile;

import org.cloudfoundry.client.lib.SampleProjects;
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
}
