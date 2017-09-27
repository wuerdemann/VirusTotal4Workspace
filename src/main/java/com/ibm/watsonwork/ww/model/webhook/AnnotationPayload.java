/**
 * 
 */
package com.ibm.watsonwork.ww.model.webhook;

import lombok.Data;

/**
 * @author miwu
 *
 */

@Data
public class AnnotationPayload {

	private String type;
	private String messageId;
	private String annotationId;
	private String version;
	private String created;
	private String createdBy;
	private String updated;
	private String updatedBy;
	private String tokenClientId;
	private String conversationId;
	private String targetDialogId;
	private String referralMessageId;
	private String actionId;
	private String targetUserId;
	private String targetAppId;
	private String hidden;
	private String spaceId;
	private Object momentSummary;

	private String momentId;
	private String algorithm;
	private Object participants;
	private Object startMessage;
	private Object lastUpdatedMessage;
	private String momentVersion;
	private String lens;
	private String category;
	private String start;
	private String end;
	private String phrase;
	private float confidence;
	private Object actions;
	private Object payload;
	private Object context;
	private ExtractedInfoResponse extractedInfo;
	private String applicationId;
	private String lensId;
	private String focusVersion;

}
