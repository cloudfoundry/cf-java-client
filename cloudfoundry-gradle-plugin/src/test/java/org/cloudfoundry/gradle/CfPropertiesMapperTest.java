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
import org.gradle.api.plugins.ExtensionContainer;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CfPropertiesMapperTest {

    private CfPropertiesMapper cfPropertiesMapper;

    private Project project;

    private CfPluginExtension pluginExtension;

    @Before
    public void setUp() {
        this.project = mock(Project.class);
        ExtensionContainer extensionContainer = mock(ExtensionContainer.class);
        this.pluginExtension = sampleExtension();
        when(extensionContainer.findByType(CfPluginExtension.class)).thenReturn(this.pluginExtension);
        when(this.project.getExtensions()).thenReturn(extensionContainer);
        this.cfPropertiesMapper = new CfPropertiesMapper(this.project);
    }

    @Test
    public void testThatPropertiesAreRetrievedWhenSetViaExtensionContainer() {
        CfProperties props = this.cfPropertiesMapper.getProperties();
        assertThat(props.name()).isEqualTo("name-fromplugin");
        assertThat(props.ccHost()).isEqualTo("cchost-fromplugin");
        assertThat(props.ccPassword()).isEqualTo("ccpassword-fromplugin");
        assertThat(props.buildpack()).isEqualTo("buildpack-fromplugin");
        assertThat(props.org()).isEqualTo("org-fromplugin");
        assertThat(props.space()).isEqualTo("space-fromplugin");
        assertThat(props.ccUser()).isEqualTo("ccuser-fromplugin");
        assertThat(props.filePath()).isEqualTo("filepath-fromplugin");
        assertThat(props.hostName()).isEqualTo("hostname-fromplugin");
        assertThat(props.domain()).isEqualTo("domain-fromplugin");
        assertThat(props.path()).isEqualTo("path-fromplugin");
        assertThat(props.state()).isEqualTo("state-fromplugin");
        assertThat(props.command()).isEqualTo("command-fromplugin");
        assertThat(props.console()).isFalse();
        assertThat(props.detectedStartCommand()).isEqualTo("detectedcommand-fromplugin");

        assertThat(props.diskQuota()).isEqualTo(1000);
        assertThat(props.enableSsh()).isTrue();
        assertThat(props.environment()).containsKeys("env1", "env2").containsValues("env1value", "env2value");

        assertThat(props.timeout()).isEqualTo(150);
        assertThat(props.healthCheckType()).isEqualTo("healthchecktype-fromplugin");
        assertThat(props.instances()).isEqualTo(5);
        assertThat(props.memory()).isEqualTo(2000);
        assertThat(props.ports()).contains(8080, 8081);

        assertThat(props.services()).contains("service1", "service2");
        assertThat(props.stagingTimeout()).isEqualTo(101);
        assertThat(props.startupTimeout()).isEqualTo(102);
    }

    @Test
    public void testThatNameIsOverriddenViaProjectProperty() {
        setProjectProperty(PropertyNameConstants.CF_APPLICATION_NAME, "newname");
        CfProperties props = this.cfPropertiesMapper.getProperties();

        assertThat(props.name()).isEqualTo("newname");
    }

    @Test
    public void testHostNameIsOverriddenViaProjectProperty() {
        setProjectProperty(PropertyNameConstants.CF_APPLICATION_HOST_NAME, "newhost");
        CfProperties props = this.cfPropertiesMapper.getProperties();

        assertThat(props.hostName()).isEqualTo("newhost");
    }

    @Test
    public void testAppDomainIsOverriddenViaProjectProperty() {
        setProjectProperty(PropertyNameConstants.CF_APPLICATION_DOMAIN, "newdomain");
        CfProperties props = this.cfPropertiesMapper.getProperties();

        assertThat(props.domain()).isEqualTo("newdomain");
    }

    @Test
    public void testFilePathIsOverriddenViaProjectProperty() {
        setProjectProperty(PropertyNameConstants.CF_FILE_PATH, "newfilepath");
        CfProperties props = this.cfPropertiesMapper.getProperties();

        assertThat(props.filePath()).isEqualTo("newfilepath");
    }

    @Test
    public void testCloudControllerHostIsOverriddenViaProjectProperty() {
        setProjectProperty(PropertyNameConstants.CC_HOST, "newcchost");
        CfProperties props = this.cfPropertiesMapper.getProperties();

        assertThat(props.ccHost()).isEqualTo("newcchost");
    }

    @Test
    public void testCcUserIsOverriddenViaProjectProperty() {
        setProjectProperty(PropertyNameConstants.CC_USER, "newuser");
        CfProperties props = this.cfPropertiesMapper.getProperties();

        assertThat(props.ccUser()).isEqualTo("newuser");
    }

    @Test
    public void testCcPasswordIsOverriddenViaProjectProperty() {
        setProjectProperty(PropertyNameConstants.CC_PASSWORD, "newpwd");
        CfProperties props = this.cfPropertiesMapper.getProperties();

        assertThat(props.ccPassword()).isEqualTo("newpwd");
    }

    @Test
    public void testBuildpackIsOverriddenViaProjectProperty() {
        setProjectProperty(PropertyNameConstants.CF_BUILDPACK, "newbuildpack");
        CfProperties props = this.cfPropertiesMapper.getProperties();

        assertThat(props.buildpack()).isEqualTo("newbuildpack");
    }

    @Test
    public void testOrgIsOverriddenViaProjectProperty() {
        setProjectProperty(PropertyNameConstants.CF_ORG, "neworg");
        CfProperties props = this.cfPropertiesMapper.getProperties();

        assertThat(props.org()).isEqualTo("neworg");
    }

    @Test
    public void testSpaceIsOverriddenViaProjectProperty() {
        setProjectProperty(PropertyNameConstants.CF_SPACE, "newspace");
        CfProperties props = this.cfPropertiesMapper.getProperties();

        assertThat(props.space()).isEqualTo("newspace");
    }

    @Test
    public void testPathIsOverriddenViaProjectProperty() {
        setProjectProperty(PropertyNameConstants.CF_PATH, "newpath");
        CfProperties props = this.cfPropertiesMapper.getProperties();

        assertThat(props.path()).isEqualTo("newpath");
    }

    @Test
    public void testInstanceCountIsOverriddenViaProjectProperty() {
        setProjectProperty(PropertyNameConstants.CF_INSTANCES, 10);
        CfProperties props = this.cfPropertiesMapper.getProperties();

        assertThat(props.instances()).isEqualTo(10);
    }

    @Test(expected = Exception.class)
    public void testNonNumericInstanceCountShouldThrowAnException() {
        setProjectProperty(PropertyNameConstants.CF_INSTANCES, "invalid");
        this.cfPropertiesMapper.getProperties();
    }

    @Test
    public void testMemoryIsOverriddenViaProjectProperty() {
        setProjectProperty(PropertyNameConstants.CF_MEMORY, 100);
        CfProperties props = this.cfPropertiesMapper.getProperties();

        assertThat(props.memory()).isEqualTo(100);
    }

    @Test
    public void testTimeoutIsOverriddenViaProjectProperty() {
        setProjectProperty(PropertyNameConstants.CF_HEALTH_CHECK_TIMEOUT, 10);
        CfProperties props = this.cfPropertiesMapper.getProperties();

        assertThat(props.timeout()).isEqualTo(10);
    }

    @Test
    public void testDiskQuotaIsOverriddenViaProjectProperty() {
        setProjectProperty(PropertyNameConstants.CF_DISK_QUOTA, 101);
        CfProperties props = this.cfPropertiesMapper.getProperties();

        assertThat(props.diskQuota()).isEqualTo(101);
    }

    @Test
    public void testStagingTimeoutIsOverriddenViaProjectProperty() {
        setProjectProperty(PropertyNameConstants.CF_STAGING_TIMEOUT, 6);
        CfProperties props = this.cfPropertiesMapper.getProperties();

        assertThat(props.stagingTimeout()).isEqualTo(6);
    }

    @Test
    public void testDefaultsForStagingTimeoutIs15Minutes() {
        pluginExtension.setStagingTimeout(null);
        CfProperties props = this.cfPropertiesMapper.getProperties();

        assertThat(props.stagingTimeout()).isEqualTo(15);
    }

    @Test
    public void testDefaultsForStartupTimeoutIs5Minutes() {
        pluginExtension.setStartupTimeout(null);
        CfProperties props = this.cfPropertiesMapper.getProperties();

        assertThat(props.startupTimeout()).isEqualTo(5);
    }


    @Test
    public void testStartupTimeoutIsOverriddenViaProjectProperty() {
        setProjectProperty(PropertyNameConstants.CF_STARTUP_TIMEOUT, 7);
        CfProperties props = this.cfPropertiesMapper.getProperties();

        assertThat(props.startupTimeout()).isEqualTo(7);
    }


    private void setProjectProperty(String propertyName, Object propertyValue) {
        when(this.project.property(propertyName)).thenReturn(propertyValue);
        when(this.project.hasProperty(propertyName)).thenReturn(true);
    }


    private CfPluginExtension sampleExtension() {
        CfPluginExtension ext = new CfPluginExtension();
        ext.setName("name-fromplugin");
        ext.setCcUser("ccuser-fromplugin");
        ext.setCcHost("cchost-fromplugin");
        ext.setCcPassword("ccpassword-fromplugin");
        ext.setBuildpack("buildpack-fromplugin");
        ext.setOrg("org-fromplugin");
        ext.setSpace("space-fromplugin");
        ext.setHostName("route-fromplugin");
        ext.setFilePath("filepath-fromplugin");
        ext.setHostName("hostname-fromplugin");
        ext.setDomain("domain-fromplugin");
        ext.setPath("path-fromplugin");
        ext.setState("state-fromplugin");
        ext.setCommand("command-fromplugin");
        ext.setDetectedStartCommand("detectedcommand-fromplugin");
        ext.setEnableSsh(Boolean.TRUE);
        ext.setDiskQuota(1000);
        ext.setConsole(false);

        Map<String, String> envs = new HashMap<>();
        envs.put("env1", "env1value");
        envs.put("env2", "env2value");
        ext.setEnvironment(envs);

        ext.setTimeout(150);
        ext.setHealthCheckType("healthchecktype-fromplugin");
        ext.setInstances(5);

        ext.setMemory(2000);
        ext.setPorts(Arrays.asList(8080, 8081));

        ext.setServices(Arrays.asList("service1", "service2"));

        ext.setStagingTimeout(101);
        ext.setStartupTimeout(102);

        return ext;
    }


}
