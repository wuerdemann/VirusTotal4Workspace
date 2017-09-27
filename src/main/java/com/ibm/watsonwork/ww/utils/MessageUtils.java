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
package com.ibm.watsonwork.ww.utils;

import java.awt.Color;
import java.util.Collections;

import javax.validation.constraints.NotNull;

import com.ibm.watsonwork.ww.model.message.Annotation;
import com.ibm.watsonwork.ww.model.message.Message;

public class MessageUtils {

	/**
	 * Creates a new app message object
	 * 
	 * @param messageTitle
	 *            Title for this App-Message
	 * @param messageText
	 *            Text for this Message
	 * @param color
	 *            Color for this App Message
	 * @return new Message
	 */
	public static Message buildMessage(String messageTitle, String messageText, @NotNull Color color) {
		Annotation annotation = new Annotation();
		annotation.setType(MessageTypes.GENERIC_ANNOTATION);
		annotation.setVersion(1.0);
		annotation.setColor(String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue()));
		annotation.setTitle(messageTitle);
		annotation.setText(messageText);
		// Actor actor = new Actor();
		// actor.setUrl("");
		// actor.setAvatar("");
		// actor.setName("Image Helper");
		// annotation.setActor(actor);
		Message message = new Message();
		message.setType(MessageTypes.APP_MESSAGE);
		message.setVersion(1.0);
		message.setAnnotations(Collections.singletonList(annotation));
		return message;
	}

	/**
	 * Creates a new app message object with default color (@see
	 * {@link MessageUtils#buildMessage(String, String, Color)}
	 * 
	 * @param messageTitle
	 *            Title for this App-Message
	 * @param messageText
	 *            Text for this Message
	 * @return new Message
	 */
	public static Message buildMessage(String messageTitle, String messageText) {
		return buildMessage(messageTitle, messageText, new Color(0x13, 0x12, 0x72));
	}
}
