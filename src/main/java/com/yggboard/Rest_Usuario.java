package com.yggboard;


import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import org.bson.types.ObjectId;
import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;


// @Lock(LockType.READ)
@RestController
@RequestMapping("/usuario")

public class Rest_Usuario {

	MongoClient mongo = new MongoClient();
	
	Commons_DB commons_db = new Commons_DB();
	Commons commons = new Commons();
	Usuario usuario = new Usuario();
 	
	@SuppressWarnings({ "unchecked" })
	@GetMapping("/confirma")	
	
	@Produces(MediaType.APPLICATION_JSON)
	public Response ConfirmaSenha(@RequestParam("id") String id) {
		
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

	@GetMapping("/reseta")	
	
	@Produces(MediaType.APPLICATION_JSON)
	public BasicDBObject ResetaSenha(@RequestParam("email") String email) {
	
		BasicDBObject result = usuario.resetaSenha (email, mongo);
		mongo.close();
		return result;

	};		

	@GetMapping("/login")	
	
	@Produces(MediaType.APPLICATION_JSON)
	public Response Login(@RequestParam("email") String email, @RequestParam("password") String password, @RequestParam("tokenadm") String tokenadm) {

		usuario.processaLogin (email, password, tokenadm, mongo);
		mongo.close();
		return Response.status(200).entity(false).build();
	};
	
	@GetMapping("/inout/curso/selecionados")	
	
	@Produces(MediaType.APPLICATION_JSON)
	public Boolean MontaCursosSelecionados(@RequestParam("token") String token, @RequestParam("cursoId") String cursoId, @RequestParam("usuarioId") String usuarioId)  {
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
	
	@GetMapping("/status/curso/selecionado")	
	
	@Produces(MediaType.APPLICATION_JSON)
	public Boolean MontaCursosSelecionados(@RequestParam("token") String token, @RequestParam("cursoId") String cursoId, @RequestParam("status") String status, @RequestParam("usuarioId") String usuarioId)  {
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

	@GetMapping("/token")	
	
	@Produces(MediaType.APPLICATION_JSON)
	public Boolean Token(@RequestParam("token") String token) {

		if (commons_db.getCollection(token, "usuarios", "documento.token", mongo, false) == null) {
			mongo.close();
			return false;
		}else {
			mongo.close();
			return true;
		}
	};
};
