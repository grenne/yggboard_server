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

public class Rest_Objetivo {

	MongoClient mongo = new MongoClient();
	Commons_DB commons_db = new Commons_DB();
	Commons commons = new Commons();
	Objetivo objetivo = new Objetivo();

	@Path("/filtros")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray filtros(@QueryParam("token") String token, 
							@QueryParam("areasAtuacao") String areaAtuacao, 
							@QueryParam("niveis") String niveis,  
							@QueryParam("usuarioParametro") String usuarioParametro,
							@QueryParam("limite") int limite,
							@QueryParam("start") int start)  {							
	
		if (token == null) {
			mongo.close();
			return null;
		};
		if ((commons_db.getCollection(token, "usuarios", "documento.token", mongo, false)) == null) {
			mongo.close();
			return null;
		};

		if (limite == 0) {
			limite = 999999999;
		};
		
		JSONArray result = commons.controlaLimite(objetivo.filtros(areaAtuacao, niveis, usuarioParametro, mongo),limite, start);
		mongo.close();
		return result;
	};
	
	@Path("/obter")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject ObterCarreira(@QueryParam("carreira") String id) {
		System.out.println("Metodo deperecate");
		return null;
	};
	@Path("/lista")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray ObterCarreiras() {
		System.out.println("Metodo deperecate");
		return null;
	};
};
