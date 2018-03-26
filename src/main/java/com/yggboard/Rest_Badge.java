package com.yggboard;


import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.mongodb.MongoClient;

	
@Singleton
// @Lock(LockType.READ)
@Path("/badges")

public class Rest_Badge {

	MongoClient  mongo = new MongoClient();
	
	@Path("/obter")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject ObterBadge(@QueryParam("id") String id)  {
		System.out.println("Metodo deperecate");
		return null;
	};
	
@Path("/lista")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray ObterBadges() {
		System.out.println("Metodo deperecate");
		return null;
	};
};
