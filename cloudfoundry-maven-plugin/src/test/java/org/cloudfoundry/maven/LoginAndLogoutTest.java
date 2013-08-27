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

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.cloudfoundry.client.lib.domain.CloudEntity;
import org.cloudfoundry.client.lib.domain.CloudInfo;
import org.cloudfoundry.client.lib.domain.CloudOrganization;
import org.cloudfoundry.client.lib.domain.CloudSpace;
import org.cloudfoundry.maven.common.AuthTokens;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.DefaultOAuth2RefreshToken;

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

	private TestableCloudFoundryMojo cloudFoundryMojo;

	@ClassRule
	public static TemporaryFolder tempFolder = new TemporaryFolder();

	@Mock
	private CloudFoundryClient client;

	@Before
	public void setup() throws Exception {
		initMocks(this);

		TestableAuthTokens authTokens = new TestableAuthTokens();

		cloudFoundryMojo = new TestableCloudFoundryMojo(authTokens);

		login = new TestableLogin(client, authTokens);
		logout = new TestableLogout(authTokens);
	}

	@Test
	public void tokenSavedOnLogin() throws MojoExecutionException, IOException, URISyntaxException {
		DefaultOAuth2RefreshToken refreshToken = new DefaultOAuth2RefreshToken("refreshtoken");
		DefaultOAuth2AccessToken accessToken = new DefaultOAuth2AccessToken("accesstoken");
		accessToken.setRefreshToken(refreshToken);
		when(client.login()).thenReturn(accessToken);

		HashMap<String, Object> info = new HashMap<String, Object>(1);
		info.put("version", "2");
		when(client.getCloudInfo()).thenReturn(new CloudInfo(info));

		Date date = new Date();
		CloudOrganization org = new CloudOrganization(new CloudEntity.Meta(UUID.randomUUID(), date, date), "my-org");
		CloudSpace space = new CloudSpace(new CloudEntity.Meta(UUID.randomUUID(), date, date), "my-space", org);
		List<CloudSpace> spaces = Arrays.asList(space);
		when(client.getSpaces()).thenReturn(spaces);

		login.doExecute();

		assertEquals(cloudFoundryMojo.retrieveToken().getValue(), "accesstoken");

		logout.doExecute();

		try {
			cloudFoundryMojo.retrieveToken();
			fail();
		} catch (MojoExecutionException e) {
			assertTrue(e.getMessage().contains("Access token could not be read"));
		}
	}
}

@Ignore
class TestableAuthTokens extends AuthTokens {
	@Override
	public String getTokensFilePath() {
		try {
			return LoginAndLogoutTest.tempFolder.newFile("tokens.yml").getAbsolutePath();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}

@Ignore
class TestableLogin extends Login {

	public TestableLogin(CloudFoundryClient client, AuthTokens authTokens) {
		super(authTokens);
		this.client = client;
	}

	@Override
	public URI getTarget() {
		try {
			return new URI("https://api.example.com");
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String getSpace() {
		return "my-space";
	}
}

@Ignore
class TestableLogout extends Logout {

	public TestableLogout(AuthTokens authTokens) {
		super(authTokens);
	}

	@Override
	public URI getTarget() {
		try {
			return new URI("https://api.example.com");
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}
}

@Ignore
class TestableCloudFoundryMojo extends AbstractCloudFoundryMojo {

	public TestableCloudFoundryMojo(AuthTokens authTokens) {
		this.authTokens = authTokens;
	}

	@Override
	public URI getTarget() {
		try {
			return new URI("https://api.example.com");
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void doExecute() throws MojoExecutionException, MojoFailureException {
	}
}