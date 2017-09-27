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

public class MessageTypes {

	// Webhook event types
	public static final String MESSAGE_CREATED = "message-created";
	public static final String SPACE_MEMBERS_ADDED = "space-members-added";
	public static final String SPACE_MEMBERS_REMOVED = "space-members-removed";
	public static final String MESSAGE_ANNOTATION_ADDED = "message-annotation-added";
	public static final String MESSAGE_ANNOTATION_REMOVED = "message-annotation-removed";
	public static final String MESSAGE_ANNOTATION_EDITED = "message-annotation-edited";
	public static final String VERIFICATION = "verification";

	// Webhook annotation types
	public static final String GENERIC_ANNOTATION = "generic";
	public static final String MENTION_ANNOTATION = "mention";
	public static final String MOMENT_ANNOTATION = "conversation-moment";
	public static final String FOCUS_ANNOTATION = "message-focus";

	// Inbound webhook message type
	public static final String APP_MESSAGE = "appMessage";
	public static final String FORM_DATA_FILE = "file";

}
