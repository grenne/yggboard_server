package com.yggboard.yggboard_server;

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
			@QueryParam("hostName") String hostName, 
			@QueryParam("userName") String userName,
			@QueryParam("password") String password,
			@QueryParam("from") String from,
			@QueryParam("to") String to,
			@QueryParam("subject") String subject,
			@QueryParam("html") String html
			) throws EmailException {
		SendEmailHtml sendEmailHtml = new SendEmailHtml();
		sendEmailHtml.sendEmailHtml(hostName, userName, password, from, to, subject, html);
		return "success";
	};
};
