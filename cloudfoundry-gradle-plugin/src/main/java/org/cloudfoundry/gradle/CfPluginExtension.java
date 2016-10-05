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

import org.cloudfoundry.operations.applications.ApplicationDetail;

import java.util.List;
import java.util.Map;

/**
 * A holder for properties set via a Gradle cfConfig closure
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
 *
 * @author Biju Kunjummen
 */
public class CfPluginExtension {

    private String ccHost;

    private String ccUser;

    private String ccPassword;

    private String org;

    private String space;

    private String name;

    private String filePath;

    private String hostName;

    private String domain;

    private String path;


    private String state;

    private String buildpack;

    private String command;

    private Boolean console;

    private Boolean debug;

    private String detectedStartCommand;

    private Integer diskQuota;

    private Boolean enableSsh;

    private Map<String, String> environment;

    private Integer timeout;

    private String healthCheckType;

    private Integer instances;

    private Integer memory;

    private List<Integer> ports;

    private List<String> services;

    private Integer stagingTimeout;

    private Integer startupTimeout;

    private ApplicationDetail applicationDetail;

    public String getCcHost() {
        return this.ccHost;
    }

    public void setCcHost(String ccHost) {
        this.ccHost = ccHost;
    }

    public String getCcUser() {
        return this.ccUser;
    }

    public void setCcUser(String ccUser) {
        this.ccUser = ccUser;
    }

    public String getCcPassword() {
        return this.ccPassword;
    }

    public void setCcPassword(String ccPassword) {
        this.ccPassword = ccPassword;
    }

    public String getOrg() {
        return this.org;
    }

    public void setOrg(String org) {
        this.org = org;
    }

    public String getSpace() {
        return this.space;
    }

    public void setSpace(String space) {
        this.space = space;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getHostName() {
        return this.hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getDomain() {
        return this.domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getState() {
        return this.state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getBuildpack() {
        return this.buildpack;
    }

    public void setBuildpack(String buildpack) {
        this.buildpack = buildpack;
    }

    public String getCommand() {
        return this.command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public Boolean getConsole() {
        return this.console;
    }

    public void setConsole(Boolean console) {
        this.console = console;
    }

    public Boolean getDebug() {
        return this.debug;
    }

    public void setDebug(Boolean debug) {
        this.debug = debug;
    }

    public String getDetectedStartCommand() {
        return this.detectedStartCommand;
    }

    public void setDetectedStartCommand(String detectedStartCommand) {
        this.detectedStartCommand = detectedStartCommand;
    }

    public Integer getDiskQuota() {
        return this.diskQuota;
    }

    public void setDiskQuota(Integer diskQuota) {
        this.diskQuota = diskQuota;
    }

    public Boolean getEnableSsh() {
        return this.enableSsh;
    }

    public void setEnableSsh(Boolean enableSsh) {
        this.enableSsh = enableSsh;
    }

    public Map<String, String> getEnvironment() {
        return this.environment;
    }

    public void setEnvironment(Map<String, String> environment) {
        this.environment = environment;
    }

    public Integer getTimeout() {
        return this.timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public String getHealthCheckType() {
        return this.healthCheckType;
    }

    public void setHealthCheckType(String healthCheckType) {
        this.healthCheckType = healthCheckType;
    }

    public Integer getInstances() {
        return this.instances;
    }

    public void setInstances(Integer instances) {
        this.instances = instances;
    }

    public Integer getMemory() {
        return this.memory;
    }

    public void setMemory(Integer memory) {
        this.memory = memory;
    }

    public List<Integer> getPorts() {
        return this.ports;
    }

    public void setPorts(List<Integer> ports) {
        this.ports = ports;
    }

    public List<String> getServices() {
        return this.services;
    }

    public void setServices(List<String> services) {
        this.services = services;
    }

    public Integer getStagingTimeout() {
        return this.stagingTimeout;
    }

    public void setStagingTimeout(Integer stagingTimeout) {
        this.stagingTimeout = stagingTimeout;
    }

    public Integer getStartupTimeout() {
        return this.startupTimeout;
    }

    public void setStartupTimeout(Integer startupTimeout) {
        this.startupTimeout = startupTimeout;
    }

    public ApplicationDetail getApplicationDetail() {
        return this.applicationDetail;
    }

    public void setApplicationDetail(ApplicationDetail applicationDetail) {
        this.applicationDetail = applicationDetail;
    }
}