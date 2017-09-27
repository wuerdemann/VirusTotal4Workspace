/**
 * 
 */
package com.ibm.watsonwork.ww.model.webhook;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * @author miwu
 *
 */
@Data
public class Keyword {

	@JsonProperty("text")
	private String text;

	@JsonProperty("relevance")
	private String relevance;

}
