package com.yggboard.yggboard_server;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

public class SendEmailHtml {
	
	public void sendEmailHtml(String hostName, String userName, String password, String from, String to, String subject, String html ){
	    
		HtmlEmail email = new HtmlEmail();
		
//		String newEmail = "<div><h3 style='color: #1c70db;'>NOW $159</h3></div>";

		try {
			email.setHostName(hostName);
			email.setSmtpPort(587);
			email.setAuthenticator(new DefaultAuthenticator("postmasterygg@yggboard.com", "ygglicious13"));
			email.setStartTLSEnabled(true);
			email.setFrom(from);
			email.setSubject(subject);
			email.setHtmlMsg(html);
			email.addTo(to);
			email.send();
			email.setTextMsg("Your email client does not support HTML messages");
		} catch(EmailException ee) {
		    ee.printStackTrace();
		}

	};
};