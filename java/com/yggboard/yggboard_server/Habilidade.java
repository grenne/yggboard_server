package com.yggboard.yggboard_server;

import java.util.ArrayList;
import java.util.Map;

import org.json.simple.JSONArray;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;

public class Habilidade {

	Commons commons = new Commons();
	Commons_DB commons_db = new Commons_DB();
	Usuario usuario = new Usuario();
	
	public BasicDBObject get(String id, MongoClient mongo) {
		
		BasicDBObject result = commons_db.getCollection(id, "habilidades", "_id", mongo, false);
		
		return result;
	
	};
	
	@SuppressWarnings("unchecked")
	public BasicDBObject getId(String id, String usuarioParametro, MongoClient mongo) {
		
		BasicDBObject result = commons_db.getCollectionDoc(id, "habilidades", "documento.id", mongo, false);
		
		BasicDBObject userPerfil = new BasicDBObject();

		if (usuarioParametro != null) {
			userPerfil = usuario.getUserPerfil(usuarioParametro, mongo);
		};

		ArrayList<String> possuiHabilidades = (ArrayList<String>) userPerfil.get("habilidades");
		ArrayList<String> interesseHabilidades = (ArrayList<String>) userPerfil.get("habilidadesInteresse");

		result.put("possuiHabilidade", commons.testaElementoArray(id, possuiHabilidades));
		result.put("interesseHabiliade",commons.testaElementoArray(id, interesseHabilidades));

		ArrayList<String> array = (ArrayList<String>) result.get("cursos");
		JSONArray arrayPossui = new JSONArray();
		JSONArray arrayInteresse = new JSONArray();
		for (int i = 0; i < array.size(); i++) {
			if (userPerfil != null) {
				if (userPerfil.get("cursos") != null) {
	  				ArrayList<String> itens = (ArrayList<String>) userPerfil.get("cursos");
	  				arrayPossui.add(commons.testaElementoArray(array.get(i), itens));
				};
				if (userPerfil.get("cursosInteresse") != null) {
	  				ArrayList<String> itens = (ArrayList<String>) userPerfil.get("cursosInteresse");
	  				arrayInteresse.add(commons.testaElementoArray(array.get(i), itens));
				};
			};
		};
		
		result.put("possuiCurso", arrayPossui);
		result.put("interesseCurso", arrayInteresse);

		array = (ArrayList<String>) result.get("objetivos");
		arrayPossui = new JSONArray();
		arrayInteresse = new JSONArray();
		for (int i = 0; i < array.size(); i++) {
			if (userPerfil != null) {
				if (userPerfil.get("carreiras") != null) {
	  				ArrayList<String> itens = (ArrayList<String>) userPerfil.get("carreiras");
	  				arrayPossui.add(commons.testaElementoArray(array.get(i), itens));
				};
				if (userPerfil.get("carreirasInteresse") != null) {
	  				ArrayList<String> itens = (ArrayList<String>) userPerfil.get("carreirasInteresse");
	  				arrayInteresse.add(commons.testaElementoArray(array.get(i), itens));
				};
			};
		};
		
		result.put("possuiObjetivo", arrayPossui);
		result.put("interesseObjetivo", arrayInteresse);

		return result;
	
	};

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public JSONArray getAll(String usuarioParametro, MongoClient mongo) {
		
		BasicDBObject userPerfil = new BasicDBObject();

		userPerfil = null;

		if (usuarioParametro != null) {
			userPerfil = usuario.getUserPerfil(usuarioParametro, mongo);
		};
		
		JSONArray result = new JSONArray();
		JSONArray array = commons_db.getCollectionListaNoKey("habilidades", mongo, false);
		for (int i = 0; i < array.size(); i++) {
			BasicDBObject item = new BasicDBObject();
			item.putAll((Map) array.get(i));
			item.put("possui", "false");
			item.put("interesse", "false");
			if (userPerfil != null) {
				if (userPerfil.get("habilidades") != null) {
  				ArrayList<String> itens = (ArrayList<String>) userPerfil.get("habilidades");
  				item.put("possui", commons.testaElementoArray(item.get("id").toString(), itens));
				};
				if (userPerfil.get("habilidadesInteresse") != null) {
  				ArrayList<String> itens = (ArrayList<String>) userPerfil.get("habilidadesInteresse");
  				item.put("interesse", commons.testaElementoArray(item.get("id").toString(), itens));
				};
			};
			result.add(item);
		};
		
		return result;
	
	};

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public JSONArray getIdNome(String usuarioParametro, MongoClient mongo) {
		
		BasicDBObject userPerfil = new BasicDBObject();

		userPerfil = null;

		if (usuarioParametro != null) {
			userPerfil = usuario.getUserPerfil(usuarioParametro, mongo);
		};
		
		JSONArray resultAll = commons_db.getCollectionListaNoKey("habilidades", mongo, false);
		
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
				if (userPerfil.get("habilidades") != null) {
  				ArrayList<String> itens = (ArrayList<String>) userPerfil.get("habilidades");
  				item.put("possui", commons.testaElementoArray(obj.get("id").toString(), itens));
				};
				if (userPerfil.get("habilidadesInteresse") != null) {
  				ArrayList<String> itens = (ArrayList<String>) userPerfil.get("habilidadesInteresse");
  				item.put("interesse", commons.testaElementoArray(obj.get("id").toString(), itens));
				};
			};
			result.add(item);
		};
		
		return result;
	
	}
	public Object getUsuarios(String objetivoPar, String usuarioParametro, String string, String full, MongoClient mongo) {
		// TODO Auto-generated method stub
		return null;
	}
	@SuppressWarnings("unchecked")
	public Object getCursos(String id, String usuarioParametro, String tipo, String full, MongoClient mongo) {
		BasicDBObject habilidade = commons_db.getCollectionDoc(id, "habilidades", "documento.id", mongo, false);

		if (habilidade == null) {
			System.out.println("habilidade invalida");
			return null;
		};
		
		BasicDBObject userPerfil = new BasicDBObject();

		userPerfil = null;

		if (usuarioParametro != null) {
			userPerfil = usuario.getUserPerfil(usuarioParametro, mongo);
		};
		
		JSONArray result = new JSONArray();
		
		ArrayList<String> array = (ArrayList<String>) habilidade.get(tipo);
		
		for (int i = 0; i < array.size(); i++) {
			BasicDBObject curso = commons_db.getCollectionDoc(array.get(i).toString(), "cursos", "documento.id", mongo, false);
			if (curso != null) {
  				ArrayList<String> parents = (ArrayList<String>) curso.get("parents");
  				if (parents.size() == 0) {
					BasicDBObject item = new BasicDBObject();
					if (full.equals("0")) {
						item.put("_id", curso.get("_id"));
						item.put("id", curso.get("id"));
						item.put("nome", curso.get("nome"));
					}else {
						item.put("documento", curso);						
					};
					item.put("possui", "false");
					item.put("interesse", "false");
					if (userPerfil != null) {
						if (userPerfil.get("cursos") != null) {
			  				ArrayList<String> itens = (ArrayList<String>) userPerfil.get("cursos");
			  				item.put("possui", commons.testaElementoArray(curso.get("id").toString(), itens));
						};
						if (userPerfil.get("cursosInteresse") != null) {
			  				ArrayList<String> itens = (ArrayList<String>) userPerfil.get("cursosInteresse");
			  				item.put("interesse", commons.testaElementoArray(curso.get("id").toString(), itens));
						};
					};
					item.put("link", curso.get("link"));
					result.add(item);
  				};
			};
		};
		return result;
	}
	@SuppressWarnings("unchecked")
	public Object getAreaConhecimento(String id, String usuarioParametro, String tipo, String full, MongoClient mongo) {
		BasicDBObject habilidade = commons_db.getCollectionDoc(id, "habilidades", "documento.id", mongo, false);

		if (habilidade == null) {
			System.out.println("habilidade invalida");
			return null;
		};
		
		JSONArray result = new JSONArray();
		
		ArrayList<String> array = (ArrayList<String>) habilidade.get(tipo);
		
		for (int i = 0; i < array.size(); i++) {
			BasicDBObject curso = commons_db.getCollectionDoc(array.get(i).toString(), "areaConhecimento", "documento.id", mongo, false);
			if (curso != null) {
				BasicDBObject item = new BasicDBObject();
				if (full.equals("0")) {
					item.put("_id", curso.get("_id"));
					item.put("id", curso.get("id"));
					item.put("nome", curso.get("nome"));
				}else {
					item.put("documento", curso);						
				};
				result.add(item);
			};
		};
		return result;
	}
	@SuppressWarnings("unchecked")
	public Object getObjetivos(String id, String usuarioParametro, String tipo, String full, MongoClient mongo) {
		BasicDBObject habilidade = commons_db.getCollectionDoc(id, "habilidades", "documento.id", mongo, false);

		if (habilidade == null) {
			System.out.println("habilidade invalida");
			return null;
		};
		
		JSONArray result = new JSONArray();
		
		ArrayList<String> array = (ArrayList<String>) habilidade.get(tipo);
		
		for (int i = 0; i < array.size(); i++) {
			BasicDBObject curso = commons_db.getCollectionDoc(array.get(i).toString(), "areaConhecimento", "documento.id", mongo, false);
			if (curso != null) {
				BasicDBObject item = new BasicDBObject();
				if (full.equals("0")) {
					item.put("_id", curso.get("_id"));
					item.put("id", curso.get("id"));
					item.put("nome", curso.get("nome"));
				}else {
					item.put("documento", curso);						
				};
				result.add(item);
			};
		};
		return result;
	};
	
};