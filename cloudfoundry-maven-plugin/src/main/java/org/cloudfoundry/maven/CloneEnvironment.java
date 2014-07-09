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
package org.cloudfoundry.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;

/**
 * Sets all environment variables that are specified in the <env/> section of the configuration as environment variables
 * that are valid within the spawned Maven process only.
 *
 * @author Alexander Orlov
 * @goal clone-env
 * @since 1.0.4
 */
public class CloneEnvironment extends AbstractApplicationAwareCloudFoundryMojo {

    /**
     * A pragmatic way to modify environment variables as there is no simple cross-platform way to set env vars.
     *
     * @param additionalEnv environment variables from <env/> section of the plugin configuration
     */
    private void updateLocalEnvironment(Map<String, String> additionalEnv) {
        try {
            Class[] classes = Collections.class.getDeclaredClasses();
            Map<String, String> env = System.getenv();
            for (Class c : classes) {
                if ("java.util.Collections$UnmodifiableMap".equals(c.getName())) {
                    Field unmodifiableMap = c.getDeclaredField("m");
                    unmodifiableMap.setAccessible(true);
                    @SuppressWarnings("unchecked")
                    Map<String, String> updatableEnv = (Map<String, String>) unmodifiableMap.get(env);
                    updatableEnv.putAll(additionalEnv);
                }
            }
        } catch (Exception e) {
            getLog().error(e.getMessage());
        }
    }

    @Override
    protected void doExecute() throws MojoExecutionException, MojoFailureException {
        updateLocalEnvironment(getEnv());
    }
}
