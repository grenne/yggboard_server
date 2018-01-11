package com.yggboard;


import java.util.Map;

import org.json.simple.JSONArray;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;

public class AreaConhecimento {

	Commons commons = new Commons();
	Commons_DB commons_db = new Commons_DB();
	
	public BasicDBObject get(String id, MongoClient mongo) {
		
		BasicDBObject result = commons_db.getCollection(id, "areaConhecimento", "_id", mongo, false);
		
		return result;
	
	};
	public JSONArray getAll(String usuarioParametro, MongoClient mongo) {
		
		JSONArray result = commons_db.getCollectionListaNoKey("areaConhecimento", mongo, false);
		
		return result;
	
	};

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public JSONArray getIdNome(String usuarioParametro, MongoClient mongo) {
		
		JSONArray resultAll = commons_db.getCollectionListaNoKey("areaConhecimento", mongo, false);
		
		JSONArray result = new JSONArray();
		
		for (int i = 0; i < resultAll.size(); i++) {
			BasicDBObject item = new BasicDBObject();
			BasicDBObject obj = new BasicDBObject();
			obj.putAll((Map) resultAll.get(i));
			item.put("_id", obj.get("_id"));
			item.put("id", obj.get("id"));
			item.put("nome", obj.get("nome"));
			result.add(item);
		};
		
		return result;
	
	};
	
};
