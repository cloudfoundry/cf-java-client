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

package org.cloudfoundry.client.v2;

import org.cloudfoundry.AbstractIntegrationTest;
import org.cloudfoundry.client.CloudFoundryClient;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.fail;

public final class SecurityGroupsTest extends AbstractIntegrationTest {

    @Autowired
    private CloudFoundryClient cloudFoundryClient;

    @Ignore("TODO: awaiting https://www.pivotaltracker.com/story/show/101522654 et al")
    @Test
    public void associateSpace() {
        fail("TODO: finish story https://www.pivotaltracker.com/story/show/101522654");
    }

    @Ignore("TODO: awaiting https://www.pivotaltracker.com/story/show/101522656 et al")
    @Test
    public void create() {
        fail("TODO: finish story https://www.pivotaltracker.com/story/show/101522656");
    }

    @Ignore("TODO: awaiting https://www.pivotaltracker.com/story/show/101522658 et al")
    @Test
    public void delete() {
        fail("TODO: finish story https://www.pivotaltracker.com/story/show/101522658");
    }

    @Ignore("TODO: awaiting https://www.pivotaltracker.com/story/show/101522664 et al")
    @Test
    public void deleteSpace() {
        fail("TODO: finish story https://www.pivotaltracker.com/story/show/101522664");
    }

    @Ignore("TODO: awaiting https://www.pivotaltracker.com/story/show/101522666 et al")
    @Test
    public void get() {
        fail("TODO: finish story https://www.pivotaltracker.com/story/show/101522666");
    }

    @Ignore("TODO: awaiting https://www.pivotaltracker.com/story/show/101522660 et al")
    @Test
    public void list() {
        fail("TODO: finish story https://www.pivotaltracker.com/story/show/101522660");
    }

    @Ignore("TODO: awaiting https://www.pivotaltracker.com/story/show/101522656 et al")
    @Test
    public void listRunningDefaults() {
        fail("TODO: finish story https://www.pivotaltracker.com/story/show/101522656 et al");
    }

    @Ignore("TODO: awaiting https://www.pivotaltracker.com/story/show/101522662 et al")
    @Test
    public void listSpaces() {
        fail("TODO: finish story https://www.pivotaltracker.com/story/show/101522662");
    }

    @Ignore("TODO: awaiting https://www.pivotaltracker.com/story/show/101522656 et al")
    @Test
    public void listStagingDefaults() {
        fail("TODO: finish story https://www.pivotaltracker.com/story/show/101522650 et al");
    }

    @Ignore("TODO: awaiting https://www.pivotaltracker.com/story/show/101522652")
    @Test
    public void setRunningDefault() {
        fail("TODO: awaiting https://www.pivotaltracker.com/story/show/101522652");
    }

    @Ignore("TODO: awaiting https://www.pivotaltracker.com/story/show/101522652")
    @Test
    public void setStagingDefault() {
        fail("TODO: awaiting https://www.pivotaltracker.com/story/show/101522652");
    }

    @Ignore("TODO: awaiting https://www.pivotaltracker.com/story/show/101522656 et al")
    @Test
    public void unsetRunningDefault() {
        fail("TODO: awaiting https://www.pivotaltracker.com/story/show/101522656 et al");
    }

    @Ignore("TODO: awaiting https://www.pivotaltracker.com/story/show/101522644")
    @Test
    public void unsetStagingDefault() {
        fail("TODO: awaiting https://www.pivotaltracker.com/story/show/101522644");
    }

    @Ignore("TODO: awaiting https://www.pivotaltracker.com/story/show/101522668 et al")
    @Test
    public void update() {
        fail("TODO: finish story https://www.pivotaltracker.com/story/show/101522668");
    }

}
