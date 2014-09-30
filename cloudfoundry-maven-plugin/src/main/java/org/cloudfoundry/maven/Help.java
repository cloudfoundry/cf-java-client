/*
 * Copyright 2009-2012 The Apache Software Foundation.
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

import java.io.*;
import java.util.Map;
import java.util.TreeMap;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.cloudfoundry.maven.common.CommonUtils;
import org.cloudfoundry.maven.common.UiUtils;

/**
 * Documentation for all available commands.
 *
 * @author Gunnar Hillert
 * @author Stephan Oudmaijer
 * @author Scott Frederick
 *
 * @since 1.0.0
 *
 * @goal help
 * @requiresProject true
 */
public class Help extends AbstractApplicationAwareCloudFoundryMojo {

	public static final String HELP_TEXT = "/help.txt";
	public static final String NOT_AVAILABLE = "N/A";

	/**
	 * 	@FIXME Not sure whether one should be able to overwrite execute()
	 *
	 *  The help goal does not require an interaction with Cloud Foundry. A
	 *  login is not necessary. Therefore, this method is overwritten.
	 *
	 */
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		doExecute();
	}

	/**
	 * @return
	 */
	private Map<String, String> getParameterMap() throws MojoExecutionException {
		final Map<String, String> parameterMap = new TreeMap<String, String>();

		parameterMap.put("appname", getAppname() != null ? getAppname() : NOT_AVAILABLE);
		parameterMap.put("command", getCommand() != null ? getCommand() : NOT_AVAILABLE);
		parameterMap.put("instances", getInstances() != null ? String.valueOf(getInstances()) : NOT_AVAILABLE);
		parameterMap.put("memory (in MB)", getMemory() != null ? String.valueOf(getMemory()) : NOT_AVAILABLE);
		parameterMap.put("diskQuota (in MB)", getDiskQuota() != null ? String.valueOf(getDiskQuota()) : NOT_AVAILABLE);
		parameterMap.put("healthCheckTimeout", getHealthCheckTimeout() != null ? String.valueOf(getHealthCheckTimeout()) : NOT_AVAILABLE);
		parameterMap.put("url", getUrl() != null ? getUrl() : NOT_AVAILABLE);
		parameterMap.put("urls", getUrls().isEmpty() ? NOT_AVAILABLE : CommonUtils.collectionToCommaDelimitedString(getUrls()));
		parameterMap.put("path", getArtifactPath());

		parameterMap.put("env", getEnv() != null ? String.valueOf(getEnv()) : NOT_AVAILABLE);
		parameterMap.put("services", getServices().isEmpty() ? NOT_AVAILABLE : CommonUtils.collectionServicesToCommaDelimitedString(getServices()));
		parameterMap.put("noStart", isNoStart() != null ? String.valueOf(isNoStart()) : NOT_AVAILABLE);

		parameterMap.put("server", getServer());
		parameterMap.put("target", getTarget() != null ? getTarget().toString() : NOT_AVAILABLE);
		parameterMap.put("org", getOrg(false) != null ? getOrg() : NOT_AVAILABLE);
		parameterMap.put("space", getSpace(false) != null ? getSpace() : NOT_AVAILABLE);
		parameterMap.put("username", getUsername() != null ? getUsername() : NOT_AVAILABLE);
		parameterMap.put("password", getPassword() != null ? CommonUtils.maskPassword(getPassword()) : NOT_AVAILABLE);

		parameterMap.put("trustSelfSignedCerts", String.valueOf(getTrustSelfSignedCerts()));

		return parameterMap;
	}

	@Override
	protected void doExecute() throws MojoExecutionException {
		final StringBuilder sb = new StringBuilder();

		sb.append("\n" + UiUtils.HORIZONTAL_LINE);
		sb.append("\nCloud Foundry Maven Plugin detected parameters and/or default values:\n\n");

		sb.append(UiUtils.renderParameterInfoDataAsTable(getParameterMap()));

		Reader reader = null;
		BufferedReader in = null;

		try {
			final InputStream is = Help.class.getResourceAsStream(HELP_TEXT);
			reader = new InputStreamReader(is);
			in = new BufferedReader(reader);

			final StringBuilder helpTextStringBuilder = new StringBuilder();

			String line = "";

			while (line != null) {
				try {
					line = in.readLine();
				} catch (IOException e) {
					throw new IllegalStateException("Problem reading internal '" + HELP_TEXT + "' file. This is a bug.", e);
				}

				if (line != null) {
					helpTextStringBuilder.append(line).append("\n");
				}
			}
			sb.append(helpTextStringBuilder);
		} finally {
			CommonUtils.closeReader(in);
			CommonUtils.closeReader(reader);
		}

		getLog().info(sb);
	}

	private String getArtifactPath() {
		String path;
		try {
			path = getPath() != null ? getPath().getAbsolutePath() : NOT_AVAILABLE;
		} catch (MojoExecutionException ex) {
			path = NOT_AVAILABLE;
		}
		return path;
	}
}
