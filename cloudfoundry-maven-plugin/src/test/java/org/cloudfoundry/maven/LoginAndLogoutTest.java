package org.cloudfoundry.maven;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;

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

	//Verify token file has been created
	@Test
	public void tokenFileCreatedTest() throws MojoExecutionException, IOException, URISyntaxException {
		when(client.login()).thenReturn("bearer qwrX12JK541ca2LPOIUYTREWQZXCVBNM");

		login.setClient(client);
		login.doExecute();

		File newFile = new File(tempFolder.getRoot(), ".mvn-cf.xml");
		assertEquals(FileUtils.readFileToString(newFile), "bearer qwrX12JK541ca2LPOIUYTREWQZXCVBNM");
	}

	//Verify token has been passed in for the next client creation
	@Test
	public void tokenPassedInForNextClientTest() throws MojoExecutionException, MojoFailureException, IOException {
		assertEquals(abstractCFMojo.retrieveToken(), "bearer qwrX12JK541ca2LPOIUYTREWQZXCVBNM");
	}

	//Verify token file has been deleted
	@Test
	public void tokenFileHasBeenDeleted() throws MojoExecutionException, IOException {
		logout.doExecute();
		File file  = new File(tempFolder.getRoot(), ".mvn-cf.xml");

		assertFalse(file.exists());
	}
}

@Ignore
class TestableLogin extends Login {

	@Override
	protected FileWriter createFileWriter() throws MojoExecutionException {
		FileWriter fileWriter = null;

		try {
			File newFile = LoginAndLogoutTest.tempFolder.newFile(".mvn-cf.xml");
			fileWriter = new FileWriter(newFile);
		} catch (IOException e) {
			throw new MojoExecutionException("Error creating token file: mvn-cf.xml", e);
		}

		return fileWriter;
	}

	public void setClient(CloudFoundryClient client) {
		this.client = client;
	}

	@Override
	public CloudFoundryClient getClient() {
		return client;
	}
}

@Ignore
class TestableLogout extends Logout {

	@Override
	protected File getFile() {
		File file  = new File(LoginAndLogoutTest.tempFolder.getRoot(), ".mvn-cf.xml");

		return file;
	}
}

@Ignore
class TestableAbstractCFMojo extends AbstractCloudFoundryMojo {

	@Override
	protected String retrieveToken() throws IOException {
		File newFile = new File(LoginAndLogoutTest.tempFolder.getRoot(), ".mvn-cf.xml");

		return FileUtils.readFileToString(newFile);
	}

	@Override
	protected void doExecute() throws MojoExecutionException,
			MojoFailureException {}
}