package com.yggboard.yggboard_server;

import java.util.Map;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;

public class UserPerfil {

	Commons commons = new Commons();
	Commons_DB commons_db = new Commons_DB();
	Usuario usuario = new Usuario();
	
	@SuppressWarnings("rawtypes")
	public BasicDBObject get(String id, MongoClient mongo) {
		
		BasicDBObject userPerfil = commons_db.getCollection(id, "userPerfil", "_id", mongo, false);

		BasicDBObject userPerfilDoc = new BasicDBObject();
		if (userPerfil != null) {
			userPerfilDoc.putAll((Map) userPerfil.get("documento"));
			if (userPerfilDoc != null) {
				return null;
			}
		};
		
		return usuario.getEmail(userPerfilDoc.get("usuario").toString(), mongo);
	
	};
};
