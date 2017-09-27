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

import com.ibm.watsonwork.ww.model.fileinfo.FileInfo;
import com.ibm.watsonwork.ww.model.message.Message;
import com.ibm.watsonwork.ww.model.message.TargetedMessage;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface WatsonWorkClient {

	@Headers({ "Content-Type: application/json" })
	@POST("/v1/spaces/{spaceId}/messages")
	Call<Message> createMessage(@Header("Authorization") String authToken, @Path("spaceId") String spaceId,
			@Body Message message);

	@Headers({ "Content-Type: application/json" })
	@GET("/files/api/v1/files/file/{fileId}")
	Call<FileInfo> getFileInfo(@Header("Authorization") String authToken, @Path("fileId") String fileId);

	@Multipart
	@POST("/photos/")
	Call<ResponseBody> uploadAppPhoto(@Header("Authorization") String authToken, @Part MultipartBody.Part file);

	@GET
	Call<ResponseBody> getFile(@Header("Authorization") String appAuthToken, @Url String downloadURL);

	@Headers({ "Content-Type: application/json" })
	@POST("/v1/spaces/{spaceId}/messages")
	Call<TargetedMessage> createTargetedMessage(@Header("Authorization") String appAuthToken,
			@Path("spaceId") String spaceId, TargetedMessage message);
}
