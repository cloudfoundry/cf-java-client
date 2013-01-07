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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

import java.net.MalformedURLException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import org.cloudfoundry.client.lib.CloudCredentials;
import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.cloudfoundry.maven.common.Assert;
import org.cloudfoundry.maven.common.SystemProperties;

import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Writes the user's token into mvn-cf file
 * in user's home directory - This will be used
 * instead of username/password
 *
 * @author Ali Moghadam
 * @since 1.0.0
 *
 * @goal login
 * @requiresProject false
 */
public class Login extends AbstractCloudFoundryMojo {

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		Assert.configurationNotNull(getUsername(), "username", SystemProperties.USERNAME);
		Assert.configurationNotNull(getPassword(), "password", SystemProperties.PASSWORD);
		Assert.configurationNotNull(getTarget(), "target", SystemProperties.TARGET);

		try {
			client = new CloudFoundryClient(new CloudCredentials(getUsername(), getPassword()), getTarget().toURL());
		} catch (MalformedURLException e) {
			throw new MojoExecutionException(
					String.format("Incorrect Cloud Foundry target url, are you sure '%s' is correct? Make sure the url contains a scheme, e.g. http://... ", getTarget()), e);
		}

		doExecute();
	}

	@Override
	protected void doExecute() throws MojoExecutionException {
		File file = createFileWriter();
		Element targets = null;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;

		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new MojoExecutionException("Error Parser Configuration", e);
		}

		if (file.exists() && file.length() != 0) {
			getLog().debug("Token file already exists.");
			boolean exist = false;
			try {
				Document doc = builder.parse(file);
				targets = doc.getDocumentElement();
				NodeList list = targets.getElementsByTagName("target");

				for (int i = 0; i < list.getLength(); i++) {
					Node childNode = list.item(i);
					if (childNode.getFirstChild().getTextContent().equals(getTarget().toString())) {
						getLog().debug("Target found in token file. Updating target");

						exist = true;
						Element token = doc.createElement("token");
						CDATASection tokenData = doc.createCDATASection(client.login());
						token.appendChild(tokenData);

						childNode.replaceChild(token, childNode.getLastChild());
						break;
					}
				}
				if (!exist) {
					getLog().debug("Target not found in token file. Adding target");

					Element target = doc.createElement("target");

					Element url = doc.createElement("url");
					url.setTextContent(getTarget().toString());

					Element token = doc.createElement("token");
					CDATASection tokenData = doc.createCDATASection(client.login());
					token.appendChild(tokenData);

					target.appendChild(url);
					target.appendChild(token);
					targets.appendChild(target);
				}
			} catch (SAXException e) {
				throw new MojoExecutionException("Error reading token file", e);
			} catch (IOException e) {
				throw new MojoExecutionException("Error locating token file", e);
			}
		} else {
			getLog().debug("Creating token file");
			Document doc = builder.newDocument();
			targets = doc.createElement("targets");

			Element target = doc.createElement("target");

			Element url = doc.createElement("url");
			url.setTextContent(getTarget().toString());

			Element token = doc.createElement("token");
			CDATASection tokenData = doc.createCDATASection(client.login());
			token.appendChild(tokenData);

			target.appendChild(url);
			target.appendChild(token);
			targets.appendChild(target);
		}

		DOMSource source = new DOMSource(targets);
		try {
			PrintStream ps = new PrintStream(file);
			StreamResult result = new StreamResult(ps);

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.transform(source, result);
		} catch (TransformerConfigurationException e) {
			throw new MojoExecutionException("Error in Transformer Configuration", e);
		} catch (TransformerException e) {
			throw new MojoExecutionException("Error in Transforming Exception", e);
		} catch (FileNotFoundException e) {
			throw new MojoExecutionException("Token File Not Found", e);
		}
		getLog().info("You are now logged in");
	}

	protected File createFileWriter() {
		File file = new File(System.getProperty("user.home") + "/.mvn-cf.xml");

		return file;
	}
}