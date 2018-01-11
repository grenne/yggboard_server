package com.yggboard;


import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.json.simple.JSONArray;

import com.mongodb.MongoClient;

@Singleton
//@Lock(LockType.READ)
@Path("/evento")

public class Rest_Eventos {

	MongoClient mongo = new MongoClient();
	
	Commons_DB commons_db = new Commons_DB();
	Commons commons = new Commons();
	Eventos eventos = new Eventos();

	@Path("/feed")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray Colaboradores(@QueryParam("token") String token, @QueryParam("usuarioId") String usuarioId)  {
		if ((commons_db.getCollection(token, "usuarios", "documento.token", mongo, false)) == null) {
			mongo.close();
			return null;
		};
		if (usuarioId == null) {
			mongo.close();
			return null;
		};
		
		JSONArray result = eventos.feed(usuarioId, mongo);
		mongo.close();
		return result;
	};
	
};
