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

	// acts when an Annotation is changed
	public ResponseEntity<?> handleAnnotationChanged(WebhookEvent event);

	// TODO: Add all the other events
}
