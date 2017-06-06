package com.yggboard.yggboard_server;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

public class SendEmail {
	
	public void sendEmail(String hostName, String userName, String password, String from, String to, String subject, String message ){
	    
		Email email = new SimpleEmail();

		try {
			email.setHostName(hostName);
			email.setSmtpPort(587);
			email.setAuthenticator(new DefaultAuthenticator(userName, "ygglicius13"));
			email.setStartTLSEnabled(true);
			email.setFrom(from);
			email.setSubject(subject);
			email.setMsg(message);
			email.addTo(to);
			System.out.println(email);
			email.send();
		} catch(EmailException ee) {
		    ee.printStackTrace();
		}

	};
};