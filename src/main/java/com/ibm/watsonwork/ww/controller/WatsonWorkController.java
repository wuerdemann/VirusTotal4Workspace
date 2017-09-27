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
package com.ibm.watsonwork.ww.controller;

import static com.ibm.watsonwork.ww.WatsonWorkConstants.X_OUTBOUND_TOKEN;
import static com.ibm.watsonwork.ww.utils.MessageTypes.VERIFICATION;

import java.awt.Color;
import java.text.MessageFormat;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ibm.watsonwork.ww.WatsonWorkProperties;
import com.ibm.watsonwork.ww.model.webhook.WebhookEvent;
import com.ibm.watsonwork.ww.service.AuthService;
import com.ibm.watsonwork.ww.service.WatsonWorkService;
import com.ibm.watsonwork.ww.service.WorkspaceBot;
import com.ibm.watsonwork.ww.utils.MessageTypes;
import com.ibm.watsonwork.ww.utils.MessageUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * @author miwu
 *
 */
@Controller
@Slf4j
public class WatsonWorkController {

	private static final String SPACEID_DEBUG_SPACE = "59ba424ee4b03f07ff2b002e";

	@Autowired
	private AuthService authService;

	@Autowired
	private WorkspaceBot bot;

	/**
	 * The Webhook to IBM Watson Workspace (this is where WW is sending events to)
	 * 
	 * @param outboundToken
	 *            taken from the HTTP header X_OUTBOUND_TOKEN
	 * @param webhookEvent
	 *            the event data
	 * @return
	 */
	@PostMapping(value = "/webhook", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> webhookCallback(@RequestHeader(X_OUTBOUND_TOKEN) String outboundToken,
			@RequestBody WebhookEvent webhookEvent) {
		ResponseEntity<?> response = ResponseEntity.badRequest().build();

		// First check
		if (VERIFICATION.equalsIgnoreCase(webhookEvent.getType())
				&& authService.isValidVerificationRequest(webhookEvent, outboundToken)) {
			log.info("Building verification response...");
			response = buildVerificationResponse(webhookEvent);
		} else {
			log.info("Processing Webhook-Event...");
			response = processWebhook(webhookEvent);
		}
		return response;
	}

	private ResponseEntity<?> buildVerificationResponse(WebhookEvent webhookEvent) {
		String responseBody = String.format("{\"response\": \"%s\"}", webhookEvent.getChallenge());
		String verificationHeader = authService.createVerificationHeader(responseBody);
		log.info("webhook verified...");
		return ResponseEntity.status(HttpStatus.OK).header(X_OUTBOUND_TOKEN, verificationHeader).body(responseBody);
	}

	@Autowired
	private WatsonWorkProperties wwProps;

	@Autowired
	private WatsonWorkService wwService;

	/**
	 * Dispatches Webhook-Event to WorkspaceBot-Service
	 * 
	 * @param webhookEvent
	 *            the event-data from IBM Watson Workspace
	 * @return HTTP-Response (should be OK 200, otherwise WW will repeat to send the
	 *         event)
	 */
	private ResponseEntity<?> processWebhook(WebhookEvent webhookEvent) {
		// TODO: Add missing cases for more event-types
		ResponseEntity<?> response = ResponseEntity.ok().build();

		if (!StringUtils.equals(wwProps.getAppId(), webhookEvent.getUserId())
				&& !SPACEID_DEBUG_SPACE.equals(webhookEvent.getSpaceId())) {

			switch (webhookEvent.getType()) {
				case MessageTypes.MESSAGE_CREATED:
					response = bot.handleMessageCreate(webhookEvent);
					break;
				case MessageTypes.MESSAGE_ANNOTATION_ADDED:
					response = bot.handleAnnotationAdded(webhookEvent);
					break;
				case MessageTypes.MESSAGE_ANNOTATION_EDITED:
					response = bot.handleAnnotationChanged(webhookEvent);
					break;
				case MessageTypes.MESSAGE_ANNOTATION_REMOVED:
					response = bot.handleAnnotationRemoved(webhookEvent);
					break;
				case MessageTypes.SPACE_MEMBERS_ADDED:
					response = bot.handleMemberAdded(webhookEvent);
					break;
				case MessageTypes.SPACE_MEMBERS_REMOVED:
					response = bot.handleMemberRemoved(webhookEvent);
					break;

				default:
					// If a debugging space is declared, copy every event to that space as a new
					// message within that space.
					// CAUTION: This might end up in a lot of msgs!
					// NB: This App must be assigned to that Debugging space, too!
					if (StringUtils.isNotBlank(SPACEID_DEBUG_SPACE)) createDebugMessage(webhookEvent);
					break;
			}
		}

		return response;
	}

	/**
	 * @param webhookEvent
	 */
	private void createDebugMessage(WebhookEvent webhookEvent) {
		Gson gson = new GsonBuilder().enableComplexMapKeySerialization().setPrettyPrinting().create();
		String json = gson.toJson(webhookEvent);
		log.info(MessageFormat.format("\nWebhook-Event: {0}\n", json));
		wwService.createMessage(SPACEID_DEBUG_SPACE,
				MessageUtils.buildMessage(
						MessageFormat.format("DEBUG in {0} von {1} ({2})", webhookEvent.getSpaceName(),
								webhookEvent.getUserName(), webhookEvent.getType()),
						MessageFormat.format("``` json\n{0}\n```", json), Color.LIGHT_GRAY));
	}

}
