package com.yggboard;


import java.util.ArrayList;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.json.simple.JSONArray;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;

	
@Singleton
// @Lock(LockType.READ)
@Path("/areaConhecimento")

public class Rest_AreaConhecimento {
	
	MongoClient mongo = new MongoClient();
	
	Commons commons = new Commons();
	Commons_DB commons_db = new Commons_DB();
	Usuario usuario = new Usuario();
	UserPerfil userPerfil = new UserPerfil();
	AreaConhecimento areaConhecimento = new AreaConhecimento();

	@SuppressWarnings({ "unchecked" })
	@Path("/lista/primarias")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray listaPrimarias(@QueryParam("token") String token)
	{
		
	System.out.println("actions");
	if (token == null) {
		System.out.println("token não informado");
		mongo.close();
		return null;
	};
	if ((commons_db.getCollection(token, "usuarios", "documento.token", mongo, false)) == null) {
		System.out.println("token invalido");
		mongo.close();
		return null;
	};
	
	
	JSONArray result = areaConhecimento.primarias(mongo);
	mongo.close();
	return result;
	};

	@SuppressWarnings({ })
	@Path("/lista/parents")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray listaParents(@QueryParam("token") String token,@QueryParam("primarias") String primarias)
	{
		
	System.out.println("actions");
	if (primarias == null) {
		System.out.println("primarias não informado");
		mongo.close();
		return null;
	};
	if (token == null) {
		System.out.println("token não informado");
		mongo.close();
		return null;
	};
	if ((commons_db.getCollection(token, "usuarios", "documento.token", mongo, false)) == null) {
		System.out.println("token invalido");
		mongo.close();
		return null;
	};
	
	
	JSONArray result = areaConhecimento.parents(primarias, mongo);
	mongo.close();
	return result;
	};
	
};
