/*
 * Copyright 2009-2013 the original author or authors.
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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.maven.artifact.manager.WagonManager;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.wagon.authentication.AuthenticationInfo;
import org.cloudfoundry.client.lib.CloudCredentials;
import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.cloudfoundry.client.lib.CloudFoundryException;
import org.cloudfoundry.maven.common.Assert;
import org.cloudfoundry.maven.common.CommonUtils;
import org.cloudfoundry.maven.common.DefaultConstants;
import org.cloudfoundry.maven.common.SystemProperties;
import org.cloudfoundry.client.lib.domain.CloudSpace;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.ResourceAccessException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Abstract goal that provides common configuration for the Cloud Foundry Maven
 * Plugin.
 *
 * @author Gunnar Hillert
 * @author Ali Moghadam
 * @since 1.0.0
 */
public abstract class AbstractCloudFoundryMojo extends AbstractMojo {

	/**
	 * @parameter expression="${project.artifactId}"
	 */
	private String artifactId;

	protected CloudFoundryClient client;

	/**
	 * @parameter expression="${cf.password}"
	 */
	protected String password;

	/**
	 * @parameter expression="${cf.server}"
	 */
	private String server;

	/**
	 * @parameter expression="${session}"
	 */
	private MavenSession session;

	/**
	 * @parameter expression="${cf.target}"
	 */
	private String target;

	/**
	 * @parameter expression="${cf.username}"
	 */
	protected String username;

	/**
	 * @parameter expression="${cf.org}"
	 */
	private String org;

	/**
	 * @parameter expression="${cf.space}"
	 */
	private String space;

	/**
	 * The Maven Wagon manager to use when obtaining server authentication details.
	 *
	 * @component role="org.apache.maven.artifact.manager.WagonManager"
	 * @required
	 * @readonly
	 */
	private WagonManager wagonManager;

	/**
	 * Default Constructor.
	 */
	public AbstractCloudFoundryMojo() {
		super();
	}

	/**
	 * Retrieve Token from .mvn-cf.xml
	 * @return token (String)
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	protected String retrieveToken() throws IOException {
		File file = new File(System.getProperty("user.home") + "/.mvn-cf.xml");
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(file);

			Element targets = doc.getDocumentElement();
			NodeList list = targets.getElementsByTagName("target");

			for (int i = 0; i < list.getLength(); i++) {
				Node childNode = list.item(i);
				if (childNode.getFirstChild().getTextContent().equals(getTarget().toString())) {
					return childNode.getLastChild().getTextContent();
				}
			}
		} catch (SAXException e) {
			throw new IOException();
		} catch (ParserConfigurationException e) {
			throw new IOException();
		}

		return null;
	}

	/**
	 * Cloud Controller Version 1 Client (Token)
	 * @param token
	 * @param target
	 * @return client
	 * @throws MojoExecutionException
	 */
	protected CloudFoundryClient createCloudFoundryClient(String token, URI target)
			throws MojoExecutionException {

		getLog().debug("Cloud Controller Version 1");
		getLog().debug(String.format("Connecting to Cloud Foundry at '%s' using Token", target.toString()));

		final CloudFoundryClient client;

		try {
			client = new CloudFoundryClient(new CloudCredentials(token), target.toURL());
		} catch (MalformedURLException e) {
			throw new MojoExecutionException(
					String.format("Incorrect Cloud Foundry target url, are you sure '%s' is correct? Make sure the url contains a scheme, e.g. http://...", target), e);
		}

		return client;
	}

	/**
	 * Cloud Controller Version 1 Client
	 * @param username
	 * @param password
	 * @param target
	 * @return client
	 * @throws MojoExecutionException
	 */
	protected CloudFoundryClient createCloudFoundryClient(String username, String password, URI target)
			throws MojoExecutionException {

		Assert.configurationNotNull(username, "username", SystemProperties.USERNAME);
		Assert.configurationNotNull(password, "password", SystemProperties.PASSWORD);

		getLog().debug("Cloud Controller Version 1");
		getLog().debug(String.format(
				"Connecting to Cloud Foundry at '%s' (Username: '%s', password: '***')",
				target, username));

		final CloudFoundryClient client;

		try {
			client = new CloudFoundryClient(new CloudCredentials(username, password), target.toURL());
			connectToCloudFoundry(client);
		} catch (MalformedURLException e) {
			throw new MojoExecutionException(
					String.format("Incorrect Cloud Foundry target url, are you sure '%s' is correct? Make sure the url contains a scheme, e.g. http://...", target), e);
		}

		return client;
	}

	/**
	 * Cloud Controller Version 2 Client (Token)
	 * @param token
	 * @param target
	 * @param org
	 * @param space
	 * @return client
	 * @throws MojoExecutionException
	 */
	protected CloudFoundryClient createCloudFoundryClient(String token, URI target, String org, String space)
			throws MojoExecutionException {

		Assert.configurationNotNull(org, "org", SystemProperties.ORG);
		Assert.configurationNotNull(space, "space", SystemProperties.SPACE);

		getLog().debug("Cloud Controller Version 2");
		getLog().debug(String.format("Connecting to Cloud Foundry at '%s' using Token", target.toString()));

		final CloudFoundryClient client;

		try {
			CloudFoundryClient authClient = new CloudFoundryClient(new CloudCredentials(token), target.toURL());

			for (CloudSpace userSpace : authClient.getSpaces()) {
				if (userSpace.getName().equals(space) && userSpace.getOrganization().getName().equals(org)) {
					client = new CloudFoundryClient(new CloudCredentials(token), target.toURL(), userSpace);
					return client;
				}
			}
		} catch (MalformedURLException e) {
			throw new MojoExecutionException(
					String.format("Incorrect Cloud Foundry target url, are you sure '%s' is correct? Make sure the url contains a scheme, e.g. http://...", target), e);
		}
		return null;
	}

	/**
	 * Cloud Controller Version 2 Client
	 * @param username
	 * @param password
	 * @param target
	 * @param org
	 * @param space
	 * @return client/null
	 * @throws MojoExecutionException
	 */
	protected CloudFoundryClient createCloudFoundryClient(String username, String password, URI target, String org, String space)
			throws MojoExecutionException {

		Assert.configurationNotNull(username, "username", SystemProperties.USERNAME);
		Assert.configurationNotNull(password, "password", SystemProperties.PASSWORD);
		Assert.configurationNotNull(org, "org", SystemProperties.ORG);
		Assert.configurationNotNull(space, "space", SystemProperties.SPACE);

		getLog().debug("Cloud Controller Version 2");
		getLog().debug(String.format(
				"Connecting to Cloud Foundry at '%s' (Username: '%s', password: '***')",
				target, username));

		final CloudFoundryClient client;

		try {
			CloudFoundryClient authClient = new CloudFoundryClient(new CloudCredentials(username, password), target.toURL());
			authClient.login();

			for (CloudSpace userSpace : authClient.getSpaces()) {
				if (userSpace.getName().equals(space) && userSpace.getOrganization().getName().equals(org)) {
					client = new CloudFoundryClient(new CloudCredentials(username, password), target.toURL(), userSpace);
					connectToCloudFoundry(client);

					return client;
				}
			}
		} catch (MalformedURLException e) {
			throw new MojoExecutionException(
					String.format("Incorrect Cloud Foundry target url, are you sure '%s' is correct? Make sure the url contains a scheme, e.g. http://...", target), e);
		}
		return null;
	}

	/**
	 * Cloud Foundry Connection Login
	 * @param client
	 * @return
	 * @throws MojoExecutionException
	 */
	protected void connectToCloudFoundry(CloudFoundryClient client) throws MojoExecutionException {
		try {
			client.login();
		} catch (CloudFoundryException e) {
			if (HttpStatus.FORBIDDEN.equals(e.getStatusCode())) {
				throw new MojoExecutionException(
						String.format("Login failed to '%s' using username '%s'. Please verify your login credentials.", target, username), e);
			} else if (HttpStatus.NOT_FOUND.equals(e.getStatusCode())) {
				throw new MojoExecutionException(
						String.format("The target host '%s' exists but it does not appear to be a valid Cloud Foundry target url.", target), e);
			} else {
				throw e;
			}
		} catch (ResourceAccessException e) {
			throw new MojoExecutionException(
					String.format("Cannot access host at '%s'.", target), e);
		}
	}

	/**
	 *  Goals will typically override this method.
	 */
	protected abstract void doExecute() throws MojoExecutionException, MojoFailureException;

	/**
	 * Base execute method. Will perform the login and logout into Cloud Foundry.
	 * Delegates to doExecute() for the actual business logic.
	 */
	public void execute() throws MojoExecutionException, MojoFailureException {
		Assert.configurationNotNull(target, "target", SystemProperties.TARGET);

		try {
			CloudFoundryClient simpleClient = new CloudFoundryClient(getTarget().toURL());
			String token = null;

			if (CommonUtils.isCloudControllerV2(simpleClient)) {
				if (getUsername() != null && getPassword() != null) {
					client = createCloudFoundryClient(getUsername(), getPassword(), getTarget(), getOrg(), getSpace());
				} else {
					try {
						token = retrieveToken();
						client = createCloudFoundryClient(token, getTarget(), getOrg(), getSpace());
					} catch (IOException e1) {
						client = createCloudFoundryClient(getUsername(), getPassword(), getTarget(), getOrg(), getSpace());
					}
				}
			} else {
				if (getUsername() != null && getPassword() != null) {
					client = createCloudFoundryClient(getUsername(), getPassword(), getTarget());
				} else {
					try {
						token = retrieveToken();
						client = createCloudFoundryClient(token, getTarget());
					} catch (IOException e) {
						client = createCloudFoundryClient(getUsername(), getPassword(), getTarget());
					}
				}
			}

			doExecute();

		} catch (RuntimeException e) {
			throw new MojoExecutionException("An exception was caught while executing Mojo.", e);
		} catch (MalformedURLException e) {
			throw new MojoExecutionException(
					String.format("Incorrect Cloud Foundry target url, are you sure '%s' is correct? Make sure the url contains a scheme, e.g. http://...", getTarget()), e);
		}
	}

	//~~~~Getters~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	/**
	 * @return Returns the Maven artifactId. Will never return null.
	 */
	public String getArtifactId() {
		Assert.notNull(artifactId, "The artifactId is not set.");
		return artifactId;
	}

	/**
	 *
	 * @return
	 */
	public CloudFoundryClient getClient() {
		return client;
	}

	/**
	 * See {@link http://maven.apache.org/plugin-developers/common-bugs.html#Using_System_Properties}
	 *
	 * @param property Supported system properties
	 * @return Null if the property is not found, otherwise returns the property
	 */
	public String getCommandlineProperty(SystemProperties property) {
		return session.getExecutionProperties().getProperty(property.getProperty());
	}

	/**
	 *
	 * @return
	 */
	public String getPassword() {

		final String passwordProperty = getCommandlineProperty(SystemProperties.PASSWORD);

		if (passwordProperty != null) {
			return passwordProperty;
		}

		if (this.password == null) {

			getLog().debug("No password defined in pom.xml and " +
					"no system property defined either. Trying to look up " +
					"password in settings.xml under server element " + this.getServer());

			AuthenticationInfo authenticationInfo = this.wagonManager.getAuthenticationInfo(this.getServer());

			if (authenticationInfo == null) {
				getLog().debug(String.format(
						"In settings.xml server element '%s' was not defined.", this.getServer()));
				return null;
			}

			if (authenticationInfo.getPassword() != null) {
				return authenticationInfo.getPassword();
			} else {
				getLog().debug(String.format(
						"In settings.xml no password was found for server element '%s'. Does the element exist?", this.getServer()));
				return null;
			}

		} else {
			return this.password;
		}

	}

	/**
	 * Maven allows for externalizing the credential for server connections to
	 * be externalized into settings.xml.
	 *
	 * If a property was provided on the command line, use that property. Otherwise
	 * use the property that was injected via Maven. If that is Null as well, default
	 * to the value specified in {@link DefaultConstants}
	 *
	 * @return The name of the Maven Server property used to resolved Cloud Foundry credentials. Never returns null.
	 *
	 */
	public String getServer() {

		final String serverProperty = getCommandlineProperty(SystemProperties.SETTINGS_SERVER);

		if (serverProperty != null) {
			return serverProperty;
		}

		if (this.server == null) {
			return DefaultConstants.MAVEN_DEFAULT_SERVER;
		}

		return this.server;
	}

	/**
	 * If the target property was provided via the command line, use that property.
	 * Otherwise use the property that was injected via Maven. If that is Null
	 * as well, Null is returned.
	 *
	 * @return Returns the Cloud Foundry Target Url - Can return Null.
	 *
	 */
	public URI getTarget() {

		final String targetProperty = getCommandlineProperty(SystemProperties.TARGET);

		if (targetProperty != null) {
			try {

				URI uri = new URI(targetProperty);

				if (uri.isAbsolute()) {
					return  uri;
				} else {
					throw new URISyntaxException(targetProperty, "URI is not opaque.");
				}

			} catch (URISyntaxException e) {
				throw new IllegalStateException(String.format("The Url parameter '%s' " +
						"which was passed in as system property is not valid.", targetProperty));
			}
		}

		if (this.target == null) {
			return null;
		}

		try {
			return new URI(this.target);
		} catch (URISyntaxException e) {
			throw new IllegalStateException(String.format("The Url parameter '%s' " +
					"which was passed in as pom.xml configiuration parameter is not valid.", this.target));
		}

	}

	/**
	 *
	 * @return
	 */
	public String getUsername() {

		final String usernameProperty = getCommandlineProperty(SystemProperties.USERNAME);

		if (usernameProperty != null) {
			return usernameProperty;
		}

		if (this.username == null) {

			getLog().debug("No username defined in pom.xml and " +
					"no system property defined either. Trying to look up " +
					"username in settings.xml under server element " + this.getServer());

			AuthenticationInfo authenticationInfo = this.wagonManager.getAuthenticationInfo(this.getServer());

			if (authenticationInfo == null) {
				getLog().debug(String.format(
						"In settings.xml server element '%s' was not defined.", this.getServer()));
				return null;
			}

			if (authenticationInfo.getUserName() != null) {
				return authenticationInfo.getUserName();
			} else {
				super.getLog().debug(String.format(
						"In settings.xml no username was found for server element '%s'. Does the element exist?", this.getServer()));
				return null;
			}

		} else {
			return username;
		}

	}

	public String getOrg() {
		Assert.notNull(org, "The org is not set.");
		return org;
	}

	public String getSpace() {
		Assert.notNull(space, "The space is not set.");
		return space;
	}
}
