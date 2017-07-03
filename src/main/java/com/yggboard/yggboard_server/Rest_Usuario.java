package com.yggboard.yggboard_server;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.bson.types.ObjectId;
import org.json.simple.JSONObject;

import com.mongodb.BasicDBObject;

	
@Singleton
// @Lock(LockType.READ)
@Path("/usuario")

public class Rest_Usuario {

	@SuppressWarnings({ "unchecked" })
	@Path("/confirma")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response ConfirmaSenha(@QueryParam("id") String id) {
		
		Commons_DB commons_db = new Commons_DB();
		
		ObjectId idObject = new ObjectId(id);
		
		ArrayList<JSONObject> keysArray = new ArrayList<>();
		JSONObject key = new JSONObject();
		key.put("key", "_id");
		key.put("value", idObject);
		keysArray.add(key);
		ArrayList<JSONObject> fieldsArray = new ArrayList<>();
		JSONObject field = new JSONObject();
		field.put("field", "status");
		field.put("value", "confirmado");
		fieldsArray.add(field);
		
		return commons_db.atualizarCrud("usuarios", fieldsArray, keysArray);

	};

	@SuppressWarnings("unchecked")
	@Path("/reseta")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response ResetaSenha(@QueryParam("email") String email) {
	
		Commons_DB commons_db = new Commons_DB();
		Commons commons = new Commons();

		Long time = commons.currentTime();
		String novaSenha = "ygg" + time;

		ArrayList<JSONObject> keysArray = new ArrayList<>();
		JSONObject key = new JSONObject();
		key.put("key", "documento.email");
		key.put("value", email);
		keysArray.add(key);
		ArrayList<JSONObject> fieldsArray = new ArrayList<>();
		JSONObject field = new JSONObject();
		field.put("field", "password");
		field.put("value", novaSenha);
		fieldsArray.add(field);
		
		return commons_db.atualizarCrud("usuarios", fieldsArray, keysArray);

	};

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Path("/login")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response Login(@QueryParam("email") String email, @QueryParam("password") String password, @QueryParam("macadress") String macadress) {
	
		Commons_DB commons_db = new Commons_DB();
		Commons commons = new Commons();
		ArrayList<JSONObject> keysArray = new ArrayList<>();
		JSONObject key = new JSONObject();
		key.put("key", "documento.email");
		key.put("value", email);
		keysArray.add(key);
		
		Response response = commons_db.obterCrud("usuarios", keysArray);
		
		BasicDBObject objUser = new BasicDBObject();
		if (!(response.getEntity() instanceof Boolean)){
			BasicDBObject doc = new BasicDBObject();
			doc.putAll((Map) response.getEntity());
			if (doc != null){
				objUser = (BasicDBObject) doc.get("documento");
				if (objUser.get("password").toString().equals(password)){
					key.put("key", "documento.email");
					key.put("value", email);
					keysArray.add(key);
					List<String> arrayToken = new ArrayList<String>();
					if (objUser.get("token") != null){
						arrayToken = (List<String>) objUser.get("token");
					};
					String token = "";
					if (macadress != null){
						byte[] tokenByte = commons.gerarHash(macadress);
						token = commons.stringHexa(tokenByte);
					};
					arrayToken.add(token);
					ArrayList<JSONObject> fieldsArray = new ArrayList<>();
					JSONObject field = new JSONObject();
					field.put("field", "token");
					field.put("value", arrayToken);
					fieldsArray.add(field);				
					commons_db.atualizarCrud("usuarios", fieldsArray, keysArray);
					objUser.remove("password");
					objUser.remove("token");
					objUser.put("token", token);
					// incluir evento
					BasicDBObject evento = new BasicDBObject();
					evento.put("idUsuario", email);
					evento.put("evento", "usuarios");
					evento.put("idEvento", email);
					evento.put("motivo", "login");
					evento.put("elemento", "usuarios");
					evento.put("idElemento", email);
					commons.insereEvento(evento);
					return Response.status(200).entity(objUser).build();
				};
			};
		};

		return Response.status(200).entity(false).build();
		

	};
};
