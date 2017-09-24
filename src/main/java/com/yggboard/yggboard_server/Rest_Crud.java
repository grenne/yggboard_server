package com.yggboard.yggboard_server;

import java.util.ArrayList;
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

import org.json.simple.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;


@Singleton
//@Lock(LockType.READ)
@Path("/crud")

public class Rest_Crud {

	MongoClient mongo = new MongoClient();
	
	@Path("/obter")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response Obter(JSONObject queryParam)  {
		System.out.println("obter:" + queryParam.get ("collection").toString());
		Commons_DB commons_db = new Commons_DB();
		Boolean testaToken = true;
		if (queryParam.get("tipo") != null){
			if (queryParam.get("tipo").toString().equals("login") && queryParam.get("collection").toString().equals("usuarios")){
				testaToken = false;
			};
		};
		if (testaToken){
			if (commons_db.getCollection(queryParam.get("token").toString(), "usuarios", "documento.token", mongo, false) == null){
				mongo.close();
				return Response.status(401).entity("invalid token").build();	
			};
		};
		if (queryParam.get("keys") != null && queryParam.get ("collection").toString() != null){
			return commons_db.obterCrud(queryParam.get ("collection").toString(), queryParam.get("keys"), mongo, true);
		}else{
			mongo.close();
			return Response.status(400).entity(null).build();	
		}
	};

	@SuppressWarnings("rawtypes")
	@Path("/incluir")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response Incluir(JSONObject queryParam)  {
		System.out.println("incluir:" + queryParam.get ("collection").toString());
		Commons_DB commons_db = new Commons_DB();
		if (commons_db.getCollection(queryParam.get("token").toString(), "usuarios", "documento.token", mongo, false) == null){
			mongo.close();
			return Response.status(401).entity("invalid token").build();	
		};
		Commons commons = new Commons();
		if (queryParam.get ("insert") != null && queryParam.get ("collection").toString() != null){
			Response response = commons_db.incluirCrud(queryParam.get ("collection").toString(), queryParam.get ("insert"), mongo, false); 
			if (queryParam.get ("collection").equals("usuarios")){
				BasicDBObject doc = new BasicDBObject();
				doc.putAll((Map) response.getEntity());
				if (doc != null){
					String id = doc.get("_id").toString();
					BasicDBObject evento = new BasicDBObject();
					evento.put("idUsuario", id);
					evento.put("evento", "usuarios");
					evento.put("idEvento", id);
					evento.put("motivo", "inclusao");
					evento.put("elemento", "usuario");
					evento.put("idElemento", id);
					commons.insereEvento(evento, mongo);
				};
			};
			mongo.close();
			return response;
		}else{
			mongo.close();
			return Response.status(400).entity(null).build();	
		}
	};

	@Path("/atualizar")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response Atualizar(JSONObject queryParam)  {
		System.out.println("atualizar:" + queryParam.get ("collection").toString());
		Commons_DB commons_db = new Commons_DB();
		if (commons_db.getCollection(queryParam.get("token").toString(), "usuarios", "documento.token", mongo, false) == null){
			mongo.close();
			return Response.status(401).entity("invalid token").build();	
		};
		if (queryParam.get("update") != null && queryParam.get ("collection").toString() != null && queryParam.get("keys") != null){
			return commons_db.atualizarCrud(queryParam.get ("collection").toString(), queryParam.get("update"), queryParam.get("keys"), null, mongo, true);
		}else{
			mongo.close();
			return Response.status(400).entity(null).build();	
		}
	};

	@Path("/lista")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response Lista(JSONObject queryParam)  {
		System.out.println("lista:" + queryParam.get ("collection").toString());
		Commons_DB commons_db = new Commons_DB();
		if (commons_db.getCollection(queryParam.get("token").toString(), "usuarios", "documento.token", mongo, false) == null){
			mongo.close();
			return Response.status(401).entity("invalid token").build();	
		};
		if (queryParam.get("keys") != null && queryParam.get ("collection").toString() != null){
			return commons_db.listaCrud(queryParam.get ("collection").toString(), queryParam.get("keys"), mongo, true);
		}else{
			mongo.close();
			return Response.status(400).entity(null).build();	
		}
	};

	@Path("/remover/all")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response RemoverAll(JSONObject queryParam)  {
		Commons_DB commons_db = new Commons_DB();
		if (commons_db.getCollection(queryParam.get("token").toString(), "usuarios", "documento.token", mongo, false) == null){
			mongo.close();
			return Response.status(401).entity("invalid token").build();	
		};
		if (queryParam.get ("collection").toString() != null){
			return commons_db.removerAllCrud(queryParam.get ("collection").toString(), mongo, true);
		}else{
			mongo.close();
			return Response.status(400).entity(null).build();	
		}
	};
	@SuppressWarnings("unchecked")
	@Path("/remover")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response Remover(JSONObject queryParam)  {
		Commons_DB commons_db = new Commons_DB();
		if (commons_db.getCollection(queryParam.get("token").toString(), "usuarios", "documento.token", mongo, false) == null){
			mongo.close();
			return Response.status(401).entity("invalid token").build();	
		};
		if (queryParam.get ("collection").toString() == null){
			mongo.close();
			return Response.status(400).entity(null).build();
		};
		
		ArrayList<JSONObject> keysArray = new ArrayList<>();
		JSONObject key = new JSONObject();
		key.put("key", queryParam.get ("key").toString());
		key.put("value", queryParam.get ("value").toString());
		keysArray.add(key);

		return commons_db.removerCrudMany(queryParam.get ("collection").toString(), keysArray, mongo, true);

	};
}
