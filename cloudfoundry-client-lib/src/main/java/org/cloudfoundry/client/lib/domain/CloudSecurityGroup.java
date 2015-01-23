/*
 * Copyright 2015 the original author or authors.
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
import java.util.List;

/**
 * Application security groups act as virtual firewalls to control outbound
 * traffic from the applications in your deployment. A security group consists
 * of a list of network egress access rules.
 * <p/>
 * An administrator can assign one or more security groups to a Cloud Foundry
 * deployment or to a specific space in an org within a deployment.
 * 
 * @author David Ehringer
 * @see http://docs.cloudfoundry.org/adminguide/app-sec-groups.html
 */
public class CloudSecurityGroup extends CloudEntity {

	private final boolean runningDefault;
	private final boolean stagingDefault;
	private final List<SecurityGroupRule> rules = new ArrayList<SecurityGroupRule>();

	public CloudSecurityGroup(String name, List<SecurityGroupRule> rules) {
		this(CloudEntity.Meta.defaultMeta(), name, rules);
	}

	public CloudSecurityGroup(Meta meta, String name,
			List<SecurityGroupRule> rules) {
		this(meta, name, rules, false, false);
	}

	public CloudSecurityGroup(String name, List<SecurityGroupRule> rules,
			boolean runningDefault, boolean stagingDefault) {
		this(CloudEntity.Meta.defaultMeta(), name, rules, runningDefault,
				stagingDefault);
	}

	public CloudSecurityGroup(Meta meta, String name,
			List<SecurityGroupRule> rules, boolean runningDefault,
			boolean stagingDefault) {
		super(meta, name);
		this.rules.addAll(rules);
		this.runningDefault = runningDefault;
		this.stagingDefault = stagingDefault;
	}

	public List<SecurityGroupRule> getRules() {
		return rules;
	}

	public boolean isRunningDefault() {
		return runningDefault;
	}

	public boolean isStagingDefault() {
		return stagingDefault;
	}

}
