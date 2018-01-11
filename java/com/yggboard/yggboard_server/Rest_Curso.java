package com.yggboard.yggboard_server;

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
import com.mongodb.MongoClient;

	
@Singleton
// @Lock(LockType.READ)
@Path("/cursos")

public class Rest_Curso {

	MongoClient mongo = new MongoClient();
	
	@SuppressWarnings({ "unchecked" })
	@Path("/obter")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject ObterCurso(@QueryParam("mail") String id)  {
		Commons_DB commons_db = new Commons_DB();

		BasicDBObject cursor = commons_db.getCollection(id, "objetivos", "documento.id", mongo, false);		
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
	public JSONArray ObterCursos() {
		Commons_DB commons_db = new Commons_DB();
		
    JSONArray cursor = commons_db.getCollectionListaNoKey("cursos", mongo, false);
		if (cursor != null){
			JSONArray documentos = new JSONArray();
			for (int i = 0; i < cursor.size(); i++) {
				BasicDBObject objCurso = new BasicDBObject();
				objCurso.putAll((Map) cursor.get(i));
				JSONObject jsonDocumento = new JSONObject();
				jsonDocumento.put("_id", objCurso.getString("_id"));
				jsonDocumento.put("documento", objCurso);
				documentos.add(jsonDocumento);
			};
			mongo.close();
			return documentos;
		};
		mongo.close();
		return null;
	};

}