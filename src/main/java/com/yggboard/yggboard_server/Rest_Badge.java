package com.yggboard.yggboard_server;

import java.net.UnknownHostException;
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
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

	
@Singleton
// @Lock(LockType.READ)
@Path("/badges")

public class Rest_Badge {

	@SuppressWarnings("unchecked")
	@Path("/obter")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject ObterBadge(@QueryParam("id") String id) throws UnknownHostException, MongoException {
		Mongo mongo = new Mongo();
		Commons commons = new Commons();
		DB db = (DB) mongo.getDB(commons.getProperties().get("database").toString());
		DBCollection collection = db.getCollection("badges");
		BasicDBObject searchQuery = new BasicDBObject("documento.id", id);
		DBObject cursor = collection.findOne(searchQuery);
		JSONObject documento = new JSONObject();
		BasicDBObject obj = (BasicDBObject) cursor.get("documento");
		documento.put("documento", obj);
		mongo.close();
		return documento;
	};
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Path("/lista")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray ObterBadges() {

		Mongo mongo;
		Commons commons = new Commons();
		try {
			mongo = new Mongo();
			DB db = (DB) mongo.getDB(commons.getProperties().get("database").toString());
			DBCollection collection = db.getCollection("badges");
			BasicDBObject searchQuery = new BasicDBObject();
			DBCursor cursor = collection.find(searchQuery);
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
						Mongo mongoHabilidade = new Mongo();
						DB dbHabilidade = (DB) mongo.getDB(commons.getProperties().get("database").toString());
						DBCollection collectionHabilidade = dbHabilidade.getCollection("habilidades");
						BasicDBObject searchQueryHabilidade = new BasicDBObject("documento.idHabilidade", arrayHabilidades[w]);
						DBObject cursorHabilidade = collectionHabilidade.findOne(searchQueryHabilidade);
						if (cursorHabilidade != null){
							JSONObject jsonHabilidades = new JSONObject();
							jsonHabilidades.put("documento", cursorHabilidade.get("documento"));
							habilidadesArray.add (jsonHabilidades);
						}
						mongoHabilidade.close();
						++w;
					};
					jsonDocumento.put("arrayHabilidades", habilidadesArray);
					documentos.add(jsonDocumento);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			};
			mongo.close();
			return documentos;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (MongoException e) {
			e.printStackTrace();
		}
		return null;
	};

}
