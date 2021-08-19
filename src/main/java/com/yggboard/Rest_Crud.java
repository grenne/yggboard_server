package com.yggboard;


import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Map;


//@Lock(LockType.READ)
@RestController
@RequestMapping("/crud")

public class Rest_Crud {

	MongoClient mongo = new MongoClient();
	
	@PostMapping("/obter")
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
			if (queryParam.get("token")!= null){
  			if (commons_db.getCollection(queryParam.get("token").toString(), "usuarios", "documento.token", mongo, false) == null){
  				mongo.close();
  				return Response.status(401).entity("invalid token").build();	
  			};
			}else {
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
	@PostMapping("/incluir")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response Incluir(JSONObject queryParam)  {
		System.out.println("incluir:" + queryParam.get ("collection").toString());
		Commons_DB commons_db = new Commons_DB();
		if (queryParam.get("token")!= null){
  		if (commons_db.getCollection(queryParam.get("token").toString(), "usuarios", "documento.token", mongo, false) == null){
  			mongo.close();
  			return Response.status(401).entity("invalid token").build();	
  		};
		}else {
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

	@PostMapping("/atualizar")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response Atualizar(JSONObject queryParam)  {
		System.out.println("atualizar:" + queryParam.get ("collection").toString());
		Commons_DB commons_db = new Commons_DB();
		if (queryParam.get("token")!= null){
  		if (commons_db.getCollection(queryParam.get("token").toString(), "usuarios", "documento.token", mongo, false) == null){
  			mongo.close();
  			return Response.status(401).entity("invalid token").build();	
  		};
		}else {
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

	@PostMapping("/lista")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response Lista(JSONObject queryParam)  {
		System.out.println("lista:" + queryParam.get ("collection").toString());
		Commons_DB commons_db = new Commons_DB();
		if (queryParam.get("token")!= null){
  		if (commons_db.getCollection(queryParam.get("token").toString(), "usuarios", "documento.token", mongo, false) == null){
  			mongo.close();
  			return Response.status(401).entity("invalid token").build();	
  		};
		}else {
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

	@GetMapping("/lista/nokey")	
	
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray listaNoKey(@RequestParam("token") String token, @RequestParam("collection") String collection)  {
		System.out.println("lista:" + collection);
		Commons_DB commons_db = new Commons_DB();
		if (collection == null){
  			mongo.close();
			return null;
		};
		if (collection == "usuarios"){
  			mongo.close();
			return null;
		};
		if (token == null){
  			mongo.close();
			return null;
		};
  		if (commons_db.getCollection(token, "usuarios", "documento.token", mongo, false) == null){
  			mongo.close();
  			return null;	
  		};
		return commons_db.getCollectionListaNoKey(collection, mongo, false);
	};

	@GetMapping("/lista/onekey")	
	
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray listaOneKey(@RequestParam("token") String token, @RequestParam("collection") String collection, @RequestParam("keyName") String keyName, @RequestParam("keyValue") String keyValue)  {
		System.out.println("lista:" + collection);
		Commons_DB commons_db = new Commons_DB();
		if (collection == null){
  			mongo.close();
			return null;
		};
		if (collection == "usuarios"){
  			mongo.close();
			return null;
		};
		if (keyName == null){
  			mongo.close();
			return null;
		};
		if (keyValue == null){
  			mongo.close();
			return null;
		};
		if (token == null){
  			mongo.close();
			return null;
		};
  		if (commons_db.getCollection(token, "usuarios", "documento.token", mongo, false) == null){
  			mongo.close();
  			return null;	
  		};
		return commons_db.getCollectionLista(keyValue, collection, keyName, mongo, false);
	};

	@PostMapping("/remover/all")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response RemoverAll(JSONObject queryParam)  {
		Commons_DB commons_db = new Commons_DB();
		if (queryParam.get("token")!= null){
  		if (commons_db.getCollection(queryParam.get("token").toString(), "usuarios", "documento.token", mongo, false) == null){
  			mongo.close();
  			return Response.status(401).entity("invalid token").build();	
  		};
		}else {
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
	@PostMapping("/remover")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response Remover(JSONObject queryParam)  {
		Commons_DB commons_db = new Commons_DB();
		if (queryParam.get("token")!= null){
  		if (commons_db.getCollection(queryParam.get("token").toString(), "usuarios", "documento.token", mongo, false) == null){
  			mongo.close();
  			return Response.status(401).entity("invalid token").build();	
  		};
		}else {
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
