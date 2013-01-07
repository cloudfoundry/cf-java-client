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
package org.cloudfoundry.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Performs logout if client exist &
 * Deletes the user's token mvn-cf file
 *
 * @author Ali Moghadam
 * @since 1.0.0
 *
 * @goal logout
 * @requiresProject false
 */
public class Logout extends AbstractCloudFoundryMojo {

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		if (getClient() != null) {
			getClient().logout();
		}
		doExecute();
	}

	@Override
	protected void doExecute() throws MojoExecutionException {
		File file  = getFile();
		Element targets = null;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(file);

			targets = doc.getDocumentElement();
			NodeList list = targets.getElementsByTagName("target");

			for (int i = 0; i < list.getLength(); i++) {
				Node childNode = list.item(i);
				if (childNode.getFirstChild().getTextContent().equals(getTarget().toString())) {
					getLog().debug("Removing the target from the token file");
					list.item(i).getParentNode().removeChild(list.item(i));
					break;
				}
			}
		} catch (SAXException e) {
			throw new MojoExecutionException("Error reading token file", e);
		} catch (ParserConfigurationException e) {
			throw new MojoExecutionException("Error Parser Configuration", e);
		} catch (IOException e) {
			throw new MojoExecutionException("Error locating token file", e);
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
		getLog().info("You are logged out");
	}

	protected File getFile() {
		File file  = new File(System.getProperty("user.home") + "/.mvn-cf.xml");

		return file;
	}
}