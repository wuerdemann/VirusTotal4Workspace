/*******************************************************************************
 * Copyright 2017 IBM Corp. All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.miwu.virustotal.ww;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties
public class WatsonWorkProperties {

    @Value("${watsonwork.webhook.secret}")
    private String webhookSecret;

    @Value("${watsonwork.app.id}")
    private String appId;

    @Value("${watsonwork.app.secret}")
    private String appSecret;

    @Value("${watsonwork.api.uri}")
    private String apiUri;

    @Value("${watsonwork.api.uri}" + "${watsonwork.api.oauth}")
    private String oauthApi;
}
