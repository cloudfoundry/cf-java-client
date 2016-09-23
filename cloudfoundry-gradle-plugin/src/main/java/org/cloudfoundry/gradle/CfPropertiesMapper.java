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

package org.cloudfoundry.gradle;

import org.gradle.api.Project;

import java.util.Optional;

/**
 * Responsible for mapping plugin extension provided via a block
 * which looks like this:
 * <pre>
 *     cfConfig {
 *       ccHost = "api.local.pcfdev.io"
 *       ccUser = "admin"
 *       ccPassword = "admin"
 *       org = "pcfdev-org"
 *       space = "pcfdev-space"
 *       ....
 *       }
 * </pre>
 * OR overridden via command line flags say:
 * <pre>
 *    ./gradlew cf-push -Pcf.name=newname
 * </pre>
 *
 * and providing a structure with the final values
 *
 * @author Biju Kunjummen
 */
public class CfPropertiesMapper {

    private final Project project;

    public CfPropertiesMapper(Project project) {
        this.project = project;
    }

    public CfProperties getProperties() {
        return CfProperties.builder()
            .name(getCfApplicationName())
            .ccHost(getCcHost())
            .ccUser(getCcUser())
            .ccPassword(getCcPassword())
            .org(getOrg())
            .space(getSpace())
            .filePath(getFilePath())
            .hostName(getAppHostName())
            .domain(getAppDomain())
            .path(getCfPath())
            .state(this.getExtension().getState())
            .buildpack(this.getBuildpack())
            .command(this.getCommand())
            .console(this.getExtension().getConsole())
            .debug(this.getExtension().getDebug())
            .detectedStartCommand(this.getExtension().getDetectedStartCommand())
            .diskQuota(this.getDiskQuota())
            .enableSsh(this.getExtension().getEnableSsh())
            .environment(this.getExtension().getEnvironment())
            .timeout(this.getTimeout())
            .healthCheckType(this.getExtension().getHealthCheckType())
            .instances(this.getInstances())
            .memory(this.getMemory())
            .ports(this.getExtension().getPorts())
            .services(this.getExtension().getServices())
            .stagingTimeout(this.getStagingTimeout())
            .startupTimeout(this.getStartupTimeout())
            .build();
    }

    CfPluginExtension getExtension() {
        return this.project.getExtensions().findByType(CfPluginExtension.class);
    }


    public String getCfApplicationName() {
        return getStringPropertyFromProject(PropertyNameConstants.CF_APPLICATION_NAME)
            .orElse(this.getExtension().getName());
    }

    public String getNewName() {
        return getStringPropertyFromProject(PropertyNameConstants.CF_APPLICATION_NEW_NAME)
            .orElse(null);
    }

    public String getCommand() {
        return this.getExtension().getCommand();
    }

    public String getAppHostName() {
        return getStringPropertyFromProject(PropertyNameConstants.CF_APPLICATION_HOST_NAME)
            .orElse(this.getExtension().getHostName());
    }

    public String getAppDomain() {
        return getStringPropertyFromProject(PropertyNameConstants.CF_APPLICATION_DOMAIN)
            .orElse(this.getExtension().getDomain());
    }

    public String getFilePath() {
        return getStringPropertyFromProject(PropertyNameConstants.CF_FILE_PATH)
            .orElse(this.getExtension().getFilePath());
    }

    public String getCcHost() {
        return getStringPropertyFromProject(PropertyNameConstants.CC_HOST)
            .orElse(this.getExtension().getCcHost());
    }

    public String getCcUser() {
        return getStringPropertyFromProject(PropertyNameConstants.CC_USER)
            .orElse(this.getExtension().getCcUser());
    }

    public String getCcPassword() {
        return getStringPropertyFromProject(PropertyNameConstants.CC_PASSWORD)
            .orElse(this.getExtension().getCcPassword());
    }

    public String getBuildpack() {
        return getStringPropertyFromProject(PropertyNameConstants.CF_BUILDPACK)
            .orElse(this.getExtension().getBuildpack());
    }

    public String getOrg() {
        return getStringPropertyFromProject(PropertyNameConstants.CF_ORG)
            .orElse(this.getExtension().getOrg());
    }

    public String getSpace() {
        return getStringPropertyFromProject(PropertyNameConstants.CF_SPACE)
            .orElse(this.getExtension().getSpace());
    }

    public String getCfPath() {
        return getStringPropertyFromProject(PropertyNameConstants.CF_PATH)
            .orElse(this.getExtension().getPath());
    }

    public Integer getInstances() {
        return getIntegerPropertyFromProject(PropertyNameConstants.CF_INSTANCES)
            .orElse(this.getExtension().getInstances());
    }

    public Integer getMemory() {
        return getIntegerPropertyFromProject(PropertyNameConstants.CF_MEMORY)
            .orElse(this.getExtension().getMemory());
    }

    public Integer getTimeout() {
        return getIntegerPropertyFromProject(PropertyNameConstants.CF_HEALTH_CHECK_TIMEOUT)
            .orElse(this.getExtension().getTimeout());
    }

    public Integer getDiskQuota() {
        return getIntegerPropertyFromProject(PropertyNameConstants.CF_DISK_QUOTA)
            .orElse(this.getExtension().getDiskQuota());

    }

    public Integer getStagingTimeout() {
        Integer stagingTimeout = getIntegerPropertyFromProject(PropertyNameConstants.CF_STAGING_TIMEOUT)
            .orElse(this.getExtension().getStagingTimeout());

        return stagingTimeout!=null?stagingTimeout:DefaultProperties.STAGING_TIMEOUT;
    }

    public Integer getStartupTimeout() {
        Integer startupTimeout = getIntegerPropertyFromProject(PropertyNameConstants.CF_STARTUP_TIMEOUT)
            .orElse(this.getExtension().getStartupTimeout());

        return startupTimeout!=null?startupTimeout:DefaultProperties.STARTUP_TIMEOUT;
    }

    public Optional<String> getStringPropertyFromProject(String propertyName) {
        if (this.project.hasProperty(propertyName)) {
            return Optional.of((String) this.project.property(propertyName));
        }
        return Optional.empty();
    }


    /**
     * Get a property value from the Project properties.
     *
     * @param propertyName name of property
     * @return value of property, @{link java.util.Optional#empty} if not available.
     */
    public Optional<Integer> getIntegerPropertyFromProject(String propertyName) {
        if (this.project.hasProperty(propertyName)) {
            return Optional.of((Integer) this.project.property(propertyName));
        }
        return Optional.empty();
    }

}
