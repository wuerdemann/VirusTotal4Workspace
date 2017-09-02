/*******************************************************************************
 * Copyright 2017 IBM Corp. All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.miwu.virustotal.ww.utils;

import static org.junit.Assert.assertEquals;

import java.awt.Color;

import org.junit.Test;

import net.miwu.virustotal.ww.model.message.Message;

/**
 * Tests the MessageUtil class
 * 
 * @see MessageUtils
 * @author miwu
 */
public class MessageUtilsTest {

	private static final String DEFAULT_COLOR = "#131272";
	private static final String TEST_COLOR = "#ff0000";
	private static final String TEST_TEXT = "TestText";
	private static final String TEST_TITLE = "TestTitle";

	@Test
	public void testBuildMessageStringStringColor() {
		Message msg = MessageUtils.buildMessage(TEST_TITLE, TEST_TEXT, Color.decode(TEST_COLOR));
		assertEquals("Title not set as expected", TEST_TITLE, msg.getAnnotations().get(0).getTitle());
		assertEquals("Text not set as expected", TEST_TEXT, msg.getAnnotations().get(0).getText());
		assertEquals("Color not set as expected", TEST_COLOR, msg.getAnnotations().get(0).getColor());

	}

	@Test
	public void testBuildMessageStringString() {
		assertEquals("Default color not set to " + DEFAULT_COLOR,
				MessageUtils.buildMessage(TEST_TITLE, TEST_TEXT, Color.decode(DEFAULT_COLOR)),
				MessageUtils.buildMessage(TEST_TITLE, TEST_TEXT));
	}

}
