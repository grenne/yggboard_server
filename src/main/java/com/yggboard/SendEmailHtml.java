package com.yggboard;


import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

public class SendEmailHtml {
	
	public void sendEmailHtml(String to, String subject, String html){
	    
		HtmlEmail email = new HtmlEmail();
		
		try {
			email.setHostName("smtp.gmail.com");
			email.setSmtpPort(587);
			email.setAuthenticator(new DefaultAuthenticator("no-reply@yggboard.com", "ygglicious1q2w3e"));
			email.setStartTLSEnabled(true);
			email.setFrom("no-reply@yggboard.com");
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