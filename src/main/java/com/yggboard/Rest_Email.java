package com.yggboard;


import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.commons.mail.EmailException;

	
@Singleton
// @Lock(LockType.READ)
@Path("/email")

public class Rest_Email {

	@Path("/sendSimpleEmail")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String sendSimpleEmail(
			@QueryParam("hostName") String hostName, 
			@QueryParam("userName") String userName,
			@QueryParam("password") String password,
			@QueryParam("from") String from,
			@QueryParam("to") String to,
			@QueryParam("subject") String subject,
			@QueryParam("message") String message
			) throws EmailException {
		SendEmail sendEmail = new SendEmail();
		sendEmail.sendEmail(hostName, userName, password, from, to, subject, message);
		return "success";
	};


	@Path("/sendEmailHtml")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String sendEmailHtml(
			@QueryParam("to") String to,
			@QueryParam("subject") String subject,
			@QueryParam("conteudo") String conteudo
			) throws EmailException {
		conteudo = "<h1>Olá,</h1><br /><p>primeira linha <b>bold</b></p><p>segunda linha</p>";
		SendEmailHtml sendEmailHtml = new SendEmailHtml();
		TemplateEmail templateEmail = new TemplateEmail(); 
		sendEmailHtml.sendEmailHtml(to, subject, templateEmail.emailYggboard(conteudo));
		return "success";
	};
};
