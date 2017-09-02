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
package net.miwu.virustotal.ww.controller;

import static net.miwu.virustotal.ww.WatsonWorkConstants.X_OUTBOUND_TOKEN;
import static net.miwu.virustotal.ww.utils.MessageTypes.VERIFICATION;

import java.text.MessageFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import lombok.extern.slf4j.Slf4j;
import net.miwu.virustotal.ww.model.WebhookEvent;
import net.miwu.virustotal.ww.service.AuthService;
import net.miwu.virustotal.ww.service.WorkspaceBot;
import net.miwu.virustotal.ww.utils.MessageTypes;

/**
 * @author miwu
 *
 */
@Controller
@Slf4j
public class WatsonWorkController {

	// @Autowired
	// private WatsonWorkProperties watsonWorkProperties;
	// @Autowired
	// private WatsonWorkService watsonWorkService;
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
		log.info(MessageFormat.format("Webhook-Event of Type {0}", webhookEvent.getType()));

		switch (webhookEvent.getType()) {
			case MessageTypes.MESSAGE_CREATED:
				response = bot.handleMessageCreate(webhookEvent);
				break;
			case MessageTypes.MESSAGE_ANNOTATION_EDITED:
				response = bot.handleAnnotationChanged(webhookEvent);
				break;

			default:
				break;
		}
		return response;
	}

}
