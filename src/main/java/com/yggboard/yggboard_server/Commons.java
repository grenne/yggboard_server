package com.yggboard.yggboard_server;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import javax.ws.rs.core.Response;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.mongodb.BasicDBObject;


public class Commons {

	public Response testaToken(String token) {
		Commons_DB commons_db = new Commons_DB();
		if (commons_db.getCollection(token, "usuarios", "documento.token") == null){
			return Response.status(401).entity("invalid token").build();	
		}else {
			return Response.status(200).entity("token ok").build();
		}
	};
	
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
	
	@SuppressWarnings("null")
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

	public Object nomeHabilidade(String id) {
		Commons_DB commons_db = new Commons_DB();
		BasicDBObject doc = commons_db.getCollection(id, "habilidades", "documento.id");
		String nome = "";
		if (doc != null){
			BasicDBObject objDoc = (BasicDBObject) doc.get("documento");
			nome = objDoc.get("nome").toString();
			return nome;
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

	public Boolean testaElementoArrayObject(BasicDBObject elemento, ArrayList<BasicDBObject> array) {

		for (int w = 0; w < array.size(); w++) {
			if (array.get(w) == elemento){
				return true;
			};
		};
		return false;
	};

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public JSONArray montaArrayPerfil(Object perfil, Object elementos) {
		JSONArray array = new JSONArray();
		if (perfil != null && elementos != null){
			for (int i = 0; i < ((ArrayList) elementos).size(); i++) {
				Boolean existe = false;
				for (int z = 0; z < ((ArrayList) perfil).size(); z++) {
					if (((ArrayList) perfil).get(z).toString().equals(((ArrayList) elementos).get(i).toString())){
						existe = true;
					};
				};
				array.add(existe);
			};
		};
		return array;
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

	@SuppressWarnings("unchecked")
	public JSONArray addString(JSONArray array, String elemento) {

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

	public Response insereEvento(BasicDBObject evento) {
	
		Commons_DB commons_db = new Commons_DB();

		BasicDBObject insertDoc = new BasicDBObject();
		
		insertDoc.put("idUsuario", evento.get("idUsuario"));
		insertDoc.put("evento", evento.get("evento"));
		insertDoc.put("idEvento", evento.get("idEvento"));
		insertDoc.put("motivo", evento.get("motivo"));
		insertDoc.put("elemento", evento.get("elemento"));
		insertDoc.put("idElemento", evento.get("idElemento"));
		insertDoc.put("data", todaysDate("inv_month_number"));
		insertDoc.put("status", "nao lido");

		BasicDBObject doc = new BasicDBObject();
		
		doc.put("documento", insertDoc);
		
		return commons_db.incluirCrud("eventos", doc);
	};
	
	public byte[] gerarHash(String frase) {
		  try {
		    MessageDigest md = MessageDigest.getInstance("MD5");
		    md.update(frase.getBytes());
		    return md.digest();
		  } catch (NoSuchAlgorithmException e) {
		    return null;
		  }
	};	
	public String stringHexa(byte[] bytes) {
		   StringBuilder s = new StringBuilder();
		   for (int i = 0; i < bytes.length; i++) {
		       int parteAlta = ((bytes[i] >> 4) & 0xf) << 4;
		       int parteBaixa = bytes[i] & 0xf;
		       if (parteAlta == 0) s.append('0');
		       s.append(Integer.toHexString(parteAlta | parteBaixa));
		   }
		   return s.toString();
	};

	public String totalArray (Object objectArray){
		ArrayList<?> arrayList = new ArrayList<Object>(); 
		arrayList = (ArrayList<?>) objectArray;

		if (arrayList != null ){
			Object array[] = arrayList.toArray();
			return Integer.toString(array.length);
		};
		return "0";
	};
	@SuppressWarnings("unchecked")
	public JSONObject getProperties(){
		Properties prop = new Properties();
		InputStream input = null;
		try {
		    input = getClass().getClassLoader().getResourceAsStream("config.properties");
		    // load a properties file
		    prop.load(input);
		    JSONObject properties = new JSONObject();
		    properties.put("database", prop.getProperty("database"));
		    properties.put("dbuser", prop.getProperty("dbuser"));
		    properties.put("dbpassword", prop.getProperty("dbpassword"));
		    return properties;
		} catch (IOException ex) {
		    ex.printStackTrace();
		} finally {
		    if (input != null) {
		        try {
		            input.close();
		        } catch (IOException e) {
		            e.printStackTrace();
		        }
		    }
		}
		return null;
	};
};
