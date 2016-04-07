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
import org.cloudfoundry.client.v2.organizationquotadefinitions.ListOrganizationQuotaDefinitionsRequest;
import org.cloudfoundry.util.PaginationUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.fail;

public final class OrganizationQuotaDefnitionsTest extends AbstractIntegrationTest {

    @Autowired
    private CloudFoundryClient cloudFoundryClient;

    @Ignore("TODO: finish story https://www.pivotaltracker.com/projects/816799/stories/101527322")
    @Test
    public void create() {
        fail("TODO: finish story https://www.pivotaltracker.com/projects/816799/stories/101527322");
    }

    @Ignore("TODO: finish story https://www.pivotaltracker.com/projects/816799/stories/101527324")
    @Test
    public void delete() {
        fail("TODO: finish story https://www.pivotaltracker.com/projects/816799/stories/101527324");
    }

    @Ignore("TODO: finish story https://www.pivotaltracker.com/projects/816799/stories/101527322")
    @Test
    public void get() {
        fail("TODO: finish story https://www.pivotaltracker.com/projects/816799/stories/101527322");
    }

    @Test
    public void list() {

        PaginationUtils.requestResources(page -> this.cloudFoundryClient.organizationQuotaDefinitions()
            .list(ListOrganizationQuotaDefinitionsRequest.builder()
                .page(page)
                .build()))
            .after()
            .subscribe(this.testSubscriber());
    }

    @Ignore("TODO: finish story https://www.pivotaltracker.com/projects/816799/stories/101527330")
    @Test
    public void update() {
        fail("TODO: finish story https://www.pivotaltracker.com/projects/816799/stories/101527330");
    }

}
