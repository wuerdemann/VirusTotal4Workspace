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
package com.ibm.watsonwork.ww.client;

import com.ibm.watsonwork.ww.model.auth.OauthResponse;
import com.ibm.watsonwork.ww.model.auth.TokenResponse;

import retrofit2.Call;
import retrofit2.http.*;

public interface AuthClient {

	@Headers({ "Content-Type: application/x-www-form-urlencoded" })
	@FormUrlEncoded
	@POST("/oauth/token")
	Call<TokenResponse> authenticateApp(@Header("Authorization") String basicAuthorization,
			@Field("grant_type") String grantType);

	@Headers({ "Content-Type: application/x-www-form-urlencoded" })
	@FormUrlEncoded
	@POST("/oauth/token")
	Call<OauthResponse> exchangeCodeForToken(@Header("Authorization") String basicAuthorization,
			@Field("code") String code, @Field("grant_type") String grantType,
			@Field("redirect_uri") String redirectUri);

}
