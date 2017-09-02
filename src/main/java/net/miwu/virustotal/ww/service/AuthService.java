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
package net.miwu.virustotal.ww.service;

import net.miwu.virustotal.ww.model.OauthResponse;
import net.miwu.virustotal.ww.model.WebhookEvent;

public interface AuthService extends Service {

	String getAppAuthToken();

	String getAppId();

	String getAppSecret();

	String getWebhookSecret();

	String createVerificationHeader(String responseBody);

	OauthResponse exchangeCodeForToken(String code, String redirectUri);

	boolean isValidVerificationRequest(WebhookEvent webhookEvent, String outBoundToken);

	OauthResponse getUserOAuthResponse(String id);

}
