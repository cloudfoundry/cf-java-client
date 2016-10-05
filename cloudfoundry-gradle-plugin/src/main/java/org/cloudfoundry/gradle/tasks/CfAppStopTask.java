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

package org.cloudfoundry.gradle.tasks;

import org.cloudfoundry.gradle.CfProperties;
import org.cloudfoundry.gradle.tasks.helper.CfAppStopDelegate;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.gradle.api.tasks.TaskAction;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Responsible for stopping an app
 *
 * @author Biju Kunjummen
 */
public class CfAppStopTask extends AbstractCfTask {

    private CfAppStopDelegate stopDelegate = new CfAppStopDelegate();

    @TaskAction
    public void stopApp() {

        CloudFoundryOperations cfOperations = getCfOperations();
        CfProperties cfAppProperties = getCfProperties();

        Mono<Void> resp = this.stopDelegate.stopApp(cfOperations, cfAppProperties);

        resp.block(Duration.ofMillis(defaultWaitTimeout));

    }

    @Override
    public String getDescription() {
        return "Stop an Application";
    }

}
