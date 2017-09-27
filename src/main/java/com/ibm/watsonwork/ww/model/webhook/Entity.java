/**
 * 
 */
package com.ibm.watsonwork.ww.model.webhook;

import java.util.List;

import lombok.Data;

/**
 *
 * @author miwu
 *
 */
@Data
public class Entity {

	private String text;

	private String type;

	private String source;

	private String count;

	private String relevance;

	private List<String> location;

}
