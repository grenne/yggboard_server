package com.yggboard.yggboard_server;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Map;

import org.json.simple.JSONArray;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;

public class Objetivo {

	Commons commons = new Commons();
	Commons_DB commons_db = new Commons_DB();
	Usuario usuario = new Usuario();
	Habilidade habilidade = new Habilidade();
	
	public BasicDBObject get(String id, MongoClient mongo) {
		
		BasicDBObject result = commons_db.getCollection(id, "objetivos", "_id", mongo, false);
		
		return result;
	
	};
	
	@SuppressWarnings("unchecked")
	public BasicDBObject getId(String id, String usuarioParametro, MongoClient mongo) {
		
		BasicDBObject userPerfil = new BasicDBObject();

		if (usuarioParametro != null) {
			userPerfil = usuario.getUserPerfil(usuarioParametro, mongo);
		};
		
		BasicDBObject result = commons_db.getCollectionDoc(id, "objetivos", "documento.id", mongo, false);
		
		ArrayList<String> array = (ArrayList<String>) result.get("necessarios");
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
	
	};

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public JSONArray getAll(String usuarioParametro, MongoClient mongo) {
		
		BasicDBObject userPerfil = new BasicDBObject();

		userPerfil = null;

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
	  				item.put("possui", commons.testaElementoArray(item.get("id").toString(), itens));
				};
				if (userPerfil.get("objetivosInteresse") != null) {
	  				ArrayList<String> itens = (ArrayList<String>) userPerfil.get("objetivosInteresse");
	  				item.put("interesse", commons.testaElementoArray(item.get("id").toString(), itens));
				};
				//  **** calcula percentual de habilidades que o usuario possui dentro do objetivo
				ArrayList<String> necessarios = (ArrayList<String>) item.get("necessarios");
				ArrayList<String> habilidadesPossui = (ArrayList<String>) userPerfil.get("habilidades");
				int qtdeHabilidadesPossui = commons.testaArrayElementosIguais(necessarios, habilidadesPossui);
				int qtdeNecessarios = necessarios.size();
				double percentual = ((double)qtdeHabilidadesPossui / (double)qtdeNecessarios) * 100;
				DecimalFormat formatador = new DecimalFormat("0.00");
				item.put("percentual", formatador.format(percentual).toString());
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
	  				item.put("possui", commons.testaElementoArray(obj.get("id").toString(), itens));
				};
				if (userPerfil.get("objetivosInteresse") != null) {
	  				ArrayList<String> itens = (ArrayList<String>) userPerfil.get("objetivosInteresse");
	  				item.put("interesses", commons.testaElementoArray(obj.get("id").toString(), itens));
				};
				//  **** calcula percentual de habilidades que o usuario possui dentro do objetivo
				ArrayList<String> necessarios = (ArrayList<String>) obj.get("necessarios");
				ArrayList<String> habilidadesPossui = (ArrayList<String>) userPerfil.get("habilidades");
				int qtdeHabilidadesPossui = commons.testaArrayElementosIguais(necessarios, habilidadesPossui);
				int qtdeNecessarios = necessarios.size();
				double percentual = ((double)qtdeHabilidadesPossui / (double)qtdeNecessarios) * 100;
				DecimalFormat formatador = new DecimalFormat("0.00");
				item.put("percentual", formatador.format(percentual).toString());
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
	public BasicDBObject getHabilidades(String id, String usuarioParametro, String tipo, String full, MongoClient mongo) {
		BasicDBObject objetivo = commons_db.getCollectionDoc(id, "objetivos", "documento.id", mongo, false);

		if (objetivo == null) {
			System.out.println("objetivo invalido");
			return null;
		};
		
		BasicDBObject userPerfil = new BasicDBObject();

		userPerfil = null;

		if (usuarioParametro != null) {
			userPerfil = usuario.getUserPerfil(usuarioParametro, mongo);
		};
		
		BasicDBObject finalResult = new BasicDBObject();
		JSONArray result = new JSONArray();
		
		ArrayList<String> array = (ArrayList<String>) objetivo.get(tipo);
		
		for (int i = 0; i < array.size(); i++) {
			System.out.println("habilidade" + array.get(i).toString());
			BasicDBObject habilidadeObj = commons_db.getCollectionDoc(array.get(i).toString(), "habilidades", "documento.id", mongo, false);
			if (habilidadeObj != null) {
				BasicDBObject item = new BasicDBObject();
				if (full.equals("0")) {
					item.put("_id", habilidadeObj.get("_id"));
					item.put("id", habilidadeObj.get("id"));
					item.put("nome", habilidadeObj.get("nome"));
				}else {
					item.put("documento", habilidadeObj);						
				};
				item.put("possui", "false");
				item.put("interesse", "false");
				if (userPerfil != null) {
					if (userPerfil.get("habilidades") != null) {
		  				ArrayList<String> itens = (ArrayList<String>) userPerfil.get("habilidades");
		  				item.put("possui", commons.testaElementoArray(habilidadeObj.get("id").toString(), itens));
					};
					if (userPerfil.get("habilidadesInteresse") != null) {
		  				ArrayList<String> itens = (ArrayList<String>) userPerfil.get("habilidadesInteresse");
		  				item.put("interesse", commons.testaElementoArray(habilidadeObj.get("id").toString(), itens));
					};
				};
				Object cursos = habilidade.getCursos(array.get(i).toString(), usuarioParametro, "cursos", "0", mongo);
				item.put("habilidadesCursos", cursos);
				result.add(item);
			};
		};
		//  **** calcula percentual de habilidades que o usuario possui dentro do objetivo
		ArrayList<String> necessarios = (ArrayList<String>) objetivo.get("necessarios");
		ArrayList<String> habilidadesPossui = (ArrayList<String>) userPerfil.get("habilidades");
		int qtdeHabilidadesPossui = commons.testaArrayElementosIguais(necessarios, habilidadesPossui);
		int qtdeNecessarios = necessarios.size();
		double percentual = ((double)qtdeHabilidadesPossui / (double)qtdeNecessarios) * 100;
		double delta = (100 / (double)qtdeNecessarios);
		DecimalFormat formatador = new DecimalFormat("0.00");
		finalResult.put("percentual", formatador.format(percentual).toString());
		finalResult.put("delta", formatador.format(delta).toString());
		finalResult.put("objetivoHabilidades", result);
		return finalResult;
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