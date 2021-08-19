package com.yggboard;


import org.apache.commons.mail.EmailException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


// @Lock(LockType.READ)
@RestController
@RequestMapping("/email")

public class Rest_Email {

	@GetMapping("/sendSimpleEmail")	
	
	@Produces(MediaType.APPLICATION_JSON)
	public String sendSimpleEmail(
			@RequestParam("hostName") String hostName,
			@RequestParam("userName") String userName,
			@RequestParam("password") String password,
			@RequestParam("from") String from,
			@RequestParam("to") String to,
			@RequestParam("subject") String subject,
			@RequestParam("message") String message
			) throws EmailException {
		SendEmail sendEmail = new SendEmail();
		sendEmail.sendEmail(hostName, userName, password, from, to, subject, message);
		return "success";
	};


	@GetMapping("/sendEmailHtml")	
	
	@Produces(MediaType.APPLICATION_JSON)
	public String sendEmailHtml(
			@RequestParam("to") String to,
			@RequestParam("subject") String subject,
			@RequestParam("conteudo") String conteudo
			) throws EmailException {
		conteudo = "<h1>Olá,</h1><br /><p>primeira linha <b>bold</b></p><p>segunda linha</p>";
		SendEmailHtml sendEmailHtml = new SendEmailHtml();
		TemplateEmail templateEmail = new TemplateEmail();
		sendEmailHtml.sendEmailHtml(to, subject, templateEmail.emailYggboard(conteudo));
		return "success";
	};
};
