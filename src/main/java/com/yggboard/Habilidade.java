package com.yggboard;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
		result.put("interesseHabilidade",commons.testaElementoArray(id, interesseHabilidades));

		ArrayList<String> array = (ArrayList<String>) result.get("cursos");
		JSONArray arrayPossui = new JSONArray();
		JSONArray arrayInteresse = new JSONArray();
		if (array != null) {
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
		};
		
		result.put("possuiCurso", arrayPossui);
		result.put("interesseCurso", arrayInteresse);

		array = (ArrayList<String>) result.get("objetivos");
		arrayPossui = new JSONArray();
		arrayInteresse = new JSONArray();
		if (array != null) {
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
		};
		
		result.put("possuiObjetivo", arrayPossui);
		result.put("interesseObjetivo", arrayInteresse);

		return result;
	
	};

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public JSONArray filtros(String areaConhecimentoSource, String usuarioParametro, MongoClient mongo) {
		
		BasicDBObject userPerfil = new BasicDBObject();

		userPerfil = null;

		if (usuarioParametro != null) {
			userPerfil = usuario.getUserPerfil(usuarioParametro, mongo);
		};
	
		String[] areaConhecimentoStringArray =  new String[100];
		List<String> areaConhecimentoList = new ArrayList<String>();
		if (areaConhecimentoSource != null) {
			areaConhecimentoStringArray = areaConhecimentoSource.split(";");
			areaConhecimentoList = Arrays.asList(areaConhecimentoStringArray);
		};
		ArrayList<String> areaConhecimento = new ArrayList<String>(areaConhecimentoList);
		
		JSONArray result = new JSONArray();
		JSONArray array = commons_db.getCollectionListaNoKey("habilidades", mongo, false);
		for (int i = 0; i < array.size(); i++) {
			BasicDBObject habilidade = new BasicDBObject();
			habilidade.putAll((Map) array.get(i));
			BasicDBObject item = new BasicDBObject();
			Boolean itemOK = true;
			ArrayList<String> areaConhecimentoHabilidade = (ArrayList<String>) habilidade.get("areaConhecimento");
			if (areaConhecimentoSource != null && !commons.testaArrayTodosElementos(areaConhecimento, areaConhecimentoHabilidade)){
				itemOK = false;
			};
			if (itemOK) {
				item.put("possui", "false");
				item.put("interesse", "false");
				item.put("id", habilidade.get("id").toString());
				item.put("nome", habilidade.get("nome").toString());
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
		};
		
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
	public JSONArray getUsuarios(String objetivoPar, String usuarioParametro, String string, String full, MongoClient mongo) {
		// TODO Auto-generated method stub
		return null;
	}
	@SuppressWarnings("unchecked")
	public JSONArray getCursos(String id, String usuarioParametro, String tipo, String full, MongoClient mongo) {
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
		
		if (array != null) {
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
							item.put("escola", curso.get("escola"));
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
		};
		return result;
	}
	@SuppressWarnings("unchecked")
	public JSONArray getAreaConhecimento(String id, String usuarioParametro, String tipo, String full, MongoClient mongo) {
		BasicDBObject habilidade = commons_db.getCollectionDoc(id, "habilidades", "documento.id", mongo, false);

		if (habilidade == null) {
			System.out.println("habilidade invalida");
			return null;
		};
		
		JSONArray result = new JSONArray();
		
		ArrayList<String> array = (ArrayList<String>) habilidade.get(tipo);
		
		for (int i = 0; i < array.size(); i++) {
			BasicDBObject areaConhecimento = commons_db.getCollectionDoc(array.get(i).toString(), "areaConhecimento", "documento.id", mongo, false);
			if (areaConhecimento != null) {
				BasicDBObject item = new BasicDBObject();
				if (full.equals("0")) {
					item.put("_id", areaConhecimento.get("_id"));
					item.put("id", areaConhecimento.get("id"));
					item.put("nome", areaConhecimento.get("nome"));
				}else {
					item.put("documento", areaConhecimento);						
				};
				result.add(item);
			};
		};
		return result;
	}
	@SuppressWarnings("unchecked")
	public JSONArray getObjetivos(String id, String usuarioParametro, String tipo, String full, MongoClient mongo) {
		BasicDBObject habilidade = commons_db.getCollectionDoc(id, "habilidades", "documento.id", mongo, false);

		if (habilidade == null) {
			System.out.println("habilidade invalida");
			return null;
		};
		
		JSONArray result = new JSONArray();
		
		ArrayList<String> array = (ArrayList<String>) habilidade.get(tipo);
		
		for (int i = 0; i < array.size(); i++) {
			BasicDBObject objetivo = commons_db.getCollectionDoc(array.get(i).toString(), "objetivos", "documento.id", mongo, false);
			if (objetivo != null) {
				BasicDBObject item = new BasicDBObject();
				if (full.equals("0")) {
					item.put("_id", objetivo.get("_id"));
					item.put("id", objetivo.get("id"));
					item.put("nome", objetivo.get("nome"));
				}else {
					item.put("documento", objetivo);						
				};
				result.add(item);
			};
		};
		return result;
	};
	
};
