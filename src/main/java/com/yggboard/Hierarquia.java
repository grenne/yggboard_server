package com.yggboard;


import java.util.ArrayList;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;


public class Hierarquia {

	Commons_DB commons_db = new Commons_DB();
	Commons commons = new Commons();
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public JSONArray colaboradores(String empresaId, String usuarioId, String perfil, MongoClient mongo) {

		ArrayList<JSONObject> keysArray = new ArrayList<>();
		JSONObject key = new JSONObject();
		key.put("key", "documento.empresaId");
		key.put("value", empresaId);
		keysArray.add(key);
		if (perfil != null) {
  		if (!perfil.equals("rh")) {
    		key = new JSONObject();
    		key.put("key", "documento.superior");
    		key.put("value", usuarioId);
    		keysArray.add(key);
  		};
		};
		Response response = commons_db.listaCrud("hierarquias", keysArray, mongo, false);

		JSONArray hierarquias = (JSONArray) response.getEntity();
		JSONArray results = new JSONArray();
		for (int i = 0; i < hierarquias.size(); i++) {
			BasicDBObject hierarquia = new BasicDBObject();
			hierarquia.putAll((Map) hierarquias.get(i));
			BasicDBObject result = new BasicDBObject();
			BasicDBObject usuario = commons_db.getCollectionDoc(hierarquia.get("colaborador").toString(), "usuarios", "_id", mongo, false);
			result.put("firstName", usuario.get("firstName"));
			result.put("lastName", usuario.get("lastName"));
			result.put("photo", usuario.get("photo"));
			result.put("id", usuario.get("_id").toString());
			BasicDBObject objetivo = commons_db.getCollectionDoc(hierarquia.get("objetivoId").toString(), "objetivos", "documento.id", mongo, false);
			result.put("objetivo", objetivo.get("nome"));
			result.put("objetivoId", hierarquia.get("objetivoId").toString());
			results.add(result);
		};
		return results;
	}
};
