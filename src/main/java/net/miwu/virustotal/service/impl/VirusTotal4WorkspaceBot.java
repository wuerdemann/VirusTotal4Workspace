/**
 * 
 */
package net.miwu.virustotal.service.impl;

import java.awt.Color;
import java.io.IOException;
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
import net.miwu.virustotal.AppProperties;
import net.miwu.virustotal.client.VirusTotalClient;
import net.miwu.virustotal.model.VTResponse;
import net.miwu.virustotal.ww.WatsonWorkProperties;
import net.miwu.virustotal.ww.model.WebhookEvent;
import net.miwu.virustotal.ww.model.fileinfo.Entry;
import net.miwu.virustotal.ww.model.fileinfo.FileInfo;
import net.miwu.virustotal.ww.service.WatsonWorkService;
import net.miwu.virustotal.ww.service.WorkspaceBot;
import net.miwu.virustotal.ww.utils.MessageUtils;
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

	@Autowired
	private WatsonWorkService wwService;

	@Autowired
	private WatsonWorkProperties wwProps;

	@Autowired
	private AppProperties vtProps;

	@Autowired
	private VirusTotalClient vtClient;

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

	@Override
	@Async(value = "WebhookThreadPoolExecutor")
	public ResponseEntity<?> handleAnnotationChanged(WebhookEvent event) {
		log.info(event.toString());
		return ResponseEntity.ok().build();
	}

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
		log.info("Call created for apikey: " + vtAPIKey + ": " + virusTotalReport.request().url().redact());
		try {
			Response<VTResponse> vtReport = virusTotalReport.execute();
			log.info("Call executed: isSuccessful: " + vtReport.isSuccessful());
			if (vtReport.isSuccessful()) {
				report = vtReport.body();
				log.info("Report: " + report.verboseMsg + ": " + report.positives + " of " + report.total);
				log.info("\n" + report.toString());
			} else {
				log.error("VT-Error: " + vtReport.code() + " " + vtReport.message());
			}
		} catch (IOException e) {
			log.error("Error while retrieving VT-Report", e);
		}
		return report;
	}
}
