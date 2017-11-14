package com.yggboard.yggboard_server;

import java.util.ArrayList;
import java.util.Map;

import org.json.simple.JSONArray;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;

public class Objetivo {

	Commons commons = new Commons();
	Commons_DB commons_db = new Commons_DB();
	Usuario usuario = new Usuario();
	
	public BasicDBObject get(String id, MongoClient mongo) {
		
		BasicDBObject result = commons_db.getCollection(id, "objetivos", "_id", mongo, false);
		
		return result;
	
	};
	
	public BasicDBObject getId(String id, MongoClient mongo) {
		
		BasicDBObject result = commons_db.getCollectionDoc(id, "objetivos", "documento.id", mongo, false);
		
		return result;
	
	};

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public JSONArray getAll(String usuarioParametro, MongoClient mongo) {
		
		BasicDBObject userPerfil = new BasicDBObject();

		if (usuarioParametro != null) {
			userPerfil = usuario.getUserPerfil(usuarioParametro, mongo);
		};
		
		JSONArray result = new JSONArray();
		JSONArray array = commons_db.getCollectionListaNoKey("objetivos", mongo, false);
		for (int i = 0; i < array.size(); i++) {
			BasicDBObject item = new BasicDBObject();
			item.putAll((Map) array.get(i));
			item.put("possui", "false");
			item.put("interesse", "false");
			if (userPerfil != null) {
				if (userPerfil.get("objetivos") != null) {
  				ArrayList<String> itens = (ArrayList<String>) userPerfil.get("objetivos");
  				item.put("possui", commons.testaElementoArray(item.getString("id"), itens));
				};
				if (userPerfil.get("objetivosInteresse") != null) {
  				ArrayList<String> itens = (ArrayList<String>) userPerfil.get("objetivosInteresse");
  				item.put("interesse", commons.testaElementoArray(item.getString("id"), itens));
				};
			};
			result.add(item);
		};
		
		return result;
	
	};

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public JSONArray getIdNome(String usuarioParametro, MongoClient mongo) {
		
		BasicDBObject userPerfil = new BasicDBObject();

		if (usuarioParametro != null) {
			userPerfil = usuario.getUserPerfil(usuarioParametro, mongo);
		};
		
		JSONArray resultAll = commons_db.getCollectionListaNoKey("objetivos", mongo, false);
		
		JSONArray result = new JSONArray();
		
		for (int i = 0; i < resultAll.size(); i++) {
			BasicDBObject item = new BasicDBObject();
			BasicDBObject obj = new BasicDBObject();
			obj.putAll((Map) resultAll.get(i));
			item.put("_id", obj.get("_id"));
			item.put("id", obj.get("id"));
			item.put("nome", obj.get("nome"));
			item.put("possui", "false");
			item.put("interesse", "false");
			if (userPerfil != null) {
				if (userPerfil.get("objetivos") != null) {
  				ArrayList<String> itens = (ArrayList<String>) userPerfil.get("objetivos");
  				item.put("possui", commons.testaElementoArray(item.getString("id"), itens));
				};
				if (userPerfil.get("objetivosInteresse") != null) {
  				ArrayList<String> itens = (ArrayList<String>) userPerfil.get("objetivosInteresse");
  				item.put("possui", commons.testaElementoArray(item.getString("id"), itens));
				};
			};
			result.add(item);
		};
		
		return result;
	
	}
	public Object getUsuarios(String usuarioPar, String usuarioParametro, String string, String full, MongoClient mongo) {
		// TODO Auto-generated method stub
		return null;
	}
	public Object getHabilidades(String usuarioPar, String usuarioParametro, String string, String full, MongoClient mongo) {
		// TODO Auto-generated method stub
		return null;
	}
	public Object getAreaAtuacao(String usuarioPar, String usuarioParametro, String string, String full, MongoClient mongo) {
		// TODO Auto-generated method stub
		return null;
	};
	
};
