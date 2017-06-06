package com.yggboard.yggboard_server;

public class TemplateEmail {

	public String emailHtml(String familyName, String emailFamily, String studentName, String emailStudent, String subject, String emailBody){
		String email =
		"		<html>" +
		"		<body>" +
		"		    <table width=\"650px\" cellspacing=\"0\" cellpadding=\"0\" border=\"0.2\" color:\"blue\">" +
		"		        <tr>" +
		"		            <td class=\"navbar navbar-inverse\" align=\"center\">" +
		"		                <!-- This setup makes the nav background stretch the whole width of the screen. -->" +
		"		                <table width=\"650px\" cellspacing=\"0\" cellpadding=\"3\" class=\"container\">" +
		"		                    <tr class=\"navbar navbar-inverse\">" +
		"		                        <td colspan=\"4\">" +
		"									<img src=\"http://52.27.128.28:8080/casamundo/img/logo/casatoronto.png\"></img>" + 
		"								</td>" +
		"		                        <td>" +
		"									<ul>" +
		"										<li>" +
		"											<span>+1(416)897-7141</span>" +
		"										</li>" +
		"										<li>" +
		"											<a href=\"mailto:josematheus@casa-toronto.com\">josematheus@casa-toronto.com</a>" +
		"										</li>" +
		"										<li>" +
		"											<a href=\"www.casa-toronto.com\">www.casa-toronto.com</a>" +
		"										</li>" +
		"										<li>" +
		"											<span >153 Beecroft Rd, suite 1512</span>" +
		"										</li>" +
		"										<li>" +
		"											<span >Toronto, ON Canada M2N 7C5</span>" +
		"										</li>" +
		"									</ul>" +
		"								</td>" +
		"		                    </tr>" +
		"		                </table>" +
		"		            </td>" +
		"		        </tr>" +
		"		        <tr>" +
		"		            <td bgcolor=\"white\" align=\"left\">" +
		"	    	            <table width=\"900px\" cellspacing=\"0\" cellpadding=\"3\" align=\"center\">" +
		"	                    <tr>" +
		"	                        <td align=\"left\">" +
		"										<p >" +
		"											<span style=\"font-style: italic;font-size: 20px;\" >Family <small id=\"familyName\" style=\"font-style: italic;font-size: 20px;\">" + familyName + "</small></span>" +
		"										</p>" +
		"										<p >" +
		"											<span style=\"font-style: italic;font-size: 20px;\" >Email <small id=\"emailFamily\" style=\"font-style: italic;font-size: 20px;\">" + emailFamily + "</small></span>" +
		"										</p>" +
		"	                        </td>" +
		"	                    </tr>" +
		"		                </table>" +
		"		            </td>" +
		"		        </tr>" +
		"		        <tr>" +
		"		            <td bgcolor=\"white\" align=\"left\">" +
		"	    	            <table width=\"900px\" cellspacing=\"0\" cellpadding=\"3\" align=\"center\">" +
		"	                    <tr>" +
		"	                        <td align=\"left\">" +
		"										<p >" +
		"											<span style=\"font-style: italic;font-size: 20px;\" >Student <small id=\"studentName\" style=\"font-style: italic;font-size: 20px;\">" + studentName + "</small></span>" +
		"										</p>" +
		"										<p >" +
		"											<span style=\"font-style: italic;font-size: 20px;\" >Email <small id=\"emailStudent\" style=\"font-style: italic;font-size: 20px;\">" + emailStudent + "</small></span>" +
		"										</p>" +
		"	                        </td>" +
		"	                    </tr>" +
		"		                </table>" +
		"		            </td>" +
		"		        </tr>" +
		"               <tr>" +
		"	   	            <table width=\"900px\" cellspacing=\"0\" cellpadding=\"3\" align=\"center\">" +
		"                    <tr>" +
		"						<td align=\"left\">" +
		"							<small style=\"font-size: 15px; font-style: italic;\" ></small><small style=\"color: blue; font-size: 15px;\" >" + emailBody + "</small>" +
		"	                    </td>" +
		"	           		</tr>" +
		"	       			</table>" +
		"	        </tr>" +
		"	    </table>" +
		"	</body>" +
		"	</html>";
					
		return email;
		
	};
}
