/**
 * 
 */
package net.miwu.virustotal.ww.service;

import org.springframework.http.ResponseEntity;

import net.miwu.virustotal.ww.model.WebhookEvent;

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
