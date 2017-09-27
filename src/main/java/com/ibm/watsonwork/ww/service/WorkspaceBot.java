/**
 * 
 */
package com.ibm.watsonwork.ww.service;

import org.springframework.http.ResponseEntity;

import com.ibm.watsonwork.ww.model.webhook.WebhookEvent;

/**
 * @author miwu
 *
 */
public interface WorkspaceBot {

	// acts when a Message is created
	public ResponseEntity<?> handleMessageCreate(WebhookEvent event);

	// acts when an Annotation is added
	public ResponseEntity<?> handleAnnotationAdded(WebhookEvent webhookEvent);

	// acts when an Annotation is changed
	public ResponseEntity<?> handleAnnotationChanged(WebhookEvent event);

	// acts when an Annotation is removed
	public ResponseEntity<?> handleAnnotationRemoved(WebhookEvent webhookEvent);

	// acts when a member is added to a space
	public ResponseEntity<?> handleMemberAdded(WebhookEvent webhookEvent);

	// acts when a member is removed from a space
	public ResponseEntity<?> handleMemberRemoved(WebhookEvent webhookEvent);

	// TODO: Add all the other events
}
