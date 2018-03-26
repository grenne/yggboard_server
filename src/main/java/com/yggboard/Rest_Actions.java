package com.yggboard;


import java.util.ArrayList;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;

	
@Singleton
// @Lock(LockType.READ)
@Path("/actions")

public class Rest_Actions {
	
	MongoClient mongo = new MongoClient();
	
	Commons commons = new Commons();
	Commons_DB commons_db = new Commons_DB();
	Actions actions = new Actions(); 

	@SuppressWarnings({ "unchecked" })
	@Path("/set")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public BasicDBObject obter(@QueryParam("token") String token, 
													@QueryParam("action") String action, 
													@QueryParam("objeto") String objeto ,
													@QueryParam("usuario") String usuarioPar,
													@QueryParam("id") String ids,
													@QueryParam("atualizacaoDireta") Boolean atualizacaoDireta)
	{
		
		System.out.println("actions");
		if (token == null) {
			System.out.println("token não informado");
			mongo.close();
			return null;
		};
		if (action == null) {
			System.out.println("action não informada");
			mongo.close();
			return null;
		};
		if ((commons_db.getCollection(token, "usuarios", "documento.token", mongo, false)) == null) {
			System.out.println("token invalido");
			mongo.close();
			return null;
		};
		
		if (usuarioPar == null ) {
			System.out.println("usuario não informado");
			mongo.close();
			return null;
		};
		
		if (ids == null ) {
			System.out.println("id não informado");
			mongo.close();
			return null;
		};
		
		if (atualizacaoDireta == null ) {
			atualizacaoDireta = false;
		};
	
		BasicDBObject result = actions.processaAction(action, objeto, usuarioPar, ids,atualizacaoDireta, mongo);
		mongo.close();
		return result;
	};
	
};
