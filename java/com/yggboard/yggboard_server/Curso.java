package com.yggboard.yggboard_server;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Map;

import org.json.simple.JSONArray;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;

@SuppressWarnings("unused")
public class Curso {

	Commons commons = new Commons();
	Commons_DB commons_db = new Commons_DB();
	Usuario usuario = new Usuario();
	
	public BasicDBObject get(String id, MongoClient mongo) {
		
		BasicDBObject result = commons_db.getCollection(id, "cursos", "_id", mongo, false);
		
		return result;
	
	};
	
	@SuppressWarnings({ "unchecked" })
	public BasicDBObject getId(String id, String usuarioParametro, MongoClient mongo) {
	
		BasicDBObject userPerfil = new BasicDBObject();

		if (usuarioParametro != null) {
			userPerfil = usuario.getUserPerfil(usuarioParametro, mongo);
		};
	
		BasicDBObject result = commons_db.getCollectionDoc(id, "cursos", "documento.id", mongo, false);
		
		ArrayList<String> array = (ArrayList<String>) result.get("habilidades");
		JSONArray arrayPossui = new JSONArray();
		JSONArray arrayInteresse = new JSONArray();
		for (int i = 0; i < array.size(); i++) {
			if (userPerfil != null) {
				if (userPerfil.get("habilidades") != null) {
	  				ArrayList<String> itens = (ArrayList<String>) userPerfil.get("habilidades");
	  				arrayPossui.add(commons.testaElementoArray(array.get(i), itens));
				};
				if (userPerfil.get("habilidadesInteresse") != null) {
	  				ArrayList<String> itens = (ArrayList<String>) userPerfil.get("habilidadesInteresse");
	  				arrayInteresse.add(commons.testaElementoArray(array.get(i), itens));
				};
			};
		};
		
		result.put("possuiHabilidade", arrayPossui);
		result.put("interesseHabilidade", arrayInteresse);
		return result;
	
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public JSONArray getAll(String usuarioParametro, MongoClient mongo) {
		
		BasicDBObject userPerfil = new BasicDBObject();

		if (usuarioParametro != null) {
			userPerfil = usuario.getUserPerfil(usuarioParametro, mongo);
		};
		
		JSONArray result = new JSONArray();
		JSONArray array = commons_db.getCollectionListaNoKey("cursos", mongo, false);
		for (int i = 0; i < array.size(); i++) {
			BasicDBObject item = new BasicDBObject();
			item.putAll((Map) array.get(i));
			item.put("possui", "false");
			item.put("interesse", "false");
			if (userPerfil != null) {
				if (userPerfil.get("cursos") != null) {
  				ArrayList<String> itens = (ArrayList<String>) userPerfil.get("cursos");
  				item.put("possui", commons.testaElementoArray(item.getString("id"), itens));
				};
				if (userPerfil.get("cursosInteresse") != null) {
  				ArrayList<String> itens = (ArrayList<String>) userPerfil.get("cursosInteresse");
  				item.put("interesse", commons.testaElementoArray(item.getString("id"), itens));
				};
				if (userPerfil.get("cursosAndamento") != null) {
  				ArrayList<String> itens = (ArrayList<String>) userPerfil.get("cursosAndamento");
  				item.put("andamento", commons.testaElementoArray(item.getString("id"), itens));
				};
				if (userPerfil.get("cursosInscritos") != null) {
  				ArrayList<String> itens = (ArrayList<String>) userPerfil.get("cursosInscritos");
  				item.put("inscritos", commons.testaElementoArray(item.getString("id"), itens));
				};
				if (userPerfil.get("cursosSugeridos") != null) {
  				ArrayList<String> itens = (ArrayList<String>) userPerfil.get("cursosSugeridos");
  				item.put("sugeridos", commons.testaElementoArray(item.getString("id"), itens));
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
		
		JSONArray resultAll = commons_db.getCollectionListaNoKey("cursos", mongo, false);
		
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
				if (userPerfil.get("cursos") != null) {
  				ArrayList<String> itens = (ArrayList<String>) userPerfil.get("cursos");
  				item.put("possui", commons.testaElementoArray(item.getString("id"), itens));
				};
				if (userPerfil.get("cursosInteresse") != null) {
  				ArrayList<String> itens = (ArrayList<String>) userPerfil.get("cursosInteresse");
  				item.put("interesse", commons.testaElementoArray(item.getString("id"), itens));
				};
				if (userPerfil.get("cursosAndamento") != null) {
  				ArrayList<String> itens = (ArrayList<String>) userPerfil.get("cursosAndamento");
  				item.put("andamento", commons.testaElementoArray(item.getString("id"), itens));
				};
				if (userPerfil.get("cursosInscritos") != null) {
  				ArrayList<String> itens = (ArrayList<String>) userPerfil.get("cursosInscritos");
  				item.put("inscritos", commons.testaElementoArray(item.getString("id"), itens));
				};
				if (userPerfil.get("cursosSugeridos") != null) {
  				ArrayList<String> itens = (ArrayList<String>) userPerfil.get("cursosSugeridos");
  				item.put("sugeridos", commons.testaElementoArray(item.getString("id"), itens));
				};
				result.add(item);
			};
		};
		
		return result;
	
	}
	public Object getBadges(String objetivoPar, String usuarioParametro, String string, String full,
			MongoClient mongo) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public Object getHabilidades(String id, String usuarioParametro, String tipo, String full, MongoClient mongo) {

		BasicDBObject curso = commons_db.getCollectionDoc(id, "cursos", "documento.id", mongo, false);

		if (curso == null) {
			System.out.println("objetivo invalido");
			return null;
		};
		
		BasicDBObject userPerfil = new BasicDBObject();

		userPerfil = null;

		if (usuarioParametro != null) {
			userPerfil = usuario.getUserPerfil(usuarioParametro, mongo);
		};
		
		JSONArray result = new JSONArray();
		
		ArrayList<String> array = (ArrayList<String>) curso.get(tipo);
		
		for (int i = 0; i < array.size(); i++) {
			System.out.println("habilidade" + array.get(i).toString());
			BasicDBObject cursoObj = commons_db.getCollectionDoc(array.get(i).toString(), "habilidades", "documento.id", mongo, false);
			if (cursoObj != null) {
				BasicDBObject item = new BasicDBObject();
				if (full.equals("0")) {
					item.put("_id", cursoObj.get("_id"));
					item.put("id", cursoObj.get("id"));
					item.put("nome", cursoObj.get("nome"));
				}else {
					item.put("documento", cursoObj);						
				};
				item.put("possui", "false");
				item.put("interesse", "false");
				if (userPerfil != null) {
					if (userPerfil.get("habilidades") != null) {
		  				ArrayList<String> itens = (ArrayList<String>) userPerfil.get("habilidades");
		  				item.put("possui", commons.testaElementoArray(cursoObj.get("id").toString(), itens));
					};
					if (userPerfil.get("habilidadesInteresse") != null) {
		  				ArrayList<String> itens = (ArrayList<String>) userPerfil.get("habilidadesInteresse");
		  				item.put("interesse", commons.testaElementoArray(cursoObj.get("id").toString(), itens));
					};
				};
				result.add(item);
			};
		};
		return result;
	}
	
};