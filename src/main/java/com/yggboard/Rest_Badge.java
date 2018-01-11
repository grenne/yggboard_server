package com.yggboard;


import java.util.ArrayList;
import java.util.Map;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;

	
@Singleton
// @Lock(LockType.READ)
@Path("/badges")

public class Rest_Badge {

	MongoClient  mongo = new MongoClient();
	
	@SuppressWarnings({ "unchecked" })
	@Path("/obter")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject ObterBadge(@QueryParam("id") String id)  {
		Commons_DB commons_db = new Commons_DB();
		BasicDBObject cursor = commons_db.getCollection(id, "badges", "documento.id", mongo, false);
		if (cursor != null){
			JSONObject documento = new JSONObject();
			BasicDBObject obj = (BasicDBObject) cursor.get("documento");
			documento.put("documento", obj);
			mongo.close();
			return documento;
		};
		mongo.close();
		return null;
	};
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Path("/lista")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray ObterBadges() {
		Commons_DB commons_db = new Commons_DB();
		JSONArray cursor = commons_db.getCollectionListaNoKey("badges", mongo, false);
		if (cursor != null){
			JSONArray documentos = new JSONArray();
			for (int i = 0; i < cursor.size(); i++) {
				BasicDBObject objBadges = new BasicDBObject();
				objBadges.putAll((Map) cursor.get(i));
				JSONObject jsonDocumento = new JSONObject();
				jsonDocumento.put("_id", objBadges.getString("_id"));
				jsonDocumento.put("documento", objBadges);
	    	ArrayList arrayListHabilidades = new ArrayList(); 
	    	arrayListHabilidades = (ArrayList) objBadges.get("habilidades");
	    	Object arrayHabilidades[] = arrayListHabilidades.toArray(); 
				int w = 0;
				JSONArray habilidadesArray = new JSONArray();
				while (w < arrayHabilidades.length) {
					BasicDBObject cursorHabilidade = commons_db.getCollection(arrayHabilidades[w].toString(), "objetivos", "documento.id", mongo, false);
					if (cursorHabilidade != null){
						BasicDBObject jsonHabilidades = new BasicDBObject();
						jsonHabilidades.put("documento", cursorHabilidade.get("documento"));
						habilidadesArray.add (jsonHabilidades);
					};
					++w;
				};
				jsonDocumento.put("arrayHabilidades", habilidadesArray);
				documentos.add(jsonDocumento);
			};
			
			mongo.close();
			return documentos;
		};
		mongo.close();
		return null;
	};
};
