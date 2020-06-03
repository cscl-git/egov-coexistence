package org.egov.infra.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class TaskController {

	@PostMapping("/task")
	public String showInbox(){
		return "inbox-view";
	}
}
