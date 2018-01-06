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
package com.ibm.watsonwork.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.extern.slf4j.Slf4j;

/**
 * @author miwu
 *
 */
@Controller
@Slf4j
public class VirusTotal4WorkspaceController {

	/*
	 * Returns Homepage-Template
	 */
	@GetMapping("/")
	public String getHomepage(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) {
		log.info("Homepage is called");
		return "index";
	}
}
