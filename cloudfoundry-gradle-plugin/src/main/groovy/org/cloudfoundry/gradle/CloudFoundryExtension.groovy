/*
 * Copyright 2012 the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cloudfoundry.gradle

import org.gradle.api.Project

/**
 * Configuration for the Cloud Foundry plugin.
 * 
 * @author Cedric Champeau
 * @author Scott Frederick
 */
class CloudFoundryExtension {
    // primary config options
    String target = 'http://api.run.pivotal.io'
    String organization
    String space
    String username
    String password
    
    // application configuration
    String application
    String command
    String buildpack
    boolean startApp = true
    int memory = 512
    int instances = 1
    String uri
    List<String> uris = []
    File file
    Map<String, String> env = [:]

    boolean useSystemProxy = true

    CloudFoundryExtension(Project project) {
        application = project.name
    }

    public List<String> getAllUris() {
        def allUris = uris.collect { it.toString() }
        if (uri) {
            allUris << uri.toString()
        }
        allUris as List<String>
    }

    protected String getRandomWord() {
        def generator = { String alphabet, int n ->
            new Random().with {
                (1..n).collect { alphabet[nextInt(alphabet.length())] }.join()
            }
        }

        generator((('a'..'z') + ('0'..'9')).join(), 5)
    }
}
