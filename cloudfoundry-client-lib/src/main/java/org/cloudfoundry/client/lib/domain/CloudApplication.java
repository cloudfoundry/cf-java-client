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

import static org.cloudfoundry.client.lib.util.CloudUtil.parse;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, creatorVisibility = Visibility.NONE)
public class CloudApplication extends CloudEntity {

	private static final String COMMAND_KEY = "command";
	private static final String BUILDPACK_URL_KEY = "buildpack";
	private static final String DETECTED_BUILDPACK_KEY = "detected_buildpack";
	private static final String MEMORY_KEY = "memory";
	private static final String DISK_KEY = "disk_quota";

	private CloudSpace space;
	private Staging staging;
	private int instances;
	private int memory;
	private int diskQuota;
	private List<String> uris;
	private List<String> services;
	private AppState state;
	private DebugMode debug;
	private int runningInstances;
	private List<String> env = new ArrayList<String>();

	public CloudApplication(Meta meta, String name) {
		super(meta, name);
	}

	public CloudApplication(String name, String command, String buildpackUrl, int memory, int instances,
						List<String> uris, List<String> serviceNames,
						AppState state) {
		super(CloudEntity.Meta.defaultMeta(), name);
		this.staging = new Staging(command, buildpackUrl);
		this.memory = memory;
		this.instances = instances;
		this.uris = uris;
		this.services = serviceNames;
		this.state = state;
	}

	@SuppressWarnings("unchecked")
	public CloudApplication(Map<String, Object> attributes) {
		super(CloudEntity.Meta.defaultMeta(), parse(attributes.get("name")));
		instances = (Integer)attributes.get("instances");
		Integer runningInstancesAttribute = (Integer) attributes.get("runningInstances");
		if (runningInstancesAttribute != null) {
			runningInstances = runningInstancesAttribute;
		}
		uris = (List<String>)attributes.get("uris");
		services = (List<String>)attributes.get("services");
		state = AppState.valueOf((String) attributes.get("state"));
		if (attributes.containsKey("memory")) {
			memory = (Integer) attributes.get("memory");
		}
		if (attributes.containsKey("disk_quota")) {
			diskQuota = (Integer) attributes.get("disk_quota");
		}
		env = (List<String>) attributes.get("env");

		Map<String, Object> metaValue = parse(Map.class,
				attributes.get("meta"));
		if (metaValue != null) {
			String debugAttribute = (String) metaValue.get("debug");
			if (debugAttribute != null) {
				debug = DebugMode.valueOf(debugAttribute);
			}
			long created = parse(Long.class, metaValue.get("created"));
			Date createdDate = created != 0 ? new Date(created * 1000) : null;
			setMeta(new Meta(null, createdDate, null));

			String command = null;
			if (metaValue.containsKey(COMMAND_KEY)) {
				command = (String) metaValue.get(COMMAND_KEY);
			}
			String buildpackUrl = null;
			if (metaValue.containsKey(BUILDPACK_URL_KEY)) {
				buildpackUrl = (String) metaValue.get(BUILDPACK_URL_KEY);
			}
			String detectedBuildpack = null;
			if (metaValue.containsKey(DETECTED_BUILDPACK_KEY)) {
				detectedBuildpack = (String) metaValue.get(DETECTED_BUILDPACK_KEY);
			}
			
			setStaging(new Staging(command, buildpackUrl, detectedBuildpack));
		}
	}

	public CloudSpace getSpace() {
		return space;
	}

	public void setSpace(CloudSpace space) {
		this.space = space;
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

	// for backward compatibility
	public Map<String,Integer> getResources() {
		Map<String, Integer> resources = new HashMap<String, Integer>();
		resources.put(MEMORY_KEY, memory);
		resources.put(DISK_KEY, diskQuota);
		return resources;
	}

	public int getInstances() {
		return instances;
	}

	public void setInstances(int instances) {
		this.instances = instances;
	}

	public int getDiskQuota() {
		return diskQuota;
	}

	public void setDiskQuota(int diskQuota) {
		this.diskQuota = diskQuota;
	}

	public int getMemory() {
		return memory;
	}

	public void setMemory(int memory) {
		this.memory = memory;
	}

	public List<String> getUris() {
		return uris;
	}

	public void setUris(List<String> uris) {
		this.uris = uris;
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
		Map<String, String> envMap = new HashMap<String, String>();
		for (String nameAndValue : env) {
			String[] parts = nameAndValue.split("=", 2);
			envMap.put(parts[0], parts.length == 2 && parts[1].length() > 0 ? parts[1] : null);
		}
		return envMap;
	}

	public List<String> getEnv() {
		return env;
	}

	public void setEnv(Map<Object, Object> env) {
		List<String> joined = new ArrayList<String>();
		for (Map.Entry<Object, Object> entry : env.entrySet()) {
			joined.add(entry.getKey().toString() + '=' + entry.getValue().toString());

			// skip this environment variable if the key is null
			if (null == entry.getKey()) {
				continue;
			}

			String value;
			// check that there is a value. If it is null, the value should be an empty string
			if(null == entry.getValue()) {
				value = "";
			} else {
				value = entry.getValue().toString();
			}

			joined.add(entry.getKey().toString() + '=' + value);
		}

		this.env = joined;
	}

	@Override
	public String toString() {
		return "CloudApplication [staging=" + staging + ", instances="
				+ instances + ", name=" + getName() 
				+ ", memory=" + memory + ", diskQuota=" + diskQuota
				+ ", state=" + state + ", debug=" + debug + ", uris=" + uris + ", services=" + services
				+ ", env=" + env + ", space=" + space.getName() + "]";
	}
}
