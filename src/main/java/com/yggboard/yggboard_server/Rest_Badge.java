package com.yggboard.yggboard_server;

import java.util.ArrayList;
import java.util.Iterator;

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
import com.mongodb.DBObject;

	
@Singleton
// @Lock(LockType.READ)
@Path("/badges")

public class Rest_Badge {

	@SuppressWarnings({ "unchecked" })
	@Path("/obter")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject ObterBadge(@QueryParam("id") String id)  {
		Commons_DB commons_db = new Commons_DB();
		BasicDBObject cursor = commons_db.getCollection(id, "objetivos", "documento.id");
		if (cursor != null){
			JSONObject documento = new JSONObject();
			BasicDBObject obj = (BasicDBObject) cursor.get("documento");
			documento.put("documento", obj);
			return documento;
		};
		return null;
	};
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Path("/lista")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray ObterBadges() {
		Commons_DB commons_db = new Commons_DB();
		BasicDBObject cursor = commons_db.getCollectionListaNoKey("badges");
		if (cursor != null){
			JSONArray documentos = new JSONArray();
			while (((Iterator<DBObject>) cursor).hasNext()) {
				JSONParser parser = new JSONParser(); 
				BasicDBObject objBadges = (BasicDBObject) ((Iterator<DBObject>) cursor).next();
				String documento = objBadges.getString("documento");
				try {
					JSONObject jsonObject; 
					jsonObject = (JSONObject) parser.parse(documento);
					JSONObject jsonDocumento = new JSONObject();
					jsonDocumento.put("_id", objBadges.getString("_id"));
					jsonDocumento.put("documento", jsonObject);
			    	ArrayList arrayListHabilidades = new ArrayList(); 
			    	arrayListHabilidades = (ArrayList) jsonObject.get("habilidades");
			    	Object arrayHabilidades[] = arrayListHabilidades.toArray(); 
					int w = 0;
					JSONArray habilidadesArray = new JSONArray();
					while (w < arrayHabilidades.length) {
						BasicDBObject cursorHabilidade = commons_db.getCollection(arrayHabilidades[w].toString(), "objetivos", "documento.id");
						if (cursorHabilidade != null){
							BasicDBObject jsonHabilidades = new BasicDBObject();
							jsonHabilidades.put("documento", cursorHabilidade.get("documento"));
							habilidadesArray.add (jsonHabilidades);
						};
						++w;
					};
					jsonDocumento.put("arrayHabilidades", habilidadesArray);
					documentos.add(jsonDocumento);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			};
			return documentos;
		};
		return null;
	};
};
