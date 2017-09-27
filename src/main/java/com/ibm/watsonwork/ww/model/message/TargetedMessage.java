/**
 * 
 */
package com.ibm.watsonwork.ww.model.message;

import java.util.List;

import lombok.Data;

/**
 * @author miwu
 *
 */
@Data
public class TargetedMessage {

	private String conversationId;
	private String targetDialogId;
	private String targetUserId;
	private List<Annotation> annotations;
	private Annotation annotation;

}
