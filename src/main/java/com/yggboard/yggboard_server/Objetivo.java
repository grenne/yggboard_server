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
	public Object getUsuarios(String id, String usuarioParametro, String string, String full, MongoClient mongo) {
		// TODO Auto-generated method stub
		return null;
	}
	@SuppressWarnings("unchecked")
	public Object getHabilidades(String id, String usuarioParametro, String tipo, String full, MongoClient mongo) {
		BasicDBObject objetivo = commons_db.getCollectionDoc(id, "objetivos", "documento.id", mongo, false);

		if (objetivo == null) {
			System.out.println("objetivo invalido");
			return null;
		};
		
		JSONArray result = new JSONArray();
		
		ArrayList<String> array = (ArrayList<String>) objetivo.get(tipo);
		
		for (int i = 0; i < array.size(); i++) {
			System.out.println("habilidade" + array.get(i).toString());
			BasicDBObject habilidade = commons_db.getCollectionDoc(array.get(i).toString(), "habilidades", "documento.id", mongo, false);
			BasicDBObject item = new BasicDBObject();
			if (full.equals("0")) {
				item.put("_id", habilidade.get("_id"));
				item.put("id", habilidade.get("id"));
				item.put("nome", habilidade.get("nome"));
			}else {
				item.put("documento", habilidade);						
			}
			result.add(item);
		};
		return result;
	}
	@SuppressWarnings("unchecked")
	public Object getAreaAtuacao(String id, String usuarioParametro, String tipo, String full, MongoClient mongo) {
		BasicDBObject objetivo = commons_db.getCollectionDoc(id, "objetivos", "documento.id", mongo, false);

		if (objetivo == null) {
			return null;
		}
		
		JSONArray result = new JSONArray();
		
		ArrayList<String> array = (ArrayList<String>) objetivo.get(tipo);
		
		for (int i = 0; i < array.size(); i++) {
			BasicDBObject areaAtuacao = commons_db.getCollectionDoc(array.get(i).toString(), "areaAtuacao", "documento.id", mongo, false);
			BasicDBObject item = new BasicDBObject();
			if (full.equals("0")) {
				item.put("_id", areaAtuacao.get("_id"));
				item.put("id", areaAtuacao.get("id"));
				item.put("nome", areaAtuacao.get("nome"));
			}else {
				item.put("documento", areaAtuacao);						
			}
			result.add(item);
		};
		return result;
	};
	
};
