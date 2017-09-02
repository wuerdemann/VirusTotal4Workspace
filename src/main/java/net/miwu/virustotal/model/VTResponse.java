/**
 * 
 */
package net.miwu.virustotal.model;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.*;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "scans", "scan_id", "sha1", "resource", "response_code", "scan_date", "permalink", "verbose_msg",
		"total", "positives", "sha256", "md5" })
public class VTResponse {

	@JsonProperty("scans")
	public Scans scans;
	@JsonProperty("scan_id")
	public String scanId;
	@JsonProperty("sha1")
	public String sha1;
	@JsonProperty("resource")
	public String resource;
	@JsonProperty("response_code")
	public Long responseCode;
	@JsonProperty("scan_date")
	public String scanDate;
	@JsonProperty("permalink")
	public String permalink;
	@JsonProperty("verbose_msg")
	public String verboseMsg;
	@JsonProperty("total")
	public Long total;
	@JsonProperty("positives")
	public Long positives;
	@JsonProperty("sha256")
	public String sha256;
	@JsonProperty("md5")
	public String md5;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}

	@JsonAnySetter
	public void setAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
	}

}
