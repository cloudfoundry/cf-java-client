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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
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
	 *
	 * @return
	 */
	private Map<String, String> getParameterMap() {
		final Map<String, String> parameterMap = new TreeMap<String, String>();

		parameterMap.put("Appname", getAppname() != null ? getAppname() : NOT_AVAILABLE);
		parameterMap.put("Command", getCommand() != null ? getCommand() : NOT_AVAILABLE);
		parameterMap.put("Framework", getFramework() != null ? getFramework() : NOT_AVAILABLE);
		parameterMap.put("Instances", getInstances() != null ? String.valueOf(getInstances()) : NOT_AVAILABLE);
		parameterMap.put("Memory (in MB)", getMemory() != null ? String.valueOf(getMemory()) : NOT_AVAILABLE);
		parameterMap.put("Env", getEnv() != null ? String.valueOf(getEnv()) : NOT_AVAILABLE);
		parameterMap.put("No-start", isNoStart() != null ? String.valueOf(isNoStart()) : NOT_AVAILABLE);
		parameterMap.put("Password", getPassword() != null ? CommonUtils.maskPassword(getPassword()) : NOT_AVAILABLE);
		parameterMap.put("Runtime", getRuntime() != null ? getRuntime() : NOT_AVAILABLE);
		parameterMap.put("Server", getServer());
		parameterMap.put("Services", getServices().isEmpty() ? NOT_AVAILABLE : CommonUtils.collectionServicesToCommaDelimitedString(getServices()));
		parameterMap.put("Target", getTarget() != null ? getTarget().toString() : NOT_AVAILABLE);
		parameterMap.put("Url", getUrl() != null ? getUrl() : NOT_AVAILABLE);
		parameterMap.put("Username", getUsername() != null ? getUsername() : NOT_AVAILABLE);
		parameterMap.put("Path", getPath() != null ? getPath().getAbsolutePath() : NOT_AVAILABLE);

		return parameterMap;
	}

	@Override
	protected void doExecute() {

		final StringBuilder sb = new StringBuilder();

		sb.append("\n" + UiUtils.HORIZONTAL_LINE);
		sb.append("\nCloud Foundry Maven Plugin detected Parameters and/or default values:\n\n");

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
					helpTextStringBuilder.append(line + "\n");
				}
			}
			sb.append(helpTextStringBuilder);
		} finally {
			CommonUtils.closeReader(in);
			CommonUtils.closeReader(reader);
		}
		getLog().info(sb);
	}
}
