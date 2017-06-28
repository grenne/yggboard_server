package com.yggboard.yggboard_server;

import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.sun.xml.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;


public class Commons {
	
	public Boolean verifyInterval (String date, String initInterval, String endInterval){
	
		DateFormat df = new SimpleDateFormat ("dd/MM/yyyy");
		try {
			if (initInterval != null && endInterval != null){
				Date d1 = df.parse (convertDateMes (date));
				Date d2 = df.parse (convertDateMes (initInterval));
				Date d3 = df.parse (convertDateMes (endInterval));
				long d1_time = d1.getTime();
				long d2_time = d2.getTime();
				long d3_time = d3.getTime();
				if (d1_time >= d2_time && d1_time <=d3_time){
					return true;
				}
			};
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		}
		return false;
	};
	
	public Long currentTime (){		
		Date d2 = new Date(System.currentTimeMillis()); 
		return d2.getTime();
	};
	
	public Long calcAge (String birthDate){
		
		DateFormat df = new SimpleDateFormat ("dd/MM/yyyy");
		try {
			Date d1 = df.parse (convertDateMes (birthDate));
			Date d2 = new Date(System.currentTimeMillis()); 
			long dt = (d2.getTime() - d1.getTime()) + 3600000;
			return ((dt / 86400000L) / 365L);
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		}
		return null;
	};
	
	public int difDate (String start, String end){
		
		DateFormat df = new SimpleDateFormat ("dd/MM/yyyy");
		try {
			Date d1 = df.parse (convertDateMes (start));
			Date d2 = df.parse (convertDateMes (end)); 
			long dt = (d2.getTime() - d1.getTime()) + 3600000;
			int daysInBetween = (int) (dt / (24*60*60*1000));
			return daysInBetween;
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		}
		return (Integer) null;
	};

	public String calcNewDate (String date, int days){
		
		DateFormat df = new SimpleDateFormat ("ddMMyyyy");
		Calendar cal = Calendar.getInstance();   
		cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(convertDateMes(date).substring(0, 2)));
		cal.set(Calendar.MONTH, (Integer.parseInt(convertDateMes(date).substring(3, 5)) ) - 1 );
		cal.set(Calendar.YEAR, Integer.parseInt(convertDateMes(date).substring(6, 10)));
		cal.add(Calendar.DAY_OF_MONTH, 5);
		return convertDateMesAlfa(df.format(cal.getTime())).replaceAll("/", "");
	};

	public Calendar convertToCalendar (String date){
		
		Calendar cal = Calendar.getInstance();   
		cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(convertDateMes(date).substring(0, 2)));
		cal.set(Calendar.MONTH, (Integer.parseInt(convertDateMes(date).substring(3, 5)) ) - 1 );
		cal.set(Calendar.YEAR, Integer.parseInt(convertDateMes(date).substring(6, 10)));
		return cal;
	};

	@SuppressWarnings("unused")
	public String todaysDate(String type) {
		
		Calendar calendar = Calendar.getInstance();
		//getTime() returns the current date in default time zone
		Date date = calendar.getTime();
		int day = calendar.get(Calendar.DATE);
		//Note: +1 the month for current month
		int month = calendar.get(Calendar.MONTH) + 1;
		int year = calendar.get(Calendar.YEAR);
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
		int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
		
		String dayString = "";
		String monthString = "";
		String yearString = "";

		if (day < 10){
			dayString = "0" + String.valueOf(day);
 		}else{
 			dayString = String.valueOf(day);
 		};

		if (month < 10){
			monthString = "0" + String.valueOf(month);
 		}else{
 			monthString = String.valueOf(month);
 		};
 		
 		String dateString = "";
 		if (type == "inv_month_number"){
 			dateString = String.valueOf(year) + monthString + dayString;
 		};
		  
		return dateString;
   
	}
	
	public Long calcTime (String date){
		DateFormat df = new SimpleDateFormat ("dd/MM/yyyy");
		try {
			Date d1 = df.parse (convertDateMes (date));
			long dt = d1.getTime();
			return dt;
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		}
		return null;
	};

	public String convertDateMes (String strDate){
		String mesNumber = "01";
		String mesAlpha = strDate.substring	(2, 5);
	    if (mesAlpha.equals("Jan")){
	    	mesNumber = "01";
	    };
	    if (mesAlpha.equals("Feb")){
	    	mesNumber = "02";
	    };
	    if (mesAlpha.equals("Mar")){
	    	mesNumber = "03";
	    };
	    if (mesAlpha.equals("Apr")){
	    	mesNumber = "04";
	    };
	    if (mesAlpha.equals("May")){
	    	mesNumber = "05";
	    };
	    if (mesAlpha.equals("Jun")){
	    	mesNumber = "06";
	    };
	    if (mesAlpha.equals("Jul")){
	    	mesNumber = "07";
	    };
	    if (mesAlpha.equals("Aug")){
	    	mesNumber = "08";
	    };
	    if (mesAlpha.equals("Sep")){
	    	mesNumber = "09";
	    };
	    if (mesAlpha.equals("Oct")){
	    	mesNumber = "10";
	    };
	    if (mesAlpha.equals("Nov")){
	    	mesNumber = "11";
	    };
	    if (mesAlpha.equals("Dec")){
	    	mesNumber = "12";
	    };
		return strDate.substring(0, 2) + "/" + mesNumber + "/" + strDate.substring(5, 9);
	};

	public String convertDateMesAlfa (String strDate){
		String mesAlpha = "Jan";
		String mesNumber = strDate.substring	(2, 4);
	    if (mesNumber.equals("01")){
	    	mesAlpha = "Jan";
	    };
	    if (mesNumber.equals("02")){
	    	mesAlpha = "Feb";
	    };
	    if (mesNumber.equals("03")){
	    	mesAlpha = "Mar";
	    };
	    if (mesNumber.equals("04")){
	    	mesAlpha = "Apr";
	    };
	    if (mesNumber.equals("05")){
	    	mesAlpha = "May";
	    };
	    if (mesNumber.equals("06")){
	    	mesAlpha = "Jun";
	    };
	    if (mesNumber.equals("07")){
	    	mesAlpha = "Jul";
	    };
	    if (mesNumber.equals("08")){
	    	mesAlpha = "Aug";
	    };
	    if (mesNumber.equals("09")){
	    	mesAlpha = "Sep";
	    };
	    if (mesNumber.equals("10")){
	    	mesAlpha = "Out";
	    };
	    if (mesNumber.equals("11")){
	    	mesAlpha = "Nov";
	    };
	    if (mesNumber.equals("12")){
	    	mesAlpha = "Dec";
	    };
		return strDate.substring(0, 2) + mesAlpha + strDate.substring(4, 8);
	};

	@SuppressWarnings("rawtypes")
	public Object nomeHabilidade(String id) {
		Commons_DB commons_db = new Commons_DB();
		Response response = commons_db.getCollection(id, "habilidades", "documento.id");
		String nome = "";
		if (!(response.getEntity() instanceof Boolean)){
			BasicDBObject doc = new BasicDBObject();
			doc.putAll((Map) response.getEntity());
			if (doc != null){
				BasicDBObject objDoc = (BasicDBObject) doc.get("documento");
				nome = objDoc.get("nome").toString();
			};
		}else{
		};
		return nome;
	};

	public Boolean testaElementoArray(String elemento, ArrayList<String> array) {

		for (int w = 0; w < array.size(); w++) {
			if (array.get(w).toString().equals(elemento)){
				return true;
			};
		};
		return false;
	};

	@SuppressWarnings("unchecked")
	public JSONArray addObjeto(JSONArray array, BasicDBObject elemento) {

		if (array != null){
			for (int i = 0; i < array.size(); i++) {
				if (array.get(i).equals(elemento)){
					return array;
				};
			};
		}else{
			return array;
		};
		array.add(elemento);
		return array;
	};

	public String preRequisito(String arrayPreRequisito) {
		if (arrayPreRequisito != null){
			String[] array = arrayPreRequisito.replace("|", "&").split("&");
			return array[0].toString();
		}else{
			return "";
		}
	};

};
