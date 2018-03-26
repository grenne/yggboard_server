package com.yggboard;


import java.util.ArrayList;

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
import com.mongodb.MongoClient;

	
@Singleton
// @Lock(LockType.READ)
@Path("/usuario")

public class Rest_Usuario {

	MongoClient mongo = new MongoClient();
	
	Commons_DB commons_db = new Commons_DB();
	Commons commons = new Commons();
	Usuario usuario = new Usuario();
 	
	@SuppressWarnings({ "unchecked" })
	@Path("/confirma")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response ConfirmaSenha(@QueryParam("id") String id) {
		
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
		
		Response result = commons_db.atualizarCrud("usuarios", fieldsArray, keysArray, null, mongo, false);
		mongo.close();
		return result;

	};

	@Path("/reseta")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public BasicDBObject ResetaSenha(@QueryParam("email") String email) {
	
		BasicDBObject result = usuario.resetaSenha (email, mongo);
		mongo.close();
		return result;

	};		

	@Path("/login")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response Login(@QueryParam("email") String email, @QueryParam("password") String password, @QueryParam("tokenadm") String tokenadm) {

		usuario.processaLogin (email, password, tokenadm, mongo);
		mongo.close();
		return Response.status(200).entity(false).build();
	};
	
	@Path("/inout/curso/selecionados")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Boolean MontaCursosSelecionados(@QueryParam("token") String token, @QueryParam("cursoId") String cursoId, @QueryParam("usuarioId") String usuarioId)  {
		if ((commons_db.getCollection(token, "userPerfil", "documento.token", mongo, false)) == null) {
			mongo.close();
			return null;
		};
		if (cursoId == null){
			mongo.close();
			return false;
		};
		if (usuarioId == null){
			mongo.close();
			return false;
		};
		Boolean result = usuario.inoutCursosSelecionados(cursoId, usuarioId, mongo);
		mongo.close();
		return result;
	};
	
	@Path("/status/curso/selecionado")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Boolean MontaCursosSelecionados(@QueryParam("token") String token, @QueryParam("cursoId") String cursoId, @QueryParam("status") String status, @QueryParam("usuarioId") String usuarioId)  {
		if ((commons_db.getCollection(token, "userPerfil", "documento.token", mongo, false)) == null) {
			mongo.close();
			return null;
		};
		if (cursoId == null){
			mongo.close();
			return false;
		};
		if (usuarioId == null){
			mongo.close();
			return false;
		};
		if (status == null){
			mongo.close();
			return false;
		};
		Boolean result =  usuario.statusCursosSelecionados(cursoId, status, usuarioId, mongo);
		mongo.close();
		return result;
	};

	@Path("/token")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Boolean Token(@QueryParam("token") String token) {

		if (commons_db.getCollection(token, "usuarios", "documento.token", mongo, false) == null) {
			mongo.close();
			return false;
		}else {
			mongo.close();
			return true;
		}
	};
};
