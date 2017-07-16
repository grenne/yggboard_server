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
@Path("/cursos")

public class Rest_Curso {

	@SuppressWarnings("unchecked")
	@Path("/obter")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject ObterCurso(@QueryParam("mail") String habilidade) throws UnknownHostException, MongoException {
		Commons commons = new Commons();
		Mongo mongo = new Mongo();
		DB db = (DB) mongo.getDB(commons.getProperties().get("database").toString());
		DBCollection collection = db.getCollection("cursos");
		BasicDBObject searchQuery = new BasicDBObject("documento.habilidade", habilidade);
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
	public JSONArray ObterCursos(@QueryParam("habilidade") String habilidade) {
		Commons commons = new Commons();
		Mongo mongo;
		try {
			mongo = new Mongo();
			DB db = (DB) mongo.getDB(commons.getProperties().get("database").toString());
			BasicDBObject setQuery = new BasicDBObject();
		    if (habilidade != null){
		    	setQuery.put("documento.habilidades.habilidade", habilidade);
		    };
			DBCollection collection = db.getCollection("cursos");
			
			DBCursor cursor = collection.find(setQuery);
			JSONArray documentos = new JSONArray();
			while (((Iterator<DBObject>) cursor).hasNext()) {
				BasicDBObject obj = (BasicDBObject) ((Iterator<DBObject>) cursor).next();
				JSONObject jsonDocumento = new JSONObject();
				jsonDocumento.put("_id", obj.getString("_id"));
				BasicDBObject setUpdate = new BasicDBObject();
				setUpdate.putAll((Map) obj.get("documento"));
				jsonDocumento.put("documento", setUpdate);
				documentos.add(jsonDocumento);
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
