package com.yggboard.yggboard_server;

import java.util.ArrayList;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Boolean inoutCursosSelecionados(String cursoId, String usuarioId, MongoClient mongo)  {
		BasicDBObject usuario = new BasicDBObject();
		usuario = commons_db.getCollection(usuarioId, "usuarios", "_id", mongo, false);

		Boolean existeCurso = false;

		BasicDBObject usuarioDoc = new BasicDBObject();
		usuarioDoc.putAll((Map) usuario.get("documento"));

		ArrayList<Object> cursosSelecionadosNew = new ArrayList<>();
		if (usuarioDoc.get("cursosSelecionados") != null) {
			ArrayList<Object> cursosSelecionados = (ArrayList<Object>) usuarioDoc.get("cursosSelecionados");
	  		for (int i = 0; i < cursosSelecionados.size(); i++) {
	  			JSONObject curso = new JSONObject();
	  			curso.putAll((Map) cursosSelecionados.get(i));
	  			String cursoIdCompare = curso.get("id").toString();
	  			if (cursoIdCompare.equals(cursoId)) {
	  				existeCurso = true;	
	  			}else {
	  				cursosSelecionadosNew.add(cursosSelecionados.get(i));
	  			};
	  		};
		};
		
		if (!existeCurso) {
			JSONObject curso = new JSONObject();
			curso.put("status", "apendente");
			curso.put("id", cursoId);
			cursosSelecionadosNew.add(curso);
		};
		
		usuarioDoc.put("cursosSelecionados", cursosSelecionadosNew);
		
		usuario.put("documento", usuarioDoc);

		ArrayList<JSONObject> keysArray = new ArrayList<>();
		JSONObject key = new JSONObject();
		key.put("key", "_id");
		key.put("value", usuarioId);
		keysArray.add(key);
		
		ArrayList<JSONObject> fieldsArray = new ArrayList<>();
		JSONObject field = new JSONObject();
		field.put("field", "documento");
		field.put("value", usuarioDoc);
		fieldsArray.add(field);

		BasicDBObject documento = new BasicDBObject();
		documento.put("documento", usuarioDoc);

		commons_db.atualizarCrud("usuarios", fieldsArray, keysArray, usuario, mongo, false);
		return true;	
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Boolean statusCursosSelecionados(String cursoId, String status, String usuarioId, MongoClient mongo) {
		BasicDBObject usuario = new BasicDBObject();
		usuario = commons_db.getCollection(usuarioId, "usuarios", "_id", mongo, false);

		BasicDBObject usuarioDoc = new BasicDBObject();
		usuarioDoc.putAll((Map) usuario.get("documento"));

		ArrayList<Object> cursosSelecionadosNew = new ArrayList<>();
		if (usuarioDoc.get("cursosSelecionados") != null){
  		ArrayList<Object> cursosSelecionados = (ArrayList<Object>) usuarioDoc.get("cursosSelecionados");
  		for (int i = 0; i < cursosSelecionados.size(); i++) {
  			JSONObject cursoCompare = new JSONObject();
  			cursoCompare.putAll((Map) cursosSelecionados.get(i));
  			String cursoIdCompare = cursoCompare.get("id").toString();
  			if (cursoIdCompare.equals(cursoId)) {
  				JSONObject curso = new JSONObject();
  				curso.put("status", status);
  				curso.put("id", cursoId);
  				cursosSelecionadosNew.add(curso);
  			}else {
  				cursosSelecionadosNew.add(cursosSelecionados.get(i));
  			};
  		};
		};
		
		usuarioDoc.put("cursosSelecionados", cursosSelecionadosNew);
		
		usuario.put("documento", usuarioDoc);

		ArrayList<JSONObject> keysArray = new ArrayList<>();
		JSONObject key = new JSONObject();
		key.put("key", "_id");
		key.put("value", usuarioId);
		keysArray.add(key);
		
		ArrayList<JSONObject> fieldsArray = new ArrayList<>();
		JSONObject field = new JSONObject();
		field.put("field", "documento");
		field.put("value", usuarioDoc);
		fieldsArray.add(field);

		BasicDBObject documento = new BasicDBObject();
		documento.put("documento", usuarioDoc);

		commons_db.atualizarCrud("usuarios", fieldsArray, keysArray, usuario, mongo, false);
		return true;	
	}

	public BasicDBObject getUserPerfil(String usuarioPar, MongoClient mongo) {
		
		BasicDBObject usuario = commons_db.getCollectionDoc(usuarioPar, "usuarios", "_id", mongo, false);
		if (usuario.get("email") != null) {
			return commons_db.getCollectionDoc(usuario.get("email").toString(), "userPerfil", "documento.usuario", mongo, false);
		}else {
			return null;
		}
		
	}

	@SuppressWarnings("unchecked")
	public JSONArray getObjetivos(String usuarioPar, String usuarioParametro, String tipo, String full, MongoClient mongo) {
		
		BasicDBObject userPerfil = getUserPerfil(usuarioPar, mongo);
		if (userPerfil == null) {
			return null;
		}
		
		JSONArray result = new JSONArray();
		
		ArrayList<String> array = (ArrayList<String>) userPerfil.get(tipo);
		
		for (int i = 0; i < array.size(); i++) {
			BasicDBObject objetivo = commons_db.getCollectionDoc(array.get(i).toString(), "objetivos", "documento.id", mongo, false);
			BasicDBObject item = new BasicDBObject();
			if (full.equals("0")) {
				item.put("_id", objetivo.get("_id"));
				item.put("id", objetivo.get("id"));
				item.put("nome", objetivo.get("nome"));
			}else {
				item.put("documento", objetivo);						
			}
			result.add(item);
		};
		return result;
	};

	@SuppressWarnings("unchecked")
	public Object getHabilidades(String usuarioPar, String usuarioParametro, String tipo, String full, MongoClient mongo) {
		
		BasicDBObject userPerfil = getUserPerfil(usuarioPar, mongo);
		if (userPerfil == null) {
			return null;
		}
		
		JSONArray result = new JSONArray();
		
		ArrayList<String> array = (ArrayList<String>) userPerfil.get(tipo);
		
		for (int i = 0; i < array.size(); i++) {
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
	public Object getCursos(String usuarioPar, String usuarioParametro, String tipo, String full, MongoClient mongo) {
		
		BasicDBObject userPerfil = getUserPerfil(usuarioPar, mongo);
		if (userPerfil == null) {
			return null;
		}
		
		JSONArray result = new JSONArray();
		
		ArrayList<String> array = (ArrayList<String>) userPerfil.get(tipo);
		
		for (int i = 0; i < array.size(); i++) {
			BasicDBObject objetivo = commons_db.getCollectionDoc(array.get(i).toString(), "cursos", "documento.id", mongo, false);
			BasicDBObject item = new BasicDBObject();
			if (full.equals("0")) {
				item.put("_id", objetivo.get("_id"));
				item.put("id", objetivo.get("id"));
				item.put("nome", objetivo.get("nome"));
			}else {
				item.put("documento", objetivo);						
			}
			result.add(item);
		};
		return result;
	}

	@SuppressWarnings("unchecked")
	public Object getBadges(String usuarioPar, String usuarioParametro, String tipo, String full, MongoClient mongo) {
		
		BasicDBObject userPerfil = getUserPerfil(usuarioPar, mongo);
		if (userPerfil == null) {
			return null;
		}
		
		JSONArray result = new JSONArray();
		
		ArrayList<String> array = (ArrayList<String>) userPerfil.get(tipo);
		
		for (int i = 0; i < array.size(); i++) {
			BasicDBObject objetivo = commons_db.getCollectionDoc(array.get(i).toString(), "badges", "documento.id", mongo, false);
			BasicDBObject item = new BasicDBObject();
			if (full.equals("0")) {
				item.put("_id", objetivo.get("_id"));
				item.put("id", objetivo.get("id"));
				item.put("nome", objetivo.get("nome"));
			}else {
				item.put("documento", objetivo);						
			}
			result.add(item);
		};
		return result;
	}

	@SuppressWarnings("unchecked")
	public Object getAreaAtuacao(String usuarioPar, String usuarioParametro, String tipo, String full, MongoClient mongo) {
		
		BasicDBObject userPerfil = getUserPerfil(usuarioPar, mongo);
		if (userPerfil == null) {
			return null;
		}
		
		JSONArray result = new JSONArray();
		
		ArrayList<String> array = (ArrayList<String>) userPerfil.get(tipo);
		
		for (int i = 0; i < array.size(); i++) {
			BasicDBObject objetivo = commons_db.getCollectionDoc(array.get(i).toString(), "badges", "documento.id", mongo, false);
			BasicDBObject item = new BasicDBObject();
			if (full.equals("0")) {
				item.put("_id", objetivo.get("_id"));
				item.put("id", objetivo.get("id"));
				item.put("nome", objetivo.get("nome"));
			}else {
				item.put("documento", objetivo);						
			}
			result.add(item);
		};
		return result;
	}

	public JSONArray getAll(String usuarioParametro, MongoClient mongo) {
		
		JSONArray result = commons_db.getCollectionListaNoKey("usuarios", mongo, false);
		
		return result;
	
	};

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public JSONArray getIdNome(String usuarioParametro, MongoClient mongo) {
		
		JSONArray resultAll = commons_db.getCollectionListaNoKey("usuarios", mongo, false);
		
		JSONArray result = new JSONArray();
		
		for (int i = 0; i < resultAll.size(); i++) {
			BasicDBObject item = new BasicDBObject();
			BasicDBObject obj = new BasicDBObject();
			obj.putAll((Map) resultAll.get(i));
			item.put("_id", obj.get("_id"));
			item.put("nome", obj.get("firstName").toString() + obj.get("lastName").toString());
			result.add(item);
		};
		
		return result;
	
	}
	
};
