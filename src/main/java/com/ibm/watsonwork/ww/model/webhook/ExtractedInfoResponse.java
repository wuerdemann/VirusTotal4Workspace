/**
 * 
 */
package com.ibm.watsonwork.ww.model.webhook;

import java.util.List;

import lombok.Data;

@Data
public class ExtractedInfoResponse {

	private List<Entity> entities;
	private List<Keyword> keywords;
	private List<Object> dates;

}
