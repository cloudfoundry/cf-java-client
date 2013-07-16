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
import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import static org.junit.Assert.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.mockito.Mockito.when;
/**
*
* @author Ali Moghadam
* @since 1.0.0
*
*/
public class LoginAndLogoutTest {

	private TestableLogin login;

	private TestableLogout logout;

	private TestableAbstractCFMojo abstractCFMojo;

	@ClassRule
	public static TemporaryFolder tempFolder = new TemporaryFolder();

	@Mock
	private CloudFoundryClient client;

	@Before
	public void setup() throws Exception {
		initMocks(this);

		abstractCFMojo = new TestableAbstractCFMojo();
		login = new TestableLogin();
		logout = new TestableLogout();
	}

	//Verify target has been created in token file
	@Test
	public void targetCreatedTest() throws MojoExecutionException, IOException, URISyntaxException {
		when(client.login()).thenReturn("bearer qwrX12JK541ca2LPOIUYTREWQZXCVBNM");

		login.setClient(client);
		login.doExecute();

		File newFile = new File(tempFolder.getRoot(), ".mvn-cf.xml");

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try {
			builder = factory.newDocumentBuilder();
			Document doc = builder.parse(newFile);
			Element targets = doc.getDocumentElement();
			NodeList list = targets.getElementsByTagName("target");

			for (int i = 0; i < list.getLength(); i++) {
				Node childNode = list.item(i);

				assertTrue("Target url found", childNode.getFirstChild().getTextContent().equals(login.getTarget().toString()));
				assertTrue("Target token found", childNode.getLastChild().getTextContent().equals("bearer qwrX12JK541ca2LPOIUYTREWQZXCVBNM"));
			}
		} catch (ParserConfigurationException e) {} catch (SAXException e) {}
	}

	//Verify target has been passed in for the next client creation
	@Test
    @Ignore
	public void targetPassedInForNextClientTest() throws MojoExecutionException, MojoFailureException, IOException {
		assertEquals(abstractCFMojo.retrieveToken(), "bearer qwrX12JK541ca2LPOIUYTREWQZXCVBNM");
	}

	//Verify target has been delete from token file
	@Test
	public void targetHasBeenDeleted() throws MojoExecutionException, IOException {
		logout.doExecute();

		File file  = new File(tempFolder.getRoot(), ".mvn-cf.xml");
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try {
			builder = factory.newDocumentBuilder();
			Document doc = builder.parse(file);
			Element targets = doc.getDocumentElement();
			NodeList list = targets.getElementsByTagName("target");

			assertFalse(list.getLength() > 0);

		} catch (ParserConfigurationException e) {} catch (SAXException e) {}
	}
}

@Ignore
class TestableLogin extends Login {

	@Override
	protected File createFileWriter() {
		File newFile = null;
		try {
			newFile = LoginAndLogoutTest.tempFolder.newFile(".mvn-cf.xml");
		} catch (IOException e) {}

		return newFile;
	}

	public void setClient(CloudFoundryClient client) {
		this.client = client;
	}

	@Override
	public CloudFoundryClient getClient() {
		return client;
	}

	@Override
	public URI getTarget() {
		URI target = null;
		try {
			target = new URI("https://api.sample.com");
		} catch (URISyntaxException e) {}

		return target;
	}
}

@Ignore
class TestableLogout extends Logout {

	@Override
	protected File getFile() {
		File file  = new File(LoginAndLogoutTest.tempFolder.getRoot(), ".mvn-cf.xml");

		return file;
	}

	@Override
	public URI getTarget() {
		URI target = null;
		try {
			target = new URI("https://api.sample.com");
		} catch (URISyntaxException e) {}

		return target;
	}
}

@Ignore
class TestableAbstractCFMojo extends AbstractCloudFoundryMojo {

	@Override
	public URI getTarget() {
		URI target = null;
		try {
			target = new URI("https://api.sample.com");
		} catch (URISyntaxException e) {}

		return target;
	}

	@Override
	protected String retrieveToken() throws IOException {
		File newFile = new File(LoginAndLogoutTest.tempFolder.getRoot(), ".mvn-cf.xml");

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(newFile);

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

	@Override
	protected void doExecute() throws MojoExecutionException,
			MojoFailureException {}
}