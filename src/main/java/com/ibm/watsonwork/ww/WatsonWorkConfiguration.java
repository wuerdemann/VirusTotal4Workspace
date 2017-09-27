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
package com.ibm.watsonwork.ww;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.watsonwork.ww.client.AuthClient;
import com.ibm.watsonwork.ww.client.WatsonWorkClient;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

@Configuration
public class WatsonWorkConfiguration {

	@Autowired
	private WatsonWorkProperties watsonWorkProperties;

	@Autowired
	private ObjectMapper objectMapper;

	@Bean
	public OkHttpClient okHttpClient() {
		return new OkHttpClient();
	}

	@Bean
	public Retrofit retrofit(OkHttpClient client) {
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return new Retrofit.Builder().addConverterFactory(JacksonConverterFactory.create(objectMapper))
				.baseUrl(watsonWorkProperties.getApiUri()).client(client).build();
	}

	@Bean
	public WatsonWorkClient watsonWorkClient(Retrofit retrofit) {
		return retrofit.create(WatsonWorkClient.class);
	}

	@Bean
	public AuthClient authClient(Retrofit retrofit) {
		return retrofit.create(AuthClient.class);
	}
}
