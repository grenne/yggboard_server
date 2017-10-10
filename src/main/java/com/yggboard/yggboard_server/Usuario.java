package com.yggboard.yggboard_server;

import java.util.Map;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;

public class Usuario {

	Commons commons = new Commons();
	Commons_DB commons_db = new Commons_DB();
	
	@SuppressWarnings("rawtypes")
	public BasicDBObject get(String id, MongoClient mongo) {
		
		BasicDBObject usuarioGet = commons_db.getCollection(id, "usuarios", "_id", mongo, false);
		
		BasicDBObject usuario = new BasicDBObject();
		
		if (usuarioGet != null) {
			BasicDBObject usuarioDoc = new BasicDBObject();
			usuarioDoc.putAll((Map) usuarioGet.get("documento"));
			if (usuarioDoc != null) {
  			usuario.put("firstName", usuarioDoc.get("firstName"));
  			usuario.put("lastName", usuarioDoc.get("lastName"));
  			usuario.put("nome", usuarioDoc.get("firstName") + " " + usuarioDoc.get("lastName"));
  			usuario.put("token", usuarioDoc.get("token"));
  			usuario.put("email", usuarioDoc.get("email"));
  			usuario.put("photo", usuarioDoc.get("photo"));
  			usuario.put("id", id);
  			return (BasicDBObject) usuario;
			}
		}
		usuario.put("firstName", "");
		usuario.put("lastName", "");
		usuario.put("nome", "");
		usuario.put("token", "");
		usuario.put("email", "");
		usuario.put("photo", "");
		usuario.put("id", id);
		
		return usuario;
	
	};
	
	@SuppressWarnings("rawtypes")
	public BasicDBObject getEmail(String email, MongoClient mongo) {	
		
		BasicDBObject usuarioGet = commons_db.getCollection(email, "usuarios", "documento.email", mongo, false);
		
		BasicDBObject usuario = new BasicDBObject();
		
		if (usuarioGet != null) {
			BasicDBObject usuarioDoc = new BasicDBObject();
			usuarioDoc.putAll((Map) usuarioGet.get("documento"));
			if (usuarioDoc != null) {
  			usuario.put("firstName", usuarioDoc.get("firstName"));
  			usuario.put("lastName", usuarioDoc.get("lastName"));
  			usuario.put("nome", usuarioDoc.get("firstName") + " " + usuarioDoc.get("lastName"));
  			usuario.put("token", usuarioDoc.get("token"));
  			usuario.put("email", usuario.get("email"));
  			usuario.put("id", usuario.get("_id").toString());
  			return (BasicDBObject) usuario;
			}
		}
		usuario.put("firstName", "");
		usuario.put("lastName", "");
		usuario.put("nome", "");
		usuario.put("token", "");
		usuario.put("email", "");
		usuario.put("id", "");
		
		return usuario;
	
	};
};
