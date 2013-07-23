/*
 * Copyright 2009-2012 the original author or authors.
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

package org.cloudfoundry.client.lib.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;

import static org.cloudfoundry.client.lib.util.CloudUtil.parse;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, creatorVisibility = Visibility.NONE)
public class CloudApplication extends CloudEntity {

    private static final String V1_FRAMEWORK_KEY = "model";
    private static final String V1_RUNTIME_KEY = "stack";
    private static final String V2_FRAMEWORK_KEY = "framework";
    private static final String V2_RUNTIME_KEY = "runtime";
    private static final String COMMAND_KEY = "command";
    private static final String MEMORY_KEY = "memory";

    public static final String JAVA_WEB = "java_web/1.0";
    public static final String SPRING = "spring_web/1.0";
    public static final String GRAILS = "grails/1.0";
    public static final String STANDALONE = "standalone";

	private Staging staging;
	private int instances;
	private List<String> uris;
	private List<String> services;
	private String plan;
	private AppState state;
	private DebugMode debug;
	private Map<String, Integer> resources = new HashMap<String, Integer>();
	private int runningInstances;
	private List<String> env = new ArrayList<String>();
    private String buildpackUrl;

    // Constructor for V2 entities
	public CloudApplication(Meta meta, String name) {
		super(meta, name);
	}

	public CloudApplication(String name, String stagingStack, String stagingModel,
						int memory, int instances,
						List<String> uris, List<String> serviceNames,
						AppState state) {
		super(CloudEntity.Meta.defaultMeta(), name);
		this.staging = new Staging(stagingStack, stagingModel);
		this.resources.put(MEMORY_KEY, memory);
		this.instances = instances;
		this.uris = uris;
		this.services = serviceNames;
		this.state = state;
	}

	@SuppressWarnings("unchecked")
	public CloudApplication(Map<String, Object> attributes) {
		super(CloudEntity.Meta.defaultMeta(), parse(attributes.get("name")));
		Map<String, String> stagingMap = (Map<String, String>) attributes.get("staging");
		if (stagingMap != null) {
			if (stagingMap.containsKey(V1_RUNTIME_KEY) && stagingMap.containsKey(V1_FRAMEWORK_KEY)) {
				setStaging(new Staging(stagingMap.get(V1_RUNTIME_KEY), stagingMap.get(V1_FRAMEWORK_KEY)));
			} else if (stagingMap.containsKey(V2_RUNTIME_KEY) && stagingMap.containsKey(V2_FRAMEWORK_KEY)) {
				setStaging(new Staging(stagingMap.get(V2_RUNTIME_KEY), stagingMap.get(V2_FRAMEWORK_KEY)));
			} else {
				setStaging(new Staging(null, null));
			}
		}
		instances = (Integer)attributes.get("instances");
		Integer runningInstancesAttribute = (Integer) attributes.get("runningInstances");
		if (runningInstancesAttribute != null) {
			runningInstances = runningInstancesAttribute;
		}
		uris = (List<String>)attributes.get("uris");
		services = (List<String>)attributes.get("services");
		state = AppState.valueOf((String) attributes.get("state"));
		resources = (Map<String, Integer>) attributes.get("resources");
		env = (List<String>) attributes.get("env");

		Map<String, Object> metaValue = parse(Map.class,
				attributes.get("meta"));
		if (metaValue != null) {
			String debugAttribute = (String) metaValue.get("debug");
			if (debugAttribute != null) {
				debug = DebugMode.valueOf(debugAttribute);
			}
			long created = parse(Long.class, metaValue.get("created"));
			Meta meta = null;
			if (created != 0) {
				meta = new Meta(null, new Date(created * 1000), null);
			}
			else {
				meta = new Meta(null, null, null);
			}
			setMeta(meta);
			if (metaValue.containsKey(COMMAND_KEY)) {
				setCommand((String) metaValue.get(COMMAND_KEY));
			}
		}
	}

    public String getBuildpackUrl() {
        return buildpackUrl;
    }

    public void setBuildpackUrl(String buildpackUrl) {
        this.buildpackUrl = buildpackUrl;
	}

	public enum AppState {
		UPDATING, STARTED, STOPPED
	}

	public enum DebugMode {
		run,
		suspend
	}

	public Staging getStaging() {
		return staging;
	}

	public void setStaging(Staging staging) {
		this.staging = staging;
	}

	public void setResources(Map<String,Integer> resources) {
		this.resources = resources;
	}

	public Map<String,Integer> getResources() {
		return new HashMap<String, Integer>(resources);
	}

	public int getInstances() {
		return instances;
	}

	public void setInstances(int instances) {
		this.instances = instances;
	}

	public int getMemory() {
		return resources.get(MEMORY_KEY);
	}

	public void setMemory(int memory) {
		resources.put(MEMORY_KEY, memory);
	}

	public List<String> getUris() {
		return uris;
	}

	public void setUris(List<String> uris) {
		this.uris = uris;
	}

	public String getPlan() {
		return plan;
	}

	public void setPlan(String plan) {
		this.plan = plan;
	}

	public AppState getState() {
		return state;
	}

	public void setState(AppState state) {
		this.state = state;
	}

	public DebugMode getDebug() {
		return debug;
	}

	public void setDebug(DebugMode debug) {
		this.debug = debug;
	}

	public List<String> getServices() {
		return services;
	}

	public void setServices(List<String> services) {
		this.services = services;
	}

	public int getRunningInstances() {
		return runningInstances;
	}

	public void setRunningInstances(int runningInstances) {
		this.runningInstances = runningInstances;
	}

	public Map<String, String> getEnvAsMap() {
		Map<String,String> envMap = new HashMap<String, String>();
		for (String nameAndValue : env) {
			String[] parts = nameAndValue.split("=");
			envMap.put(parts[0], parts[1]);
		}
		return envMap;
	}

	public List<String> getEnv() {
		return env;
	}

	public void setEnv(Map<String, String> env) {
		List<String> joined = new ArrayList<String>();
		for (Map.Entry<String, String> entry : env.entrySet()) {
			joined.add(entry.getKey() + '=' + entry.getValue());
		}
		this.env = joined;
	}

	public void setEnv(List<String> env) {
		for (String s : env) {
			if (!s.contains("=")) {
				throw new IllegalArgumentException("Environment setting without '=' is invalid: " + s);
			}
		}
		this.env = env;
	}

	public void setCommand(String command) {
		this.staging.setCommand(command);
	}

	@Override
	public String toString() {
		return "CloudApplication [framework=" + staging.getFramework() + ", instances="
				+ instances + ", name=" + getName() + ", runtime=" + staging.getRuntime()
				+ ", memory=" + resources.get(MEMORY_KEY)
				+ ", state=" + state + ", debug=" + debug + ", uris=" + uris + ",services=" + services
				+ ", env=" + env + "]";
	}
}
