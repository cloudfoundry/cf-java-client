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

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static org.junit.Assert.assertTrue;

/**
 * Provides access to sample projects.
 *
 * @author Ramnivas Laddad
 * @author A.B.Srinivasan
 * @author Phillip Webb
 * @author Jennifer Hickey
 */
public class SampleProjects {

    private static final String TEST_APP_DIR = "src/test/resources/apps";

    public static File appWithScmMetaData(TemporaryFolder temporaryFolder) throws IOException {
        File appDirectory = new File(TEST_APP_DIR + "/app-with-scm-metadata");
        File tmpAppDirectory = copyAppToTempDir(temporaryFolder, appDirectory);
        addExampleGitRepo(tmpAppDirectory);

        // For the scenario of having a repo in a subdirectory
        File subProject = new File(tmpAppDirectory, "sub-project");
        subProject.mkdir();
        addExampleGitRepo(subProject);

        return tmpAppDirectory;
    }

    /**
     * Returns a bad spring application that will cause crashes.
     *
     * @return the bad-spring-app WAR file
     * @throws IOException
     */
    public static File badSpringApp() throws IOException {
        ClassPathResource cpr = new ClassPathResource("bad-spring-app.war");
        File file = cpr.getFile();
        assertTrue("Expected test app at " + file.getCanonicalPath(), file.exists());
        return file;
    }

    /**
     * Returns a spring application using a file with a non-ascii name.
     *
     * @return the non-ascii-file-name WAR file
     * @throws IOException
     */
    public static File nonAsciFileName() throws IOException {
        ClassPathResource cpr = new ClassPathResource("non-ascii-file-name.war");
        File file = cpr.getFile();
        assertTrue("Expected test app at " + file.getCanonicalPath(), file.exists());
        return file;
    }

    /**
     * Returns a sample CF service broker app.
     *
     * @return the haash-broker JAR file
     * @throws IOException
     */
    public static File sampleServiceBrokerApp() throws IOException {
        ClassPathResource cpr = new ClassPathResource("haash-broker.jar");
        File file = cpr.getFile();
        assertTrue("Expected test app at " + file.getCanonicalPath(), file.exists());
        return file;
    }

    /**
     * Returns a simple spring application.
     *
     * @return the simple-spring-app WAR file
     * @throws IOException
     */
    public static File simpleSpringApp() throws IOException {
        ClassPathResource cpr = new ClassPathResource("simple-spring-app.war");
        File file = cpr.getFile();
        assertTrue("Expected test app at " + file.getCanonicalPath(), file.exists());
        return file;
    }

    /**
     * Returns the spring travel MVC reference application.
     *
     * @return the spring travel reference WAR file
     * @throws IOException
     */
    public static File springTravel() throws IOException {
        File file = new File(TEST_APP_DIR + "/travelapp/swf-booking-mvc.war");
        assertTrue("Expected test app at " + file.getCanonicalPath(), file.exists());
        return file;
    }

    /**
     * Returns the spring travel MVC reference application in an exploded directory.
     *
     * @param temporaryFolder a {@link TemporaryFolder} {@link Rule} used to create folders
     * @return the spring travel reference exploded files
     * @throws IOException
     */
    public static File springTravelUnpacked(TemporaryFolder temporaryFolder) throws IOException {
        return explodeTestApp(springTravel(), temporaryFolder);
    }

    /**
     * @return The directory containign a simple standalone Node app
     * @throws IOException
     */
    public static File standaloneNode() throws IOException {
        File file = new File(TEST_APP_DIR + "/standalone-node-app");
        assertTrue("Expected test app at " + file.getCanonicalPath(), file.exists());
        return file;
    }

    /**
     * @return The directory containing a simple standalone Ruby script
     * @throws IOException
     */
    public static File standaloneRuby() throws IOException {
        File file = new File(TEST_APP_DIR + "/standalone-ruby-app");
        assertTrue("Expected test app at " + file.getCanonicalPath(), file.exists());
        return file;
    }

    private static void addExampleGitRepo(File directory) throws IOException {
        // You can't add a Git repository to a Git repo so we have to create
        // files that look like a repo on the fly.
        File repo = new File(directory, ".git");
        repo.mkdir();
        new File(repo, "HEAD").createNewFile();
        new File(repo, "branches").mkdir();
        new File(repo, "config").createNewFile();
        new File(repo, "description").createNewFile();
        new File(repo, "hooks").mkdir();
        new File(repo, "index").createNewFile();
        new File(repo, "info").mkdir();
        new File(repo, "logs").mkdir();
        new File(repo, "objects").mkdir();
        new File(repo, "packed-refs").createNewFile();
        new File(repo, "refs").mkdir();
    }

    private static File copyAppToTempDir(TemporaryFolder temporaryFolder,
                                         File file) throws IOException {
        File tmpDir = temporaryFolder.newFolder(file.getName());
        if (tmpDir.exists()) {
            FileUtils.forceDelete(tmpDir);
        }
        tmpDir.mkdirs();
        FileUtils.copyDirectory(file, tmpDir);
        return tmpDir;
    }

    private static File explodeTestApp(File file, TemporaryFolder temporaryFolder) throws IOException {
        File unpackDir = temporaryFolder.newFolder(file.getName());
        if (unpackDir.exists()) {
            FileUtils.forceDelete(unpackDir);
        }
        unpackDir.mkdir();
        ZipFile zipFile = new ZipFile(file);
        try {
            unpackZip(zipFile, unpackDir);
        } finally {
            zipFile.close();
        }
        return unpackDir;
    }

    private static void unpackZip(ZipFile zipFile, File unpackDir) throws IOException {
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            File destination = new File(unpackDir.getAbsolutePath() + "/" + entry.getName());
            if (entry.isDirectory()) {
                destination.mkdirs();
            } else {
                destination.getParentFile().mkdirs();
                FileCopyUtils.copy(zipFile.getInputStream(entry), new FileOutputStream(destination));
            }
            if (entry.getTime() != -1) {
                destination.setLastModified(entry.getTime());
            }
        }
    }
}
