/*
 * Copyright 2013-2017 the original author or authors.
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

public final class ServicesTest extends AbstractIntegrationTest {

    @Autowired
    private CloudFoundryClient cloudFoundryClient;

    //TODO: Await https://github.com/cloudfoundry/cf-java-client/issues/619
    @Ignore("Await https://github.com/cloudfoundry/cf-java-client/issues/619")
    @Test
    public void delete() {
        //
    }

    //TODO: Await https://github.com/cloudfoundry/cf-java-client/issues/619
    @Ignore("Await https://github.com/cloudfoundry/cf-java-client/issues/619")
    @Test
    public void deleteAsyncFalse() {
        //
    }

    //TODO: Await https://github.com/cloudfoundry/cf-java-client/issues/619
    @Ignore("Await https://github.com/cloudfoundry/cf-java-client/issues/619")
    @Test
    public void deletePurge() {
        //
    }

    //TODO: Await https://github.com/cloudfoundry/cf-java-client/issues/619
    @Ignore("Await https://github.com/cloudfoundry/cf-java-client/issues/619")
    @Test
    public void get() {
        //
    }

    //TODO: Await https://github.com/cloudfoundry/cf-java-client/issues/619
    @Ignore("Await https://github.com/cloudfoundry/cf-java-client/issues/619")
    @Test
    public void list() {
        //
    }

    //TODO: Await https://github.com/cloudfoundry/cf-java-client/issues/619
    @Ignore("Await https://github.com/cloudfoundry/cf-java-client/issues/619")
    @Test
    public void listFilterByActive() {
        //
    }

    //TODO: Await https://github.com/cloudfoundry/cf-java-client/issues/619
    @Ignore("Await https://github.com/cloudfoundry/cf-java-client/issues/619")
    @Test
    public void listFilterByLabels() {
        //
    }

    //TODO: Await https://github.com/cloudfoundry/cf-java-client/issues/619
    @Ignore("Await https://github.com/cloudfoundry/cf-java-client/issues/619")
    @Test
    public void listFilterByProviders() {
        //
    }

    //TODO: Await https://github.com/cloudfoundry/cf-java-client/issues/619
    @Ignore("Await https://github.com/cloudfoundry/cf-java-client/issues/619")
    @Test
    public void listFilterByServiceBrokerIds() {
        //
    }

    //TODO: Await https://github.com/cloudfoundry/cf-java-client/issues/619
    @Ignore("Await https://github.com/cloudfoundry/cf-java-client/issues/619")
    @Test
    public void listNoneFound() {
        //
    }

    //TODO: Await https://github.com/cloudfoundry/cf-java-client/issues/619
    @Ignore("Await https://github.com/cloudfoundry/cf-java-client/issues/619")
    @Test
    public void listServicePlans() {
        //
    }

    //TODO: Await https://github.com/cloudfoundry/cf-java-client/issues/619
    @Ignore("Await https://github.com/cloudfoundry/cf-java-client/issues/619")
    @Test
    public void listServicePlansFilterByActive() {
        //
    }

    //TODO: Await https://github.com/cloudfoundry/cf-java-client/issues/619
    @Ignore("Await https://github.com/cloudfoundry/cf-java-client/issues/619")
    @Test
    public void listServicePlansFilterByServiceBrokerIds() {
        //
    }

    //TODO: Await https://github.com/cloudfoundry/cf-java-client/issues/619
    @Ignore("Await https://github.com/cloudfoundry/cf-java-client/issues/619")
    @Test
    public void listServicePlansFilterByServiceInstanceIds() {
        //
    }

    //TODO: Await https://github.com/cloudfoundry/cf-java-client/issues/619
    @Ignore("Await https://github.com/cloudfoundry/cf-java-client/issues/619")
    @Test
    public void listServicePlansNoneFound() {
        //
    }

}
