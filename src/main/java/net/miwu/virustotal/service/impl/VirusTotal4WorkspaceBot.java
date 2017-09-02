/**
 * 
 */
package net.miwu.virustotal.service.impl;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.validation.constraints.NotNull;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import net.miwu.virustotal.ww.WatsonWorkProperties;
import net.miwu.virustotal.ww.model.WebhookEvent;
import net.miwu.virustotal.ww.model.fileinfo.Entry;
import net.miwu.virustotal.ww.model.fileinfo.FileInfo;
import net.miwu.virustotal.ww.service.WatsonWorkService;
import net.miwu.virustotal.ww.service.WorkspaceBot;
import net.miwu.virustotal.ww.utils.MessageUtils;

/**
 * This is the Bot
 * 
 * @author miwu
 */
@Service
@Slf4j
@EnableAsync
public class VirusTotal4WorkspaceBot implements WorkspaceBot {

	@Autowired
	private WatsonWorkService wwService;

	@Autowired
	private WatsonWorkProperties wwProps;

	@Override
	@Async(value = "WebhookThreadPoolExecutor")
	public ResponseEntity<?> handleMessageCreate(@NotNull WebhookEvent event) {
		log.info(MessageFormat.format("handleMessageCreate(event):{0}", event));
		if (StringUtils.equals(wwProps.getAppId(), event.getUserId())) {
			log.info("ignoring self messages...");
			return ResponseEntity.ok().build();
		}

		String msgcontent = event.getContent();
		String[] images = StringUtils.substringsBetween(msgcontent, "<$image|", "|");
		String[] files = StringUtils.substringsBetween(msgcontent, "<$file|", "|");
		String[] joinedArray = ArrayUtils.addAll(images, files);
		Set<String> attachments = new LinkedHashSet<String>(0);
		if (null != joinedArray) attachments.addAll(Arrays.asList(joinedArray));
		// if (log.isDebugEnabled())
		wwService.createMessage(event.getSpaceId(), MessageUtils.buildMessage("Debug Echo", MessageFormat
				.format("{0}\nDateien: {1}", StringEscapeUtils.escapeHtml4(msgcontent), attachments.size())));
		if (attachments.size() > 0) {
			for (String fileID : attachments) {
				log.info("Request for FileID: " + fileID);
				FileInfo fileInfo = wwService.getFileInfo(fileID);
				if (fileInfo != null) {
					for (Entry entry : fileInfo.entries) {
						log.info(MessageFormat.format("Entry: {0} ({1})", entry.getName(), entry.contentType));
						String downloadURL = entry.getUrls().getRedirectDownload();
						byte[] fileBytes = wwService.downloadFile(downloadURL);
						try {
							MessageDigest digest = MessageDigest.getInstance("SHA256");
							Date now = new Date();
							byte[] hash = digest.digest(fileBytes);
							long timetohash = new Date().getTime() - now.getTime();
							String sha256 = DatatypeConverter.printHexBinary(hash);
							log.info(MessageFormat.format("Hash SHA-256 for {0} is {1} in {2}ms", downloadURL, sha256,
									timetohash));
						} catch (NoSuchAlgorithmException e) {
							log.error("Error while computing SHA-256", e);
						}
					}
				}
			}
		}

		return ResponseEntity.ok().build();
	}

	@Override
	@Async(value = "WebhookThreadPoolExecutor")
	public ResponseEntity<?> handleAnnotationChanged(WebhookEvent event) {
		log.info(event.toString());
		return ResponseEntity.ok().build();
	}

}
