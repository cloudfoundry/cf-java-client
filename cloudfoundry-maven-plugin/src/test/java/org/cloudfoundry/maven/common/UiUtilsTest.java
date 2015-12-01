/*
 * Copyright 2009-2013 the original author or authors.
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

package org.cloudfoundry.maven.common;

import junit.framework.Assert;
import org.cloudfoundry.client.lib.domain.CloudApplication;
import org.cloudfoundry.client.lib.domain.CloudApplication.AppState;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Gunnar Hillert
 */
public class UiUtilsTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(UiUtilsTest.class);

    @Test
    public void testRenderTextTable() {
        final List<String> services = new ArrayList<String>();

        services.add("mysql");
        services.add("MyMongoInstance");

        final List<String> uris = new ArrayList<String>();

        uris.add("cf-rocks.api.run.pivotal.io");
        uris.add("spring-rocks.api.run.pivotal.io");

        String expectedTableAsString = null;

        try {
            expectedTableAsString = FileCopyUtils.copyToString(new InputStreamReader(
                    UiUtilsTest.class.getResourceAsStream("testRenderTextTable-expected-output.txt")));
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail();
        }

        Assert.assertNotNull(expectedTableAsString);

        final CloudApplication app1 = new CloudApplication("first", "command",
                "buildpack", 512, 1, uris, services, AppState.STARTED);
        final CloudApplication app2 = new CloudApplication("second", "command",
                "buildpack", 1024, 2, uris, services, AppState.STOPPED);

        final List<CloudApplication> applications = Arrays.asList(app1, app2);

        final String renderedTableAsString = UiUtils.renderCloudApplicationsDataAsTable(applications);

        LOGGER.info("\n" + renderedTableAsString);

        Assert.assertEquals(expectedTableAsString, renderedTableAsString);
    }

}
