/*******************************************************************************
 * Copyright 2017 IBM Corp. All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.miwu.virustotal.ww.service.impl;

import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.miwu.virustotal.ww.WatsonWorkConstants;
import net.miwu.virustotal.ww.WatsonWorkProperties;
import net.miwu.virustotal.ww.client.AuthClient;
import net.miwu.virustotal.ww.model.OauthResponse;
import net.miwu.virustotal.ww.model.TokenResponse;
import net.miwu.virustotal.ww.model.WebhookEvent;
import net.miwu.virustotal.ww.service.AuthService;
import retrofit2.Response;

@Service
@Slf4j
public class DefaultAuthService implements AuthService {

	private String appToken;
	private Date appTokenExpireTime;
	private Map<String, OauthResponse> oauthResponseMap = new ConcurrentHashMap<>();

	@Autowired
	private WatsonWorkProperties watsonWorkProperties;

	@Autowired
	private AuthClient authClient;

	@Override
	public String getAppAuthToken() {
		// if we never got the token or if the token is expired, set it
		if (appTokenExpireTime == null || appTokenExpireTime.before(new Date())) {
			try {
				TokenResponse tokenResponse = authClient
						.authenticateApp(createAppAuthHeader(), WatsonWorkConstants.CLIENT_CREDENTIALS).execute()
						.body();
				appTokenExpireTime = getDate(tokenResponse.getExpiresIn());
				appToken = tokenResponse.getAccessToken();
			} catch (Exception e) {
				log.error(e.getMessage());
			}
		}
		return WatsonWorkConstants.BEARER + appToken;
	}

	@Override
	public String getAppId() {
		return watsonWorkProperties.getAppId();
	}

	@Override
	public String getAppSecret() {
		return watsonWorkProperties.getAppSecret();
	}

	@Override
	public String getWebhookSecret() {
		return watsonWorkProperties.getWebhookSecret();
	}

	@Override
	public String createVerificationHeader(String responseBody) {
		return HmacUtils.hmacSha256Hex(getWebhookSecret(), responseBody);
	}

	@Override
	@SneakyThrows
	public OauthResponse exchangeCodeForToken(String code, String redirectUri) {
		Response<OauthResponse> response = authClient
				.exchangeCodeForToken(createAppAuthHeader(), code, WatsonWorkConstants.AUTHORIZATION_CODE, redirectUri)
				.execute();
		OauthResponse oauthResponse = response.body();
		oauthResponseMap.put(oauthResponse.getId(), oauthResponse);
		return oauthResponse;
	}

	@Override
	public boolean isValidVerificationRequest(WebhookEvent webhookEvent, String outboundToken) {
		String requestBody = String.format("{\"type\":\"verification\",\"challenge\":\"%s\"}",
				webhookEvent.getChallenge());
		String verificationHeader = createVerificationHeader(requestBody);
		return outboundToken.equals(verificationHeader);
	}

	@Override
	public OauthResponse getUserOAuthResponse(String userId) {
		return oauthResponseMap.getOrDefault(userId, null);
	}

	private Date getDate(Integer secondsFromNow) {
		long millisFromNow = secondsFromNow * 1000L;
		return new Date(System.currentTimeMillis() + millisFromNow);
	}

	private String createAppAuthHeader() {
		return WatsonWorkConstants.BASIC
				+ Base64.getEncoder().encodeToString((getAppId() + ":" + getAppSecret()).getBytes());
	}
}
