package org.cloudfoundry.client.lib.tokens;

import com.esotericsoftware.yamlbeans.YamlConfig;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.esotericsoftware.yamlbeans.YamlWriter;
import org.cloudfoundry.client.lib.domain.CloudInfo;
import org.cloudfoundry.client.lib.domain.CloudSpace;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.DefaultOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class TokensFile {
	public OAuth2AccessToken retrieveToken(URI target) {
		TargetInfos targetInfos = getTokensFromFile();

		if (targetInfos == null) {
			return null;
		}

		HashMap<String, String> targetInfo = targetInfos.get(target);

		if (targetInfo == null) {
			return null;
		}

		DefaultOAuth2RefreshToken refreshToken = targetInfos.getRefreshToken(targetInfo);
		DefaultOAuth2AccessToken token = targetInfos.getToken(targetInfo);
		token.setRefreshToken(refreshToken);

		return token;
	}

	public void saveToken(URI target, OAuth2AccessToken token, CloudInfo cloudInfo, CloudSpace space) {
		TargetInfos targetInfos = getTokensFromFile();

		if (targetInfos == null) {
			targetInfos = new TargetInfos();
		}

		HashMap<String, String> targetInfo = targetInfos.get(target);

		if (targetInfo == null) {
			targetInfo = new LinkedHashMap<String, String>();
		}

		targetInfos.putToken(targetInfo, token);
		targetInfos.putRefreshToken(targetInfo, token.getRefreshToken());
		targetInfos.putVersion(targetInfo, cloudInfo.getVersion());
		targetInfos.putSpace(targetInfo, space.getMeta().getGuid().toString());
		targetInfos.putOrganization(targetInfo, space.getOrganization().getMeta().getGuid().toString());

		targetInfos.put(target, targetInfo);

		saveTokensToFile(targetInfos);
	}

	public void removeToken(URI target) {
		TargetInfos targetInfos = getTokensFromFile();
		targetInfos.remove(target);
		saveTokensToFile(targetInfos);
	}

	public String getTokensFilePath() {
		return System.getProperty("user.home") + "/.cf/tokens.yml";
	}

	protected TargetInfos getTokensFromFile() {
		final File tokensFile = getTokensFile();
		try {
			YamlReader reader = new YamlReader(new FileReader(tokensFile));
			return reader.read(TargetInfos.class);
		} catch (FileNotFoundException fnfe) {
			return new TargetInfos();
		} catch (IOException e) {
			throw new RuntimeException("An error occurred reading the tokens file at " +
					tokensFile.getPath() + ":" + e.getMessage(), e);
		}

	}

	protected void saveTokensToFile(TargetInfos targetInfos) {
		final File tokensFile = getTokensFile();
		tokensFile.getParentFile().mkdirs();
		try {
			FileWriter fileWriter = new FileWriter(tokensFile);

			YamlConfig config = new YamlConfig();
			config.writeConfig.setAlwaysWriteClassname(false);
			config.writeConfig.setWriteRootElementTags(false);
			config.writeConfig.setWriteRootTags(false);
			config.writeConfig.setExplicitFirstDocument(true);
			YamlWriter yamlWriter = new YamlWriter(fileWriter, config);

			yamlWriter.write(targetInfos);

			yamlWriter.close();
			fileWriter.close();
		} catch (IOException e) {
			throw new RuntimeException("An error occurred writing the tokens file at " +
					tokensFile.getPath() + ":" + e.getMessage(), e);
		}
	}

	protected File getTokensFile() {
		return new File(getTokensFilePath());
	}
}
