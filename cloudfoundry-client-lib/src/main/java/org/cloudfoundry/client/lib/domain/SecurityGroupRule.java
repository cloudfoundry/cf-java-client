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

/**
 * A single rule within a security group. See <a href="http://docs.cloudfoundry.org/adminguide/app-sec-groups.html">
 * http://docs.cloudfoundry.org/adminguide/app-sec-groups.html</a> for more details.
 * 
 * @author David Ehringer
 * @see http://docs.cloudfoundry.org/adminguide/app-sec-groups.html
 */
public class SecurityGroupRule {

	private final String protocol;
	private final String ports;
	private final String destination;
	private final Boolean log;
	private final Integer type;
	private final Integer code;
	
	public SecurityGroupRule(String protocol, String ports, String destination) {
		this(protocol, ports, destination, null);
	}
	
	public SecurityGroupRule(String protocol, String ports, String destination, Boolean log) {
		this(protocol, ports, destination, log, null, null);
	}
	
	/**
	 * 
	 * @param protocol network protocol (tcp,icmp,udp,all)
	 * @param ports port or port range (applicable for tcp,udp,all), may be conditionally <code>null</code>
	 * @param destination destination CIDR or destination range
	 * @param log enables logging for the egress rule, may be <code>null</code>
	 * @param type control signal for icmp, may be <code>null</code>
	 * @param code control signal for icmp, may be <code>null</code>
	 */
	public SecurityGroupRule(String protocol, String ports, String destination, Boolean log, Integer type, Integer code) {
		this.protocol = protocol;
		this.ports = ports;
		this.destination = destination;
		this.log = log;
		this.type = type;
		this.code = code;
	}

	public String getProtocol() {
		return protocol;
	}
	
	public String getPorts() {
		return ports;
	}
	
	public String getDestination() {
		return destination;
	}

	public Boolean getLog() {
		return log;
	}

	public Integer getType() {
		return type;
	}

	public Integer getCode() {
		return code;
	}
	
}
