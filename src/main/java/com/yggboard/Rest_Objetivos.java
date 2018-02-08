package com.yggboard;


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
@Path("/objetivos")

public class Rest_Objetivos {

	MongoClient mongo = new MongoClient();
	Commons_DB commons_db = new Commons_DB();
	Commons commons = new Commons();
	Objetivo objetivo = new Objetivo();

	@Path("/filtros")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray CriaMapa(@QueryParam("token") String token, @QueryParam("areasAtuacao") String areaAtuacao, @QueryParam("niveis") String niveis,  @QueryParam("usuarioParametro") String usuarioParametro)  {
	
		if (token == null) {
			mongo.close();
			return null;
		};
		if ((commons_db.getCollection(token, "usuarios", "documento.token", mongo, false)) == null) {
			mongo.close();
			return null;
		};
		
		JSONArray result = objetivo.filtros(areaAtuacao, niveis, usuarioParametro, mongo);
		mongo.close();
		return result;
	};
	
	@SuppressWarnings({ "unchecked" })
	@Path("/obter")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject ObterCarreira(@QueryParam("carreira") String id) {
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
	public JSONArray ObterCarreiras() {
		Commons_DB commons_db = new Commons_DB();
		JSONArray cursor = commons_db.getCollectionListaNoKey("objetivos", mongo, false);		
		if (cursor != null){
			JSONArray documentos = new JSONArray();
			for (int i = 0; i < cursor.size(); i++) {
				BasicDBObject objCarreiras = new BasicDBObject();
				objCarreiras.putAll((Map) cursor.get(i));
				JSONObject jsonDocumento = new JSONObject();
				jsonDocumento.put("_id", objCarreiras.getString("_id"));
				jsonDocumento.put("documento", objCarreiras);
				documentos.add(jsonDocumento);				
			};
			mongo.close();
			return documentos;
		};
		mongo.close();
		return null;
	};
};
