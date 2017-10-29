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
package com.ibm.watsonwork.ww.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.MessageFormat;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import com.ibm.watsonwork.ww.client.WatsonWorkClient;
import com.ibm.watsonwork.ww.model.fileinfo.FileInfo;
import com.ibm.watsonwork.ww.model.message.Message;
import com.ibm.watsonwork.ww.model.message.TargetedMessage;
import com.ibm.watsonwork.ww.service.AuthService;
import com.ibm.watsonwork.ww.service.WatsonWorkService;
import com.ibm.watsonwork.ww.utils.MessageTypes;

import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.GET;

@Service
@EnableAsync
@Slf4j
public class DefaultWatsonWorkService implements WatsonWorkService {

	// Authentication Service
	@Autowired(required = true)
	private AuthService authService;

	// Link to Watson Workspace
	@Autowired(required = true)
	private WatsonWorkClient watsonWorkClient;

	@Override
	@Async(value = "WebhookThreadPoolExecutor")
	public void createMessage(@NotNull String spaceId, @NotNull Message message) {
		Call<Message> call = watsonWorkClient.createMessage(authService.getAppAuthToken(), spaceId, message);
		call.enqueue(new Callback<Message>() {

			@Override
			public void onResponse(Call<Message> call, Response<Message> response) {
				log.debug("Message successfully posted to Inbound Webhook.");
			}

			@Override
			public void onFailure(Call<Message> call, Throwable t) {
				log.error("Posting message to Inbound Webhook failed.", t);
			}
		});
	}

	@Override
	@Async(value = "WebhookThreadPoolExecutor")
	public void createTargetedMessage(@NotNull String spaceId, @NotNull TargetedMessage message) {
		Call<TargetedMessage> call = watsonWorkClient.createTargetedMessage(authService.getAppAuthToken(), spaceId,
				message);
		call.enqueue(new Callback<TargetedMessage>() {

			@Override
			public void onResponse(Call<TargetedMessage> call, Response<TargetedMessage> response) {
				log.debug("Message successfully posted to Inbound Webhook.");
			}

			@Override
			public void onFailure(Call<TargetedMessage> call, Throwable t) {
				log.error("Posting message to Inbound Webhook failed.", t);
			}
		});
	}

	@Override
	@PostConstruct
	@Async(value = "WebhookThreadPoolExecutor")
	public void uploadAppPhoto() {
		File file;
		try {
			file = ResourceUtils.getFile("classpath:app-photo.jpg");

			MediaType mediaType = MediaType.parse(org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE);
			MultipartBody.Part filePart = MultipartBody.Part.createFormData(MessageTypes.FORM_DATA_FILE, file.getName(),
					RequestBody.create(mediaType, file));
			Call<ResponseBody> uploadAppPhoto = watsonWorkClient.uploadAppPhoto(authService.getAppAuthToken(),
					filePart);
			uploadAppPhoto.enqueue(new Callback<ResponseBody>() {

				@Override
				public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
					if (response.code() == HttpStatus.UNSUPPORTED_MEDIA_TYPE.value()) {
						log.error("Failed to upload app photo. Supported Media Type is .jpg or .jpeg");
					}
					log.info("App photo successfully uploaded");
				}

				@Override
				public void onFailure(Call<ResponseBody> call, Throwable t) {
					log.error("Failed to upload app photo.", t);
				}
			});
		} catch (FileNotFoundException e) {
			log.error("Error while uploading app photo", e);
		}
	}

	@Override
	@GET
	public FileInfo getFileInfo(String fileID) {
		FileInfo fileinfo = null;
		Call<FileInfo> call = watsonWorkClient.getFileInfo(authService.getAppAuthToken(), fileID);
		Response<FileInfo> response;
		try {
			response = call.execute();

			String body = "[empty]";
			if (response == null) {
				body = "response: null for " + fileID;
			} else {
				if (response.errorBody() != null)
					body = MessageFormat.format("Error for {0}\n{1}", fileID, response.errorBody().string());
				if (response.body() != null) {
					body = response.body().toString();
					log.info(MessageFormat.format("Got file info:\n{0}\nisSuccessful(): {1}", body,
							response.isSuccessful()));
					fileinfo = response.body();
				}
			}
		} catch (IOException e) {
			log.error("Error while retrieving File Info for ID " + fileID, e);
		}
		return fileinfo;
	}

	/**
	 * @param downloadURL
	 */
	@Override
	@GET
	public byte[] downloadFile(@NotBlank String downloadURL) {
		byte[] fileBytes = {};
		Call<ResponseBody> downloadCall = watsonWorkClient.getFile(authService.getAppAuthToken(), downloadURL);

		try {
			Response<ResponseBody> downloadResponse = downloadCall.execute();
			log.debug(MessageFormat.format("Download: {0} Auth: {1} ({2}) {3}", downloadURL,
					authService.getAppAuthToken(), downloadResponse.code(), downloadResponse.message()));
			if (downloadResponse.isSuccessful()) {
				ResponseBody downloadBody = downloadResponse.body();
				fileBytes = downloadBody.bytes();
				log.info(MessageFormat.format("Downloaded {0} bytes", fileBytes.length));

			}
		} catch (IOException e) {
			log.error(MessageFormat.format("Error while working with file {0}", downloadURL), e);
		}

		return fileBytes;
	}
}
