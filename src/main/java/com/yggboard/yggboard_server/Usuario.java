package com.yggboard.yggboard_server;

import java.util.ArrayList;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

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
			curso.put("status", "pendente");
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
	};
	
};
