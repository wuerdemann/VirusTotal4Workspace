package com.ibm.watsonwork.service.impl;

import java.awt.Color;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.Arrays;
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

import com.ibm.watsonwork.AppProperties;
import com.ibm.watsonwork.client.VirusTotalClient;
import com.ibm.watsonwork.model.VTResponse;
import com.ibm.watsonwork.ww.WatsonWorkProperties;
import com.ibm.watsonwork.ww.model.fileinfo.Entry;
import com.ibm.watsonwork.ww.model.fileinfo.FileInfo;
import com.ibm.watsonwork.ww.model.webhook.WebhookEvent;
import com.ibm.watsonwork.ww.service.WatsonWorkService;
import com.ibm.watsonwork.ww.service.WorkspaceBot;
import com.ibm.watsonwork.ww.utils.MessageUtils;

import lombok.extern.slf4j.Slf4j;
import retrofit2.Call;
import retrofit2.Response;

/**
 * This is the Bot
 * 
 * @author miwu
 */
@Service
@Slf4j
@EnableAsync
public class VirusTotal4WorkspaceBot implements WorkspaceBot {

	private static final String VERY_BAD_FILE_SHA256 = "706d185e58c8b55f642262da94f85595975841c94e3cda609b453f79db1ed0ae";

	@Autowired
	private WatsonWorkService wwService;

	@Autowired
	private WatsonWorkProperties wwProps;

	@Autowired
	private AppProperties vtProps;

	@Autowired
	private VirusTotalClient vtClient;

	/**
	 * This method sends a request to VirusTotal and collects the responses
	 * 
	 * @param sha256
	 *            Hash of the file
	 */
	private VTResponse checkHashOnVirusTotal(String sha256) {
		VTResponse report = null;
		String vtAPIKey = vtProps.getVtAPIKey();
		Call<VTResponse> virusTotalReport = vtClient.getVirusTotalReport(vtAPIKey, sha256);
		log.debug(MessageFormat.format("Call created for apikey: {0}: {1}", vtAPIKey,
				virusTotalReport.request().url().redact()));
		try {
			Response<VTResponse> vtReport = virusTotalReport.execute();
			log.info(MessageFormat.format("Call executed: isSuccessful: {0}", vtReport.isSuccessful()));
			if (vtReport.isSuccessful()) {
				report = vtReport.body();
				log.info(MessageFormat.format("Report: {0}: {1} of {2}", report.verboseMsg, report.positives,
						report.total));
				log.debug(MessageFormat.format("\n{0}", report.toString()));
			} else {
				log.error(MessageFormat.format("VT-Error: {0} {1}", vtReport.code(), vtReport.message()));
			}
		} catch (IOException e) {
			log.error("Error while retrieving VT-Report", e);
		}
		return report;
	}

	@Override
	@Async(value = "WebhookThreadPoolExecutor")
	public ResponseEntity<?> handleAnnotationAdded(WebhookEvent webhookEvent) {
		log.debug(webhookEvent.toString());
		return ResponseEntity.ok().build();
	}

	@Override
	@Async(value = "WebhookThreadPoolExecutor")
	public ResponseEntity<?> handleAnnotationChanged(WebhookEvent event) {
		log.debug(event.toString());
		return ResponseEntity.ok().build();
	}

	@Override
	@Async(value = "WebhookThreadPoolExecutor")
	public ResponseEntity<?> handleAnnotationRemoved(WebhookEvent webhookEvent) {
		log.debug(webhookEvent.toString());
		return ResponseEntity.ok().build();
	}

	@Override
	@Async(value = "WebhookThreadPoolExecutor")
	public ResponseEntity<?> handleMemberAdded(WebhookEvent webhookEvent) {
		log.debug(webhookEvent.toString());
		return ResponseEntity.ok().build();
	}

	@Override
	@Async(value = "WebhookThreadPoolExecutor")
	public ResponseEntity<?> handleMemberRemoved(WebhookEvent webhookEvent) {
		log.debug(webhookEvent.toString());
		return ResponseEntity.ok().build();
	}

	@Override
	@Async(value = "WebhookThreadPoolExecutor")
	public ResponseEntity<?> handleMessageCreate(@NotNull WebhookEvent event) {
		log.info(MessageFormat.format("handleMessageCreate(event):{0}", event));
		if (StringUtils.equals(wwProps.getAppId(), event.getUserId())) {
			log.debug("ignoring self messages...");
			return ResponseEntity.ok().build();
		}

		String msgcontent = event.getContent();
		String[] images = StringUtils.substringsBetween(msgcontent, "<$image|", "|");
		String[] files = StringUtils.substringsBetween(msgcontent, "<$file|", "|");
		String[] joinedArray = ArrayUtils.addAll(images, files);
		Set<String> attachments = new LinkedHashSet<String>(0);
		if (null != joinedArray) attachments.addAll(Arrays.asList(joinedArray));
		if (log.isDebugEnabled())
			wwService.createMessage(event.getSpaceId(), MessageUtils.buildMessage("Debug Echo", MessageFormat
					.format("{0}\nNo. of files: {1}", StringEscapeUtils.escapeHtml4(msgcontent), attachments.size())));
		if (attachments.size() > 0) {
			for (String fileID : attachments) {
				log.info("Request for FileID: " + fileID);
				FileInfo fileInfo = wwService.getFileInfo(fileID);
				if (fileInfo != null) {
					for (Entry entry : fileInfo.entries) {
						log.info(MessageFormat.format("Virus-Scan for: {0} ({1} by {2} in space {3})", entry.getName(),
								entry.contentType, event.getUserName(), event.getSpaceName()));
						String downloadURL = entry.getUrls().getRedirectDownload();
						byte[] fileBytes = wwService.downloadFile(downloadURL);
						try {
							MessageDigest digest = MessageDigest.getInstance("SHA256");
							byte[] hash = digest.digest(fileBytes);
							String sha256 = DatatypeConverter.printHexBinary(hash);

							// HACK: for Demo
							if (entry.getName().contains("BadFile")) {
								sha256 = VERY_BAD_FILE_SHA256;
							}

							VTResponse report = checkHashOnVirusTotal(sha256);

							if (report.responseCode == 0) {
								wwService.createMessage(event.getSpaceId(),
										MessageUtils.buildMessage(
												MessageFormat.format("Unknown File: {0} ({1})", entry.getName(),
														entry.getContentType()),
												MessageFormat.format("VirusTotal reports: {0}", report.verboseMsg)));
							} else {
								if (report.positives > 0) {
									wwService.createMessage(event.getSpaceId(), MessageUtils.buildMessage(
											MessageFormat.format("Known Bad File: {0} ({1})", entry.getName(),
													entry.getContentType()),
											MessageFormat.format(
													"VirusTotal reports: {0}\n{1} of {2} virus scanners know this file.\nYou can find the scan report here: {3}",
													report.verboseMsg, report.positives, report.total,
													report.permalink),
											Color.RED));
								} else {

									wwService.createMessage(event.getSpaceId(), MessageUtils.buildMessage(
											MessageFormat.format("Known Good File: {0} ({1})", entry.getName(),
													entry.getContentType()),
											MessageFormat.format(
													"VirusTotal reports: {0}\n{1} of {2} virus scanners know this file.\nYou can find the scan report here: {3}",
													report.verboseMsg, report.positives, report.total,
													report.permalink),
											Color.GREEN));
								}
							}
						} catch (NoSuchAlgorithmException e) {
							log.error("Error while computing SHA-256", e);
						}
					}
				}
			}
		}

		return ResponseEntity.ok().build();
	}
}
