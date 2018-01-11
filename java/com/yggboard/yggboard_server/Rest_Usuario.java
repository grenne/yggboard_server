package com.yggboard.yggboard_server;

import java.util.ArrayList;
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

	@SuppressWarnings("unchecked")
	@Path("/reseta")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public BasicDBObject ResetaSenha(@QueryParam("email") String email) {
	
		Long time = commons.currentTime();
		String novaSenha = "ygg" + time;
		byte[] tokenByte = commons.gerarHash(novaSenha);
		String pwmd5 = commons.stringHexa(tokenByte);

		ArrayList<JSONObject> keysArray = new ArrayList<>();
		JSONObject key = new JSONObject();
		key.put("key", "documento.email");
		key.put("value", email);
		keysArray.add(key);
		ArrayList<JSONObject> fieldsArray = new ArrayList<>();
		JSONObject field = new JSONObject();
		field.put("field", "password");
		field.put("value", pwmd5);
		fieldsArray.add(field);
		
		Response result = commons_db.atualizarCrud("usuarios", fieldsArray, keysArray, null, mongo, false);
		BasicDBObject resultFinal = new BasicDBObject();
		resultFinal.put("result", result);
		resultFinal.put("newPass", novaSenha);
		mongo.close();
		return resultFinal;

	};

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Path("/login")	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response Login(@QueryParam("email") String email, @QueryParam("password") String password, @QueryParam("tokenadm") String tokenadm) {
	
		ArrayList<JSONObject> keysArray = new ArrayList<>();
		JSONObject key = new JSONObject();
		key.put("key", "documento.email");
		key.put("value", email);
		key.put("tipo", "login");
		keysArray.add(key);
		
		Response response = commons_db.obterCrud("usuarios", keysArray, mongo, false);
		if ((response.getStatus() == 200)){
			BasicDBObject cursor = new BasicDBObject();
			cursor.putAll((Map) response.getEntity());
			BasicDBObject objUser = new BasicDBObject();
			String usuarioId = cursor.get("_id").toString();
			objUser.putAll((Map) cursor.get("documento"));
			if (objUser.get("password") != null){
				if (objUser.get("password").toString().equals(password)){
					if (tokenadm != null && objUser.get("lastLogin") != null) {
						if (objUser.get("lastLogin").toString().replace("-","").equals(commons.todaysDate("yyyymmdd"))){
							objUser.remove("password");
							mongo.close();
							return Response.status(200).entity(objUser).build();
						};
					};
					key.clear();
					keysArray.clear();
					key.put("key", "documento.email");
					key.put("value", email);
					key.put("tipo", "login");
					keysArray.add(key);
					byte[] tokenByte = commons.gerarHash(commons.currentTime().toString());
					String token = commons.stringHexa(tokenByte);
					ArrayList<JSONObject> fieldsArray = new ArrayList<>();
					JSONObject field = new JSONObject();					
					field.put("field", "token");
					field.put("value", token);
					fieldsArray.add(field);				
					field = new JSONObject();					
					field.put("field", "lastLogin");
					field.put("value", commons.todaysDate("yyyy-mm-dd"));
					fieldsArray.add(field);				
					commons_db.atualizarCrud("usuarios", fieldsArray, keysArray, null, mongo, false);
	/*
	 * 					atualizar perfil
	 */
					key.clear();
					keysArray.clear();
					key.put("key", "documento.usuario");
					key.put("value", email);
					keysArray.add(key);
					commons_db.atualizarCrud("userPerfil", fieldsArray, keysArray, null, mongo, false);
					objUser.remove("password");
					objUser.remove("token");
					objUser.put("token", token);

					objUser.put("_id", cursor.get("_id").toString());
					// obter dados user perfil
					BasicDBObject userPerfil = commons_db.getCollection(email, "userPerfil", "documento.usuario", mongo, false);
					if (userPerfil != null) {
						objUser.put("idUserPerfil", userPerfil.get("_id").toString());
					};
					// obter dados da hierarquia
					BasicDBObject hierarquia = commons_db.getCollectionDoc(usuarioId, "hierarquias", "documento.colaborador", mongo, false);
					if (hierarquia != null && hierarquia.get("objetivoId") != null) {
						objUser.put("objetivoId", hierarquia.get("objetivoId").toString());
					}else {
						objUser.put("objetivoId", null);
					};
					// incluir evento
					BasicDBObject evento = new BasicDBObject();
					evento.put("idUsuario", email);
					evento.put("evento", "usuarios");
					evento.put("idEvento", email);
					evento.put("motivo", "login");
					evento.put("elemento", "usuarios");
					evento.put("idElemento", email);
					commons.insereEvento(evento, mongo);
					mongo.close();
					return Response.status(200).entity(objUser).build();
				};
			};
		};
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
