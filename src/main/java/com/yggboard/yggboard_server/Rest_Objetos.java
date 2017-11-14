package com.yggboard.yggboard_server;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;

	
@Singleton
// @Lock(LockType.READ)
@Path("/objetos")

public class Rest_Objetos {
	
	MongoClient mongo = new MongoClient();
	
	Commons commons = new Commons();
	Commons_DB commons_db = new Commons_DB();
	Usuario usario = new Usuario();
	UserPerfil userPerfil = new UserPerfil();

	@Path("/obter")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject obter(@QueryParam("token") String token, @QueryParam("item") String item, @QueryParam("start") String start ,@QueryParam("limite") String limite, @QueryParam("full") String full, @QueryParam("assunto") String assunto, @QueryParam("ids") String ids)  {
		
		if (token == null) {
			mongo.close();
			return null;
		};
		if ((commons_db.getCollection(token, "usuarios", "documento.token", mongo, false)) == null) {
			mongo.close();
			return null;
		};
		
		mongo.close();
		return null;
	};
};
