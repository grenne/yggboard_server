package com.yggboard.yggboard_server;

import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.Map;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

	
@Singleton
// @Lock(LockType.READ)
@Path("/carreiras")

public class Rest_Carreira {

	@SuppressWarnings("unchecked")
	@Path("/obter")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject ObterCarreira(@QueryParam("carreira") String carreira) throws UnknownHostException, MongoException {
		Mongo mongo = new Mongo();
		Commons commons = new Commons();
		DB db = (DB) mongo.getDB(commons.getProperties().get("database").toString());
		DBCollection collection = db.getCollection("objetivos");
		BasicDBObject searchQuery = new BasicDBObject("documento.mail", carreira);
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
	public JSONArray ObterCarreiras() {
		Commons commons = new Commons();
		Mongo mongo;
		try {
			mongo = new Mongo();
			DB db = (DB) mongo.getDB(commons.getProperties().get("database").toString());
			DBCollection collection = db.getCollection("objetivos");
			BasicDBObject searchQuery = new BasicDBObject();			
			DBCursor cursor = collection.find(searchQuery);
			JSONArray documentos = new JSONArray();
			while (((Iterator<DBObject>) cursor).hasNext()) {
				BasicDBObject objCarreiras = (BasicDBObject) ((Iterator<DBObject>) cursor).next();
				JSONObject jsonDocumento = new JSONObject();
				jsonDocumento.put("_id", objCarreiras.getString("_id"));
				BasicDBObject setUpdate = new BasicDBObject();
				setUpdate.putAll((Map) objCarreiras.get("documento"));
				jsonDocumento.put("documento", setUpdate);
				documentos.add(jsonDocumento);
			};
			mongo.close();
			return documentos;
		} catch (MongoException e) {
			e.printStackTrace();
		};
		return null;
	};

}
