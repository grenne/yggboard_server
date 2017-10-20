package com.yggboard.yggboard_server;

import java.util.ArrayList;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;


public class Eventos {

	Commons_DB commons_db = new Commons_DB();
	Commons commons = new Commons();
	UserPerfil userPerfil = new UserPerfil();
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public JSONArray feed(String usuarioId, MongoClient mongo)  {

		ArrayList<JSONObject> keysArray = new ArrayList<>();
		JSONObject key = new JSONObject();
		key.put("key", "documento.idSeguindo");
		key.put("value", usuarioId);
		keysArray.add(key);
		key = new JSONObject();
		key.put("key", "documento.status");
		key.put("value", "nao lido");
		keysArray.add(key);

		Response response = commons_db.listaCrud("eventos", keysArray, mongo, false);

		JSONArray eventos = (JSONArray) response.getEntity();
		JSONArray results = new JSONArray();
		for (int i = 0; i < eventos.size(); i++) {
			BasicDBObject evento = new BasicDBObject();
			evento.putAll((Map) eventos.get(i));
			BasicDBObject result = new BasicDBObject();
			BasicDBObject usuario = commons_db.getCollectionDoc(usuarioId, "usuarios", "_id", mongo, false);
			result.put("data", evento.get("data"));
			result.put("nome", usuario.get("firstName") + " " + usuario.get("lastName"));
			result.put("photo", usuario.get("photo"));
			result.put("idFeed", evento.get("_id").toString());
			result.put("titulo", commons.frase(evento.get("motivo").toString(), evento.get("elemento").toString(), "1", evento.get("idElemento").toString(), mongo));
			String assunto = "";
			switch (evento.get("elemento").toString()) {
			case "badgeConsquista":
				assunto = "badges";
				break;
			case "carreiras":
				assunto = "objetivos";
				break;
			default:
				assunto = "";
				break;
			}
			if (!assunto.equals("")) {
				result.put("imageFeed", commons_db.getCollectionDoc(evento.get("idElemento").toString(), assunto, "documento.id", mongo, false).get("badge"));
				result.put("nomeFeed", commons_db.getCollectionDoc(evento.get("idElemento").toString(), assunto, "documento.id", mongo, false).get("nome"));
			};
			result.put("descricao", commons.frase(evento.get("motivo").toString(), evento.get("elemento").toString(), "2", evento.get("idElemento").toString(), mongo));			
			results.add(result);
		};
		return results;
	};
};
